package com.example.todoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.todoapp.model.TaskEntity
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.viewmodel.TaskViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()
    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var repository: TaskRepository

    @Mock
    private lateinit var observer: Observer<List<TaskEntity>>

    private lateinit var viewModel: TaskViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        viewModel = TaskViewModel(repository)
    }


    @After
    fun tearDown(){
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testInsertTask(): Unit = runBlocking {
        val task = TaskEntity(null, "Title", "Description", false, "High", 1655971200000L, null)
        viewModel.insert(task)
        verify(repository).insertTask(task)
    }

    @Test
    fun testUpdateTask() = runBlocking {
        val task = TaskEntity(1, "Updated Title", "Updated Description", true, "Medium", 1655971200000L, null)
        viewModel.update(task)
        verify(repository).updateTask(task)
    }

    @Test
    fun testDeleteTask() = runBlocking {
        val task = TaskEntity(1, "Title", "Description", false, "Low", 1655971200000L, null)
        viewModel.delete(task)
        verify(repository).deleteTask(task)
    }


    @Test
    fun testGetTaskById() {
        val task = TaskEntity(1, "Title", "Description", false, "High", 1655971200000L, null)
        `when`(repository.getTaskById(1)).thenReturn(mock(LiveData::class.java) as LiveData<TaskEntity>)
        viewModel.getTaskById(1).observeForever { }
        task.id?.let { verify(repository).getTaskById(it) }
    }

    @Test
    fun testGetTasksByStatus() {
        listOf(
            TaskEntity(1, "Title1", "Description1", true, "High", 1655971200000L, null),
            TaskEntity(2, "Title2", "Description2", false, "Medium", 1655971200000L, null)
        )
        `when`(repository.getTasksByStatus(true)).thenReturn(mock(LiveData::class.java) as LiveData<List<TaskEntity>>)
        viewModel.getTasksByStatus(true).observeForever { }
        verify(repository).getTasksByStatus(true)
    }

    @Test
    fun testGetTasksByPriority() {
        listOf(
            TaskEntity(1, "Title1", "Description1", false, "High", 1655971200000L, null),
            TaskEntity(2, "Title2", "Description2", true, "High", 1655971200000L, null)
        )
        `when`(repository.getTasksByPriority("High")).thenReturn(mock(LiveData::class.java) as LiveData<List<TaskEntity>>)
        viewModel.getTasksByPriority("High").observeForever { }
        verify(repository).getTasksByPriority("High")
    }
}