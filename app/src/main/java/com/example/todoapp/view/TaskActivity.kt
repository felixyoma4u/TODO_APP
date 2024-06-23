package com.example.todoapp.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.adapter.TaskAdapter
import com.example.todoapp.databinding.ActivityTaskBinding
import com.example.todoapp.db.TaskDatabase
import com.example.todoapp.repository.TaskRepository
import com.example.todoapp.viewmodel.TaskViewModel
import com.example.todoapp.viewmodel.TaskViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskBinding
    private lateinit var adapter: TaskAdapter

    private val viewModel: TaskViewModel by viewModels() {
        val db = TaskDatabase.getDatabase(this)
        val repository = TaskRepository(db.taskDao())
        TaskViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        setSupportActionBar(findViewById<Toolbar>(R.id.topAppBar))
        adapter = TaskAdapter(viewModel)

        binding.run {
            // ... recycler view setup
            recyclerViewTask.layoutManager = LinearLayoutManager(this@TaskActivity)
            recyclerViewTask.adapter = adapter

            addTaskButton.setOnClickListener {
                startActivity(Intent(this@TaskActivity, AddEditTaskActivity::class.java))
            }
        }

        viewModel.run {
            allTasks.observe(this@TaskActivity) { tasks ->
                tasks?.let {
                    adapter.onInsert(it)
                    binding.emptyTextView.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                }
            }

        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = adapter.getTaskAtPosition(position)
                viewModel.delete(task)
                Toast.makeText(this@TaskActivity, "Task Deleted", Toast.LENGTH_SHORT).show()
            }

        }).attachToRecyclerView(binding.recyclerViewTask)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                showFilterDialog()
                true
            }

            R.id.action_sort -> {
                showSortDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFilterDialog() {
        val filterOptions = arrayOf("All", "Completed", "In Progress")
        MaterialAlertDialogBuilder(this)
            .setTitle("Filter Tasks")
            .setItems(filterOptions) { _, item ->

                when (filterOptions[item]) {
                    "Completed" -> {
                        getTaskByStatus(true)
                    }

                    "In Progress" -> {
                        getTaskByStatus(false)
                    }

                    else -> {
                        getAllTask()
                    }

                }
            }
            .show()
    }

    private fun showSortDialog() {
        val sortOptions = arrayOf("All", "High Priority", "Medium Priority", "Low Priority")
        MaterialAlertDialogBuilder(this)
            .setTitle("Sort Tasks")
            .setItems(sortOptions) { _, which ->

                when (which) {
                    1 -> {
                        getPriorityList("High")
                    }

                    2 -> {
                        getPriorityList("Medium")
                    }

                    3 -> {
                        getPriorityList("Low")
                    }

                    else -> {
                        getAllTask()
                    }
                }
            }
            .show()
    }


    private fun getPriorityList(priority: String) {
        viewModel.getTasksByPriority(priority).observe(this@TaskActivity) { tasks ->
            tasks?.let {
                adapter.onInsert(it)
            }
        }
    }

    private fun getAllTask() {
        viewModel.allTasks.observe(this@TaskActivity) { tasks ->
            tasks?.let {
                adapter.onInsert(it)
            }
        }
    }

    private fun getTaskByStatus(status: Boolean) {
        viewModel.getTasksByStatus(status).observe(this@TaskActivity) { tasks ->
            tasks?.let {
                adapter.onInsert(it)
            }
        }
    }
}