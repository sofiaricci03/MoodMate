package com.corsolp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corsolp.domain.models.Mood
import com.corsolp.domain.models.Quote
import com.corsolp.domain.models.Weather
import com.corsolp.domain.repository.MoodRepository
import com.corsolp.domain.repository.QuoteRepository
import com.corsolp.domain.repository.PreferencesRepository
import com.corsolp.domain.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class HomeViewModel(
    private val weatherRepository: WeatherRepository,
    private val quoteRepository: QuoteRepository,
    private val moodRepository: MoodRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<HomeUiState>()
    val uiState: LiveData<HomeUiState> = _uiState

    fun loadData(lat: Double, lon: Double) {
        viewModelScope.launch {
            val userEmail = preferencesRepository.getSavedUserEmail() ?: ""
            val todayDbFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val todayMood = withContext(Dispatchers.IO) {
                moodRepository.getMoodByDate(userEmail, todayDbFormat)
            }

            val weather = withContext(Dispatchers.IO) {
                weatherRepository.getCurrentWeather(lat, lon)
            }

            val quote = withContext(Dispatchers.IO) {
                quoteRepository.getRandomQuote()
            }

            _uiState.value = HomeUiState(
                todayMood = todayMood,
                weather = weather,
                quote = quote
            )
        }
    }
}
data class HomeUiState(
    val weather: Weather?,
    val quote: Quote?,
    val todayMood: Mood?
)
