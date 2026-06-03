package com.corsolp.data.di

import android.content.Context
import com.corsolp.data.local.db.AppDatabase
import com.corsolp.data.remote.api.RetrofitClient
import com.corsolp.data.repositories.MoodRepositoryImpl
import com.corsolp.data.repository.UserRepositoryImpl
import com.corsolp.data.repository.WeatherRepositoryImpl
import com.corsolp.domain.di.RepositoryProvider
import com.corsolp.data.repository.PreferencesRepositoryImpl
import com.corsolp.domain.repository.PreferencesRepository
import com.corsolp.domain.repository.NotificationRepository
import com.corsolp.data.repository.NotificationRepositoryImpl
import com.corsolp.domain.repository.MoodRepository
import com.corsolp.domain.repository.WeatherRepository
import com.corsolp.domain.repository.QuoteRepository
import com.corsolp.data.repository.QuoteRepositoryImpl

class RepositoryProviderImpl(private val context: Context) : RepositoryProvider {

    private val weatherRepository: WeatherRepository by lazy {
        WeatherRepositoryImpl(RetrofitClient.weatherApi)
    }

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
        return MoodRepositoryImpl(
            moodDao = AppDatabase.getDatabase(context).moodDao()
        )
    }
    override fun weatherRepository(): WeatherRepository {
        return weatherRepository
    }
    private val quoteRepository by lazy { QuoteRepositoryImpl() }

    override fun quoteRepository(): QuoteRepository {
        return quoteRepository
    }
}
