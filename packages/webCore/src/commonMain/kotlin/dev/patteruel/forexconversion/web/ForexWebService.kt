package dev.patteruel.forexconversion.web

import dev.patteruel.forexconversion.ForexService
import dev.patteruel.forexconversion.api.ApiConfiguration
import dev.patteruel.forexconversion.api.ForexAPI
import dev.patteruel.forexconversion.api.NetworkChecker
import dev.patteruel.forexconversion.models.api.ConvertRequest
import dev.patteruel.forexconversion.models.api.RateRequest
import dev.patteruel.forexconversion.models.Converted
import dev.patteruel.forexconversion.models.Currency
import dev.patteruel.forexconversion.models.Rate
import dev.patteruel.forexconversion.models.service.CalculateRequest
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalJsExport::class)
@JsExport
class ForexWebService {
    // Use MainScope for JS which works in browser environments
    private val scope: CoroutineScope = MainScope()
    
    private val config: ApiConfiguration by lazy {
        ApiConfiguration()
    }
    private val forexAPI: ForexAPI by lazy { ForexAPI(config) }
    private val networkChecker: NetworkChecker by lazy { NetworkChecker() }
    private val forexService: ForexService by lazy { ForexService() }

    // In-memory cache for the latest rate
    private var cachedRate: Rate? = null

    fun getBaseURL(): String = config.baseUrl

    fun setBaseURL(url: String) {
        config.baseUrl = url
    }

    fun toggleSimulatedNetwork(shouldSimulate: Boolean) {
        networkChecker.simulatedOffline = shouldSimulate
    }

    fun isSimulatedOffline(): Boolean = networkChecker.simulatedOffline

    fun getCurrencyUSD(): Currency = Currency.USD
    fun getCurrencyPHP(): Currency = Currency.PHP

    fun fetchLatestRate(onSuccess: (Rate) -> Unit, onError: (String) -> Unit) {
        scope.launch {
            try {
                val request = RateRequest(
                    fromCurrency = Currency.USD,
                    toCurrency = Currency.PHP
                )
                val response = forexAPI.fetchRate(request)
                cachedRate = response.rate
                onSuccess(response.rate)
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun getCachedRate(): Rate? = cachedRate

    fun convertCurrency(amount: Double, from: Currency, to: Currency, onSuccess: (Converted) -> Unit, onError: (String) -> Unit) {
        scope.launch {
            try {
                val isNetworkAvailable = networkChecker.isNetworkAvailable()
                val result = if (!isNetworkAvailable) {
                    val rate = cachedRate
                        ?: throw IllegalStateException("No cached rate available while offline")
                    val calculated = forexService.calculate(
                        request = CalculateRequest(
                            rate = rate,
                            amount = amount,
                        )
                    )
                    calculated.result
                } else {
                    val request = ConvertRequest(
                        fromCurrency = from,
                        toCurrency = to,
                        amount = amount
                    )
                    val response = forexAPI.convert(request)
                    response.converted
                }
                onSuccess(result)
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error occurred")
            }
        }
    }
}
