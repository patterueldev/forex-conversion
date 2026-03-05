# Compose Web Integration: Success Documentation

## 🎉 Status: WORKING

**Date Proven:** 2026-02-18  
**Method:** Micro-frontend architecture with standalone JavaScript bundle

---

## What We Achieved

Successfully integrated Compose Multiplatform UI components into an existing React application, proving that **the same Compose UI code can run on iOS, Android, AND Web**.

### Shared Component
- **File:** `packages/sharedUi/src/commonMain/kotlin/.../ui/ResultScreen.kt`
- **Platforms:** iOS ✅ | Android ✅ | Web ✅
- **Lines of Code:** Single implementation, zero duplication

---

## Architecture Overview

### Module Structure

```
packages/
├── sharedUi/              # Compose UI components (commonMain)
│   ├── commonMain/        # Platform-agnostic Compose UI
│   ├── iosMain/           # iOS-specific code
│   ├── androidMain/       # Android-specific code
│   └── jsMain/            # Web-specific code
│
├── webUI/                 # Web bundle builder ⭐ NEW
│   ├── build.gradle.kts   # Uses binaries.executable()
│   └── src/jsMain/
│       └── Main.kt        # Initializes ComposeViewport, exposes window API
│
├── forex-web-sdk/         # API client for npm (library mode)
│   └── build.gradle.kts   # Uses binaries.library()
│
└── mobileCore/            # iOS/Android integration
    └── commonMain/        # Shared logic

apps/
├── web/                   # React + Vite app
│   ├── public/
│   │   └── compose-web-ui.js  # Built from webUI module
│   ├── src/
│   │   └── components/
│   │       └── OfflineDemoView.jsx  # Calls window.renderResultScreen()
│   └── index.html         # Loads script: <script src="/compose-web-ui.js?timestamp">
│
├── iosApp/                # SwiftUI + Compose
└── androidApp/            # Jetpack Compose
```

---

## Implementation Steps

### 1. Create WebUI Module

**File:** `packages/webUI/build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    js(IR) {
        browser {
            binaries.executable()  // ⭐ KEY: Use executable, not library
        }
    }
    
    sourceSets {
        jsMain.dependencies {
            implementation(projects.packages.sharedUi)  // Import shared UI
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.material3)
        }
    }
}

// Task to copy bundle to web app
tasks.register<Copy>("copyWebBundle") {
    dependsOn("jsBrowserProductionWebpack")
    from("build/kotlin-webpack/js/productionExecutable")
    into("$rootDir/apps/web/public")
    include("*.js", "*.wasm")
}
```

### 2. Initialize ComposeViewport

**File:** `packages/webUI/src/jsMain/kotlin/.../Main.kt`

```kotlin
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Create permanent container - initially hidden
    val container = document.createElement("div").apply {
        id = "compose-root"
        setAttribute("style", "position: fixed; top: 0; left: 0; width: 100%; height: 100%; z-index: 9999; display: none;")
    } as HTMLElement
    
    composeContainer = container
    document.body?.appendChild(container)
    
    // Initialize ComposeViewport ONCE ⭐ CRITICAL
    ComposeViewport(container) {
        ComposeApp()  // Reactive composable
    }
    
    // Expose to JavaScript
    window.asDynamic().renderResultScreen = { data: dynamic ->
        currentResult = ConversionResult(
            status = if (data.status == "Online") Status.ONLINE else Status.OFFLINE,
            fromCurrency = data.fromCurrency as String,
            toCurrency = data.toCurrency as String,
            inputAmount = (data.inputAmount as Number).toDouble(),
            convertedAmount = (data.convertedAmount as Number).toDouble()
        )
        showResultScreen = true
        composeContainer?.setAttribute("style", "...display: block;")
    }
}

@Composable
fun ComposeApp() {
    if (showResultScreen && currentResult != null) {
        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            ResultScreen(result = currentResult!!)  // ⭐ Shared component
            Button(onClick = { showResultScreen = false }) {
                Text("Close")
            }
        }
    }
}
```

