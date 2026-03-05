package dev.patteruel.forexconversion.android

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.patteruel.forexconversion.sharedui.models.ConversionResult
import dev.patteruel.forexconversion.sharedui.models.Status
import dev.patteruel.forexconversion.sharedui.navigation.AndroidResultScreenNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineDemoScreen(viewModel: OfflineDemoViewModel, navigator: AndroidResultScreenNavigator? = null) {
    val simulateOffline = viewModel.simulateOffline.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()
    val latestStoredRate = viewModel.latestStoredRate.collectAsState()
    val rateLastUpdatedTime = viewModel.rateLastUpdatedTime.collectAsState()
    val convertedAmount = viewModel.convertedAmount.collectAsState()
    val amountText = viewModel.amountText.collectAsState()
    val showingBaseURLModal = viewModel.showingBaseURLModal.collectAsState()
    val errorMessage = viewModel.errorMessage.collectAsState()
    val lastConversionResult = viewModel.lastConversionResult.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchOnStartUpOnce()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Offline/Online Demo") },
                actions = {
                    IconButton(onClick = { viewModel.toggleBaseURLModal(true) }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    SectionHeader("Mode")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Simulate Offline Mode", modifier = Modifier.weight(1f))
                        Switch(
                            checked = simulateOffline.value,
                            onCheckedChange = { viewModel.updateSimulateOffline(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SectionHeader("Currencies")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                    ) {
                        Text("From")
                        Text("USD", style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Normal
                        ))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                    ) {
                        Text("To")
                        Text("PHP", style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Normal
                        ))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SectionHeader("Amount")
                    TextField(
                        value = amountText.value,
                        onValueChange = { viewModel.updateAmountText(it) },
                        label = { Text("Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SectionHeader("Rates")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                    ) {
                        Text("Latest Cached Rate:")
                        Text(latestStoredRate.value)
                    }

                    rateLastUpdatedTime.value?.let { updatedTime ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                        ) {
                            Text("Updated:", style = MaterialTheme.typography.labelSmall)
                            Text(
                                updatedTime,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    // Display error message if available
                    errorMessage.value?.let { error ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Error: $error",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(8.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.fetchLatestRateAndSave() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Fetch & Store Latest Rate")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.convert() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Convert")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SectionHeader("Output")
                    Text(
                        convertedAmount.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable(enabled = lastConversionResult.value != null) {
                                lastConversionResult.value?.let { converted ->
                                    val status = if (simulateOffline.value) Status.OFFLINE else Status.ONLINE
                                    val result = ConversionResult(
                                        status = status,
                                        fromCurrency = "USD",
                                        toCurrency = "PHP",
                                        inputAmount = converted.originalAmount,
                                        convertedAmount = converted.convertedAmount
                                    )
                                    navigator?.navigate(result)
                                }
                            },
                        color = if (lastConversionResult.value != null) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace
                    )
                }

                if (isLoading.value) {
                    LoadingOverlay()
                }

                if (showingBaseURLModal.value) {
                    SettingsDialog(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Processing...")
            }
        }
    }
}
