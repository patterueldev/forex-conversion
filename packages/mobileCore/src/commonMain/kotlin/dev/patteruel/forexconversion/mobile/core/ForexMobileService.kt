package dev.patteruel.forexconversion.mobile.core

import dev.patteruel.forexconversion.ForexService
import dev.patteruel.forexconversion.api.ForexAPI
import dev.patteruel.forexconversion.api.ApiConfiguration
import dev.patteruel.forexconversion.api.NetworkChecker
import dev.patteruel.forexconversion.mobile.core.data.OfflineDatabase
import dev.patteruel.forexconversion.mobile.core.data.RateEntity
import dev.patteruel.forexconversion.mobile.core.data.createDatabase
import dev.patteruel.forexconversion.mobile.core.data.getCurrentTimeMillis
import dev.patteruel.forexconversion.mobile.core.models.CachedRate
import dev.patteruel.forexconversion.models.service.CalculateRequest
import dev.patteruel.forexconversion.models.api.ConvertRequest
import dev.patteruel.forexconversion.models.Converted
import dev.patteruel.forexconversion.models.Currency
import dev.patteruel.forexconversion.models.Rate
import dev.patteruel.forexconversion.models.api.RateRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForexMobileService {
    private val config: ApiConfiguration by lazy {
        ApiConfiguration()
    }
    private val forexAPI: ForexAPI by lazy { ForexAPI(config) }
    private val networkChecker: NetworkChecker by lazy { NetworkChecker() }
    private val forexService: ForexService by lazy { ForexService() }
    private val offlineDatabase: OfflineDatabase by lazy { createDatabase() }

    private val scope = CoroutineScope(Dispatchers.Default)

    fun getBaseURL(): String {
        return config.baseUrl
    }

    fun setBaseURL(url: String) {
        config.baseUrl = url
    }

    fun toggleSimulatedNetwork(shouldSimulate: Boolean) {
        networkChecker.simulatedOffline = shouldSimulate
    }

    fun isSimulatedOffline(): Boolean {
        return networkChecker.simulatedOffline
    }

    // Helper functions to access Currency enum values (for Swift interop)
    fun getCurrencyUSD(): Currency = Currency.USD
    fun getCurrencyPHP(): Currency = Currency.PHP

    // This is called on app startup - fetches latest rate ONLY if cache is empty
    // Has fallback logic to ensure the database always has a rate
    suspend fun fetchOnStartUpOnce() {
        // First check if we already have a cached rate
        val cachedRate = offlineDatabase.rateDao().getRate(Currency.USD.code, Currency.PHP.code)
        if (cachedRate != null) {
            println("Rate already cached, skipping fetch on startup")
            return
        }

        // No cached rate yet, fetch from API
        try {
            // fetch latest rate for USD to PHP
            val request = RateRequest(
                fromCurrency = Currency.USD,
                toCurrency = Currency.PHP
            )
            val response = forexAPI.fetchRate(request)
            // store it in the offline database
            val rateEntity = RateEntity(
                fromCurrency = response.rate.fromCurrency.code,
                toCurrency = response.rate.toCurrency.code,
                amount = response.rate.amount,
                updatedAt = getCurrentTimeMillis()
            )
            offlineDatabase.rateDao().upsert(rateEntity)
            println("Rate ${rateEntity.fromCurrency} has been successfully updated")
        } catch (e: Exception) {
            println("Failed to fetch latest rate: ${e.message}")
            // Fallback to hardcoded rate: 1 USD = 50 PHP
            val fallbackRate = RateEntity(
                fromCurrency = Currency.USD.code,
                toCurrency = Currency.PHP.code,
                amount = 50.0,
                updatedAt = getCurrentTimeMillis()
            )
            offlineDatabase.rateDao().upsert(fallbackRate)
            println("Using fallback rate: ${fallbackRate.amount} ${fallbackRate.toCurrency} per ${fallbackRate.fromCurrency}")
        }
    }

    // This is called when the user manually taps the "Fetch & Store Latest Rate" button
    // No fallback logic - if it fails, we just don't update the cache
    suspend fun fetchAndStoreLatestRate() {
        try {
            // fetch latest rate for USD to PHP
            val request = RateRequest(
                fromCurrency = Currency.USD,
                toCurrency = Currency.PHP
            )
            val response = forexAPI.fetchRate(request)
            // store it in the offline database
            val rateEntity = RateEntity(
                fromCurrency = response.rate.fromCurrency.code,
                toCurrency = response.rate.toCurrency.code,
                amount = response.rate.amount,
                updatedAt = getCurrentTimeMillis()
            )
            offlineDatabase.rateDao().upsert(rateEntity)
            println("Rate ${rateEntity.fromCurrency} has been successfully updated")
        } catch (e: Exception) {
            println("Failed to fetch latest rate: ${e.message}")
            // No fallback - let the existing cached rate remain
        }
    }

    // Returns the cached rate and its last update timestamp as a formatted string
    @Throws(IllegalStateException::class)
    suspend fun getCachedRate(from: Currency, to: Currency): CachedRate {
        val rateEntity = offlineDatabase.rateDao().getRate(from.code, to.code)
            ?: throw IllegalStateException("No cached rate found for $from to $to")
        return CachedRate(
            rate = Rate(
                fromCurrency = from,
                toCurrency = to,
                amount = rateEntity.amount
            ),
            formattedUpdatedAt = DateRenderer.format(rateEntity.updatedAt)
        )
    }

    // This is called when the user tries to convert currency
    suspend fun convertCurrency(amount: Double, from: Currency, to: Currency): Converted {
        try {
            val isNetworkAvailable = networkChecker.isNetworkAvailable()
            if (!isNetworkAvailable) {
                // If we're offline, get the cached rate from the database
                // Then use the proprietary conversion formula to calculate the converted amount
                val cachedRateResult = getCachedRate(from, to)
                val calculated = forexService.calculate(
                    request = CalculateRequest(
                        rate = cachedRateResult.rate,
                        amount = amount,
                    )
                )
                return calculated.result
            }

            // Attempt to convert it from the server
            val request = ConvertRequest(
                fromCurrency = from,
                toCurrency = to,
                amount = amount
            )
            val response = forexAPI.convert(request)
            return response.converted
        } catch (e: Exception) {
            // handle error, e.g. show a message to the user
            throw e
        }
    }
}