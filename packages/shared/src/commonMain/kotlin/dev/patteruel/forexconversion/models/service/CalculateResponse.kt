package dev.patteruel.forexconversion.models.service

import dev.patteruel.forexconversion.models.Converted
import kotlinx.serialization.Serializable

@Serializable
data class CalculateResponse(
    val result: Converted,
)