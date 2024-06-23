package com.example.todoapp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.db.entities.TaskEntity

@Dao
interface TaskDao {

   @Query("SELECT * FROM tasks ORDER BY date DESC")
    fun getAllTask(): LiveData<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = :isCompleted ORDER BY date DESC")
    fun getTasksByStatus(isCompleted: Boolean): LiveData<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY date DESC")
    fun getTasksByPriority(priority: String): LiveData<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTaskById(id: Int): LiveData<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

}