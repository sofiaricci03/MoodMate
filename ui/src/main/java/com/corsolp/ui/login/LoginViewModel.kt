package com.corsolp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corsolp.domain.repository.PreferencesRepository
import com.corsolp.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val userRepository: UserRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    sealed class LoginResult {
        object AlreadyLoggedIn : LoginResult()
        object Success : LoginResult()
        object InvalidCredentials : LoginResult()
        object EmptyFields : LoginResult()
    }

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun checkSavedUser() {
        if (preferencesRepository.getSavedUserEmail() != null) {
            _loginResult.value = LoginResult.AlreadyLoggedIn
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginResult.value = LoginResult.EmptyFields
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val loggedUser = userRepository.login(email, password)
            withContext(Dispatchers.Main) {
                if (loggedUser != null) {
                    preferencesRepository.saveUserEmail(email)
                    _loginResult.value = LoginResult.Success
                } else {
                    _loginResult.value = LoginResult.InvalidCredentials
                }
            }
        }
    }
}