package com.corsolp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.corsolp.data.local.entities.Mood

@Dao
interface MoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(mood: Mood)

    @Query("SELECT * FROM mood_table WHERE userEmail = :email AND date LIKE :yearMonth || '%'")
    suspend fun getMoodsByMonth(email: String, yearMonth: String): List<Mood>

    @Query("SELECT * FROM mood_table WHERE userEmail = :email AND date >= :startDate AND date <= :endDate ORDER BY date ASC")
    suspend fun getMoodsByDateRange(email: String, startDate: String, endDate: String): List<Mood>

    @Query("SELECT * FROM mood_table WHERE userEmail = :email AND date = :date LIMIT 1")
    suspend fun getMoodByDate(email: String, date: String): Mood?
}