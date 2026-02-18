package dev.patteruel.forexconversion.webui

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import dev.patteruel.forexconversion.sharedui.models.ConversionResult
import dev.patteruel.forexconversion.sharedui.models.Status
import dev.patteruel.forexconversion.sharedui.ui.ResultScreen
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement

/**
 * Main entry point for the Compose Web UI module.
 * This exposes functions to the window object that external JavaScript/React apps can call.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Initialize: Expose our functions to the window object
    window.asDynamic().renderResultScreen = { data: dynamic ->
        renderResultScreen(
            ConversionResult(
                status = if (data.status == "Online") Status.ONLINE else Status.OFFLINE,
                fromCurrency = data.fromCurrency as String,
                toCurrency = data.toCurrency as String,
                inputAmount = (data.inputAmount as Number).toDouble(),
                convertedAmount = (data.convertedAmount as Number).toDouble()
            )
        )
    }
    
    println("✅ Compose Web UI initialized - window.renderResultScreen available")
}

/**
 * Renders the ResultScreen Compose component in a full-screen overlay.
 * This is the actual function that displays the shared UI component.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun renderResultScreen(result: ConversionResult) {
    // Get or create a container for the Compose viewport
    val containerId = "compose-result-container"
    var container = document.getElementById(containerId) as? HTMLElement
    
    if (container == null) {
        container = document.createElement("div").apply {
            id = containerId
            setAttribute("style", """
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                z-index: 9999;
                background: white;
            """.trimIndent())
            document.body?.appendChild(this)
        } as HTMLElement
    }
    
    // Clear previous content
    container.innerHTML = ""
    
    // Add close button container
    val closeButtonContainer = document.createElement("div").apply {
        setAttribute("style", """
            position: absolute;
            top: 16px;
            right: 16px;
            z-index: 10000;
        """.trimIndent())
        innerHTML = """
            <button style="
                padding: 8px 16px;
                background: #f44336;
                color: white;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 14px;
            ">Close</button>
        """.trimIndent()
    }
    
    container.appendChild(closeButtonContainer)
    
    // Add click handler to close button
    closeButtonContainer.firstElementChild?.addEventListener("click", {
        container?.remove()
    })
    
    // Render the Compose component
    ComposeViewport(container) {
        ResultScreen(result = result)
    }
    
    println("✅ ResultScreen rendered with data: $result")
}
