package com.corsolp.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.corsolp.domain.di.ServiceLocator
import com.corsolp.ui.R
import com.corsolp.ui.login.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tasto Modifica
        view.findViewById<Button>(R.id.editProfileButton).setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        // Tasto Logout
        view.findViewById<TextView>(R.id.logoutButton).setOnClickListener {
            val pref = ServiceLocator.requireRepositoryProvider().preferencesRepository()
            pref.clearUser()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Ricarica i dati ogni volta che torni sul profilo
        caricaDatiUtente()
    }

    private fun caricaDatiUtente() {
        val repoProvider = ServiceLocator.requireRepositoryProvider()
        val userRepository = repoProvider.userRepository()

        // Recupera l'email dalle preferenze
        val userEmail = repoProvider.preferencesRepository().getSavedUserEmail() ?: ""

        if (userEmail.isEmpty()) return

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val user = userRepository.getUserByEmail(userEmail)

            withContext(Dispatchers.Main) {
                if (isAdded && user != null) {
                    val root = view ?: return@withContext

                    // Popolamento testi
                    root.findViewById<TextView>(R.id.profileName).text = user.name
                    root.findViewById<TextView>(R.id.profileJobHeader).text = user.job
                    root.findViewById<TextView>(R.id.profileAge).text = user.age.toString()
                    root.findViewById<TextView>(R.id.profileJob).text = user.job
                    root.findViewById<TextView>(R.id.profileWorkHours).text = "${user.workHours} h"
                    root.findViewById<TextView>(R.id.profileSleepHours).text = "${user.sleepHours} h"
                    root.findViewById<TextView>(R.id.profileBio).text = user.bio

                    // Caricamento immagine
                    val profileImg = root.findViewById<ImageView>(R.id.profileImage)
                    if (!user.profileImageUri.isNullOrEmpty()) {
                        try {
                            profileImg.setImageURI(Uri.parse(user.profileImageUri))
                        } catch (e: Exception) {
                            profileImg.setImageResource(R.drawable.user)
                        }
                    } else {
                        profileImg.setImageResource(R.drawable.user)
                    }
                }
            }
        }
    }

    companion object {
        private const val ARG_USER_EMAIL = "USER_EMAIL"
        fun newInstance(userEmail: String?) = ProfileFragment().apply {
            arguments = Bundle().apply { putString(ARG_USER_EMAIL, userEmail) }
        }
    }
}