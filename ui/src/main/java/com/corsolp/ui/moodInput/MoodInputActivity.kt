package com.corsolp.ui.home

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.corsolp.ui.R
import java.text.SimpleDateFormat
import java.util.*

class MoodInputActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_input)

        // Imposta la data corrente
        val dateTextView = findViewById<TextView>(R.id.currentDateText)
        val sdf = SimpleDateFormat("d MMMM", Locale.ITALIAN)
        val currentDate = sdf.format(Date())
        dateTextView.text = currentDate

        // Logica per il pulsante Salva
        //findViewById<Button>(R.id.saveMoodButton).setOnClickListener { finish() }
    }
}