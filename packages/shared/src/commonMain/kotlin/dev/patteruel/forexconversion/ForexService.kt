package dev.patteruel.forexconversion

import dev.patteruel.forexconversion.models.service.CalculateRequest
import dev.patteruel.forexconversion.models.service.CalculateResponse
import dev.patteruel.forexconversion.models.Converted

class ForexService {
    fun calculate(request: CalculateRequest): CalculateResponse {
        // Extract the first numeric value from the returned string (handles formats like "50.25", "1,234.56", "1 USD = 50.25 PHP", etc.)
        val numberRegex = Regex("-?\\d[\\d,]*\\.?\\d*")
        val match = numberRegex.find(request.rate.amount.toString()) // simulating complexity with Amounts and Floating values
        val rate = match?.value?.replace(",", "")?.toDoubleOrNull() ?: 0.0

        val convertedAmount = request.amount * rate
        return CalculateResponse(
            result = Converted(
                rate = request.rate,
                originalAmount = request.amount,
                convertedAmount = convertedAmount,
            )
        )
    }
}
