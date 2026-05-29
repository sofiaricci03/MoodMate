package com.corsolp.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.corsolp.domain.di.ServiceLocator
import com.corsolp.domain.models.Mood
import com.corsolp.ui.R
import com.corsolp.ui.profile.ProfileActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MoodInputActivity : AppCompatActivity() {

    private var selectedMood: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_input)

        // Recupero repository tramite ServiceLocator (rispetta la Clean Architecture)
        val repositoryProvider = ServiceLocator.requireRepositoryProvider()
        val moodRepository = repositoryProvider.moodRepository()
        val preferencesRepository = repositoryProvider.preferencesRepository()

        // Prendiamo l'email dalle preferenze o dall'intent
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: preferencesRepository.getSavedUserEmail() ?: ""

        // 1. Imposta Data Odierna
        val dateText = findViewById<TextView>(R.id.currentDateText)
        val sdfVisual = SimpleDateFormat("d MMMM", Locale.ITALIAN)
        val sdfDb = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Date()
        dateText.text = sdfVisual.format(today)
        val dateForDb = sdfDb.format(today)

        // 2. Gestione Click Faccine
        setupMoodButton(R.id.cardFelice, "Felice")
        setupMoodButton(R.id.cardSereno, "Sereno")
        setupMoodButton(R.id.cardNeutrale, "Neutrale")
        setupMoodButton(R.id.cardTriste, "Triste")
        setupMoodButton(R.id.cardArrabbiato, "Arrabbiato")
        setupMoodButton(R.id.cardStressato, "Stressato")

        // 3. Bottone Salva (Usa il repository, non il DB direttamente)
        findViewById<Button>(R.id.saveMoodButton).setOnClickListener {
            val note = findViewById<EditText>(R.id.editMoodNote).text.toString()

            if (selectedMood == null) {
                Toast.makeText(this, "Seleziona un umore!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val newMood = Mood(
                    userEmail = userEmail,
                    date = dateForDb,
                    moodType = selectedMood!!,
                    note = note
                )

                moodRepository.insertMood(newMood)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MoodInputActivity, "Umore salvato!", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
            }
        }

        // 4. Navigazione Sidebar (Footer)
        findViewById<LinearLayout>(R.id.nav_profilo).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }
    }

    private fun setupMoodButton(cardId: Int, moodName: String) {
        findViewById<CardView>(cardId).setOnClickListener {
            selectedMood = moodName
            Toast.makeText(this, "Hai selezionato: $moodName", Toast.LENGTH_SHORT).show()
        }
    }
}