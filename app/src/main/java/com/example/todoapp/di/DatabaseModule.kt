package com.example.todoapp.di

import android.content.Context
import androidx.room.Room
import com.example.todoapp.data.TaskDatabase
import com.example.todoapp.data.dao.TaskDao
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.utils.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        TaskDatabase::class.java,
        DATABASE_NAME
    ).build()

    @Provides
    @Singleton
    fun provideTaskDao(database: TaskDatabase) = database.taskDao()

    @Provides
    @Singleton
    fun provideTaskRepository(taskDao: TaskDao) = TaskRepository(taskDao)


}