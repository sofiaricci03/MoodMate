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
import androidx.lifecycle.ViewModelProvider
import com.corsolp.domain.di.ServiceLocator
import com.corsolp.ui.R
import com.corsolp.ui.login.MainActivity

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repoProvider = ServiceLocator.requireRepositoryProvider()
        val factory = ProfileViewModelFactory(
            repoProvider.userRepository(),
            repoProvider.preferencesRepository()
        )
        profileViewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                // Popolamento testi
                view.findViewById<TextView>(R.id.profileName).text = user.name
                view.findViewById<TextView>(R.id.profileJobHeader).text = user.job
                view.findViewById<TextView>(R.id.profileAge).text = user.age.toString()
                view.findViewById<TextView>(R.id.profileJob).text = user.job
                view.findViewById<TextView>(R.id.profileWorkHours).text = "${user.workHours} h"
                view.findViewById<TextView>(R.id.profileSleepHours).text = "${user.sleepHours} h"
                view.findViewById<TextView>(R.id.profileBio).text = user.bio

                // Caricamento immagine
                val profileImg = view.findViewById<ImageView>(R.id.profileImage)
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

        profileViewModel.loadUser()

        // Tasto Modifica
        view.findViewById<Button>(R.id.editProfileButton).setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        // Tasto Logout
        view.findViewById<TextView>(R.id.logoutButton).setOnClickListener {
            profileViewModel.logout()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Ricarica i dati ogni volta che torni sul profilo
        if (::profileViewModel.isInitialized) {
            profileViewModel.loadUser()
        }
    }

    companion object {
        private const val ARG_USER_EMAIL = "USER_EMAIL"
        fun newInstance(userEmail: String?) = ProfileFragment().apply {
            arguments = Bundle().apply { putString(ARG_USER_EMAIL, userEmail) }
        }
    }
}