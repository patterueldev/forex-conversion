package dev.patteruel.forexconversion.sharedui.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
data class ConversionResult(
    val status: Status,
    val fromCurrency: String,
    val toCurrency: String,
    val inputAmount: Double,
    val convertedAmount: Double
)

@OptIn(ExperimentalJsExport::class)
@JsExport
enum class Status {
    ONLINE, OFFLINE
}
