package com.corsolp.data.repository

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.corsolp.data.worker.MoodNotificationWorker
import com.corsolp.domain.repository.NotificationRepository
import java.util.concurrent.TimeUnit

class NotificationRepositoryImpl(private val context: Context) : NotificationRepository {

    override fun scheduleDailyMoodReminder() {
        val dailyWorkRequest = PeriodicWorkRequestBuilder<MoodNotificationWorker>(24, TimeUnit.HOURS)
            .build()


        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DailyMoodNotification",
            ExistingPeriodicWorkPolicy.KEEP, 
            dailyWorkRequest
        )
    }
}