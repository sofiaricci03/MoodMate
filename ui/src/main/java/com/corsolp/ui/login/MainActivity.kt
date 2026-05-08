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
import com.corsolp.ui.registration.RegisterActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userRepository = ServiceLocator.requireRepositoryProvider().userRepository()

        binding.loginButton.setOnClickListener {
            val emailInserita = binding.emailEditText.text.toString()
            val passwordInserita = binding.passwordEditText.text.toString()

            if (emailInserita.isNotEmpty() && passwordInserita.isNotEmpty()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val loggedUser = userRepository.login(emailInserita, passwordInserita)

                    withContext(Dispatchers.Main) {
                        if (loggedUser != null) {
                            Toast.makeText(
                                this@MainActivity,
                                "Benvenuto ${loggedUser.email}!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Email o password errati",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Inserisci email e password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registerLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
