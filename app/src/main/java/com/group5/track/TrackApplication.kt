package com.group5.track

import android.app.Application
import com.group5.track.data.local.database.AppDatabase
import com.group5.track.data.repository.CurrencyRepository
import com.group5.track.data.repository.PropertyRepository
import com.group5.track.worker.ListingAuditScheduler

class TrackApplication : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    val repository: PropertyRepository by lazy {
        PropertyRepository(database.propertyDao())
    }

    val currencyRepository: CurrencyRepository by lazy {
        CurrencyRepository(this)
    }

    override fun onCreate() {
        super.onCreate()
        ListingAuditScheduler.schedulePeriodic(this)
    }
}
