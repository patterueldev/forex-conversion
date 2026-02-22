package dev.patteruel.forexconversion.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

/**
 * Minimal Unirate API client to fetch conversions from /api/convert.
 * Adds curl-style request logging and logs raw response bodies to help debugging deserialization issues.
 */
class UnirateAPI(private val client: HttpClient, private val base: String = "https://api.unirateapi.com/api") {
    private val apiKey: String? = System.getenv("UNIRATE_API_KEY")
    private val logger = LoggerFactory.getLogger(javaClass)

    private fun endpoint(path: String) = "$base/${path.trimStart('/') }"

    suspend fun rate(from: String, to: String): UnirateResponse {
        require(!apiKey.isNullOrBlank()) { "UNIRATE_API_KEY environment variable is not set" }

        val url = endpoint("rates")

        val curl = "curl -G '" + url + "?api_key=" + apiKey + "&from=" + from + "&to=" + to + "&format=json' -H 'accept: application/json'"
        logger.info("Unirate API request (curl): {}", curl)

        val responseCall = client.get(url) {
            parameter("api_key", apiKey)
            parameter("from", from)
            parameter("to", to)
            parameter("format", "json")
            header("accept", "application/json")
        }

        val bodyText = responseCall.bodyAsText()
        logger.info("Unirate API response status={}, body={}", responseCall.status, bodyText)

        try {
            return Json { ignoreUnknownKeys = true }.decodeFromString(bodyText)
        } catch (e: Exception) {
            logger.error("Failed to deserialize UnirateResponse from body; body follows:\n{}", bodyText)
            throw e
        }
    }
}
