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
import androidx.lifecycle.ViewModelProvider
import com.corsolp.domain.di.ServiceLocator
import com.corsolp.ui.R

class MoodInputFragment : Fragment() {

    private lateinit var viewModel: MoodInputViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_mood_input, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val provider = ServiceLocator.requireRepositoryProvider()
        val factory = MoodInputViewModelFactory(
            provider.moodRepository(),
            provider.preferencesRepository()
        )

        viewModel = ViewModelProvider(this, factory)[MoodInputViewModel::class.java]

        setupMoodButtons(view)

        view.findViewById<Button>(R.id.saveMoodButton).setOnClickListener {
            val note = view.findViewById<EditText>(R.id.editMoodNote).text.toString()
            viewModel.saveMood(note)
        }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            updateUI(view, state)
        }
    }

    private fun setupMoodButtons(view: View) {
        val moodCards = mapOf(
            R.id.cardFelice      to "Felice",
            R.id.cardSereno      to "Sereno",
            R.id.cardNeutrale    to "Neutrale",
            R.id.cardTriste      to "Triste",
            R.id.cardArrabbiato  to "Arrabbiato",
            R.id.cardStressato   to "Stressato"
        )
        for ((cardId, moodName) in moodCards) {
            view.findViewById<CardView>(cardId).setOnClickListener {
                viewModel.selectMood(moodName)
                Toast.makeText(requireContext(), "Hai selezionato: $moodName", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(view: View, state: MoodInputUiState) {
        val saveBtn   = view.findViewById<Button>(R.id.saveMoodButton)
        val noteEdit  = view.findViewById<EditText>(R.id.editMoodNote)
        val dateText  = view.findViewById<TextView>(R.id.currentDateText)
        val header    = view.findViewById<TextView>(R.id.moodInputHeader)

        dateText.text = state.currentDateVisual

        // Mostra errore se presente
        state.errorMessage?.let {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        if (state.isSaved && state.todayMood != null) {
            // Mood già inserito oggi — blocca la UI
            saveBtn.isEnabled = false
            saveBtn.text = "Umore già inserito oggi"
            noteEdit.isEnabled = false
            noteEdit.setText(state.todayMood.note)
            header.text = "Mood già inserito oggi: ${state.todayMood.moodType}"
        } else {
            // Mood da inserire
            saveBtn.isEnabled = true
            saveBtn.text = "Salva"
            header.text = "Come ti senti oggi?"
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