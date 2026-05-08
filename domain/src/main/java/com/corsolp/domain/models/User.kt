package com.corsolp.domain.models

data class User(
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