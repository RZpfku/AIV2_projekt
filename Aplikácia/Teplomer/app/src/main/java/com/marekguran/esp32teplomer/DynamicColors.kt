package com.marekguran.esp32teplomer

import android.app.Application
import com.google.android.material.color.DynamicColors

class DynamicColors : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}