### 3. Load in React App

**File:** `apps/web/index.html`

```html
<!doctype html>
<html lang="en">
  <head>...</head>
  <body>
    <div id="root"></div>
    
    <!-- Load Compose bundle -->
    <script src="/compose-web-ui.js?1771380061"></script>
    
    <script type="module" src="/src/main.jsx"></script>
  </body>
</html>
```

**Note:** Update timestamp after each rebuild to bust cache.

### 4. Call from React

**File:** `apps/web/src/components/OfflineDemoView.jsx`

```javascript
const handleShowResult = () => {
  if (typeof window.renderResultScreen === 'function') {
    window.renderResultScreen({
      status: 'Online',
      fromCurrency: 'USD',
      toCurrency: 'PHP',
      inputAmount: 100,
      convertedAmount: 5783.00
    });
  } else {
    console.warn('Compose Web UI not loaded');
  }
};

return (
  <Paper onClick={handleShowResult}>
    <Typography>Click to show Compose UI</Typography>
  </Paper>
);
```

---

## Build Process

### Build Web Bundle
```bash
./gradlew :packages:webUI:jsBrowserProductionWebpack
```

**Output:**
- `packages/webUI/build/kotlin-webpack/js/productionExecutable/compose-web-ui.js` (2.1MB)
- `packages/webUI/build/kotlin-webpack/js/productionExecutable/*.wasm` (8.2MB)

### Copy to Web App
```bash
cp packages/webUI/build/kotlin-webpack/js/productionExecutable/*.{js,wasm} apps/web/public/
```

### Update Cache Buster
```bash
# Get timestamp
date +%s  # e.g., 1771380061

# Update index.html
<script src="/compose-web-ui.js?1771380061"></script>
```

---

## Critical Patterns

### ⚠️ ComposeViewport Can Only Initialize ONCE

**Wrong ❌:**
```kotlin
window.asDynamic().renderResultScreen = {
    ComposeViewport(container) {  // Called every time!
        ResultScreen(...)
    }
}
// Result: TypeError: this.w1q_1.hz is not a function
```

**Correct ✅:**
```kotlin
// Initialize ONCE in main()
ComposeViewport(container) {
    ComposeApp()  // Reactive to state changes
}

// Control via state
window.asDynamic().renderResultScreen = {
    showResultScreen = true  // Triggers recomposition
}
```

### ⚠️ Container Visibility Management

```kotlin
// Show: Set both state AND DOM property
showResultScreen = true
composeContainer?.setAttribute("style", "...display: block;")

// Hide: Set both state AND DOM property
showResultScreen = false
composeContainer?.setAttribute("style", "...display: none;")
```

### ⚠️ Cache Busting Required

Every time you rebuild the bundle:
1. Build: `./gradlew :packages:webUI:jsBrowserProductionWebpack`
2. Copy: `cp packages/webUI/build/.../compose-web-ui.js apps/web/public/`
3. Update timestamp in `index.html`: `<script src="/compose-web-ui.js?NEW_TIMESTAMP">`
4. Hard refresh browser: Cmd+Shift+R / Ctrl+Shift+R

---

## Trade-offs Analysis

### ✅ Advantages

1. **True Code Sharing**: Same UI file runs on iOS, Android, Web
2. **Single Source of Truth**: Update UI once, affects all platforms
3. **Type Safety**: Kotlin type system across all platforms
4. **Proven Pattern**: Similar to Google Maps, Stripe, Auth0 SDKs

### ⚠️ Disadvantages

1. **Bundle Size**: 10MB total (2.1MB JS + 8.2MB WASM)
   - Could be optimized with code splitting
   - Not ideal for low-bandwidth users
   
2. **Integration Complexity**: 
   - Not idiomatic npm/ES module pattern
   - Requires script tag management
   - Window API coordination between React and Compose
   
3. **Development Workflow**:
   - Must rebuild Kotlin, copy files, update timestamp
   - Can't use npm/Vite hot module reload for Compose parts
   - Slower iteration vs pure React development
   
