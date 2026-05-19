package com.corsolp.domain.di

import com.corsolp.domain.repository.UserRepository
import com.corsolp.domain.repository.PreferencesRepository


interface RepositoryProvider {
    fun userRepository(): UserRepository
    fun preferencesRepository(): PreferencesRepository
}