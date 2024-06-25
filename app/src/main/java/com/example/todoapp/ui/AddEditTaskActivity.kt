package com.example.todoapp.ui

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapp.R
import com.example.todoapp.databinding.ActivityAddEditTaskBinding
import com.example.todoapp.model.TaskEntity
import com.example.todoapp.utils.NotificationReceiver
import com.example.todoapp.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddEditTaskActivity : AppCompatActivity() {

    // creating of variable instances
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var binding: ActivityAddEditTaskBinding
    private var taskId: Int? = null
    private var reminderTime: Long? = null


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

        // updating of task by using the task id to fetch the task to be updated
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
        }

// Actions button to used for saving, updating and displaying time picker dialog
        binding.run {
            setReminderButton.setOnClickListener { showTimePickerDialog() }
            saveButton.setOnClickListener { saveTask() }
        }

    }

    // this function contains the logic for the saving, updating and scheduling notification
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

    // The time picker logic
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