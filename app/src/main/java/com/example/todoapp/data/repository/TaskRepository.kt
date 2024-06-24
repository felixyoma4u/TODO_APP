package com.example.todoapp.data.repository

import androidx.lifecycle.LiveData
import com.example.todoapp.data.dao.TaskDao
import com.example.todoapp.model.TaskEntity
import com.google.firebase.firestore.FirebaseFirestore

class TaskRepository(private val taskDoo: TaskDao) {

    val allTasks: LiveData<List<TaskEntity>> = taskDoo.getAllTask()

    private val db = FirebaseFirestore.getInstance()

    fun getTasksByStatus(isCompleted: Boolean) = taskDoo.getTasksByStatus(isCompleted)

    fun getTasksByPriority(priority: String) = taskDoo.getTasksByPriority(priority)

    fun getTaskById(id: Int) = taskDoo.getTaskById(id)

    suspend fun insertTask(task: TaskEntity) {
        taskDoo.insertTask(task)
        db.collection("tasks").document(task.id.toString()).set(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDoo.updateTask(task)
        db.collection("tasks").document(task.id.toString()).set(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDoo.deleteTask(task)
        db.collection("tasks").document(task.id.toString()).delete()
    }


}