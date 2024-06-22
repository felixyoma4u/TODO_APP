package com.example.todoapp.view

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
import com.example.todoapp.viewmodel.TaskViewModel
import com.example.todoapp.viewmodel.TaskViewModelFactory
import java.util.Date

class AddEditTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEditTaskBinding
    private var taskId: Int? = null
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
            binding.prioritySpinner.adapter = adapter
        }

        taskId = intent.getIntExtra("TASK_ID", -1)
        if (taskId != -1) {
            binding.run {
                taskTitleInput.setText(intent.getStringExtra("TASK_TITLE"))
                taskDescriptionInput.setText(intent.getStringExtra("TASK_DESCRIPTION"))
                taskCompleted.isChecked = intent.getBooleanExtra("TASK_COMPLETED", false)
                val priority = intent.getStringExtra("TASK_PRIORITY")
                prioritySpinner.setSelection(
                    resources.getStringArray(R.array.priority_items).indexOf(priority)
                )
            }
        }

        binding.run {
            saveButton.setOnClickListener {
                saveTask()
            }
        }

    }

    private fun saveTask() {
        val title = binding.taskTitleInput.text.toString().trim()
        val description = binding.taskDescriptionInput.text.toString().trim()
        val priority = binding.prioritySpinner.selectedItem.toString()
        val isCompleted = binding.taskCompleted.isActivated
        if (title.isBlank() || description.isBlank()) {
            Toast.makeText(this, "Please enter a title and description", Toast.LENGTH_SHORT).show()
            return
        }
        val task = TaskEntity(
            id = taskId?.toLong() ?: 0,
            title = title,
            description = description,
            priority = priority,
            isCompleted = isCompleted,
        )
        if (taskId != -1){
            viewModel.update(task)
            Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show()
        }else{
            viewModel.insert(task)
            Toast.makeText(this, "Task saved", Toast.LENGTH_SHORT).show()
        }
        finish()
    }
}