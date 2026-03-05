# webUI - Compose Multiplatform Web UI Bundle

This package builds a standalone JavaScript bundle containing the shared Compose UI components that can be loaded into any web application (React, Vue, vanilla JS, etc.).

## What's Inside

- **compose-web-ui.js** (1.9MB) - Full Compose runtime + shared UI components
- **e9b28911e687b1ee6b42.wasm** (8.2MB) - Skiko graphics engine

## Building

```bash
./gradlew :packages:webUI:build
```

Output will be in: `packages/webUI/build/dist/`

## Integration with React App

### Step 1: Copy bundles to React app

```bash
# From project root
cp packages/webUI/build/dist/* apps/web/public/
```

### Step 2: Load in index.html

Add to `apps/web/index.html` before closing `</body>`:

```html
<!-- Load Compose Web UI Bundle -->
<script src="/compose-web-ui.js"></script>
```

### Step 3: Call from React

```javascript
// In your React component
function MyComponent() {
  const handleShowResult = () => {
    // Call the global function exposed by Compose bundle
    window.renderResultScreen({
      status: 'Online',
      fromCurrency: 'USD',
      toCurrency: 'PHP',
      inputAmount: 100,
      convertedAmount: 57.92
    })
  }
  
  return <button onClick={handleShowResult}>Show Result</button>
}
```

## API

### `window.renderResultScreen(data)`

Renders the shared `ResultScreen` Compose component in a full-screen overlay.

**Parameters:**
- `data.status`: String - "Online" or "Offline"
- `data.fromCurrency`: String - Source currency code (e.g., "USD")
- `data.toCurrency`: String - Target currency code (e.g., "PHP")
- `data.inputAmount`: Number - Amount to convert
- `data.convertedAmount`: Number - Converted result

**Example:**
```javascript
window.renderResultScreen({
  status: 'Online',
  fromCurrency: 'USD',
  toCurrency: 'PHP',
  inputAmount: 100,
  convertedAmount: 57.92
})
```

The overlay includes a "Close" button in the top-right corner.

## Architecture

This demonstrates **true cross-platform UI reuse**:

```
packages/sharedUi/
└── src/commonMain/kotlin/.../ResultScreen.kt  <-- Single source of truth
    ├── iOS (via mobileCore) ✅
    ├── Android (via mobileCore) ✅
    └── Web (via webUI) ✅
```

The same `ResultScreen.kt` component works on all three platforms!

## Why Standalone Bundle?

Compose Multiplatform Web requires `binaries.executable()` mode to include the full runtime. This cannot be packaged as an npm library that React imports directly (see `docs/COMPOSE_WEB_INTEGRATION_RESEARCH.md` for details).

The script tag approach is the recommended pattern for integrating Compose Web with existing web apps.

## File Sizes

- **Development**: ~10MB total (unoptimized)
- **Production**: ~10MB total (minified)
- **First Load**: ~10MB (cached afterward)

The bundle includes:
- Compose Runtime
- Material 3 components
- Skiko WASM graphics engine
- Shared UI components

## Development Workflow

1. Make changes to `packages/sharedUi/` components
2. Rebuild: `./gradlew :packages:webUI:build`
3. Copy to React app: `cp packages/webUI/build/dist/* apps/web/public/`
4. Refresh browser (the script loads from public/)

## Testing

After loading the script, verify in browser console:

```javascript
// Check if function is available
console.log(typeof window.renderResultScreen) // Should be "function"

// Test it
window.renderResultScreen({
  status: 'Online',
  fromCurrency: 'USD',
  toCurrency: 'PHP',
  inputAmount: 100,
  convertedAmount: 57.92
})
```

You should see the ResultScreen component appear in a full-screen overlay.

## Production Deployment

Options:

1. **Serve from same domain**: Copy bundles to `public/` and deploy with React app
2. **CDN**: Upload to CDN and reference via absolute URL in script tag
3. **Separate service**: Host on different domain (requires CORS setup)

Recommended: Option 1 (same domain) for simplicity.
