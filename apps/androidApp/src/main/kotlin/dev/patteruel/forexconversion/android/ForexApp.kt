package dev.patteruel.forexconversion.android

import android.app.Application
import dev.patteruel.forexconversion.mobile.core.data.setDatabaseContext
import timber.log.Timber

class ForexApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize database context for mobileCore
        setDatabaseContext(this)
        Timber.d("Database context initialized")
        
        // Initialize Timber logging
        Timber.plant(Timber.DebugTree())
        
        Timber.d("ForexApp initialized")
    }
}
