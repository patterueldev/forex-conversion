package dev.patteruel.forexconversion.mobile.core.data

// Factory to create the OfflineDatabase instance.
// Platform modules (Android/iOS) provide the actual implementation.
expect fun createDatabase(): OfflineDatabase
