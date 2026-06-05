package com.corsolp.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.corsolp.domain.di.RepositoryProvider
import com.corsolp.domain.di.ServiceLocator
import com.corsolp.ui.R
import com.corsolp.ui.calendar.CalendarFragment
import com.corsolp.ui.home.HomeFragment
import com.corsolp.ui.moodInput.MoodInputFragment
import com.corsolp.ui.profile.ProfileFragment
import com.corsolp.ui.statistics.StatisticsFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private var userEmail: String? = null
    private lateinit var bottomNav: LinearLayout
    private lateinit var greetingText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val repositoryProvider = ServiceLocator.requireRepositoryProvider()
        userEmail = intent.getStringExtra("USER_EMAIL")
            ?: repositoryProvider.preferencesRepository().getSavedUserEmail()

        bottomNav = findViewById(R.id.bottomNavigation)
        greetingText = findViewById(R.id.headerGreetingText)

        // Carica il nome e mostra la scritta
        loadUserName(repositoryProvider)

        findViewById<FloatingActionButton>(R.id.fabAddMood).setOnClickListener {
            greetingText.text = "Come ti senti oggi?" // Titolo per l'inserimento mood
            greetingText.visibility = View.VISIBLE
            showFragment(MoodInputFragment.Companion.newInstance(userEmail), addToBackStack = true)
            clearSelectedTab()
        }

        bottomNav.getChildAt(0).setOnClickListener { showHome() }
        bottomNav.getChildAt(1).setOnClickListener { showCalendar() }
        bottomNav.getChildAt(3).setOnClickListener { showStatistics() }
        bottomNav.getChildAt(4).setOnClickListener { showProfile() }

        if (savedInstanceState == null) {
            showHome() // Avvia con la home visibile
        }

        // Gestione permessi notifiche
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
        repositoryProvider.notificationRepository().scheduleDailyMoodReminder()
    }

    private fun loadUserName(repositoryProvider: RepositoryProvider) {
        lifecycleScope.launch(Dispatchers.IO) {
            val user = repositoryProvider.userRepository().getUserByEmail(userEmail ?: "")
            withContext(Dispatchers.Main) {
                if (user != null) {
                    greetingText.text = "Buongiorno, ${user.name}!"
                    // Se siamo nella Home, rendila visibile subito
                    greetingText.visibility = View.VISIBLE
                }
            }
        }
    }


    private fun showHome() {
        loadUserName(ServiceLocator.requireRepositoryProvider())
        greetingText.visibility = View.VISIBLE
        showFragment(HomeFragment())
        selectTab(0)
    }

    private fun showCalendar() {
        // Titolo specifico per il calendario
        greetingText.text = "Come è andato questo mese?"
        greetingText.visibility = View.VISIBLE
        showFragment(CalendarFragment.Companion.newInstance(userEmail))
        selectTab(1)
    }

    private fun showStatistics() {
        loadUserName(ServiceLocator.requireRepositoryProvider())
        greetingText.visibility = View.VISIBLE
        showFragment(StatisticsFragment())
        selectTab(3)
    }

    private fun showProfile() {
        greetingText.visibility = View.GONE
        showFragment(ProfileFragment.Companion.newInstance(userEmail))
        selectTab(4)
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = false) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.homeFragmentContainer, fragment)
            if (addToBackStack) {
                addToBackStack(fragment::class.java.simpleName)
            }
        }.commit()
    }

    private fun selectTab(index: Int) {
        clearSelectedTab()
        bottomNav.getChildAt(index).setBackgroundResource(R.drawable.nav_item_selected)
    }

    private fun clearSelectedTab() {
        for (i in 0 until bottomNav.childCount) {
            val child = bottomNav.getChildAt(i)
            if (child !is FrameLayout) { child.background = null }
        }
    }
}