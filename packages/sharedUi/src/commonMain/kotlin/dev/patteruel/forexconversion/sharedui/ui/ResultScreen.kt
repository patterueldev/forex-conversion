package dev.patteruel.forexconversion.sharedui.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.patteruel.forexconversion.sharedui.models.ConversionResult
import dev.patteruel.forexconversion.sharedui.models.Status

@Composable
fun ResultScreen(
    result: ConversionResult,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Conversion Result",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        ResultField(label = "Status", value = result.status.name)
        Spacer(modifier = Modifier.size(16.dp))

        ResultField(label = "From", value = result.fromCurrency)
        Spacer(modifier = Modifier.size(16.dp))

        ResultField(label = "To", value = result.toCurrency)
        Spacer(modifier = Modifier.size(16.dp))

        ResultField(label = "Amount", value = result.inputAmount.toString())
        Spacer(modifier = Modifier.size(16.dp))

        ResultField(label = "Result", value = formatAmount(result.convertedAmount))
    }
}

@Composable
private fun ResultField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatAmount(amount: Double): String {
    val rounded = (amount * 100).toLong() / 100.0
    return rounded.toString()
}
