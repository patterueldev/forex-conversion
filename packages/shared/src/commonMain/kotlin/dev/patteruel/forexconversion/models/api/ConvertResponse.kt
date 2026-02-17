package dev.patteruel.forexconversion.models.api

import dev.patteruel.forexconversion.models.Converted
import kotlinx.serialization.Serializable

@Serializable
data class ConvertResponse(
    val converted: Converted,
)