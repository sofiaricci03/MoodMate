package com.corsolp.data.repository

import com.corsolp.data.local.dao.UserDao
import com.corsolp.domain.models.User
import com.corsolp.domain.repository.UserRepository

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun insertUser(user: User) {
        userDao.insertUser(user.toEntity())
    }

    override suspend fun login(email: String, password: String): User? {
        return userDao.login(email, password)?.toDomain()
    }

    override suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)?.toDomain()
    }
}

// AGGIORNATO: Ora include profileImageUri
private fun com.corsolp.data.local.entities.User.toDomain(): User {
    return User(
        email = email,
        password = password,
        name = name,
        surname = surname,
        age = age,
        job = job,
        workHours = workHours,
        sleepHours = sleepHours,
        bio = bio,
        profileImageUri = profileImageUri
    )
}

// AGGIORNATO: Ora include profileImageUri
private fun User.toEntity(): com.corsolp.data.local.entities.User {
    return com.corsolp.data.local.entities.User(
        email = email,
        password = password,
        name = name,
        surname = surname,
        age = age,
        job = job,
        workHours = workHours,
        sleepHours = sleepHours,
        bio = bio,
        profileImageUri = profileImageUri
    )
}
