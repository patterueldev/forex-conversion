package dev.patteruel.forexconversion.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlinx.serialization.Serializable

@OptIn(ExperimentalJsExport::class)
@JsExport
@Serializable
data class Converted (
    val rate: Rate,
    val originalAmount: Double,
    val convertedAmount: Double
)