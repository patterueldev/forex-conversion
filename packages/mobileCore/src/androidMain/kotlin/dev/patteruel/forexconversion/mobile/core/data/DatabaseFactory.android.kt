package dev.patteruel.forexconversion.mobile.core.data

import android.content.Context
import androidx.room.Room

// This should be called with Android's Context
private var context: Context? = null

fun setDatabaseContext(appContext: Context) {
    context = appContext
}

actual fun createDatabase(): OfflineDatabase {
    val appContext = context ?: throw IllegalStateException("Database context not set. Call setDatabaseContext() first.")
    return Room.databaseBuilder(
        appContext,
        OfflineDatabase::class.java,
        "forex.db"
    ).fallbackToDestructiveMigration(dropAllTables = true)
        .build()
}
