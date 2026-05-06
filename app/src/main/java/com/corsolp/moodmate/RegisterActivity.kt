package com.corsolp.moodmate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

// Classe RegisterActivity estende la classe AppCompatActivity
class RegisterActivity : AppCompatActivity() {

    // onCreate è il primo metodo che viene eseguito quando la pagina viene aperta
    override fun onCreate(savedInstanceState: Bundle?) {
        // Chiama il metodo onCreate della classe padre
        // per eseguire le operazioni necessarie
        super.onCreate(savedInstanceState)

        // Collega il file Kotlin al file XML activity_register.xml
        setContentView(R.layout.activity_register)

        // Troviamo il bottone registrati nel layout
        // Era necessario importare android.widget.Button
        val registerButton = findViewById<Button>(R.id.registerButton)

        // Impostiamo l'azione al click
        registerButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)

            // finish() chiude questa pagina così non si torna indietro al form
            finish()
        }
    }
}