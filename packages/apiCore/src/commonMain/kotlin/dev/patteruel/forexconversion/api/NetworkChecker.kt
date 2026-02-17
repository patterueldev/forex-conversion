package dev.patteruel.forexconversion.api

class NetworkChecker {
    var simulatedOffline: Boolean = false

    suspend fun isNetworkAvailable(): Boolean {
        // TODO: implement reachability logic; but for now, do a simulated check
        return !simulatedOffline
    }
}
