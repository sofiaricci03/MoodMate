package com.corsolp.domain.repository
import com.corsolp.domain.models.Mood

interface MoodRepository {
    suspend fun insertMood(mood: Mood)
}
