package com.corsolp.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corsolp.domain.models.User
import com.corsolp.domain.repository.PreferencesRepository
import com.corsolp.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileViewModel(
    private val userRepository: UserRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _saveResult = MutableLiveData<SaveResult>()
    val saveResult: LiveData<SaveResult> = _saveResult

    private val _validationError = MutableLiveData<String?>()
    val validationError: LiveData<String?> = _validationError

    fun loadUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val email = preferencesRepository.getSavedUserEmail() ?: ""
            val user = if (email.isNotEmpty()) userRepository.getUserByEmail(email) else null
            withContext(Dispatchers.Main) {
                _currentUser.value = user
            }
        }
    }

    fun saveProfile(
        email: String,
        name: String,
        surname: String,
        ageText: String,
        job: String,
        workHoursText: String,
        sleepHoursText: String,
        bio: String,
        selectedImageUri: String?
    ) {
        val age = ageText.toIntOrNull() ?: 0
        val workHours = workHoursText.toFloatOrNull() ?: 0f
        val sleepHours = sleepHoursText.toFloatOrNull() ?: 0f

        if (age <= 0 || age > 200) {
            _validationError.value = "Inserisci un'età valida compresa tra 1 e 200 anni"
            return
        }

        if (workHours + sleepHours > 24f) {
            _validationError.value = "La somma di lavoro e sonno non può superare 24 ore"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentUser = userRepository.getUserByEmail(email)
                val updatedUser = User(
                    email = email,
                    password = currentUser?.password ?: "",
                    name = name,
                    surname = surname,
                    age = age,
                    job = job,
                    workHours = workHours,
                    sleepHours = sleepHours,
                    bio = bio,
                    profileImageUri = selectedImageUri ?: currentUser?.profileImageUri
                )
                userRepository.insertUser(updatedUser)
                withContext(Dispatchers.Main) {
                    _saveResult.value = SaveResult.Success
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _saveResult.value = SaveResult.Error("Errore durante il salvataggio")
                }
            }
        }
    }

    sealed class SaveResult {
        object Success : SaveResult()
        data class Error(val message: String) : SaveResult()
    }
}