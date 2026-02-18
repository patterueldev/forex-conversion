# Package Architecture - Web Integration

## Overview

After research and experimentation, we've established the correct architecture for integrating Compose Multiplatform UI with web applications.

## Package Structure

### 📦 packages/webCore (v0.1.2)
**Purpose**: API client library for npm  
**Type**: `binaries.library()`  
**Distribution**: npm package (forex-web-sdk)

**Contents**:
- Ktor HTTP client wrapper
- API models and serialization
- Data layer only (no UI)

**Usage**:
```javascript
import { ForexWebService } from 'forex-web-sdk'
const service = new ForexWebService()
```

**Clean**: ✅ No Compose dependencies, no UI code

---

### 📦 packages/webUI (v0.1.0)
**Purpose**: Compose UI bundle for web  
**Type**: `binaries.executable()`  
**Distribution**: Script tag loading

**Contents**:
- Full Compose runtime (~1.9MB JS)
- Skiko WASM graphics (~8.2MB)
- Shared UI components
- Window API exposure

**Output**:
- `build/dist/compose-web-ui.js`
- `build/dist/*.wasm`

**Usage**:
```html
<script src="/compose-web-ui.js"></script>
<script>
  window.renderResultScreen({...data})
</script>
```

**Includes**: ✅ Full ComposeViewport implementation

---

### 📦 packages/mobileCore (v0.1.0)
**Purpose**: Shared code for iOS & Android  
**Type**: KMP library  
**Distribution**: CocoaPods, Maven

**Contents**:
- Business logic
- Platform interfaces
- API integration

**Usage**:
- iOS: Framework dependency
- Android: Gradle dependency

---

### 📦 packages/sharedUi (v0.1.0)
**Purpose**: Shared Compose UI components  
**Type**: KMP library  
**Distribution**: Internal dependency

**Contents**:
- `ResultScreen.kt` - The shared component
- `ConversionResult` data models
- Platform-specific navigators (expect/actual)

**Used by**:
- webUI (Web)
- mobileCore (iOS, Android)

---

## Why Two Web Packages?

### Problem
Kotlin/JS has two binary modes, each with limitations:

| Mode | npm Compatible | Full Compose Runtime |
|------|----------------|---------------------|
| `library()` | ✅ Yes | ❌ No (stub only) |
| `executable()` | ❌ No | ✅ Yes (43k lines) |

### Solution
**Separate concerns**:
- **webCore**: API layer (library mode) → npm package
- **webUI**: UI layer (executable mode) → script bundle

## Integration Patterns

### Web (React)
```
React App
├── npm install forex-web-sdk       # webCore for API
├── <script src="compose-web-ui.js"> # webUI for UI
└── window.renderResultScreen()     # Call Compose UI
```

### iOS
```
iOS App
└── CocoaPods mobileCore           # Includes sharedUi
    └── ComposeUIViewController    # Render Compose
```

### Android
```
Android App
└── Gradle mobileCore              # Includes sharedUi
    └── setContent { ResultScreen() } # Render Compose
```

## Build Commands

```bash
# Build webCore (npm package)
./gradlew :packages:forex-web-sdk:build
# Output: packages/webCore/build/packages/js/

# Build webUI (script bundle)
./gradlew :packages:webUI:build
# Output: packages/webUI/build/dist/

# Build mobileCore (iOS framework)
./gradlew :packages:mobileCore:linkDebugFrameworkIosArm64

# Build mobileCore (Android library)
./gradlew :packages:mobileCore:assembleDebug
```

## Publishing

### webCore → npm
```bash
./npm-publish.sh
# Publishes to https://www.npmjs.com/package/forex-web-sdk
```

### webUI → CDN or public/
```bash
# Option 1: Copy to React app
cp packages/webUI/build/dist/* apps/web/public/

# Option 2: Upload to CDN
# scp packages/webUI/build/dist/* cdn:/path/
```

### mobileCore → CocoaPods/Maven
```bash
# iOS: Already accessible via local CocoaPods
# Android: Available via project dependency
```

## Summary

✅ **webCore**: Clean npm package for API (no UI, no Compose)  
✅ **webUI**: Standalone Compose bundle for web (full runtime)  
✅ **Separation of concerns**: API vs UI  
✅ **Same UI everywhere**: ResultScreen.kt works on iOS, Android, Web  

This architecture works around Kotlin/JS limitations while achieving the goal of cross-platform UI reuse.

---

**Branch**: `compose-web-ui`  
**Date**: February 18, 2026  
**Status**: ✅ webCore clean, webUI working
