package com.example.to_dolist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Upsert
    suspend fun upsertTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task): Int

    @Query("SELECT * FROM task ORDER BY id ASC")
    fun getTasks(): Flow<List<Task>>

    @Query("SELECT * FROM task ORDER BY title ASC")
    fun getTasksOrderedByTitle(): Flow<List<Task>>

    @Query("SELECT * FROM task ORDER BY date ASC")
    fun getTasksOrderedByDate(): Flow<List<Task>>

}