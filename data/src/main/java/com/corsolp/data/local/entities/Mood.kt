package com.corsolp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_table")
data class Mood(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val date: String,    // Formato "yyyy-MM-dd"
    val moodType: String, // "Felice", "Triste", etc.
    val note: String
)