package com.corsolp.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.corsolp.domain.repository.PreferencesRepository

class PreferencesRepositoryImpl(context: Context) : PreferencesRepository {

    // Crea un file sul telefono chiamato "MoodMatePrefs"
    private val prefs: SharedPreferences = context.getSharedPreferences("MoodMatePrefs", Context.MODE_PRIVATE)

    override fun saveUserEmail(email: String) {
        prefs.edit().putString("USER_EMAIL", email).apply()
    }

    override fun getSavedUserEmail(): String? {
        // Recupera l'email (se non c'è, restituisce null)
        return prefs.getString("USER_EMAIL", null)
    }

    override fun clearUser() {
        prefs.edit().remove("USER_EMAIL").apply()
    }
}