package com.example.to_dolist

sealed interface TaskEvent {
    object SaveTask: TaskEvent
    data class SetTitle(val title: String): TaskEvent
    data class SetDescription(val description: String): TaskEvent
    data class SetIsCompleted(val isCompleted: Boolean): TaskEvent
    object ShowDialog: TaskEvent
    object HideDialog: TaskEvent
    data class SortTasks(val sortType: SortType): TaskEvent
    data class DeleteTask(val task: Task): TaskEvent
}