package com.corsolp.domain.repository

interface PreferencesRepository {
    fun saveUserEmail(email: String)
    fun getSavedUserEmail(): String?
    fun clearUser() //Logout
}