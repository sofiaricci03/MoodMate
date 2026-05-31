package com.corsolp.ui.moodInput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.corsolp.domain.di.ServiceLocator
import com.corsolp.domain.models.Mood
import com.corsolp.ui.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MoodInputFragment : Fragment() {
    private var selectedMood: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_mood_input, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repositoryProvider = ServiceLocator.requireRepositoryProvider()
        val moodRepository = repositoryProvider.moodRepository()
        val preferencesRepository = repositoryProvider.preferencesRepository()
        val userEmail = arguments?.getString(ARG_USER_EMAIL)
            ?: preferencesRepository.getSavedUserEmail()
            ?: ""

        val sdfVisual = SimpleDateFormat("d MMMM", Locale.ITALIAN)
        val sdfDb = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Date()
        val dateForDb = sdfDb.format(today)

        view.findViewById<TextView>(R.id.currentDateText).text = sdfVisual.format(today)

        setupMoodButton(view, R.id.cardFelice, "Felice")
        setupMoodButton(view, R.id.cardSereno, "Sereno")
        setupMoodButton(view, R.id.cardNeutrale, "Neutrale")
        setupMoodButton(view, R.id.cardTriste, "Triste")
        setupMoodButton(view, R.id.cardArrabbiato, "Arrabbiato")
        setupMoodButton(view, R.id.cardStressato, "Stressato")

        view.findViewById<Button>(R.id.saveMoodButton).setOnClickListener {
            val note = view.findViewById<EditText>(R.id.editMoodNote).text.toString()
            val mood = selectedMood

            if (mood == null) {
                Toast.makeText(requireContext(), "Seleziona un umore!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val newMood = Mood(
                    userEmail = userEmail,
                    date = dateForDb,
                    moodType = mood,
                    note = note
                )

                moodRepository.insertMood(newMood)

                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Umore salvato!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun setupMoodButton(view: View, cardId: Int, moodName: String) {
        view.findViewById<CardView>(cardId).setOnClickListener {
            selectedMood = moodName
            Toast.makeText(requireContext(), "Hai selezionato: $moodName", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val ARG_USER_EMAIL = "USER_EMAIL"

        fun newInstance(userEmail: String?) = MoodInputFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_USER_EMAIL, userEmail)
            }
        }
    }
}
