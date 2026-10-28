package com.example.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.SmartRestaurantApp
import com.example.data.inventory.ExpiryTrackerService
import com.example.data.notification.ExpiryNotificationManager
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

/**
 * Background worker to periodically check expiring inventory items for the active restaurant
 * and dispatch notifications using the existing ExpiryNotificationManager and ExpiryTrackerService.
 */
class ExpiryCheckWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val app = applicationContext as? SmartRestaurantApp ?: return Result.success()
            val authRepo = app.container.authRepository
            val inventoryRepo = app.container.inventoryRepository

            val activeRestaurant = authRepo.getActiveRestaurant().first()
            val restaurantId = activeRestaurant?.id ?: return Result.success()

            if (restaurantId > 0) {
                val inventoryItems = inventoryRepo.getInventoryItems(restaurantId).first()
                val todayDate = ExpiryTrackerService.getTodayDateString()

                ExpiryNotificationManager.createNotificationChannel(applicationContext)
                ExpiryNotificationManager.checkAndNotifyExpiringItems(
                    context = applicationContext,
                    restaurantId = restaurantId,
                    restaurantName = activeRestaurant.restaurantName,
                    items = inventoryItems,
                    todayDateStr = todayDate
                )
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "FoodWiseExpiryCheckWorker"

        fun schedulePeriodicCheck(context: Context) {
            try {
                val workRequest = PeriodicWorkRequestBuilder<ExpiryCheckWorker>(
                    24, TimeUnit.HOURS
                ).build()

                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    workRequest
                )
            } catch (ignored: Exception) {
                // Ignore initialization failures in background restricted environments
            }
        }
    }
}
