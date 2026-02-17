package dev.patteruel.forexconversion

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform