package dev.patteruel.forexconversion.mobile.core

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual object DateRenderer {
    actual fun format(timestampMillis: Long): String {
        val formatter = SimpleDateFormat("MMM d, yyyy, h:mm a", Locale.getDefault())
        return formatter.format(Date(timestampMillis))
    }
}
