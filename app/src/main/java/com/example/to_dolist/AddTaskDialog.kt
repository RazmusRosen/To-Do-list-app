package com.example.to_dolist

import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddTaskDialog(
    state: TaskState,
    onEvent: (TaskEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { onEvent(TaskEvent.HideDialog) },
        title = {
            Text(text = "Add Task")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = state.title,
                    onValueChange = { onEvent(TaskEvent.SetTitle(it)) },
                    placeholder = { Text(text = "Title") }
                )
                TextField(
                    value = state.description,
                    onValueChange = { onEvent(TaskEvent.SetDescription(it)) },
                    placeholder = { Text(text = "Description") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onEvent(TaskEvent.SaveTask) }) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = { onEvent(TaskEvent.HideDialog) }) {
                Text(text = "Cancel")
            }
        }
    )
}
