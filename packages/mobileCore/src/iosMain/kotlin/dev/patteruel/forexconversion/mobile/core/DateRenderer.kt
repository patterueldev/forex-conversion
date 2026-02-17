package dev.patteruel.forexconversion.mobile.core

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterMediumStyle
import platform.Foundation.timeIntervalSince1970

actual object DateRenderer {
    actual fun format(timestampMillis: Long): String {
        val date = NSDate(timeIntervalSinceReferenceDate = timestampMillis / 1000.0 - 978307200.0)
        val formatter = NSDateFormatter().apply {
            dateStyle = NSDateFormatterMediumStyle
            timeStyle = NSDateFormatterMediumStyle
        }
        return formatter.stringFromDate(date) ?: ""
    }
}
