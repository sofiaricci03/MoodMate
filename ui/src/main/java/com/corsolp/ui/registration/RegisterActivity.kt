package com.corsolp.ui.registration

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.corsolp.domain.di.ServiceLocator
import com.corsolp.domain.models.User
import com.corsolp.ui.R
import com.corsolp.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val userRepository = ServiceLocator.requireRepositoryProvider().userRepository()
        val factory = RegisterViewModelFactory(userRepository)
        registerViewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]

        registerViewModel.registrationEvents.observe(this) { result ->
            when (result) {
                is RegisterViewModel.RegistrationResult.Success -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registrazione completata! Ora effettua il login.",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                is RegisterViewModel.RegistrationResult.Error -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        result.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        val registerButton = findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener {
            // Riferimenti alle EditText per poter mostrare gli errori visivi
            val editEmail = findViewById<EditText>(R.id.regEmail)
            val editPassword = findViewById<EditText>(R.id.regPassword)
            val editEta = findViewById<EditText>(R.id.regEta)
            val editOreLavoro = findViewById<EditText>(R.id.regOreLavoro)
            val editOreSonno = findViewById<EditText>(R.id.regOreSonno)

            // Estrazione dei valori
            val email = editEmail.text.toString()
            val password = editPassword.text.toString()
            val name = findViewById<EditText>(R.id.regNome).text.toString()
            val surname = findViewById<EditText>(R.id.regCognome).text.toString()
            val age = editEta.text.toString().toIntOrNull() ?: 0
            val job = findViewById<EditText>(R.id.regProfessione).text.toString()
            val workHours = editOreLavoro.text.toString().toFloatOrNull() ?: 0f
            val sleepHours = editOreSonno.text.toString().toFloatOrNull() ?: 0f
            val bio = findViewById<EditText>(R.id.regStileVita).text.toString()

            var isValid = true

            // 1. Controllo Email e Password obbligatorie
            if (email.isEmpty()) {
                editEmail.error = "L'email è obbligatoria"
                isValid = false
            }
            if (password.isEmpty()) {
                editPassword.error = "La password è obbligatoria"
                isValid = false
            }

            // 2. Controllo Età (limite massimo 200 e validazione input vuoto/zero)
            if (age <= 0 || age > 200) {
                editEta.error = "Inserisci un'età valida compresa tra 1 e 200 anni"
                isValid = false
            }

            // 3. Controllo Somma Ore (limite massimo 24 ore)
            if (workHours + sleepHours > 24f) {
                editOreLavoro.error = "La somma di lavoro e sonno non può superare 24 ore"
                editOreSonno.error = "La somma di lavoro e sonno non può superare 24 ore"
                isValid = false
            }

            // Se uno o più controlli falliscono, interrompiamo la registrazione
            if (!isValid) {
                Toast.makeText(
                    this,
                    "Controlla i dati inseriti. Alcuni campi contengono errori.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // Se tutto è valido, procediamo al salvataggio nel database
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

            registerViewModel.registerUser(newUser)
        }
    }
}