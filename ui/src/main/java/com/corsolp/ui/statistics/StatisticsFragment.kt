package com.corsolp.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.corsolp.domain.di.ServiceLocator
import com.corsolp.ui.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatisticsFragment : Fragment() {

    // Mappa che converte il testo del Mood nel "peso" dell'istogramma
    private val moodWeights = mapOf(
        "Felice" to 6f,
        "Sereno" to 5f,
        "Neutrale" to 4f,
        "Triste" to 3f,
        "Stressato" to 2f,
        "Arrabbiato" to 1f
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repositoryProvider = ServiceLocator.requireRepositoryProvider()
        val moodRepository = repositoryProvider.moodRepository()
        val userEmail = repositoryProvider.preferencesRepository().getSavedUserEmail() ?: ""

        val barChartContainer = view.findViewById<LinearLayout>(R.id.barChartContainer)

        // Calcola la settimana (Da Lunedì a Domenica)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        // Di default la settimana inizia di Domenica in USA, impostiamo di lunedì
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        // Riempe la lista con i 7 giorni  di questa settimana
        val currentWeekDays = mutableListOf<String>()
        for (i in 0..6) {
            currentWeekDays.add(format.format(calendar.time))
            // Dopo aver salvato il giorno, va avanti di 1
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val startDate = currentWeekDays.first() // Lunedì
        val endDate = currentWeekDays.last()    // Domenica

        lifecycleScope.launch(Dispatchers.IO) {
            val weeklyMoods = moodRepository.getMoodsByDateRange(userEmail, startDate, endDate)

            withContext(Dispatchers.Main) {
                // Svuota il contenitore principale
                barChartContainer.removeAllViews()

                // Disegna una barra per ogni giorno della settimana
                for ((index, day) in currentWeekDays.withIndex()) {

                    // Cerca se c'è un mood per questa specifica data
                    val moodForDay = weeklyMoods.find { it.date == day }

                    // Calcola il peso (altezza della barra)
                    var weight = moodForDay?.let { moodWeights[it.moodType] } ?: 0f

                    // Crea il contenitore verticale per il singolo giorno
                    val barContainer = LinearLayout(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
                        orientation = LinearLayout.VERTICAL
                        weightSum = 6f // L'altezza massima totale è 6 (Felice)
                        // Aggiusta il padding laterale per centrare le barre sopra le lettere
                        setPadding(18, 0, 18, 0)
                    }

                    // Lo "spazio vuoto" in alto che spinge la barra verso il basso
                    val emptySpaceWeight = 6f - weight
                    if (emptySpaceWeight > 0f) {
                        val spacer = View(requireContext()).apply {
                            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, emptySpaceWeight)
                        }
                        barContainer.addView(spacer)
                    }

                    // 2° PEZZO: La barra colorata in basso
                    if (weight > 0f) {
                        val bar = View(requireContext()).apply {
                            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, weight)
                            setBackgroundColor(Color.parseColor("#4CAF50"))
                        }
                        barContainer.addView(bar)
                    }

                    barChartContainer.addView(barContainer)
                }
            }
        }
    }
}