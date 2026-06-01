package com.corsolp.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.corsolp.ui.R

//Adapter per gestire la visualizzazione dei giorni del calendario in una griglia (RecyclerView).
//Riceve in ingresso una lista di [CalendarDayModel], che contiene il numero del giorno e l'eventuale mood.

class CalendarAdapter(private val days: List<CalendarDayModel>) :
    RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    // ViewHolder: definisce i riferimenti agli elementi grafici di ogni singola cella del calendario.
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDay: TextView = view.findViewById(R.id.txtDayNumber)
        val imgMood: ImageView = view.findViewById(R.id.imgMoodEmoji)
    }

    //Chiamato quando il RecyclerView ha bisogno di creare una nuova cella (ViewHolder).
     //Carica il layout XML 'item_calendar_day'.

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = days[position]
        
        // Imposta il numero del giorno (es. "1", "2", ecc.)
        holder.txtDay.text = day.dayNumber

        // Se il numero del giorno è vuoto, significa che è uno spazio vuoto (offset)
        // necessario per allineare il primo giorno del mese al giorno della settimana corretto.
        if (day.dayNumber.isEmpty()) {
            holder.imgMood.visibility = View.GONE
            holder.txtDay.visibility = View.GONE
        } else {
            // Se è un giorno reale del mese, lo rendiamo visibile
            holder.txtDay.visibility = View.VISIBLE
            holder.imgMood.visibility = View.VISIBLE

            // Se l'utente ha registrato un umore per questo giorno, mostriamo l'emoji corrispondente
            if (day.moodType != null) {
                val resId = when (day.moodType) {
                    "Felice" -> R.drawable.felice
                    "Sereno" -> R.drawable.sereno
                    "Triste" -> R.drawable.triste
                    "Arrabbiato" -> R.drawable.arrabbiato
                    "Neutrale" -> R.drawable.neutro
                    "Stressato" -> R.drawable.stressato
                    else -> R.drawable.mood_gray_circle
                }
                holder.imgMood.setImageResource(resId)
                holder.imgMood.alpha = 1.0f
            } else {
                // Se non c'è umore registrato, mostriamo un cerchio grigio standard
                holder.imgMood.setImageResource(R.drawable.mood_gray_circle)
                holder.imgMood.alpha = 1.0f
            }
        }
    }

    //Restituisce il numero totale di celle (giorni + spazi vuoti) da visualizzare.
    override fun getItemCount() = days.size
}
