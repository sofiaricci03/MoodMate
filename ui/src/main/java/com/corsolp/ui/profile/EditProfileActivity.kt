package com.corsolp.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.corsolp.domain.di.ServiceLocator
import com.corsolp.domain.models.User
import com.corsolp.ui.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private lateinit var imgProfile: ImageView

    // Picker per la galleria
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            imgProfile.setImageURI(uri)
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_edit_profile)

        val repositoryProvider = ServiceLocator.requireRepositoryProvider()
        val userRepository = repositoryProvider.userRepository()
        val preferencesRepository = repositoryProvider.preferencesRepository()
        val email = preferencesRepository.getSavedUserEmail() ?: ""

        // Inizializzazione Viste
        imgProfile = findViewById(R.id.editProfileImage)
        val btnUpload = findViewById<View>(R.id.btnUploadPhoto)
        val btnSalva = findViewById<Button>(R.id.btnSave)
        val btnAnnulla = findViewById<Button>(R.id.btnCancel)

        val editNome = findViewById<EditText>(R.id.editNome)
        val editCognome = findViewById<EditText>(R.id.editCognome)
        val editEta = findViewById<EditText>(R.id.editEta)
        val editProfessione = findViewById<EditText>(R.id.editProfessione)
        val editOreLavoro = findViewById<EditText>(R.id.editOreLavoro)
        val editOreSonno = findViewById<EditText>(R.id.editOreSonno)
        val editStileVita = findViewById<EditText>(R.id.editStileVita)

        // 1. PRE-COMPILAZIONE: Carica dati attuali dal DB
        lifecycleScope.launch(Dispatchers.IO) {
            val user = userRepository.getUserByEmail(email)
            withContext(Dispatchers.Main) {
                user?.let {
                    editNome.setText(it.name)
                    editCognome.setText(it.surname)
                    editEta.setText(it.age.toString())
                    editProfessione.setText(it.job)
                    editOreLavoro.setText(it.workHours.toString())
                    editOreSonno.setText(it.sleepHours.toString())
                    editStileVita.setText(it.bio)

                    if (!it.profileImageUri.isNullOrEmpty()) {
                        try {
                            imgProfile.setImageURI(Uri.parse(it.profileImageUri))
                        } catch (e: Exception) { e.printStackTrace() }
                    }

                    findViewById<TextView>(R.id.editProfileNameLabel).text = it.name
                    findViewById<TextView>(R.id.editProfileJobLabel).text = it.job
                }
            }
        }

        // 2. UPLOAD FOTO
        btnUpload.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // 3. SALVATAGGIO
        btnSalva.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val currentUser = userRepository.getUserByEmail(email)
                val updatedUser = User(
                    email = email,
                    password = currentUser?.password ?: "",
                    name = editNome.text.toString(),
                    surname = editCognome.text.toString(),
                    age = editEta.text.toString().toIntOrNull() ?: 0,
                    job = editProfessione.text.toString(),
                    workHours = editOreLavoro.text.toString().toFloatOrNull() ?: 0f,
                    sleepHours = editOreSonno.text.toString().toFloatOrNull() ?: 0f,
                    bio = editStileVita.text.toString(),
                    profileImageUri = selectedImageUri?.toString() ?: currentUser?.profileImageUri
                )
                userRepository.insertUser(updatedUser)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditProfileActivity, "Profilo aggiornato!", Toast.LENGTH_SHORT).show()
                    finish() // Torna al profilo
                }
            }
        }

        btnAnnulla.setOnClickListener { finish() }
    }
}