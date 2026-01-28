package com.example.moodmate.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey
    val email: String,
    val password: String,
    val name: String,
    val surname: String,
    val age: Int,
    val job: String,
    val workHours: Float,
    val sleepHours: Float,
    val bio: String
)