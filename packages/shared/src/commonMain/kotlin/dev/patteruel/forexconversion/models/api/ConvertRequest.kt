package dev.patteruel.forexconversion.models.api

import dev.patteruel.forexconversion.models.Currency
import kotlinx.serialization.Serializable

@Serializable
data class ConvertRequest(
    val amount: Double,
    val fromCurrency: Currency,
    val toCurrency: Currency
)

