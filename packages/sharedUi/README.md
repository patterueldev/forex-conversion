# SharedUI Module

A Compose Multiplatform module providing reusable UI components for the Forex Conversion app.

## Features

- **ResultScreen**: A composable screen displaying forex conversion results with platform-specific navigation
- **Cross-Platform**: Works seamlessly on Android, iOS, and Web platforms
- **Material Design 3**: Uses Material 3 design system for consistent styling
- **Mock Data**: Comes with built-in mock data for testing and preview

## Components

### ResultScreen Composable

Displays a forex conversion result with the following fields:
- **Status**: Online/Offline indicator
- **From**: Source currency code (e.g., USD)
- **To**: Target currency code (e.g., PHP)
- **Amount**: Input amount entered by user
- **Result**: Converted amount (formatted to 2 decimal places)

```kotlin
@Composable
fun ResultScreen(
    result: ConversionResult,
    modifier: Modifier = Modifier
)
```

### ConversionResult Data Class

```kotlin
data class ConversionResult(
    val status: Status,
    val fromCurrency: String,
    val toCurrency: String,
    val inputAmount: Double,
    val convertedAmount: Double
)

enum class Status {
    ONLINE, OFFLINE
}
```

### ResultScreenNavigator Interface

Platform-independent navigation interface with platform-specific implementations:

```kotlin
interface ResultScreenNavigator {
    fun navigate(result: ConversionResult)
}
```

#### Platform Implementations

**Android**: `AndroidResultScreenNavigator`
```kotlin
val navigator = AndroidResultScreenNavigator(context)
navigator.navigate(result)
```

**iOS**: `IosResultScreenNavigator`
```kotlin
val navigator = IosResultScreenNavigator()
navigator.navigate(result)
```

**Web**: `WebResultScreenNavigator`
```kotlin
val navigator = WebResultScreenNavigator()
navigator.navigate(result)
```

## Usage Example

```kotlin
import dev.patteruel.forexconversion.sharedui.examples.createMockConversionResult
import dev.patteruel.forexconversion.sharedui.ui.ResultScreen

@Composable
fun MyScreen() {
    val result = createMockConversionResult()
    ResultScreen(
        result = result,
        modifier = Modifier.fillMaxSize()
    )
}
```

## Build Status

✅ Android: Verified with `assembleDebug`
✅ iOS: Verified with `linkDebugFrameworkIosArm64`
✅ Web: Verified with `build`

## Project Structure

```
packages/sharedUi/
├── src/
│   ├── commonMain/
│   │   └── kotlin/dev/patteruel/forexconversion/sharedui/
│   │       ├── models/
│   │       │   └── ConversionResult.kt
│   │       ├── navigation/
│   │       │   └── ResultScreenNavigator.kt
│   │       ├── ui/
│   │       │   └── ResultScreen.kt
│   │       └── examples/
│   │           └── Usage.kt
│   ├── androidMain/
│   │   └── kotlin/dev/patteruel/forexconversion/sharedui/navigation/
│   │       └── ResultScreenNavigator.android.kt
│   ├── iosMain/
│   │   └── kotlin/dev/patteruel/forexconversion/sharedui/navigation/
│   │       └── ResultScreenNavigator.ios.kt
│   └── jsMain/
│       └── kotlin/dev/patteruel/forexconversion/sharedui/navigation/
│           └── ResultScreenNavigator.js.kt
└── build.gradle.kts
```

## Dependencies

- Compose Multiplatform: 1.10.0
- Kotlin Multiplatform: 2.3.0
- Material3: 1.10.0-alpha05

## Testing

For preview and testing:

```kotlin
@Preview
@Composable
fun ResultScreenPreview() {
    ResultScreen(
        result = ConversionResult(
            status = Status.ONLINE,
            fromCurrency = "USD",
            toCurrency = "PHP",
            inputAmount = 100.0,
            convertedAmount = 5550.75
        )
    )
}
```
