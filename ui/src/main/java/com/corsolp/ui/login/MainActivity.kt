package com.corsolp.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.corsolp.domain.di.ServiceLocator
import com.corsolp.ui.databinding.ActivityMainBinding
import com.corsolp.ui.home.HomeActivity // Assicurati che questo import sia presente
import com.corsolp.ui.registration.RegisterActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    // Il ViewBinding serve per accedere agli elementi grafici (ID) senza usare tanti findViewById

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inizializzazione del ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        // Gestione dei margini per le barre di sistema (status bar e navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
// Recupero del Repository tramite il ServiceLocator
        // Il repository fa da intermediario tra il database (Room) e questa Activity
        val userRepository = ServiceLocator.requireRepositoryProvider().userRepository()

        //gestione del pulsante di login
        binding.loginButton.setOnClickListener {
            // Recupero delle stringhe inserite dall'utente
            val emailInserita = binding.emailEditText.text.toString()
            val passwordInserita = binding.passwordEditText.text.toString()

            // Controllo che i campi non siano vuoti
            if (emailInserita.isNotEmpty() && passwordInserita.isNotEmpty()) {

                // Usiamo Dispatchers.IO perché la ricerca nel database è un'operazione di lettura su disco
                lifecycleScope.launch(Dispatchers.IO) {
                    // Chiama il metodo login del repository (che a sua volta interroga il DAO di Room)
                    val loggedUser = userRepository.login(emailInserita, passwordInserita)

                    // Torniamo al thread principale per mostrare il risultato all'utente
                    withContext(Dispatchers.Main) {
                        if (loggedUser != null) {
                            // Se l'utente esiste (non è null), mostriamo un messaggio di benvenuto
                            Toast.makeText(
                                this@MainActivity,
                                "Benvenuto ${loggedUser.email}!",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Navigazione verso la Home
                            val intent = Intent(this@MainActivity, HomeActivity::class.java)
                            intent.putExtra("USER_EMAIL", emailInserita)  // Passiamo l'email alla Home
                            startActivity(intent)
                            finish() // Chiude la MainActivity così l'utente non torna al login col tasto indietro

                        } else {
                            Toast.makeText(
                                // Se il database restituisce null, le credenziali sono sbagliate
                                this@MainActivity,
                                "Email o password errati",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                // Avviso se l'utente tenta di cliccare senza aver scritto nulla
                Toast.makeText(this, "Inserisci email e password", Toast.LENGTH_SHORT).show()
            }
        }
        //Gestione del pulsante di registrazione
        binding.registerLink.setOnClickListener {
            // Apre semplicemente la pagina di registrazione
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}