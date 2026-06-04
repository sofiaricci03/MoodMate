package com.corsolp.ui.home

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.lifecycle.ViewModelProvider
import com.corsolp.domain.di.ServiceLocator
import com.corsolp.ui.R
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private var currentCityName: String = "Roma"
    // Launcher nativo per richiedere i permessi di posizione all'utente
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.entries.any { it.value }

        if (isGranted) {
            val (lat, lon) = getDeviceLocation()
            currentCityName = getCityName(lat, lon)
            viewModel.loadData(lat, lon)
        } else {
            // Permesso rifiutato, usa le coordinate di Default (Roma)
            viewModel.loadData(41.53, 12.28)
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

        val provider = ServiceLocator.requireRepositoryProvider()
        val factory = HomeViewModelFactory(
            provider.weatherRepository(),
            provider.quoteRepository(),
            provider.moodRepository(),
            provider.preferencesRepository()
        )

        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            updateUI(view, state)
        }

        checkLocationPermissionsAndLoad()
    }
    // Controlla se i permessi sono già attivi, se non lo sono li richiede a schermo
    private fun checkLocationPermissionsAndLoad() {
        val coarseGranted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val fineGranted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (coarseGranted || fineGranted) {
            val (lat, lon) = getDeviceLocation()
            currentCityName = getCityName(lat, lon)
            viewModel.loadData(lat, lon)
        } else {
            requestPermissionLauncher.launch(
                arrayOf( Manifest.permission.ACCESS_COARSE_LOCATION,  Manifest.permission.ACCESS_FINE_LOCATION)
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
    private fun getCityName(lat: Double, lon: Double): String {
        if (lat == 41.53 && lon == 12.28) return "Roma"

        return try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].locality ?: "Roma"
            } else {
                "Roma"
            }
        } catch (e: Exception) {
            "Roma"
        }
    }

    private fun updateUI(view: View, state: HomeUiState) {
        val moodStatusText = view.findViewById<TextView>(R.id.homeMoodStatus)
        val weatherTempText = view.findViewById<TextView>(R.id.weatherTemp)
        val weatherDescText = view.findViewById<TextView>(R.id.weatherDesc)
        val homeQuoteText = view.findViewById<TextView>(R.id.homeQuote)
        val healthyTipText = view.findViewById<TextView>(R.id.homeTipDesc)

        moodStatusText.text = if (state.todayMood != null) {
            "Oggi ti senti ${state.todayMood.moodType.lowercase()}"
        } else {
            "Come ti senti oggi?"
        }

        if (state.weather != null) {
            weatherTempText.text = "$currentCityName ${state.weather.temperature.toInt()}°C"
            weatherDescText.text = translateWeatherCode(state.weather.weatherCode)
        } else {
            weatherTempText.text = "$currentCityName --°C"
            weatherDescText.text = "Meteo non disponibile"
        }
        // Mostra la citazione se internet è disponibile, altrimenti mostra una citazione di default
        homeQuoteText.text = if (state.quote != null) {
            "\"${state.quote.text}\"\n- ${state.quote.author}"
        } else {
            "\"Concediti il tempo di cui hai bisogno. Non c'è fretta.\""
        }

        healthyTipText.text = getHealthyTip(state.weather?.weatherCode)
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

    private fun getHealthyTip(weatherCode: Int?): String {
        // Frasi per bel tempo
        val outdoorTips = listOf(
            "Il tempo è fantastico! Esci a fare una passeggiata di 20 minuti.",
            "Prendi un po' di sole per fare il pieno di vitamina D.",
            "Organizza un'uscita all'aperto o un caffè con un amico.",
            "Fai un po' di stretching o esercizio all'aria aperta."
        )
        // Frasi per brutto tempo
        val indoorTips = listOf(
            "Goditi il meteo fuori con una tisana calda e un buon libro.",
            "Ottima giornata per dedicarti a quell'hobby casalingo che rimandi da un po'.",
            "Fai 10 minuti di yoga o meditazione nel tuo salotto.",
            "Il brutto tempo è la scusa perfetta per un bel film sotto le coperte.",
            "Riordina il tuo spazio: un ambiente pulito aiuta a rilassare la mente."
        )
        // Frasi neutre
        val neutralTips = listOf(
            "Mettiti comodo e ascolta il tuo podcast o album preferito.",
            "Sperimenta in cucina e prova una nuova ricetta salutare.",
            "Prenditi 5 minuti per scrivere su un diario 3 cose per cui sei grato oggi."
        )
        // Se meteo non disponibile, consiglio neutro
        if (weatherCode == null) return neutralTips.random()

        return when (weatherCode) {
            0, 1, 2, 3 -> outdoorTips.random() // Sereno o parzialmente nuvoloso
            51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 71, 73, 75, 77, 80, 81, 82, 85, 86, 95, 96, 99 -> indoorTips.random() // Pioggia, Neve, Temporale
            else -> neutralTips.random()
        }
    }
}