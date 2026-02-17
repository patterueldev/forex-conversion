package dev.patteruel.forexconversion.models.api

import dev.patteruel.forexconversion.models.Rate
import kotlinx.serialization.Serializable

@Serializable
data class RateResponse(
    val rate: Rate
)