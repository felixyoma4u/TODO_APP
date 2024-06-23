package com.example.todoapp.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    var title: String,
    var description: String,
    var isCompleted: Boolean,
    var priority: String,
    var date: Long = System.currentTimeMillis(), // Default date is current time
    var reminderTime: Long? = null
){
    fun formatDate(): String{
        val sdf = SimpleDateFormat("dd/MMM/yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(date))
    }
}
