package dev.patteruel.forexconversion.mobile.core.models

import dev.patteruel.forexconversion.models.Rate

data class CachedRate(
    val rate: Rate,
    val formattedUpdatedAt: String
)
