package dev.patteruel.forexconversion.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlinx.serialization.Serializable

@OptIn(ExperimentalJsExport::class)
@JsExport
@Serializable
data class Rate (
    val fromCurrency: Currency,
    val toCurrency: Currency,
    val amount: Double
)

