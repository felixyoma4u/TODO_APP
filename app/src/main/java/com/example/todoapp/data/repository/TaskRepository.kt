package com.example.todoapp.data.repository

import androidx.lifecycle.LiveData
import com.example.todoapp.data.dao.TaskDao
import com.example.todoapp.model.TaskEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TaskRepository(private val taskDoo: TaskDao) {

    val allTasks: LiveData<List<TaskEntity>> = taskDoo.getAllTask()

    private val db = Firebase.firestore

    fun getTasksByStatus(isCompleted: Boolean) = taskDoo.getTasksByStatus(isCompleted)

    fun getTasksByPriority(priority: String) = taskDoo.getTasksByPriority(priority)

    fun getTaskById(id: Int) = taskDoo.getTaskById(id)

    suspend fun insertTask(task: TaskEntity) {
        taskDoo.insertTask(task)
// Get the auto-generated ID after inserting into Room
        val taskId = task.id ?: return
        db.collection("tasks").document(taskId.toString()).set(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDoo.updateTask(task)
        val taskId = task.id ?: return
        db.collection("tasks").document(taskId.toString()).set(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDoo.deleteTask(task)
        val taskId = task.id ?: return
        db.collection("tasks").document(taskId.toString()).delete()
    }


}