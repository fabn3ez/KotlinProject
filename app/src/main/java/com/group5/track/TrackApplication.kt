package com.group5.track

import android.app.Application
import com.group5.track.data.local.database.AppDatabase
import com.group5.track.data.repository.PropertyRepository

class TrackApplication : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    val repository: PropertyRepository by lazy {
        PropertyRepository(database.propertyDao())
    }
}
