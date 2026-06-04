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
import kotlinx.coroutines.async
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

    fun loadData(lat: Double, lon: Double, cityName: String) {
        if (_uiState.value?.isLoaded == true) return

        viewModelScope.launch {
            val userEmail = preferencesRepository.getSavedUserEmail() ?: ""
            val todayDbFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // Chiamate parallele
            val moodDeferred    = async(Dispatchers.IO) { moodRepository.getMoodByDate(userEmail, todayDbFormat) }
            val weatherDeferred = async(Dispatchers.IO) { weatherRepository.getCurrentWeather(lat, lon) }
            val quoteDeferred   = async(Dispatchers.IO) { quoteRepository.getRandomQuote() }

            val todayMood = moodDeferred.await()
            val weather   = weatherDeferred.await()
            val quote     = quoteDeferred.await()

            _uiState.value = HomeUiState(
                todayMood = todayMood,
                weather = weather,
                quote = quote,
                weatherTemp = if (weather != null) "$cityName ${weather.temperature.toInt()}°C" else "$cityName --°C",
                weatherDesc = if (weather != null) translateWeatherCode(weather.weatherCode) else "Meteo non disponibile",
                quoteText = if (quote != null) "\"${quote.text}\"\n- ${quote.author}" else "\"Concediti il tempo di cui hai bisogno. Non c'è fretta.\"",
                moodStatusText = if (todayMood != null) "Oggi ti senti ${todayMood.moodType.lowercase()}" else "Come ti senti oggi?",
                healthyTip = getHealthyTip(weather?.weatherCode),
                isLoaded = true
            )
        }
    }

    private fun translateWeatherCode(code: Int): String {
        return when (code) {
            0 -> "Sereno"
            1, 2, 3 -> "Parzialmente nuvoloso"
            45, 48 -> "Nebbia"
            51, 53, 55 -> "Pioggia lieve"
            56, 57 -> "Pioggia gelata"
            61, 63, 65 -> "Pioggia"
            66, 67 -> "Pioggia gelata"
            71, 73, 75 -> "Neve"
            77 -> "Nevischio"
            80, 81, 82 -> "Rovesci"
            85, 86 -> "Rovesci di neve"
            95, 96, 99 -> "Temporale"
            else -> "Variabile"
        }
    }

    private fun getHealthyTip(weatherCode: Int?): String {
        val outdoorTips = listOf(
            "Il tempo è fantastico! Esci a fare una passeggiata di 20 minuti.",
            "Prendi un po' di sole per fare il pieno di vitamina D.",
            "Organizza un'uscita all'aperto o un caffè con un amico.",
            "Fai un po' di stretching o esercizio all'aria aperta."
        )
        val indoorTips = listOf(
            "Goditi il meteo fuori con una tisana calda e un buon libro.",
            "Ottima giornata per dedicarti a quell'hobby casalingo che rimandi da un po'.",
            "Fai 10 minuti di yoga o meditazione nel tuo salotto.",
            "Il brutto tempo è la scusa perfetta per un bel film sotto le coperte.",
            "Riordina il tuo spazio: un ambiente pulito aiuta a rilassare la mente."
        )
        val neutralTips = listOf(
            "Mettiti comodo e ascolta il tuo podcast o album preferito.",
            "Sperimenta in cucina e prova una nuova ricetta salutare.",
            "Prenditi 5 minuti per scrivere su un diario 3 cose per cui sei grato oggi."
        )
        if (weatherCode == null) return neutralTips.random()
        return when (weatherCode) {
            0, 1, 2, 3 -> outdoorTips.random()
            51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 71, 73, 75, 77, 80, 81, 82, 85, 86, 95, 96, 99 -> indoorTips.random()
            // Se meteo non disponibile o caso di nebbia
            else -> neutralTips.random()
        }
    }
}
data class HomeUiState(
    val todayMood: Mood? = null,
    val weather: Weather? = null,
    val quote: Quote? = null,
    val weatherTemp: String = "--°C",
    val weatherDesc: String = "Caricamento meteo...",
    val quoteText: String = "\"Concediti il tempo di cui hai bisogno. Non c'è fretta.\"",
    val moodStatusText: String = "Come ti senti oggi?",
    val healthyTip: String = "",
    val isLoaded: Boolean = false
)
