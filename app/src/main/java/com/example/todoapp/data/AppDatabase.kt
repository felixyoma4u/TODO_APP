package com.example.todoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todoapp.data.dao.TaskDao
import com.example.todoapp.model.TaskEntity
import com.example.todoapp.utils.Constants.DATABASE_NAME

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class TaskDatabase: RoomDatabase(){

    abstract fun taskDao(): TaskDao

}