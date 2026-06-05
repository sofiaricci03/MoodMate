package com.corsolp.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.corsolp.domain.di.ServiceLocator
import com.corsolp.ui.databinding.ActivityMainBinding
import com.corsolp.ui.main.MainActivity
import com.corsolp.ui.registration.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var loginViewModel: LoginViewModel
    // Il ViewBinding serve per accedere agli elementi grafici (ID) senza usare tanti findViewById

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inizializzazione del ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Recupero repository
        val repositoryProvider = ServiceLocator.requireRepositoryProvider()
        val userRepository = repositoryProvider.userRepository()
        val preferencesRepository = repositoryProvider.preferencesRepository()

        val factory = LoginViewModelFactory(userRepository, preferencesRepository)
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        loginViewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginViewModel.LoginResult.AlreadyLoggedIn -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                is LoginViewModel.LoginResult.Success -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Benvenuto!",
                        Toast.LENGTH_SHORT
                    ).show()

                    val emailInserita = binding.emailEditText.text.toString()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("USER_EMAIL", emailInserita)
                    startActivity(intent)
                    finish()
                }

                is LoginViewModel.LoginResult.InvalidCredentials -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Email o password errati",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is LoginViewModel.LoginResult.EmptyFields -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Inserisci email e password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Controlla se l'utente ha già loggato in precedenza
        loginViewModel.checkSavedUser()

        // Gestione dei margini per le barre di sistema (status bar e navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //gestione del pulsante di login
        binding.loginButton.setOnClickListener {
            // Recupero delle stringhe inserite dall'utente
            val emailInserita = binding.emailEditText.text.toString()
            val passwordInserita = binding.passwordEditText.text.toString()

            // Controllo che i campi non siano vuoti
            loginViewModel.login(emailInserita, passwordInserita)
        }

        //Gestione del pulsante di registrazione
        binding.registerLink.setOnClickListener {
            // Apre semplicemente la pagina di registrazione
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}