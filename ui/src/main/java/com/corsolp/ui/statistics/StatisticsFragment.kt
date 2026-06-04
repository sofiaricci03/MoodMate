package com.corsolp.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.corsolp.domain.di.ServiceLocator
import com.corsolp.ui.R

class StatisticsFragment : Fragment() {

    private lateinit var viewModel: StatisticsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val provider = ServiceLocator.requireRepositoryProvider()
        val factory = StatisticsViewModelFactory(
            provider.moodRepository(),
            provider.preferencesRepository()
        )

        // Lega all'Activity
        viewModel = ViewModelProvider(requireActivity(), factory)[StatisticsViewModel::class.java]

        // Osserva lo stato e aggiorna la UI
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            updateUI(view, state)
        }

        viewModel.loadWeeklyStats()
    }

    private fun updateUI(view: View, state: StatisticsUiState) {
        val barChartContainer = view.findViewById<LinearLayout>(R.id.barChartContainer)
        val statMediaScore = view.findViewById<TextView>(R.id.statMediaScore)
        val statMediaLabel = view.findViewById<TextView>(R.id.statMediaText)

        // Aggiorna media
        statMediaScore.text = state.averageScore
        statMediaLabel.text = state.averageLabel

        // Ridisegna le barre
        barChartContainer.removeAllViews()
        for (day in state.currentWeekDays) {
            val moodForDay = state.weeklyMoods.find { it.date == day }
            val weight = moodForDay?.let { viewModel.getMoodWeight(it.moodType) } ?: 0f
            barChartContainer.addView(buildBarView(weight))
        }
    }

    private fun buildBarView(weight: Float): LinearLayout {
        val barContainer = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
            orientation = LinearLayout.VERTICAL
            weightSum = 6f
            setPadding(18, 0, 18, 0)
        }

        // Lo "spazio vuoto" in alto che spinge la barra verso il basso
        val emptySpaceWeight = 6f - weight
        if (emptySpaceWeight > 0f) {
            val spacer = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, emptySpaceWeight
                )
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

        return barContainer
    }
}