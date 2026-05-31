package com.corsolp.ui.statistics

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.corsolp.ui.R
import com.corsolp.ui.home.HomeActivity

class StatisticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        val bottomNav = findViewById<LinearLayout>(R.id.bottomNavigation)
        val homeTab = bottomNav.getChildAt(0)

        homeTab.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}