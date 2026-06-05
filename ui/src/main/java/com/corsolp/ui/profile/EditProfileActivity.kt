package com.corsolp.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.corsolp.domain.di.ServiceLocator
import com.corsolp.ui.R

class EditProfileActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private lateinit var imgProfile: ImageView
    private lateinit var editProfileViewModel: EditProfileViewModel

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

        val factory = EditProfileViewModelFactory(userRepository, preferencesRepository)
        editProfileViewModel = ViewModelProvider(this, factory)[EditProfileViewModel::class.java]

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

        editProfileViewModel.currentUser.observe(this) { user ->
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
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                findViewById<TextView>(R.id.editProfileNameLabel).text = it.name
                findViewById<TextView>(R.id.editProfileJobLabel).text = it.job
            }
        }

        editProfileViewModel.saveResult.observe(this) { result ->
            when (result) {
                is EditProfileViewModel.SaveResult.Success -> {
                    Toast.makeText(this@EditProfileActivity, "Profilo aggiornato!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is EditProfileViewModel.SaveResult.Error -> {
                    Toast.makeText(this@EditProfileActivity, result.message, Toast.LENGTH_LONG).show()
                }
                null -> {}
            }
        }

        editProfileViewModel.validationError.observe(this) { message ->
            message?.let {
                Toast.makeText(this@EditProfileActivity, "Controlla i dati inseriti. Alcuni campi contengono errori.", Toast.LENGTH_LONG).show()
            }
        }

        editProfileViewModel.loadUser()

        // 2. UPLOAD FOTO
        btnUpload.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // 3. SALVATAGGIO
        btnSalva.setOnClickListener {
            // Estraiamo i valori inseriti dall'utente (fall-back a 0f se il campo è vuoto)
            val eta = editEta.text.toString()
            val oreLavoro = editOreLavoro.text.toString()
            val oreSonno = editOreSonno.text.toString()

            val ageValue = eta.toIntOrNull() ?: 0
            val workHoursValue = oreLavoro.toFloatOrNull() ?: 0f
            val sleepHoursValue = oreSonno.toFloatOrNull() ?: 0f

            var isValid = true

            // Controllo Età (limite massimo 200, possiamo anche evitare l'età a 0)
            if (ageValue <= 0 || ageValue > 200) {
                editEta.error = "Inserisci un'età valida compresa tra 1 e 200 anni"
                isValid = false
            }

            // Controllo Somma Ore (limite massimo 24 ore)
            if (workHoursValue + sleepHoursValue > 24f) {
                editOreLavoro.error = "La somma di lavoro e sonno non può superare 24 ore"
                editOreSonno.error = "La somma di lavoro e sonno non può superare 24 ore"
                isValid = false
            }

            // Se uno dei controlli è fallito, interrompiamo il salvataggio e avvisiamo l'utente
            if (!isValid) {
                Toast.makeText(
                    this@EditProfileActivity,
                    "Controlla i dati inseriti. Alcuni campi contengono errori.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            editProfileViewModel.saveProfile(
                email = preferencesRepository.getSavedUserEmail() ?: "",
                name = editNome.text.toString(),
                surname = editCognome.text.toString(),
                ageText = editEta.text.toString(),
                job = editProfessione.text.toString(),
                workHoursText = editOreLavoro.text.toString(),
                sleepHoursText = editOreSonno.text.toString(),
                bio = editStileVita.text.toString(),
                selectedImageUri = selectedImageUri?.toString()
            )
        }

        btnAnnulla.setOnClickListener { finish() }
    }
}