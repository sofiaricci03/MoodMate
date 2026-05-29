package com.corsolp.domain.models

data class Mood(
    val userEmail: String,
    val date: String,    // yyyy-MM-dd
    val moodType: String, // "Felice", "Triste", ecc.
    val note: String
)