4. **State Management**:
   - Two state systems (React + Compose)
   - Must bridge data between them
   - Potential for sync issues
   
5. **SEO & SSR**:
   - Compose components not server-side renderable
   - May impact SEO if used for main content
   - Better for modal/overlay UI patterns

### 🤔 When to Use This Approach

**Good for:**
- High-value, complex UI components (charts, editors, forms)
- Components that need perfect parity across platforms
- Projects already using Compose for iOS/Android
- Internal tools where bundle size less critical

**Not good for:**
- Simple components easily built in React
- SEO-critical content
- Bandwidth-constrained users
- Teams without Kotlin expertise

---

## Comparison: npm Library vs Script Bundle

| Aspect | npm Library (library mode) | Script Bundle (executable mode) |
|--------|---------------------------|----------------------------------|
| Bundle Size | ~475 lines (50KB) | ~43k lines (2.1MB + 8.2MB WASM) |
| Compose Runtime | ❌ Missing | ✅ Included |
| Integration | ES modules, tree-shakeable | Script tag, window API |
| npm-publish | ✅ Supported | ❌ Not supported |
| ComposeViewport | ❌ Not callable | ✅ Works |
| Use Case | API/logic libraries | UI components |
| Status | Standard, recommended | Works but unconventional |

---

## Alternative Approaches Considered

### 1. Full Compose Web App
- Rebuild entire site with Compose Web
- **Pro:** Maximum code sharing
- **Con:** Lose React ecosystem, major rewrite

### 2. Separate Web UI
- Keep React for web, Compose for mobile only
- **Pro:** Simpler, idiomatic
- **Con:** Duplicate UI code

### 3. Server-Side Rendering
- Render Compose on server, send HTML
- **Pro:** No client-side bundle
- **Con:** Not interactive, complex backend

### 4. WebView Embedding
- Load Compose Web app in iframe/webview
- **Pro:** Full isolation
- **Con:** Awkward UX, communication overhead

---

## Lessons Learned

1. **Kotlin/JS has two distinct modes** with different purposes:
   - `library()` for npm packages (API/logic only)
   - `executable()` for standalone apps (includes runtime)

2. **ComposeViewport is stateful** and must only initialize once
   - Use Compose's reactivity (`mutableStateOf`) for visibility
   - Manage DOM separately from Compose state

3. **Micro-frontends are viable** but require:
   - Clear module boundaries
   - Disciplined state management
   - Good documentation

4. **Trade-offs are real**:
   - Code sharing vs development velocity
   - Bundle size vs maintenance burden
   - Innovation vs idiomatic patterns

---

## Future Improvements

### Potential Optimizations

1. **Lazy Loading**:
   ```javascript
   const loadComposeUI = () => {
     const script = document.createElement('script');
     script.src = '/compose-web-ui.js?timestamp';
     document.body.appendChild(script);
   };
   // Load only when needed
   ```

2. **Code Splitting**:
   - Split bundle by feature/component
   - Load ResultScreen separately from other components

3. **Development Tools**:
   - Script to auto-update timestamp
   - Gradle task to watch and rebuild
   - Better debugging integration

4. **State Bridge Library**:
   - Create formal API for React ↔ Compose communication
   - Type-safe bindings
   - State synchronization helpers

---

## Conclusion

✅ **It works!** Compose Multiplatform UI can integrate into React apps.

⚠️ **It's complicated** - Micro-frontend architecture has real trade-offs.

🤔 **Choose wisely** - Evaluate if benefits outweigh costs for your use case.

📚 **Document everything** - Future developers will thank you.

This proof-of-concept demonstrates technical feasibility. Production use requires careful consideration of:
- Bundle size impact
- Development workflow
- Maintenance burden
- Team expertise
- Long-term support

**For most projects:** Continue using React for web, Compose for mobile, share business logic via npm.

**For specific high-value components:** This approach is proven viable.
