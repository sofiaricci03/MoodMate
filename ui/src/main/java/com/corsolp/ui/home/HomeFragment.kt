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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repositoryProvider = ServiceLocator.requireRepositoryProvider()
        val userRepository = repositoryProvider.userRepository()
        val moodRepository = repositoryProvider.moodRepository()
        val weatherRepository = repositoryProvider.weatherRepository()
        val userEmail = repositoryProvider.preferencesRepository().getSavedUserEmail() ?: ""

        val greetingText = view.findViewById<TextView>(R.id.homeGreeting)
        val dateText = view.findViewById<TextView>(R.id.homeDate)
        val moodStatusText = view.findViewById<TextView>(R.id.homeMoodStatus)
        val weatherTempText = view.findViewById<TextView>(R.id.weatherTemp)
        val weatherDescText = view.findViewById<TextView>(R.id.weatherDesc)

        val dateFormat = SimpleDateFormat("d MMMM", Locale.ITALIAN)
        dateText.text = dateFormat.format(Date())

        lifecycleScope.launch(Dispatchers.IO) {
            val user = userRepository.getUserByEmail(userEmail)
            val todayDbFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val todayMood = moodRepository.getMoodByDate(userEmail, todayDbFormat)
            val weather = weatherRepository.getCurrentWeather(44.49, 11.34)

            withContext(Dispatchers.Main) {
                user?.let { greetingText.text = "Buongiorno, ${it.name}!" }

                moodStatusText.text = if (todayMood != null) {
                    "Oggi ti senti ${todayMood.moodType.lowercase()}"
                } else {
                    "Come ti senti oggi?"
                }

                if (weather != null) {
                    weatherTempText.text = "Bologna ${weather.temperature.toInt()}°C"
                    weatherDescText.text = translateWeatherCode(weather.weatherCode)
                } else {
                    weatherTempText.text = "Bologna --°C"
                    weatherDescText.text = "Meteo non disponibile"
                }
            }
        }
    }

    private fun translateWeatherCode(code: Int): String {
        return when (code) {
            0 -> "Sereno"
            1, 2, 3 -> "Parzialmente nuvoloso"
            45, 48 -> "Nebbia"
            51, 53, 55 -> "Pioggia lieve"
            56, 57 -> "Pioggia gelata"
            61, 63, 65 -> "Pioggia"
            66, 67 -> "Pioggia gelata"
            71, 73, 75 -> "Neve"
            77 -> "Nevischio"
            80, 81, 82 -> "Rovesci"
            85, 86 -> "Rovesci di neve"
            95, 96, 99 -> "Temporale"
            else -> "Variabile"
        }
    }
}
