package com.corsolp.ui.calendar

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.corsolp.domain.di.ServiceLocator
import com.corsolp.ui.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

//Fragment gestisce la visualizzazione del calendario mensile.
 //Permette di navigare tra i mesi e visualizzare gli umori registrati per ogni giorno.
class CalendarFragment : Fragment() {
    
    // Calendario per tracciare il mese e l'anno correntemente visualizzati
    private var currentCalendar: Calendar = Calendar.getInstance()
    
    // Email dell'utente per recuperare i dati dal database
    private var userEmail: String? = null
    
    // Componenti della UI
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var txtMonthYear: TextView

    companion object {

        // Crea una nuova istanza del fragment passando l'email dell'utente come argomento.
        fun newInstance(email: String?) = CalendarFragment().apply {
            arguments = Bundle().apply { putString("USER_EMAIL", email) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Carica il layout del fragment
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        
        // Recupera l'email passata tramite newInstance
        userEmail = arguments?.getString("USER_EMAIL")

        // Inizializza le viste
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView)
        txtMonthYear = view.findViewById(R.id.txtMonthYear)

        // Pulsante Mese Precedente
        view.findViewById<TextView>(R.id.btnPrevMonth).setOnClickListener {
            currentCalendar.add(Calendar.MONTH, -1)
            updateCalendar()
        }

        // Pulsante Mese Successivo
        view.findViewById<TextView>(R.id.btnNextMonth).setOnClickListener {
            currentCalendar.add(Calendar.MONTH, 1)
            updateCalendar()
        }

        // Carica i dati per il mese corrente
        updateCalendar()
        return view
    }

    //Aggiorna il titolo del mese e carica i mood dal database.

    private fun updateCalendar() {
        // Formatta il titolo (es: GENNAIO 2024)
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.ITALIAN)
        txtMonthYear.text = monthYearFormat.format(currentCalendar.time).uppercase()

        // Prefisso per la query al DB (formato "yyyy-MM")
        val dbFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val monthPrefix = dbFormat.format(currentCalendar.time)

        // Ottiene il repository tramite ServiceLocator
        val moodRepository = ServiceLocator.requireRepositoryProvider().moodRepository()

        // Esegue la ricerca nel database in un thread secondario (Dispatchers.IO)
        lifecycleScope.launch(Dispatchers.IO) {
            val moods = moodRepository.getMoodsByMonth(userEmail ?: "", monthPrefix)

            // Torna sul thread principale per aggiornare l'interfaccia
            withContext(Dispatchers.Main) {
                if (isAdded) { // Verifica che il fragment sia ancora attaccato all'activity
                    val daysInMonth = generateDaysList(currentCalendar, moods)
                    
                    // Imposta la griglia a 7 colonne (giorni della settimana)
                    calendarRecyclerView.layoutManager = GridLayoutManager(context, 7)
                    calendarRecyclerView.adapter = CalendarAdapter(daysInMonth)
                }
            }
        }
    }

    //Genera la lista di giorni da visualizzare nel RecyclerView.
     //Include gli spazi vuoti iniziali per allineare il 1° del mese al corretto giorno della settimana.

    private fun generateDaysList(cal: Calendar, moods: List<com.corsolp.domain.models.Mood>): List<CalendarDayModel> {
        val days = mutableListOf<CalendarDayModel>()
        
        // Copia il calendario corrente per non modificarlo e imposta il 1° giorno del mese
        val tempCal = cal.clone() as Calendar
        tempCal.set(Calendar.DAY_OF_MONTH, 1)

        // Calcola il primo giorno della settimana (1=Dom, 2=Lun, ecc.)
        // Adattiamo per far sì che la settimana inizi di Lunedì (L=1, D=7)
        var firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK) - 1
        if (firstDayOfWeek == 0) firstDayOfWeek = 7 // Se è domenica, diventa 7

        // Aggiunge spazi vuoti all'inizio della lista (offset)
        for (i in 1 until firstDayOfWeek) {
            days.add(CalendarDayModel("", null))
        }

        // Numero massimo di giorni nel mese corrente
        val maxDays = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dbFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Cicla su tutti i giorni del mese e controlla se esiste un mood salvato
        for (i in 1..maxDays) {
            tempCal.set(Calendar.DAY_OF_MONTH, i)
            val dateStr = dbFormat.format(tempCal.time)
            
            // Cerca se nei dati scaricati dal DB c'è un mood per questa data
            val moodFound = moods.find { it.date == dateStr }
            
            // Aggiunge il giorno alla lista
            days.add(CalendarDayModel(i.toString(), moodFound?.moodType))
        }
        return days
    }
}

/**
 * Modello dati per rappresentare un singolo quadratino nel calendario.
 * @param dayNumber Numero del giorno (es: "15"). Vuoto per gli spazi di offset.
 * @param moodType Tipo di umore registrato (es: "Felice"), null se non presente.
 */
data class CalendarDayModel(val dayNumber: String, val moodType: String?)
