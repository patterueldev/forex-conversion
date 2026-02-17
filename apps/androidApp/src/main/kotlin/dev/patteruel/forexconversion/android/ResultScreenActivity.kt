package dev.patteruel.forexconversion.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import dev.patteruel.forexconversion.sharedui.models.ConversionResult
import dev.patteruel.forexconversion.sharedui.models.Status
import dev.patteruel.forexconversion.sharedui.ui.ResultScreen

class ResultScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Extract data from intent extras
        val status = when (intent.getStringExtra("status")) {
            Status.ONLINE.name -> Status.ONLINE
            Status.OFFLINE.name -> Status.OFFLINE
            else -> Status.ONLINE
        }
        val fromCurrency = intent.getStringExtra("fromCurrency") ?: "USD"
        val toCurrency = intent.getStringExtra("toCurrency") ?: "PHP"
        val inputAmount = intent.getDoubleExtra("inputAmount", 0.0)
        val convertedAmount = intent.getDoubleExtra("convertedAmount", 0.0)

        val result = ConversionResult(
            status = status,
            fromCurrency = fromCurrency,
            toCurrency = toCurrency,
            inputAmount = inputAmount,
            convertedAmount = convertedAmount
        )

        setContent {
            ForexConversionTheme {
                ResultScreen(result = result)
            }
        }
    }
}

@Composable
fun ResultScreenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
