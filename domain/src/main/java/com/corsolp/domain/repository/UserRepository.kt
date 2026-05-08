package com.corsolp.domain.repository

import com.corsolp.domain.models.User

interface UserRepository {
    suspend fun insertUser(user: User)
    suspend fun login(email: String, password: String): User?
    suspend fun getUserByEmail(email: String): User?
}
