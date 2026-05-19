package com.corsolp.ui.registration

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.corsolp.domain.di.ServiceLocator
import com.corsolp.domain.models.User
import com.corsolp.ui.R
import com.corsolp.ui.home.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Collega la logica al file grafico activity_register.xml
        setContentView(R.layout.activity_register)

        // La UI deve parlare solo con il Domain per rispettare la Clean Architecture
        val userRepository = ServiceLocator.requireRepositoryProvider().userRepository()

        //Recupero del bottone di registrazione
        val registerButton = findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener {
            // Recupero dei dati dagli EditText
            // Legge il testo dagli EditText e lo converte in Stringa
            val email = findViewById<EditText>(R.id.regEmail).text.toString()
            val password = findViewById<EditText>(R.id.regPassword).text.toString()
            val name = findViewById<EditText>(R.id.regNome).text.toString()
            val surname = findViewById<EditText>(R.id.regCognome).text.toString()

            // Per i numeri usiamo toIntOrNull o toFloatOrNull per evitare crash
            // se l'utente lascia il campo vuoto (restituendo un valore di default 0)
            val age = findViewById<EditText>(R.id.regEta).text.toString().toIntOrNull() ?: 0
            val job = findViewById<EditText>(R.id.regProfessione).text.toString()
            val workHours = findViewById<EditText>(R.id.regOreLavoro).text.toString().toFloatOrNull() ?: 0f
            val sleepHours = findViewById<EditText>(R.id.regOreSonno).text.toString().toFloatOrNull() ?: 0f
            val bio = findViewById<EditText>(R.id.regStileVita).text.toString()

            //Controllo che i campi email e password non siano vuoti
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Usiamo lifecycleScope per far sì che se l'utente chiude la pagina, l'operazione si fermi
                // Usiamo Dispatchers.IO perché le operazioni su disco (DB) non devono rallentare la grafica
                lifecycleScope.launch(Dispatchers.IO) {
                    // Creazione entità User con i dati inseriti
                    val newUser = User(
                        email = email,
                        password = password,
                        name = name,
                        surname = surname,
                        age = age,
                        job = job,
                        workHours = workHours,
                        sleepHours = sleepHours,
                        bio = bio
                    )

                    // Inserimento nella repository
                    userRepository.insertUser(newUser)
                    // Per mostrare messaggi (Toast) o cambiare pagina dobbiamo tornare sul thread Main
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, "Registrazione completata!", Toast.LENGTH_SHORT).show()

                        //Apertura della HomeActivity
                        val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
                        intent.putExtra("USER_EMAIL", email)
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                // Avviso se i campi obbligatori mancano
                Toast.makeText(this, "Email e Password sono obbligatorie", Toast.LENGTH_SHORT).show()
            }
        }
    }
}