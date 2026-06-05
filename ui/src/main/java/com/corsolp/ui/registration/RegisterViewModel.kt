package com.corsolp.ui.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corsolp.domain.models.User
import com.corsolp.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _registrationEvents = MutableLiveData<RegistrationResult>()
    val registrationEvents: LiveData<RegistrationResult> = _registrationEvents

    fun registerUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                userRepository.insertUser(user)
                withContext(Dispatchers.Main) {
                    _registrationEvents.value = RegistrationResult.Success
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _registrationEvents.value = RegistrationResult.Error("Errore durante il salvataggio")
                }
            }
        }
    }

    sealed class RegistrationResult {
        object Success : RegistrationResult()
        data class Error(val message: String) : RegistrationResult()
    }
}