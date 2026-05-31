package com.corsolp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.corsolp.domain.di.ServiceLocator
import com.corsolp.ui.R
import com.corsolp.ui.home.HomeActivity
import com.corsolp.ui.login.MainActivity
import com.corsolp.ui.statistics.StatisticsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Otteniamo il repository tramite il ServiceLocator
        val repositoryProvider = ServiceLocator.requireRepositoryProvider()
        val userRepository = repositoryProvider.userRepository()
        val preferencesRepository = repositoryProvider.preferencesRepository()

        // Recupera l'email dalle SharedPreferences
        val userEmail = preferencesRepository.getSavedUserEmail() ?: ""

        //Caricamento dati dal database tramite Coroutine
        lifecycleScope.launch(Dispatchers.IO) {
            val user = userRepository.getUserByEmail(userEmail)

            withContext(Dispatchers.Main) {
                user?.let {
                    // Autocompila i campi dell'header
                    findViewById<TextView>(R.id.profileName).text = it.name
                    findViewById<TextView>(R.id.profileJobHeader).text = it.job

                    // Autocompila i campi nelle Card
                    findViewById<TextView>(R.id.profileAge).text = it.age.toString()
                    findViewById<TextView>(R.id.profileJob).text = it.job
                    findViewById<TextView>(R.id.profileWorkHours).text = it.workHours.toString()
                    findViewById<TextView>(R.id.profileSleepHours).text = it.sleepHours.toString()
                    findViewById<TextView>(R.id.profileBio).text = it.bio
                }
            }
        }

        // Gestione evidenziazione Sidebar (Profilo selezionato)
        val navProfilo = findViewById<LinearLayout>(R.id.nav_profilo)
        // Impostiamo lo sfondo (nav_item_selected.xml)
        navProfilo.setBackgroundResource(R.drawable.nav_item_selected)

        // Gestione Logout
        findViewById<TextView>(R.id.logoutButton).setOnClickListener {
            //cancella credenziali salvate
            preferencesRepository.clearUser()

            val intent = Intent(this, MainActivity::class.java)
            // Pulisce lo stack delle attività per impedire di tornare indietro col tasto back
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Barra di navigazione
        val bottomNav = findViewById<LinearLayout>(R.id.bottomNavigation)

        val homeTab = bottomNav.getChildAt(0)
        homeTab.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        val statisticsTab = bottomNav.getChildAt(3)
        statisticsTab.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }

    }
}