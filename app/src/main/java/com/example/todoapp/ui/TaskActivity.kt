package com.example.todoapp.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.ui.adapter.TaskAdapter
import com.example.todoapp.databinding.ActivityTaskBinding
import com.example.todoapp.viewmodel.TaskViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskActivity : AppCompatActivity() {

    // Declaration of variables
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var binding: ActivityTaskBinding
    private lateinit var adapter: TaskAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Binding the layout
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Setting up the toolbar
        setSupportActionBar(findViewById<Toolbar>(R.id.topAppBar))

        // Setting up the recycler view
        adapter = TaskAdapter(viewModel)


        binding.run {
            // ... recycler view setup
            recyclerViewTask.layoutManager = LinearLayoutManager(this@TaskActivity)
            recyclerViewTask.adapter = adapter

            addTaskButton.setOnClickListener {
                startActivity(Intent(this@TaskActivity, AddEditTaskActivity::class.java))
            }
        }

        // observing the data from the database and updating the recycler view
        viewModel.run {
            allTasks.observe(this@TaskActivity) { tasks ->
                tasks?.let {
                    adapter.onInsert(it)
                    binding.emptyTextView.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                }
            }

        }


        // this is a item touch listener that work with the recycler view to enable swipe to delete
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

    override fun onResume() {
        super.onResume()
        checkInternet()
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

    // function to show the filter dialog
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

    // function to show the sort dialog
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


    // function to get the tasks by priority
    private fun getPriorityList(priority: String) {
        viewModel.getTasksByPriority(priority).observe(this@TaskActivity) { tasks ->
            tasks?.let {
                adapter.onInsert(it)
            }
        }
    }

    // function to get all the tasks
    private fun getAllTask() {
        viewModel.allTasks.observe(this@TaskActivity) { tasks ->
            tasks?.let {
                adapter.onInsert(it)
            }
        }
    }

    // function to get the tasks by status
    private fun getTaskByStatus(status: Boolean) {
        viewModel.getTasksByStatus(status).observe(this@TaskActivity) { tasks ->
            tasks?.let {
                adapter.onInsert(it)
            }
        }
    }

    // function to check the internet connection
    private fun checkInternet() {
        val connectionManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectionManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            Log.d("Network", "Network Available")
        } else {
            MaterialAlertDialogBuilder(this)
                .setTitle("Network Not Available")
                .setMessage("Please check your internet connection")
                .show()
        }
    }
}