package com.corsolp.domain.di

import com.corsolp.domain.repository.UserRepository

interface RepositoryProvider {
    fun userRepository(): UserRepository
}