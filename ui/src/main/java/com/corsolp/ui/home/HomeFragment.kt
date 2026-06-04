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
    // Launcher nativo per richiedere i permessi di posizione all'utente
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.entries.any { it.value }
        if (isGranted) {
            val (lat, lon) = getDeviceLocation()
            viewModel.loadData(lat, lon, getCityName(lat, lon))
        } else {
            // Permesso rifiutato, usa le coordinate di Default (Roma)
            viewModel.loadData(41.53, 12.28, "Roma")
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
        viewModel = ViewModelProvider(requireActivity(), factory)[HomeViewModel::class.java]

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            updateUI(view, state)
        }

        checkLocationPermissionsAndLoad()
    }
    // Controlla se i permessi sono già attivi, se non lo sono li richiede a schermo
    private fun checkLocationPermissionsAndLoad() {
        // Se i dati sono già nello state, non ricaricare
        if (viewModel.uiState.value?.isLoaded == true) return

        val coarseGranted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val fineGranted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (coarseGranted || fineGranted) {
            val (lat, lon) = getDeviceLocation()
            viewModel.loadData(lat, lon, getCityName(lat, lon))
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
                if (bestLocation == null || loc.accuracy < bestLocation.accuracy) bestLocation = loc
            }
            if (bestLocation != null) return Pair(bestLocation.latitude, bestLocation.longitude)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
        // Se i servizi di localizzazione sono spenti, default su Roma
        return Pair(41.53, 12.28)
    }

    // Ritorna nome città
    private fun getCityName(lat: Double, lon: Double): String {
        if (lat == 41.53 && lon == 12.28) return "Roma"
        return try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            if (!addresses.isNullOrEmpty()) addresses[0].locality ?: "Roma" else "Roma"
        } catch (e: Exception) {
            "Roma"
        }
    }

    private fun updateUI(view: View, state: HomeUiState) {
        view.findViewById<TextView>(R.id.homeMoodStatus).text  = state.moodStatusText
        view.findViewById<TextView>(R.id.weatherTemp).text     = state.weatherTemp
        view.findViewById<TextView>(R.id.weatherDesc).text     = state.weatherDesc
        view.findViewById<TextView>(R.id.homeQuote).text       = state.quoteText
        view.findViewById<TextView>(R.id.homeTipDesc).text     = state.healthyTip
    }
}