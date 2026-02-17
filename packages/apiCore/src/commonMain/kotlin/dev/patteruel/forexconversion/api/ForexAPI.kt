package dev.patteruel.forexconversion.api

import dev.patteruel.forexconversion.models.api.ConvertRequest
import dev.patteruel.forexconversion.models.api.ConvertResponse
import dev.patteruel.forexconversion.models.api.RateRequest
import dev.patteruel.forexconversion.models.api.RateResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ForexAPI(private val config: ApiConfiguration) {
    private val client: HttpClient = HttpClient {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
    }

    private fun endpoint(path: String): String {
        println("Constructing endpoint URL with base ${config.baseUrl} and path $path")
        return "${config.baseUrl}/$path"
    }

    suspend fun fetchRate(request: RateRequest): RateResponse {
        return client.post(endpoint("rate")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun convert(request: ConvertRequest): ConvertResponse {
        return client.post(endpoint("convert")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
