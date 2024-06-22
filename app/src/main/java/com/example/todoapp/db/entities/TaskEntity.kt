package com.example.todoapp.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val priority: String,
    val date: Long = System.currentTimeMillis() // Default date is current time
){
    fun formatDate(): String{
        val sdf = SimpleDateFormat("dd/MMM/yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(date))
    }
}
