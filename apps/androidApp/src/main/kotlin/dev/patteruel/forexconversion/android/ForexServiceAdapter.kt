package dev.patteruel.forexconversion.android

import dev.patteruel.forexconversion.mobile.core.ForexMobileService
import timber.log.Timber

class ForexServiceAdapter(private val service: ForexMobileService) {
    
    suspend fun fetchOnStartUpOnce() {
        try {
            Timber.d("fetchOnStartUpOnce: Starting initial fetch")
            service.fetchOnStartUpOnce()
            Timber.d("fetchOnStartUpOnce: Initial fetch completed successfully")
        } catch (e: Exception) {
            Timber.e(e, "fetchOnStartUpOnce: Error during initial fetch")
            throw e
        }
    }

    suspend fun fetchAndStoreLatestRate() {
        try {
            Timber.d("fetchAndStoreLatestRate: Fetching latest rate")
            service.fetchAndStoreLatestRate()
            Timber.d("fetchAndStoreLatestRate: Rate fetched and stored successfully")
        } catch (e: Exception) {
            Timber.e(e, "fetchAndStoreLatestRate: Error fetching rate")
            throw e
        }
    }

    fun getBaseURL(): String {
        val url = service.getBaseURL()
        Timber.d("getBaseURL: %s", url)
        return url
    }

    fun setBaseURL(url: String) {
        Timber.d("setBaseURL: Setting to %s", url)
        service.setBaseURL(url = url)
    }

    suspend fun getCachedRate(from: String, to: String): Pair<String, String> {
        try {
            Timber.d("getCachedRate: Fetching cached rate from %s to %s", from, to)
            val cachedRate = service.getCachedRate(from = service.getCurrencyUSD(), to = service.getCurrencyPHP())
            val result = Pair(cachedRate.rate.amount.toString(), cachedRate.formattedUpdatedAt)
            Timber.d("getCachedRate: Got rate = %s, updated at = %s", result.first, result.second)
            return result
        } catch (e: Exception) {
            Timber.e(e, "getCachedRate: Error fetching cached rate")
            throw e
        }
    }

    suspend fun convertCurrency(amount: Double, from: String, to: String): dev.patteruel.forexconversion.models.Converted {
        try {
            Timber.d("convertCurrency: Converting %f %s to %s", amount, from, to)
            val result = service.convertCurrency(amount = amount, from = service.getCurrencyUSD(), to = service.getCurrencyPHP())
            Timber.d("convertCurrency: Result = %f %s (using rate: %f)", result.convertedAmount, result.rate.toCurrency, result.rate.amount)
            return result
        } catch (e: Exception) {
            Timber.e(e, "convertCurrency: Error converting currency")
            throw e
        }
    }

    fun toggleSimulatedNetwork(shouldSimulate: Boolean) {
        Timber.d("toggleSimulatedNetwork: Setting to %s", shouldSimulate)
        service.toggleSimulatedNetwork(shouldSimulate = shouldSimulate)
    }

    fun isSimulatedOffline(): Boolean {
        val offline = service.isSimulatedOffline()
        Timber.d("isSimulatedOffline: %s", offline)
        return offline
    }
}

