package dev.patteruel.forexconversion.sharedui.navigation

import androidx.activity.ComponentActivity
import android.content.Intent
import dev.patteruel.forexconversion.sharedui.models.ConversionResult

class AndroidResultScreenNavigator(
    private val activity: ComponentActivity
) {
    fun navigate(result: ConversionResult) {
        try {
            // Dynamically load the ResultScreenActivity class
            val resultActivityClass = Class.forName(
                "dev.patteruel.forexconversion.android.ResultScreenActivity"
            ) as Class<*>
            
            val intent = Intent(activity, resultActivityClass).apply {
                putExtra("status", result.status.name)
                putExtra("fromCurrency", result.fromCurrency)
                putExtra("toCurrency", result.toCurrency)
                putExtra("inputAmount", result.inputAmount)
                putExtra("convertedAmount", result.convertedAmount)
            }
            activity.startActivity(intent)
        } catch (e: Exception) {
            // Handle error if ResultScreenActivity is not found
            e.printStackTrace()
        }
    }
}
