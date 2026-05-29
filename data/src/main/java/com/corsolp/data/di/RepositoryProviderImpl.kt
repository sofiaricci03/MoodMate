package com.corsolp.data.di

import android.content.Context
import com.corsolp.data.local.db.AppDatabase
import com.corsolp.data.repositories.MoodRepositoryImpl
import com.corsolp.data.repository.UserRepositoryImpl
import com.corsolp.domain.di.RepositoryProvider
import com.corsolp.data.repository.PreferencesRepositoryImpl
import com.corsolp.domain.repository.PreferencesRepository
import com.corsolp.domain.repository.NotificationRepository
import com.corsolp.data.repository.NotificationRepositoryImpl
import com.corsolp.domain.repository.MoodRepository

class RepositoryProviderImpl(private val context: Context) : RepositoryProvider {
    override fun userRepository() = UserRepositoryImpl(
        userDao = AppDatabase.getDatabase(context).userDao()
    )

    override fun preferencesRepository(): PreferencesRepository {
        return PreferencesRepositoryImpl(context)
    }

    override fun notificationRepository(): NotificationRepository {
        return NotificationRepositoryImpl(context)
    }

    override fun moodRepository(): MoodRepository {
        // MODIFICA QUI: Estrai il moodDao dal database invece di passare il context
        return MoodRepositoryImpl(
            moodDao = AppDatabase.getDatabase(context).moodDao()
        )
    }
}