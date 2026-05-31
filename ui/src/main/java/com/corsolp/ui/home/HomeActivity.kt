package com.corsolp.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.corsolp.domain.di.ServiceLocator
import com.corsolp.ui.R
import com.corsolp.ui.moodInput.MoodInputFragment
import com.corsolp.ui.profile.ProfileFragment
import com.corsolp.ui.statistics.StatisticsFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {
    private var userEmail: String? = null
    private lateinit var bottomNav: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val repositoryProvider = ServiceLocator.requireRepositoryProvider()
        userEmail = intent.getStringExtra("USER_EMAIL")
            ?: repositoryProvider.preferencesRepository().getSavedUserEmail()

        bottomNav = findViewById(R.id.bottomNavigation)

        findViewById<FloatingActionButton>(R.id.fabAddMood).setOnClickListener {
            showFragment(MoodInputFragment.newInstance(userEmail), addToBackStack = true)
            clearSelectedTab()
        }

        bottomNav.getChildAt(0).setOnClickListener {
            showStatistics()
        }

        bottomNav.getChildAt(3).setOnClickListener {
            showStatistics()
        }

        bottomNav.getChildAt(4).setOnClickListener {
            showProfile()
        }

        if (savedInstanceState == null) {
            showStatistics()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        repositoryProvider.notificationRepository().scheduleDailyMoodReminder()
    }

    private fun showStatistics() {
        showFragment(StatisticsFragment())
        selectTab(3)
    }

    private fun showProfile() {
        showFragment(ProfileFragment.newInstance(userEmail))
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
            if (child !is FrameLayout) {
                child.background = null
            }
        }
    }
}
