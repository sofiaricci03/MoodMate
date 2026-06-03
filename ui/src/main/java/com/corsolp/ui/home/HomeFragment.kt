package com.corsolp.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import android.annotation.SuppressLint

class HomeFragment : Fragment() {

    // Launcher nativo per richiedere i permessi di posizione all'utente
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val currentView = view ?: return@registerForActivityResult

        // Controlliamo se almeno dei due permessi è stato concesso
        val isGranted = permissions.entries.any { it.value }

        if (isGranted) {
            val (lat, lon) = getDeviceLocation()
            loadHomeData(currentView, lat, lon)
        } else {
            // Permesso rifiutato, usa le coordinate di Default (Roma)
            loadHomeData(currentView, 41.53, 12.28)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateText = view.findViewById<TextView>(R.id.homeDate)
        val dateFormat = SimpleDateFormat("d MMMM", Locale.ITALIAN)
        dateText.text = dateFormat.format(Date())

        checkLocationPermissionsAndLoad(view)
    }

    // Controlla se i permessi sono già attivi, se non lo sono li richiede a schermo
    private fun checkLocationPermissionsAndLoad(view: View) {
        val coarseGranted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val fineGranted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (coarseGranted || fineGranted) {
            val (lat, lon) = getDeviceLocation()
            loadHomeData(view, lat, lon)
        } else {
            requestPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            )
        }
    }

    // Recupera l'ultima coordinata nota dal provider hardware più preciso disponibile
    @SuppressLint("MissingPermission")
    private fun getDeviceLocation(): Pair<Double, Double> {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            val providers = locationManager.getProviders(true)
            var bestLocation: Location? = null
            for (provider in providers) {
                val loc = locationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || loc.accuracy < bestLocation.accuracy) {
                    bestLocation = loc
                }
            }
            if (bestLocation != null) {
                return Pair(bestLocation.latitude, bestLocation.longitude)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
        // Se i servizi di localizzazione sono spenti, default su Roma
        return Pair(41.53, 12.28)
    }

    // Svolge il caricamento dal DB e dall'API con le coordinate dinamiche
    private fun loadHomeData(view: View, lat: Double, lon: Double) {
        //Recupero repository
        val repositoryProvider = ServiceLocator.requireRepositoryProvider()
        val userRepository = repositoryProvider.userRepository()
        val moodRepository = repositoryProvider.moodRepository()
        val weatherRepository = repositoryProvider.weatherRepository()
        val quoteRepository = repositoryProvider.quoteRepository()
        val userEmail = repositoryProvider.preferencesRepository().getSavedUserEmail() ?: ""

        val moodStatusText = view.findViewById<TextView>(R.id.homeMoodStatus)
        val weatherTempText = view.findViewById<TextView>(R.id.weatherTemp)
        val weatherDescText = view.findViewById<TextView>(R.id.weatherDesc)
        val homeQuoteText = view.findViewById<TextView>(R.id.homeQuote)

        lifecycleScope.launch(Dispatchers.IO) {
            val user = userRepository.getUserByEmail(userEmail)
            val todayDbFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val todayMood = moodRepository.getMoodByDate(userEmail, todayDbFormat)

            val weather = weatherRepository.getCurrentWeather(lat, lon)
            val quote = quoteRepository.getRandomQuote()

            // Converte le coordinate GPS nel nome del comune
            val cityName = if (lat == 41.53 && lon == 12.28) {
                "Roma"
            } else {
                try {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    if (!addresses.isNullOrEmpty()) {
                        addresses[0].locality
                    } else {
                        "Roma"
                    }
                } catch (e: Exception) {
                    "Roma"
                }
            }

            withContext(Dispatchers.Main) {

                moodStatusText.text = if (todayMood != null) {
                    "Oggi ti senti ${todayMood.moodType.lowercase()}"
                } else {
                    "Come ti senti oggi?"
                }

                if (weather != null) {
                    weatherTempText.text = "$cityName ${weather.temperature.toInt()}°C"
                    weatherDescText.text = translateWeatherCode(weather.weatherCode)
                } else {
                    weatherTempText.text = "$cityName --°C"
                    weatherDescText.text = "Meteo non disponibile"
                }

                // Mostra la citazione se internet è disponibile, altrimenti mostra una citazione di default
                if (quote != null) {
                    homeQuoteText.text = "\"${quote.text}\"\n- ${quote.author}"
                } else {
                    homeQuoteText.text = "\"Concediti il tempo di cui hai bisogno. Non c'è fretta.\""
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