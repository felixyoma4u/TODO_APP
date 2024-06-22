package com.example.todoapp.repository

import androidx.lifecycle.LiveData
import com.example.todoapp.db.dao.TaskDao
import com.example.todoapp.db.entities.TaskEntity

class TaskRepository(private val taskDoo: TaskDao) {

    val allTasks: LiveData<List<TaskEntity>> = taskDoo.getAllTask()

    fun getTasksByStatus(isCompleted: Boolean) = taskDoo.getTasksByStatus(isCompleted)

    fun getTasksByPriority(priority: String) = taskDoo.getTasksByPriority(priority)

    suspend fun insertTask(task: TaskEntity) = taskDoo.insertTask(task)

    suspend fun updateTask(task: TaskEntity) = taskDoo.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) = taskDoo.deleteTask(task)


}