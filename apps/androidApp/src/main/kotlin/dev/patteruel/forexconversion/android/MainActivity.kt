package dev.patteruel.forexconversion.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import dev.patteruel.forexconversion.mobile.core.ForexMobileService
import dev.patteruel.forexconversion.sharedui.navigation.AndroidResultScreenNavigator

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val forexService = ForexMobileService()
        val adapter = ForexServiceAdapter(forexService)
        val viewModel = OfflineDemoViewModel(adapter)
        val androidNavigator = AndroidResultScreenNavigator(this)

        setContent {
            ForexConversionTheme {
                OfflineDemoScreen(viewModel = viewModel, navigator = androidNavigator)
            }
        }
    }
}

@Composable
fun ForexConversionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
