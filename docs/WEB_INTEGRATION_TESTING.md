# Web Integration Complete - Testing Guide

## What Was Done

### 1. Copied Compose Web UI Bundles
```bash
# Files copied to apps/web/public/
- compose-web-ui.js (1.9MB)
- e9b28911e687b1ee6b42.wasm (8.2MB)
```

### 2. Updated index.html
Added script tag to load Compose runtime:
```html
<script src="/compose-web-ui.js"></script>
```

### 3. Updated OfflineDemoView.jsx
- Added `handleResultClick()` function
- Made Result section clickable with hover effect
- Parses conversion result and calls `window.renderResultScreen()`
- Shows hint: "Click to view in Compose UI"

### 4. Updated main.jsx
Added detection logging to confirm Compose Web UI loads

## How to Test

### Step 1: Start the Web App
```bash
cd /Users/pat/Projects/PAT/ForexConversion/apps/web
npm run dev
```

### Step 2: Open Browser
Navigate to: `http://localhost:5173` (or whatever port Vite assigns)

### Step 3: Check Console
You should see:
```
✅ Compose Web UI initialized - window.renderResultScreen available
✅ Compose Web UI loaded successfully
```

### Step 4: Perform Conversion
1. Enter an amount (e.g., 100)
2. Click "Convert" button
3. Wait for result to appear

### Step 5: Click Result Section
- The Result section should be clickable (pointer cursor on hover)
- Hover effect: slight lift and background color change
- Click it!

### Expected Result

A full-screen white overlay should appear with:
- ✅ The **same** ResultScreen.kt Compose component
- ✅ Status, From, To, Amount, Result displayed
- ✅ "Close" button in top-right corner (red)
- ✅ Same UI as iOS and Android!

Console should show:
```
🎯 Opening Compose ResultScreen with data: {...}
✅ ResultScreen rendered with data: {...}
```

## Troubleshooting

### Issue: "Compose Web UI not loaded" alert
**Cause**: Script didn't load  
**Fix**: 
1. Check browser console for errors
2. Verify files exist in `apps/web/public/`
3. Hard refresh (Cmd+Shift+R / Ctrl+Shift+F5)

### Issue: Nothing happens on click
**Cause**: Result format doesn't match expected pattern  
**Check**: Console for warnings  
**Fix**: Ensure conversion succeeded and shows format: "X USD → Y PHP @ rate=Z"

### Issue: Result section not clickable
**Cause**: No conversion result yet  
**Fix**: Click "Convert" button first to get a result

### Issue: WASM not loading
**Cause**: Large file, slow network  
**Wait**: 8.2MB WASM takes time to load  
**Check**: Network tab in DevTools

## What This Proves

✅ **True Cross-Platform UI Reuse**
```
packages/sharedUi/src/commonMain/kotlin/.../ResultScreen.kt
├── iOS (via ComposeUIViewController) ✅
├── Android (via setContent{}) ✅
└── Web (via ComposeViewport) ✅
```

The **same Kotlin Compose component** works on all three platforms!

## File Structure

```
apps/web/
├── public/
│   ├── compose-web-ui.js     ← Compose runtime + UI
│   └── *.wasm                ← Graphics engine
├── index.html                ← Loads script
└── src/
    ├── main.jsx              ← Detection logging
    └── components/
        └── OfflineDemoView.jsx  ← Calls window.renderResultScreen()
```

## Architecture

```
React App (Vite)
├── Material UI components (React)
├── <script src="compose-web-ui.js">
│   └── Exposes: window.renderResultScreen()
└── On Click
    └── window.renderResultScreen(data)
        └── ComposeViewport renders ResultScreen.kt
            └── Same component as iOS/Android! 🎉
```

## Next Steps After Successful Test

1. ✅ Commit changes on `compose-web-ui` branch
2. ✅ Document findings
3. ✅ Consider adding:
   - Loading indicator while WASM loads
   - Better error handling
   - Animation when overlay appears
   - More shared UI components

## Build Updates

If you update `packages/sharedUi/` components:

```bash
# 1. Rebuild webUI
./gradlew :packages:webUI:build

# 2. Copy to web app
cp packages/webUI/build/dist/*.{js,wasm} apps/web/public/

# 3. Hard refresh browser
```

---

**Status**: ✅ Ready to test  
**Branch**: compose-web-ui  
**Date**: February 18, 2026
