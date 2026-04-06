package com.corsolp.moodmate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

// Classe RegisterActivity estende la classe AppCompatActivity
class RegisterActivity : AppCompatActivity() {
    //onCreate è il primo metodo che viene eseguito quando la pagina viene aperta
    override fun onCreate(savedInstanceState: Bundle?) {
        // Chiama il metodo onCreate della classe padre ed eseguire le operazioni necessarie
        // per la creazione della pagina
        super.onCreate(savedInstanceState)
        // Questo collega il file Kotlin al file XML activity_register.xml
        setContentView(R.layout.activity_register)
    }
}