package dev.patteruel.forexconversion.models.api

import dev.patteruel.forexconversion.models.Currency
import kotlinx.serialization.Serializable

@Serializable
data class RateRequest(
    val fromCurrency: Currency,
    val toCurrency: Currency
)


