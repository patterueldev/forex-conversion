package dev.patteruel.forexconversion.sharedui.navigation

import androidx.compose.ui.window.ComposeUIViewController
import dev.patteruel.forexconversion.sharedui.models.ConversionResult
import dev.patteruel.forexconversion.sharedui.ui.ResultScreen
import platform.UIKit.UINavigationController
import platform.UIKit.UIViewController

class IosResultScreenNavigator {
    fun navigate(result: ConversionResult, parentViewController: UIViewController) {
        val vc = ComposeUIViewController({
            ResultScreen(result = result)
        })
        // Present the view controller (this is a simplified example, you may want to handle this differently)
        if (parentViewController is UINavigationController) {
            parentViewController.pushViewController(vc, animated = true)
        } else {
            parentViewController.presentViewController(vc, animated = true, completion = null)
        }
    }
}