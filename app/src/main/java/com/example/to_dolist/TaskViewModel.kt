package com.example.to_dolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModel(
    private val dao: TaskDao
): ViewModel() {

    private val _sortType = MutableStateFlow(SortType.DATE)
    private val _tasks = _sortType
        .flatMapLatest { sortType ->
            when(sortType) {
                SortType.TITLE -> dao.getTasksOrderedByTitle()
                SortType.DATE -> dao.getTasksOrderedByDate()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(TaskState())
    val state = combine(_state, _sortType, _tasks) { state, sortType, tasks ->
        state.copy(
            tasks = tasks,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TaskState())

    fun onEvent(event: TaskEvent) {
        when(event) {
            is TaskEvent.DeleteTask -> {
                viewModelScope.launch {
                    dao.deleteTask(event.task)
                }
            }
            TaskEvent.HideDialog -> {
                _state.update {
                    it.copy(
                        isAddingTask = false
                    )
                }
            }
            TaskEvent.SaveTask -> {
                val title = state.value.title
                val description = state.value.description
                val isCompleted = state.value.isCompleted
                val date = System.currentTimeMillis()

                if(title.isBlank() || description.isBlank()) {
                    return
                }
                val task = Task(
                    title = title,
                    description = description,
                    isCompleted = isCompleted,
                    date = date
                )
                viewModelScope.launch {
                    dao.upsertTask(task)
                }
                _state.update {
                    it.copy(
                        isAddingTask = false,
                        title = "",
                        description = "",
                        isCompleted = false
                    )
                }
            }
            is TaskEvent.SetDescription -> {
                _state.update {
                    it.copy(
                        description = event.description
                    )
                }
            }
            is TaskEvent.SetIsCompleted -> {
                _state.update {
                    it.copy(
                        isCompleted = event.isCompleted
                    )
                }
            }
            is TaskEvent.SetTitle -> {
                _state.update {
                    it.copy(
                        title = event.title
                    )
                }
            }
            TaskEvent.ShowDialog -> {
                _state.update {
                    it.copy(
                        isAddingTask = true
                    )
                }
            }
            is TaskEvent.SortTasks -> {
                _sortType.value = event.sortType

            }
        }
    }
}