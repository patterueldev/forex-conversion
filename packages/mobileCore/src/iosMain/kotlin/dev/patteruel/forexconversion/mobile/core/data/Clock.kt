package dev.patteruel.forexconversion.mobile.core.data

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun getCurrentTimeMillis(): Long {
    val now = NSDate()
    return (now.timeIntervalSince1970 * 1000).toLong()
}
