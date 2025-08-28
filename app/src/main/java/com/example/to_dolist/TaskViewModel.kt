package com.example.to_dolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskViewModel(
    private val dao: TaskDao
) : ViewModel() {

    // --- UI state (dialog open, title, description, etc.) ---
    private val _uiState = MutableStateFlow(TaskState())
    val uiState: StateFlow<TaskState> = _uiState.asStateFlow()

    // --- Sorting ---
    private val _sortType = MutableStateFlow(SortType.DATE)
    val sortType: StateFlow<SortType> = _sortType.asStateFlow()

    // --- Tasks from Room ---
    private val _tasks = _sortType
        .flatMapLatest { sortType ->
            when (sortType) {
                SortType.TITLE -> dao.getTasksOrderedByTitle()
                SortType.DATE -> dao.getTasksOrderedByDate()
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // --- Combined state for Compose ---
    val state: StateFlow<TaskState> = combine(_uiState, _tasks, _sortType) { ui, tasks, sort ->
        ui.copy(
            tasks = tasks,
            sortType = sort
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value)

    // --- Handle events from UI ---
    fun onEvent(event: TaskEvent) {
        when (event) {
            is TaskEvent.ShowDialog -> {
                _uiState.update { it.copy(isAddingTask = true) }
            }
            is TaskEvent.HideDialog -> {
                _uiState.update { it.copy(isAddingTask = false) }
            }
            is TaskEvent.SetTitle -> {
                _uiState.update { it.copy(title = event.title) }
            }
            is TaskEvent.SetDescription -> {
                _uiState.update { it.copy(description = event.description) }
            }
            is TaskEvent.SetIsCompleted -> {
                _uiState.update { it.copy(isCompleted = event.isCompleted) }
            }
            is TaskEvent.SortTasks -> {
                _sortType.value = event.sortType
            }
            is TaskEvent.SaveTask -> {
                val title = _uiState.value.title
                val description = _uiState.value.description
                val isCompleted = _uiState.value.isCompleted
                val date = System.currentTimeMillis()

                if (title.isBlank() || description.isBlank()) return

                val task = Task(
                    title = title,
                    description = description,
                    isCompleted = isCompleted,
                    date = date
                )

                viewModelScope.launch {
                    dao.upsertTask(task)
                }

                // reset dialog and input fields
                _uiState.update {
                    it.copy(
                        isAddingTask = false,
                        title = "",
                        description = "",
                        isCompleted = false
                    )
                }
            }
            is TaskEvent.DeleteTask -> {
                viewModelScope.launch {
                    dao.deleteTask(event.task)
                }
            }
        }
    }
}
