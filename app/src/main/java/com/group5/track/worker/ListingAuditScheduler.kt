package com.group5.track.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

object ListingAuditScheduler {

    fun schedulePeriodic(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val request = PeriodicWorkRequestBuilder<ListingAuditWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "listing_audit_periodic",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    // For the demo: 0 days means every available listing triggers the notification
    fun runNow(context: Context, staleDays: Int = 0) {
        val request = OneTimeWorkRequestBuilder<ListingAuditWorker>()
            .setInputData(workDataOf(ListingAuditWorker.KEY_STALE_DAYS to staleDays))
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "listing_audit_demo",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}