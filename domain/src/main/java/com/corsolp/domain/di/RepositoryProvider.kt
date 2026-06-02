package com.corsolp.domain.di

import com.corsolp.domain.repository.MoodRepository
import com.corsolp.domain.repository.UserRepository
import com.corsolp.domain.repository.PreferencesRepository
import com.corsolp.domain.repository.NotificationRepository
import com.corsolp.domain.repository.WeatherRepository


interface RepositoryProvider {
    fun userRepository(): UserRepository
    fun preferencesRepository(): PreferencesRepository
    fun notificationRepository(): NotificationRepository
    fun moodRepository(): MoodRepository
    fun weatherRepository(): WeatherRepository
}