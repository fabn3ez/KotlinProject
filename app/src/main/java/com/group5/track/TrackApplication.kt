package com.group5.track

import android.app.Application
import com.group5.track.data.local.database.AppDatabase
import com.group5.track.data.repository.PropertyRepository
import kotlinx.coroutines.flow.MutableStateFlow

class TrackApplication : Application() {

    // Prototype role switch: false = Tenant (browse only), true = Agent (manage listings)
    val isAgentMode = MutableStateFlow(false)

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    val repository: PropertyRepository by lazy {
        PropertyRepository(database.propertyDao())
    }
}