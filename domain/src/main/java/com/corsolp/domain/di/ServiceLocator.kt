package com.corsolp.domain.di

object ServiceLocator {
    @Volatile
    private var repositoryProvider: RepositoryProvider? = null

    fun setRepositoryProvider(provider: RepositoryProvider) {
        repositoryProvider = provider
    }

    fun requireRepositoryProvider(): RepositoryProvider {
        return repositoryProvider
            ?: error("RepositoryProvider not set. Call ServiceLocator.setRepositoryProvider() from app module.")
    }
}

