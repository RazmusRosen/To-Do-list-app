package com.example.to_dolist

import androidx.room.Dao
import androidx.room.Upsert

@Dao
interface TaskDao {

    @Upsert
    fun upsertTask(task: Task)
}