package dev.patteruel.forexconversion.webui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import dev.patteruel.forexconversion.sharedui.models.ConversionResult
import dev.patteruel.forexconversion.sharedui.models.Status
import dev.patteruel.forexconversion.sharedui.ui.ResultScreen
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement

// Global state for showing/hiding the ResultScreen
private var showResultScreen by mutableStateOf(false)
private var currentResult by mutableStateOf<ConversionResult?>(null)
private var composeContainer: HTMLElement? = null

/**
 * Main entry point for the Compose Web UI module.
 * Initializes ComposeViewport ONCE and exposes functions to control it.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Create a permanent container for Compose - initially hidden
    val container = document.createElement("div").apply {
        id = "compose-root"
        setAttribute("style", "position: fixed; top: 0; left: 0; width: 100%; height: 100%; z-index: 9999; display: none;")
    } as HTMLElement
    
    composeContainer = container
    document.body?.appendChild(container)
    
    // Initialize ComposeViewport ONCE with a composable that responds to state
    ComposeViewport(container) {
        ComposeApp()
    }
    
    // Expose functions to JavaScript
    window.asDynamic().renderResultScreen = { data: dynamic ->
        println("🔍 renderResultScreen called with data: $data")
        println("🔍 composeContainer exists: ${composeContainer != null}")
        
        currentResult = ConversionResult(
            status = if (data.status == "Online") Status.ONLINE else Status.OFFLINE,
            fromCurrency = data.fromCurrency as String,
            toCurrency = data.toCurrency as String,
            inputAmount = (data.inputAmount as Number).toDouble(),
            convertedAmount = (data.convertedAmount as Number).toDouble()
        )
        println("🔍 Created ConversionResult: $currentResult")
        println("🔍 showResultScreen BEFORE: $showResultScreen")
        
        showResultScreen = true
        
        println("🔍 showResultScreen AFTER: $showResultScreen")
        println("🔍 Attempting to show container...")
        
        // Show the container and force a repaint
        composeContainer?.apply {
            setAttribute("style", "position: fixed; top: 0; left: 0; width: 100%; height: 100%; z-index: 9999; display: block; background-color: white;")
            // Force browser repaint/reflow by reading layout property
            offsetHeight // Reading this property forces reflow
        }
        
        // Trigger window resize event to force Skiko to render
        kotlinx.browser.window.dispatchEvent(org.w3c.dom.events.Event("resize"))
        
        println("✅ Container display set to block")
        println("✅ Showing ResultScreen with data: ${currentResult}")
    }
    
    window.asDynamic().hideResultScreen = {
        showResultScreen = false
        // Hide the container
        composeContainer?.setAttribute("style", "position: fixed; top: 0; left: 0; width: 100%; height: 100%; z-index: 9999; display: none;")
        println("✅ Hiding ResultScreen")
    }
    
    println("✅ Compose Web UI initialized - window.renderResultScreen available")
}

/**
 * Root Compose application that manages the ResultScreen visibility
 */
@Composable
fun ComposeApp() {
    println("🎨 ComposeApp recomposing - showResultScreen=$showResultScreen, currentResult=$currentResult")
    
    if (showResultScreen && currentResult != null) {
        println("🎨 Rendering ResultScreen UI")
        // Full-screen overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Close button
            Button(
                onClick = { 
                    println("🔘 Close button clicked")
                    showResultScreen = false
                    // Hide the container
                    composeContainer?.setAttribute("style", "position: fixed; top: 0; left: 0; width: 100%; height: 100%; z-index: 9999; display: none;")
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Text("Close")
            }
            
            // The actual ResultScreen component
            ResultScreen(result = currentResult!!)
        }
    } else {
        println("🎨 Not rendering - showResultScreen=$showResultScreen, currentResult=$currentResult")
    }
}
