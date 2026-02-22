package dev.patteruel.forexconversion

import dev.patteruel.forexconversion.api.UnirateAPI
import dev.patteruel.forexconversion.models.service.CalculateRequest
import dev.patteruel.forexconversion.models.service.CalculateResponse
import dev.patteruel.forexconversion.models.api.ConvertResponse
import dev.patteruel.forexconversion.models.api.ConvertRequest
import dev.patteruel.forexconversion.models.Rate
import dev.patteruel.forexconversion.models.api.RateRequest
import dev.patteruel.forexconversion.models.api.RateResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val allowedHosts = listOf("localhost", "thursday.local")
    val allowedPorts = listOf("5173")
    // Install CORS plugin
    install(CORS) {
        for (host in allowedHosts) {
            for (port in allowedPorts) {
                allowHost("$host:$port")
            }
        }
        allowHeader("Content-Type")
        allowHeader("Authorization")
        allowMethod(io.ktor.http.HttpMethod.Get)
        allowMethod(io.ktor.http.HttpMethod.Post)
        allowMethod(io.ktor.http.HttpMethod.Options)
        allowMethod(io.ktor.http.HttpMethod.Put)
        allowMethod(io.ktor.http.HttpMethod.Delete)
        allowCredentials = false
    }

    // Install server-side ContentNegotiation so call.receive/deserialization works
    install(ServerContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }

    // Create an HttpClient with ContentNegotiation so client.body<T>() works
    val client = HttpClient(CIO) {
        install(ClientContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val unirateAPI = UnirateAPI(client)
    val forexService = ForexService()

    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }

        // Use POST for /rate because clients commonly send JSON bodies with POST
        post("/rate") {
            val request: RateRequest = call.receive()
            val rate = unirateAPI.rate(request.fromCurrency.code, request.toCurrency.code)
            val response = RateResponse(
                rate = Rate(
                    fromCurrency = request.fromCurrency,
                    toCurrency = request.toCurrency,
                    amount = rate.rate
                )
            )
            call.respond(response)
        }

        post("/convert") {
            val request: ConvertRequest = call.receive()
            // get the rate from UnirateAPI
            val response = unirateAPI.rate(
                request.fromCurrency.code,
                request.toCurrency.code
            )

            // use our proprietary conversion logic to calculate the converted amount
            val result: CalculateResponse = forexService.calculate(
                request = CalculateRequest(
                    amount = request.amount,
                    rate = Rate(fromCurrency = request.fromCurrency, toCurrency = request.toCurrency, amount = response.rate)
                )
            )

            call.respond(
                ConvertResponse(
                    converted = result.result,
                )
            )
        }
    }
}