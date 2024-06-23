package com.example.todoapp.view

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todoapp.R
import com.example.todoapp.databinding.ActivityAddEditTaskBinding
import com.example.todoapp.db.TaskDatabase
import com.example.todoapp.db.entities.TaskEntity
import com.example.todoapp.repository.TaskRepository
import com.example.todoapp.utils.NotificationReceiver
import com.example.todoapp.viewmodel.TaskViewModel
import com.example.todoapp.viewmodel.TaskViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddEditTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEditTaskBinding
    private var taskId: Int? = null
    private var reminderTime: Long? = null

    private val viewModel: TaskViewModel by viewModels() {
        val db = TaskDatabase.getDatabase(this)
        val repository = TaskRepository(db.taskDao())
        TaskViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setup Spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.priority_items,
            android.R.layout.simple_spinner_item,
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.taskPriority.adapter = adapter
        }

        taskId = intent.getIntExtra("TASK_ID", -1).takeIf { it != -1 }

        taskId?.let {
            viewModel.getTaskById(it).observe(this) { task ->
                binding.apply {
                    taskTitle.setText(task.title)
                    taskDescription.setText(task.description)
                    val priority = intent.getStringExtra("TASK_PRIORITY")
                    taskPriority.setSelection(
                        resources.getStringArray(R.array.priority_items).indexOf(priority)
                    )
                    reminderTime = task.reminderTime
                    taskCompletedCheckbox.isChecked = task.isCompleted
                }
            }

//        if (taskId == null) {
//            binding.run {
//                taskTitleInput.setText(intent.getStringExtra("TASK_TITLE"))
//                taskDescriptionInput.setText(intent.getStringExtra("TASK_DESCRIPTION"))
//                taskCompleted.isChecked = intent.getBooleanExtra("TASK_IS_COMPLETED", false)
//                val priority = intent.getStringExtra("TASK_PRIORITY")
//                prioritySpinner.setSelection(
//                    resources.getStringArray(R.array.priority_items).indexOf(priority)
//                )
//            }
//        }
        }

        binding.run {
            setReminderButton.setOnClickListener { showTimePickerDialog() }
            saveButton.setOnClickListener { saveTask() }
        }

    }

    private fun saveTask() {
        val title = binding.taskTitle.text.toString().trim()
        val description = binding.taskDescription.text.toString().trim()
        val priority = binding.taskPriority.selectedItem.toString()
        val isCompleted = binding.taskCompletedCheckbox.isChecked
        if (title.isBlank() || description.isBlank()) {
            Toast.makeText(this, "Please enter a title and description", Toast.LENGTH_SHORT)
                .show()
            return
        }
        val task = TaskEntity(
            id = taskId,
            title = title,
            description = description,
            priority = priority,
            isCompleted = isCompleted,
            reminderTime = reminderTime
        )
        if (taskId != null) {
            viewModel.update(task)
            Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.insert(task)
            Toast.makeText(this, "Task saved", Toast.LENGTH_SHORT).show()
        }
        if (reminderTime != null) {
            scheduleNotification(task)
        }
        finish()
    }


    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            reminderTime = calendar.timeInMillis
            binding.reminderTimeTextView.text =
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(reminderTime!!))
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
        timePickerDialog.show()
    }

    // notification scheduling logic
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification(task: TaskEntity) {
        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("notificationId", task.id)
            putExtra("title", task.title)
            putExtra("description", task.description)
        }
        task.id?.let {
            val pendingIntent =
                PendingIntent.getBroadcast(this, it, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, task.reminderTime!!, pendingIntent)

        }
    }

}