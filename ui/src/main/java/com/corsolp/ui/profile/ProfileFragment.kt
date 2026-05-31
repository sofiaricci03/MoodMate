package com.corsolp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        val repositoryProvider = ServiceLocator.requireRepositoryProvider()
        val userRepository = repositoryProvider.userRepository()
        val preferencesRepository = repositoryProvider.preferencesRepository()
        val userEmail = arguments?.getString(ARG_USER_EMAIL)
            ?: preferencesRepository.getSavedUserEmail()
            ?: ""

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val user = userRepository.getUserByEmail(userEmail)

            withContext(Dispatchers.Main) {
                user?.let {
                    view.findViewById<TextView>(R.id.profileName).text = it.name
                    view.findViewById<TextView>(R.id.profileJobHeader).text = it.job
                    view.findViewById<TextView>(R.id.profileAge).text = it.age.toString()
                    view.findViewById<TextView>(R.id.profileJob).text = it.job
                    view.findViewById<TextView>(R.id.profileWorkHours).text = it.workHours.toString()
                    view.findViewById<TextView>(R.id.profileSleepHours).text = it.sleepHours.toString()
                    view.findViewById<TextView>(R.id.profileBio).text = it.bio
                }
            }
        }

        view.findViewById<TextView>(R.id.logoutButton).setOnClickListener {
            preferencesRepository.clearUser()

            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    companion object {
        private const val ARG_USER_EMAIL = "USER_EMAIL"

        fun newInstance(userEmail: String?) = ProfileFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_USER_EMAIL, userEmail)
            }
        }
    }
}
