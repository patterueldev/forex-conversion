package dev.patteruel.forexconversion.android

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp

@Composable
fun SettingsDialog(viewModel: OfflineDemoViewModel) {
    val baseURL = viewModel.baseURL.collectAsState()

    AlertDialog(
        onDismissRequest = { viewModel.toggleBaseURLModal(false) },
        title = { Text("Settings") },
        text = {
            Column {
                Text("Base URL", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = baseURL.value,
                    onValueChange = { viewModel.updateBaseURL(it) },
                    label = { Text("Base URL") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.resetBaseURL() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset to Default")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { viewModel.toggleBaseURLModal(false) }) {
                Text("Done")
            }
        }
    )
}
