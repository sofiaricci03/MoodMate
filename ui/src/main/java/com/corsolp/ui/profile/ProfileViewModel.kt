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

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    fun loadUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val userEmail = preferencesRepository.getSavedUserEmail() ?: ""
            if (userEmail.isNotEmpty()) {
                val loadedUser = userRepository.getUserByEmail(userEmail)
                withContext(Dispatchers.Main) {
                    _user.value = loadedUser
                }
            }
        }
    }

    fun logout() {
        preferencesRepository.clearUser()
    }
}