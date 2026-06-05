package com.corsolp.ui.moodInput

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corsolp.domain.models.Mood
import com.corsolp.domain.repository.MoodRepository
import com.corsolp.domain.repository.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MoodInputViewModel(
    private val moodRepository: MoodRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<MoodInputUiState>()
    val uiState: LiveData<MoodInputUiState> = _uiState

    private val sdfVisual = SimpleDateFormat("d MMMM", Locale.ITALIAN)
    private val sdfDb = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val todayDbFormat = sdfDb.format(Date())

    init {
        // Imposta subito la data visuale e controlla se esiste già un mood oggi
        _uiState.value = MoodInputUiState(
            currentDateVisual = sdfVisual.format(Date())
        )
        loadExistingMood()
    }

    private fun loadExistingMood() {
        viewModelScope.launch {
            val userEmail = getUserEmail()
            val existing = kotlinx.coroutines.withContext(Dispatchers.IO) {
                moodRepository.getMoodByDate(userEmail, todayDbFormat)
            }
            _uiState.value = _uiState.value?.copy(
                todayMood = existing,
                isSaved = existing != null
            )
        }
    }

    // Chiamato dal Fragment quando l'utente clicca su una card umore
    fun selectMood(moodType: String) {
        // Non permettere selezione se già salvato oggi
        if (_uiState.value?.isSaved == true) return
        _uiState.value = _uiState.value?.copy(
            selectedMood = moodType,
            errorMessage = null
        )
    }

    // Chiamato dal Fragment quando l'utente preme "Salva"
    fun saveMood(note: String) {
        val currentState = _uiState.value ?: return

        // Validazione: mood non selezionato
        if (currentState.selectedMood == null) {
            _uiState.value = currentState.copy(errorMessage = "Seleziona un umore!")
            return
        }

        viewModelScope.launch {
            val userEmail = getUserEmail()

            // Controlla nel DB
            val existing = kotlinx.coroutines.withContext(Dispatchers.IO) {
                moodRepository.getMoodByDate(userEmail, todayDbFormat)
            }

            if (existing != null) {
                _uiState.value = _uiState.value?.copy(
                    todayMood = existing,
                    isSaved = true
                )
                return@launch
            }

            val newMood = Mood(
                userEmail = userEmail,
                date = todayDbFormat,
                moodType = currentState.selectedMood,
                note = note
            )

            kotlinx.coroutines.withContext(Dispatchers.IO) {
                moodRepository.insertMood(newMood)
            }

            _uiState.value = _uiState.value?.copy(
                todayMood = newMood,
                isSaved = true,
                errorMessage = null
            )
        }
    }

    private fun getUserEmail(): String {
        return preferencesRepository.getSavedUserEmail() ?: ""
    }
}

data class MoodInputUiState(
    val todayMood: Mood? = null,
    val selectedMood: String? = null,
    val currentDateVisual: String = "",
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)