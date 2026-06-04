package com.corsolp.ui.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corsolp.domain.repository.MoodRepository
import com.corsolp.domain.repository.PreferencesRepository
import com.corsolp.domain.models.Mood
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class StatisticsViewModel(
    private val moodRepository: MoodRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    // Mappa che converte il testo del Mood nel "peso" dell'istogramma
    private val moodWeights = mapOf(
        "Felice" to 6f,
        "Sereno" to 5f,
        "Neutrale" to 4f,
        "Triste" to 3f,
        "Stressato" to 2f,
        "Arrabbiato" to 1f
    )

    private val _uiState = MutableLiveData<StatisticsUiState>()
    val uiState: LiveData<StatisticsUiState> = _uiState

    fun loadWeeklyStats() {
        // Non ricarica se i dati sono già presenti
        if (_uiState.value?.isLoaded == true) return

        viewModelScope.launch {
            val userEmail = preferencesRepository.getSavedUserEmail() ?: ""
            val (currentWeekDays, startDate, endDate) = getWeekRange()

            val weeklyMoods = withContext(Dispatchers.IO) {
                moodRepository.getMoodsByDateRange(userEmail, startDate, endDate)
            }

            val (averageScore, averageLabel) = calculateAverage(weeklyMoods)

            _uiState.value = StatisticsUiState(
                weeklyMoods = weeklyMoods,
                currentWeekDays = currentWeekDays,
                averageScore = averageScore,
                averageLabel = averageLabel,
                isLoaded = true
            )
        }
    }

    // Calcola la settimana (Da Lunedì a Domenica)
    private fun getWeekRange(): Triple<List<String>, String, String> {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val days = mutableListOf<String>()
        for (i in 0..6) {
            days.add(format.format(calendar.time))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return Triple(days, days.first(), days.last())
    }

    // Calcola la media e restituisce score e label già formattati
    private fun calculateAverage(moods: List<com.corsolp.domain.models.Mood>): Pair<String, String> {
        if (moods.isEmpty()) return Pair("-/6", "Nessun dato")

        var sommaPunteggi = 0f
        var giorniValidi = 0

        for (mood in moods) {
            val weight = moodWeights[mood.moodType]
            if (weight != null) {
                sommaPunteggi += weight
                giorniValidi++
            }
        }

        if (giorniValidi == 0) return Pair("-/6", "Nessun dato")

        val media = sommaPunteggi / giorniValidi
        val mediaFormattata = String.format(Locale.getDefault(), "%.1f", media)
        val label = when {
            media >= 5.0 -> "Ottimo"
            media >= 4.0 -> "Buono"
            media >= 3.0 -> "Discreto"
            media >= 2.0 -> "Basso"
            else -> "Critico"
        }
        return Pair("$mediaFormattata/6", label)
    }

    // Restituisce il peso di un tipo di mood
    fun getMoodWeight(moodType: String): Float = moodWeights[moodType] ?: 0f
}

data class StatisticsUiState(
    val weeklyMoods: List<Mood> = emptyList(),
    val currentWeekDays: List<String> = emptyList(),
    val averageScore: String = "-/6",
    val averageLabel: String = "Nessun dato",
    val isLoaded: Boolean = false
)