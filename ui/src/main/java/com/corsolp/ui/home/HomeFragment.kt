package com.corsolp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.corsolp.domain.di.ServiceLocator
import com.corsolp.ui.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repositoryProvider = ServiceLocator.requireRepositoryProvider()
        val userRepository = repositoryProvider.userRepository()
        val moodRepository = repositoryProvider.moodRepository()
        val userEmail = repositoryProvider.preferencesRepository().getSavedUserEmail() ?: ""

        val greetingText = view.findViewById<TextView>(R.id.homeGreeting)
        val dateText = view.findViewById<TextView>(R.id.homeDate)
        val moodStatusText = view.findViewById<TextView>(R.id.homeMoodStatus)

        // Imposta la data odierna
        val dateFormat = SimpleDateFormat("d MMMM", Locale.ITALIAN)
        dateText.text = dateFormat.format(Date())

        lifecycleScope.launch(Dispatchers.IO) {

            val user = userRepository.getUserByEmail(userEmail)

            // Controlla se l'utente ha già inserito l'umore OGGI
            val todayDbFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val todayMood = moodRepository.getMoodByDate(userEmail, todayDbFormat)

            withContext(Dispatchers.Main) {
                // Saluto personalizzato
                user?.let {
                    greetingText.text = "Buongiorno, ${it.name}!"
                }

                if (todayMood != null) {
                    moodStatusText.text = "Oggi ti senti ${todayMood.moodType.lowercase()}"
                } else {
                    moodStatusText.text = "Come ti senti oggi? Ricorda di inserire il tuo mood"
                }
            }
        }
    }
}