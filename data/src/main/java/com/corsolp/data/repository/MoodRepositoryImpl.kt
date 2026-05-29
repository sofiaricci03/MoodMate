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
}