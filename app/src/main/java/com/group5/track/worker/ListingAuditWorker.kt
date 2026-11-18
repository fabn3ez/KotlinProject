package com.group5.track.worker

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.group5.track.data.local.database.AppDatabase
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class ListingAuditWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val staleDays = inputData.getInt(KEY_STALE_DAYS, DEFAULT_STALE_DAYS)
            val cutoff = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(staleDays.toLong())

            val dao = AppDatabase.getDatabase(applicationContext).propertyDao()
            val staleListings = dao.getAvailableProperties()
                .first()
                .filter { it.createdAt <= cutoff }

            if (staleListings.isNotEmpty()) {
                showNotification(staleListings.size, staleListings.first().title)
            }
            Result.success()
        } catch (_: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(count: Int, firstTitle: String) {
        val context = applicationContext
        createChannel(context)

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) &&
            (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED)
        ) return

        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName) ?: Intent()
        val pendingIntent = PendingIntent.getActivity(
            context, 0, launchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Listing needs attention")
            .setContentText("$count listing(s) have been available too long. Update availability or review pricing.")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "$count listing(s) have been available too long, e.g. \"$firstTitle\". Update availability or review pricing."
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Property alerts",
                NotificationManager.IMPORTANCE_HIGH,
            )
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "property_alerts"
        const val KEY_STALE_DAYS = "stale_days"
        const val DEFAULT_STALE_DAYS = 14
        private const val NOTIFICATION_ID = 1001
    }
}