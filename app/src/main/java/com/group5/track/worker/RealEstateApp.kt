package com.group5.track.worker

import android.app.Application

class RealEstateApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ListingAuditScheduler.schedulePeriodic(this)
    }
}