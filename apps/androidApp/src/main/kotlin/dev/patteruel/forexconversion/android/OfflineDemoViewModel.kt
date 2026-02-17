package dev.patteruel.forexconversion.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class OfflineDemoViewModel(private val forexService: ForexServiceAdapter) : ViewModel() {
    
    private val _simulateOffline = MutableStateFlow(false)
    val simulateOffline: StateFlow<Boolean> = _simulateOffline

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _latestStoredRate = MutableStateFlow("-")
    val latestStoredRate: StateFlow<String> = _latestStoredRate

    private val _rateLastUpdatedTime = MutableStateFlow<String?>(null)
    val rateLastUpdatedTime: StateFlow<String?> = _rateLastUpdatedTime

    private val _convertedAmount = MutableStateFlow("-")
    val convertedAmount: StateFlow<String> = _convertedAmount

    private val _amountText = MutableStateFlow("1.0")
    val amountText: StateFlow<String> = _amountText

    private val _baseURL = MutableStateFlow(forexService.getBaseURL())
    val baseURL: StateFlow<String> = _baseURL

    private val _showingBaseURLModal = MutableStateFlow(false)
    val showingBaseURLModal: StateFlow<Boolean> = _showingBaseURLModal

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private var hasCalledFetchOnStartUp = false

    init {
        Timber.d("OfflineDemoViewModel initialized")
    }

    fun updateSimulateOffline(value: Boolean) {
        Timber.d("updateSimulateOffline: %s", value)
        _simulateOffline.value = value
        forexService.toggleSimulatedNetwork(shouldSimulate = value)
    }

    fun updateAmountText(value: String) {
        Timber.d("updateAmountText: %s", value)
        _amountText.value = value
    }

    fun updateBaseURL(value: String) {
        Timber.d("updateBaseURL: %s", value)
        _baseURL.value = value
        if (value.isNotEmpty()) {
            forexService.setBaseURL(url = value)
        }
    }

    fun toggleBaseURLModal(show: Boolean) {
        Timber.d("toggleBaseURLModal: %s", show)
        _showingBaseURLModal.value = show
    }

    fun fetchOnStartUpOnce() {
        forexService.setBaseURL("http://192.168.254.101:8080") // one-off
        if (hasCalledFetchOnStartUp) {
            Timber.d("fetchOnStartUpOnce: Already called, skipping")
            return
        }
        hasCalledFetchOnStartUp = true
        Timber.d("fetchOnStartUpOnce: Starting initial load")

        viewModelScope.launch {
            try {
                _isLoading.value = true
                Timber.d("fetchOnStartUpOnce: Calling service.fetchOnStartUpOnce()")
                forexService.fetchOnStartUpOnce()
                Timber.d("fetchOnStartUpOnce: Service call completed, loading stored rate")
                loadStoredRate()
                _errorMessage.value = null
                Timber.d("fetchOnStartUpOnce: Initial load successful")
            } catch (e: Exception) {
                Timber.e(e, "fetchOnStartUpOnce: Error during initial load")
                _latestStoredRate.value = "error"
                _errorMessage.value = e.message ?: "Unknown error"
            }
            _isLoading.value = false
        }
    }

    fun fetchLatestRateAndSave() {
        Timber.d("fetchLatestRateAndSave: Starting")
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Timber.d("fetchLatestRateAndSave: Calling service.fetchAndStoreLatestRate()")
                forexService.fetchAndStoreLatestRate()
                Timber.d("fetchLatestRateAndSave: Service call completed, loading stored rate")
                loadStoredRate()
                _errorMessage.value = null
                Timber.d("fetchLatestRateAndSave: Completed successfully")
            } catch (e: Exception) {
                Timber.e(e, "fetchLatestRateAndSave: Error fetching rate")
                _latestStoredRate.value = "error"
                _errorMessage.value = e.message ?: "Unknown error"
            }
            _isLoading.value = false
        }
    }

    private suspend fun loadStoredRate() {
        try {
            Timber.d("loadStoredRate: Starting")
            val (rate, updatedAtString) = forexService.getCachedRate(from = "USD", to = "PHP")
            _latestStoredRate.value = rate
            _rateLastUpdatedTime.value = updatedAtString
            _errorMessage.value = null
            Timber.d("loadStoredRate: Successfully loaded - rate: %s, updated: %s", rate, updatedAtString)
        } catch (e: Exception) {
            Timber.e(e, "loadStoredRate: Error loading cached rate")
            _latestStoredRate.value = "error"
            _rateLastUpdatedTime.value = null
            _errorMessage.value = e.message ?: "Unknown error"
        }
    }

    fun convert() {
        Timber.d("convert: Starting conversion")
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val amount = _amountText.value.toDoubleOrNull() ?: 0.0
                Timber.d("convert: Converting %f USD to PHP (offline: %s)", amount, _simulateOffline.value)
                val converted = forexService.convertCurrency(amount = amount, from = "USD", to = "PHP")
                val isOfflineStr = if (_simulateOffline.value) "offline" else "online"
                _convertedAmount.value = "$isOfflineStr | ${converted.originalAmount} ${converted.rate.fromCurrency} -> ${String.format("%.2f", converted.convertedAmount)} ${converted.rate.toCurrency} @ rate=${String.format("%.2f", converted.rate.amount)}"
                _errorMessage.value = null
                Timber.d("convert: Conversion successful: %s", _convertedAmount.value)
            } catch (e: Exception) {
                Timber.e(e, "convert: Error during conversion")
                _convertedAmount.value = "error"
                _errorMessage.value = e.message ?: "Unknown error"
            }
            _isLoading.value = false
        }
    }

    fun resetBaseURL() {
        Timber.d("resetBaseURL: Resetting")
        _baseURL.value = forexService.getBaseURL()
    }
}
