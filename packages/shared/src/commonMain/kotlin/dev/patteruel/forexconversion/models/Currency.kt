package dev.patteruel.forexconversion.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlinx.serialization.Serializable

@OptIn(ExperimentalJsExport::class)
@JsExport
@Serializable
enum class Currency(val code: String) {
    USD("USD"),
    PHP("PHP"),
}

