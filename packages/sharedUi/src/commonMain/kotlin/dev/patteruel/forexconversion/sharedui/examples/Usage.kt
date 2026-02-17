package dev.patteruel.forexconversion.sharedui.examples

import dev.patteruel.forexconversion.sharedui.models.ConversionResult
import dev.patteruel.forexconversion.sharedui.models.Status
import dev.patteruel.forexconversion.sharedui.ui.ResultScreen

/**
 * Example usage of the ResultScreen module.
 *
 * Sample Data:
 * - Status: Online/Offline
 * - From: USD
 * - To: PHP
 * - Amount: Entered amount
 * - Result: Converted amount (formatted to 2 decimal places)
 */

// Sample conversion result data
fun createMockConversionResult(): ConversionResult {
    return ConversionResult(
        status = Status.ONLINE,
        fromCurrency = "USD",
        toCurrency = "PHP",
        inputAmount = 100.0,
        convertedAmount = 5550.75
    )
}

/**
 * Android Example:
 *
 * In your Android Activity:
 * ```
 * val navigator = AndroidResultScreenNavigator(this)
 * val result = createMockConversionResult()
 * navigator.navigate(result)
 * ```
 */

/**
 * iOS Example:
 *
 * In your iOS ViewController:
 * ```
 * let navigator = IosResultScreenNavigator()
 * let result = createMockConversionResult()
 * navigator.navigate(result: result)
 *
 * // To display the ResultScreen in SwiftUI or UIKit:
 * // Create a Compose-based view with ResultScreen(result: result)
 * ```
 */

/**
 * Web/React Example:
 *
 * In your React component:
 * ```
 * const navigator = WebResultScreenNavigator()
 * const result = createMockConversionResult()
 * navigator.navigate(result)
 *
 * // To display the ResultScreen in Composable:
 * // <ResultScreen result={result} />
 * ```
 */

/**
 * Composable Usage Example:
 *
 * In your Compose code:
 * ```
 * @Composable
 * fun MyScreen() {
 *     val result = createMockConversionResult()
 *     ResultScreen(
 *         result = result,
 *         modifier = Modifier.fillMaxSize()
 *     )
 * }
 * ```
 */
