package com.example.to_dolist

data class TaskState(
    val tasks: List<Task> = emptyList(),
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val isAddingTask: Boolean = false,
    val sortType: SortType = SortType.DATE
)
