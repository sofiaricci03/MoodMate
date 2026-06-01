package com.corsolp.data.repositories

import com.corsolp.data.local.dao.MoodDao
import com.corsolp.domain.models.Mood
import com.corsolp.domain.repository.MoodRepository

class MoodRepositoryImpl(private val moodDao: MoodDao) : MoodRepository {
    override suspend fun insertMood(mood: Mood) {
        // Mappiamo il modello del Domain in quello che Room capisce (Entity)
        val moodEntity = com.corsolp.data.local.entities.Mood(
            userEmail = mood.userEmail,
            date = mood.date,
            moodType = mood.moodType,
            note = mood.note
        )
        moodDao.insertMood(moodEntity)
    }

    override suspend fun getMoodsByDateRange(email: String, startDate: String, endDate: String): List<Mood> {
        val entities = moodDao.getMoodsByDateRange(email, startDate, endDate)
        // Mappiamo l'entità del database nel modello del domain
        return entities.map {
            Mood(it.userEmail,
                it.date,
                it.moodType,
                it.note)
        }
    }

    override suspend fun getMoodByDate(email: String, date: String): Mood? {
        val entity = moodDao.getMoodByDate(email, date)

        return entity?.let {
            Mood(
                userEmail = it.userEmail,
                date = it.date,
                moodType = it.moodType,
                note = it.note
            )
        }
    }
}

