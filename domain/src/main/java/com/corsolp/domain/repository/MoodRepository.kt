package com.corsolp.domain.repository
import com.corsolp.domain.models.Mood

interface MoodRepository {
    suspend fun insertMood(mood: Mood)

    suspend fun getMoodsByDateRange(email: String, startDate: String, endDate: String): List<Mood>

    suspend fun getMoodByDate(email: String, date: String): Mood?
}
