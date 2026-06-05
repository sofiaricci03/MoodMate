package com.corsolp.ui.moodInput

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.corsolp.domain.repository.MoodRepository
import com.corsolp.domain.repository.PreferencesRepository

class MoodInputViewModelFactory(
    private val moodRepository: MoodRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MoodInputViewModel(moodRepository, preferencesRepository) as T
    }
}