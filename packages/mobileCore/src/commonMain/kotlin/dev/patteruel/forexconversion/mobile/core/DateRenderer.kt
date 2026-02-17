package dev.patteruel.forexconversion.mobile.core

// Common interface for platform-specific date rendering
expect object DateRenderer {
    fun format(timestampMillis: Long): String
}
