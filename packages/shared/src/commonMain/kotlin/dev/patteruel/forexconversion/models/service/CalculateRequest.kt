package dev.patteruel.forexconversion.models.service

import dev.patteruel.forexconversion.models.Rate
import kotlinx.serialization.Serializable

@Serializable
data class CalculateRequest(
    val rate: Rate,
    val amount: Double
)