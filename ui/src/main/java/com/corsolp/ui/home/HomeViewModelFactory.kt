package com.corsolp.ui.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.corsolp.domain.repository.MoodRepository
import com.corsolp.domain.repository.QuoteRepository
import com.corsolp.domain.repository.PreferencesRepository
import com.corsolp.domain.repository.WeatherRepository


class HomeViewModelFactory(
    private val weatherRepo: WeatherRepository,
    private val quoteRepo: QuoteRepository,
    private val moodRepo: MoodRepository,
    private val prefsRepo: PreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(weatherRepo, quoteRepo, moodRepo, prefsRepo) as T
    }
}