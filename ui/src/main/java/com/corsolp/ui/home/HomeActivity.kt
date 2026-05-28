package com.corsolp.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.corsolp.ui.R
import com.corsolp.ui.profile.ProfileActivity
import com.corsolp.domain.di.ServiceLocator
import com.google.android.material.floatingactionbutton.FloatingActionButton // IMPORTANTE

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Recuperiamo l'email passata dal Login o dalla Registrazione
        val userEmail = intent.getStringExtra("USER_EMAIL")

        // 1. GESTIONE PULSANTE CENTRALE (+) PER INSERIRE UMORE
        val fabAddMood = findViewById<FloatingActionButton>(R.id.fabAddMood)
        fabAddMood.setOnClickListener {
            val intent = Intent(this, MoodInputActivity::class.java)
            // Passiamo l'email anche qui per sapere a quale utente associare l'umore
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }

        // 2. GESTIONE TAB PROFILO
        val bottomNav = findViewById<LinearLayout>(R.id.bottomNavigation)
        val profileTab = bottomNav.getChildAt(4) // Il 5° elemento (indice 4) è il Profilo

        profileTab.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }

        // Attivazione workManager notifica
        val notificationRepo = ServiceLocator.requireRepositoryProvider().notificationRepository()
        notificationRepo.scheduleDailyMoodReminder()
    }
}