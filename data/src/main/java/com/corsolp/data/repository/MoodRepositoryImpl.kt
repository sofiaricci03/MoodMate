package com.corsolp.data.repositories

import com.corsolp.data.local.dao.MoodDao
import com.corsolp.domain.models.Mood as DomainMood
import com.corsolp.data.local.entities.Mood as EntityMood
import com.corsolp.domain.repository.MoodRepository

/**
 * Implementazione della Repository per i Mood.
 * Questa classe funge da ponte tra il modulo di data (database Room) e il modulo domain (logica di business).
 */
class MoodRepositoryImpl(private val moodDao: MoodDao) : MoodRepository {

    /**
     * Inserisce un nuovo mood nel database.
     * Riceve un oggetto [DomainMood] dal livello superiore, lo converte in [EntityMood]
     * (il formato capito da Room) e lo salva.
     */
    override suspend fun insertMood(mood: DomainMood) {
        val moodEntity = EntityMood(
            userEmail = mood.userEmail,
            date = mood.date,
            moodType = mood.moodType,
            note = mood.note
        )
        moodDao.insertMood(moodEntity)
    }

    /**
     * Recupera la lista dei mood per un determinato utente in un mese specifico.
     * @param email L'email dell'utente.
     * @param yearMonth La stringa del mese in formato "yyyy-MM".
     * @return Una lista di [DomainMood] pronti per essere visualizzati nella UI.
     */
    override suspend fun getMoodsByMonth(email: String, yearMonth: String): List<DomainMood> {
        // Interroga il DAO per ottenere le entità dal database
        val entities = moodDao.getMoodsByMonth(email, yearMonth)

        // Trasformiamo ogni riga (Entity) del DB in un modello (Domain) per la UI
        // Questo mapping assicura che il modulo UI non dipenda direttamente dalle tabelle del database.
        return entities.map { entity ->
            DomainMood(
                userEmail = entity.userEmail,
                date = entity.date,
                moodType = entity.moodType,
                note = entity.note
            )
        }
    }
}
