package dev.patteruel.forexconversion.api

import kotlinx.serialization.Serializable

@Serializable
data class UnirateResponse(
    val amount: Double,
    val base: String,
    val to: String,
    val rate: Double,
    val result: Double
)