package com.example.todoapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.todoapp.db.entities.TaskEntity
import com.example.todoapp.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository
): ViewModel() {

    private val job = Job()
    private val coroutineScope = CoroutineScope(job + Dispatchers.IO)

    val allTasks: LiveData<List<TaskEntity>> = repository.allTasks

    fun getTasksByStatus(isCompleted: Boolean) = repository.getTasksByStatus(isCompleted)

    fun getTasksByPriority(priority: String) = repository.getTasksByPriority(priority)

    fun insert(task: TaskEntity) {
        coroutineScope.launch {
            repository.insertTask(task)
        }
    }

    fun update(task: TaskEntity) {
        coroutineScope.launch {
            repository.updateTask(task)
        }
    }

    fun delete(task: TaskEntity) {
        coroutineScope.launch {
            repository.deleteTask(task)
        }
    }
}