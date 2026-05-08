package com.corsolp.moodmate

import android.app.Application
import com.corsolp.data.di.RepositoryProviderImpl
import com.corsolp.domain.di.ServiceLocator


class Moodmate: Application() {

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.setRepositoryProvider(RepositoryProviderImpl(this))
    }
}