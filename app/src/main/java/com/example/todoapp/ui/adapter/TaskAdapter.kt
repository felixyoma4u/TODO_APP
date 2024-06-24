package com.example.todoapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.ItemTaskBinding
import com.example.todoapp.model.TaskEntity
import com.example.todoapp.ui.AddEditTaskActivity
import com.example.todoapp.viewmodel.TaskViewModel

class TaskAdapter(private val viewModel: TaskViewModel): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var tasksList: List<TaskEntity> = listOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context) , parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = tasksList[position]
        holder.onBindView(currentTask)
    }

    override fun getItemCount(): Int {
        return tasksList.size
    }

    fun onInsert(task: List<TaskEntity>){
        this.tasksList = task
        notifyDataSetChanged()
    }

    fun getTaskAtPosition(postion: Int): TaskEntity {
        return tasksList[postion]
    }


    inner class TaskViewHolder(private val binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root), View.OnClickListener{
        init {
            itemView.setOnClickListener(this)
        }

        fun onBindView(task: TaskEntity){
            binding.apply {
                taskTitle.text = task.title
                taskDescription.text = task.description
                taskPriority.text = task.priority
                taskDate.text = task.formatDate()
                taskStatus.isChecked = task.isCompleted

                taskStatus.setOnClickListener {
                    task.isCompleted = taskStatus.isChecked
                    viewModel.update(task)
                }
            }
        }

        override fun onClick(view: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                itemView.context.startActivity(Intent(itemView.context, AddEditTaskActivity::class.java ).apply {
                    putExtra("TASK_ID", tasksList[position].id)
                    putExtra("TASK_TITLE", tasksList[position].title)
                    putExtra("TASK_DESCRIPTION", tasksList[position].description)
                    putExtra("TASK_PRIORITY", tasksList[position].priority)
                    putExtra("TASK_IS_COMPLETED", tasksList[position].isCompleted)
                })
            }
        }
    }
}