package com.corsolp.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.corsolp.ui.R
import com.corsolp.ui.profile.ProfileActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Recuperiamo l'email passata dal Login o dalla Registrazione
        val userEmail = intent.getStringExtra("USER_EMAIL")

        // Troviamo il contenitore del Profilo nella barra di navigazione
        val bottomNav = findViewById<LinearLayout>(R.id.bottomNavigation)
        val profileTab = bottomNav.getChildAt(4) // Il 5° elemento (indice 4) è il Profilo

        profileTab.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            // Passiamo l'email alla pagina Profilo così può caricare i dati
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }
    }
}
