package dev.patteruel.forexconversion.sharedui.navigation

import dev.patteruel.forexconversion.sharedui.models.ConversionResult

/**
 * Web-specific implementation of ResultScreenNavigator.
 * At this commit (add461c), we don't have expect/actual yet.
 * This is just a placeholder class for web platform.
 */
class ResultScreenNavigator {
    fun navigate(result: ConversionResult) {
        // For now, just log - actual navigation is handled by webUI module
        console.log("Web navigation to result screen: $result")
    }
}
