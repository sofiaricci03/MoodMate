package com.corsolp.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.corsolp.domain.repository.MoodRepository
import com.corsolp.domain.repository.PreferencesRepository

class StatisticsViewModelFactory(
    private val moodRepo: MoodRepository,
    private val preferencesRepo: PreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StatisticsViewModel(moodRepo, preferencesRepo) as T
    }
}