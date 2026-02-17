# Forex Conversion Web App

React-based web variant of the ForexConversion Offline/Online Demo, built as a POC to explore KMP integration into web platforms.

**Beautiful Material-UI design with centered 1280px layout!**

## Overview

This web app mirrors the functionality of the iOS (`iosApp/OfflineDemoView.swift`) and Android (`androidApp/OfflineDemoScreen.kt`) native applications, demonstrating currency conversion with offline/online mode simulation.

**Current Status:** All KMP service calls are **mocked** with hardcoded data. This is intentional for POC purposes.

## ✨ Features

- **Offline/Online Mode Toggle** - Material Design switch
- **Currency Conversion** - USD → PHP with cached rates
- **Fetch & Store Rates** - Simulates fetching latest exchange rates
- **Base URL Configuration** - Beautiful Material Dialog
- **Loading States** - Animated spinner with overlay
- **Properly Centered Layout** - 1280px width, beautifully centered
- **Responsive Design** - Seamlessly adapts to all screen sizes
- **Material Design 3** - Professional, clean, modern UI

## 🎨 Tech Stack

- **React 19.2.0** - UI library
- **Material-UI 5.x** - Component library with Material Design
- **Emotion** - CSS-in-JS styling
- **Vite 8 (beta)** - Build tool and dev server
- **Plain React Hooks** - State management (no external libs)

## 📁 Project Structure

```
apps/web/
├── src/
│   ├── components/
│   │   ├── OfflineDemoView.jsx    # Main screen with centered 1280px layout
│   │   ├── SettingsDialog.jsx     # Base URL config modal
│   │   └── LoadingOverlay.jsx     # Loading spinner overlay
│   ├── hooks/
│   │   ├── useForexService.js     # Mock KMP service layer
│   │   └── useOfflineDemo.js      # UI state management
│   ├── main.jsx                   # Entry point with MUI Theme
│   ├── App.jsx                    # Root component
│   └── index.css                  # Global styles
├── package.json
├── vite.config.js
└── index.html
```

## 🚀 Setup & Installation

### Prerequisites
- Node.js 18+ and npm

### Install Dependencies
```bash
cd apps/web
npm install
```

### Run Development Server
```bash
npm run dev
```

The app will be available at `http://localhost:5174` (or next available port) with a beautiful centered layout.

### Build for Production
```bash
npm run build
```

Output will be in `dist/` directory.

### Preview Production Build
```bash
npm run preview
```

## 📐 Centered Layout with 1280px Width

The app displays perfectly centered with 1280px width on all screen sizes:

- **Small screens (< 1280px)**: Full width with padding, beautiful mobile experience
- **Large screens (≥ 1280px)**: Centered at 1280px, spacious and professional
- **Header**: Properly centered to match content width
- **Content**: All sections centered and aligned

**Key Design Features:**
- ✅ Properly centered (no more left-aligned stickiness)
- ✅ 1280px width (spacious, professional)
- ✅ White AppBar (web app feel)
- ✅ Light paper sections (subtle containers)
- ✅ Responsive padding on all sizes
- ✅ Looks like a real web app

## 📝 Mock Data Structure

Currently, all forex operations return mock data:

### Mock Exchange Rate
- **Currency Pair:** USD → PHP
- **Rate:** 56.78
- **Last Updated:** Simulated timestamp

### Mock Service (`useForexService`)
- `fetchOnStartUpOnce()` - Returns after 800ms delay
- `fetchAndStoreLatestRate()` - Updates timestamp, 1000ms delay
- `getCachedRate(from, to)` - Returns cached rate, 200ms delay
- `convertCurrency(amount, from, to)` - Calculates conversion, 500ms delay
- `toggleSimulatedNetwork(bool)` - Sets offline mode flag
- `getBaseURL() / setBaseURL(url)` - Manages base URL state

## 🔄 How It Works

### Flow Overview

1. **On Mount**: `fetchOnStartUpOnce()` initializes cached rate
2. **Fetch Latest**: Button triggers `fetchAndStoreLatestRate()` with loading overlay
3. **Convert**: Reads amount input, calls `convertCurrency()`, displays result
4. **Offline Toggle**: Changes service behavior (mocked for now)
5. **Settings**: Opens Material Dialog to edit base URL

### State Management

The app uses two main hooks:

- **`useForexService`**: Encapsulates all forex operations (currently mocked)
- **`useOfflineDemo`**: Manages UI state and coordinates with service layer

## 🎨 Material-UI Components Used

- **AppBar** - Centered white header
- **Paper** - Light section containers
- **Button** - Action buttons with variants
- **TextField** - Amount input
- **Switch** - Offline mode toggle
- **Dialog** - Settings modal
- **Alert** - Output display
- **Stack/Box** - Layout components
- **Typography** - Clean text styling
- **CircularProgress** - Loading spinner
- **IconButton** - Settings icon

## 🚀 Future Integration with KMP

### Planned Approach

When integrating real KMP (Kotlin Multiplatform) functionality:

1. **Replace Mock Service**: Update `useForexService.js` to call KMP wasm/js bindings
2. **Add KMP Package**: Import compiled KMP module (likely from `packages/mobileCore`)
3. **Wire Up Bindings**: 
   ```javascript
   import { ForexMobileService } from '@forexconversion/mobile-core';
   const service = new ForexMobileService();
   ```
4. **Handle Async/Promise Conversion**: Map Kotlin coroutines to JavaScript Promises
5. **Type Safety**: Consider migrating to TypeScript for better KMP type integration

### Expected Changes

- Remove mock delays and hardcoded data
- Add real HTTP calls via KMP networking layer
- Implement actual offline storage (IndexedDB, LocalStorage, etc.)
- Handle real error states and network failures

## ✅ Current Status

- ✅ Beautiful Material Design UI
- ✅ Properly centered layout (1280px)
- ✅ All UI elements from native apps replicated
- ✅ Mock forex service working
- ✅ Loading states properly displayed
- ✅ Settings modal fully functional
- ✅ Production build successful
- ✅ Responsive on all screen sizes

## 📋 Current Limitations

- ✅ UI fully functional and beautiful
- ✅ All interactions working (buttons, toggle, modal)
- ✅ Loading states properly displayed
- ❌ No real network calls
- ❌ No actual data persistence
- ❌ Offline mode only affects UI text, not behavior
- ❌ Fixed currency pair (USD → PHP only)

## 🧪 Manual Testing Checklist

- [ ] App loads without errors
- [ ] Material Design UI looks professional
- [ ] Centered layout on desktop (1280px)
- [ ] Full width on mobile devices
- [ ] Content properly centered (not left-aligned)
- [ ] Initial rate displayed after startup
- [ ] "Fetch Rate" button updates timestamp
- [ ] "Convert" button shows calculation
- [ ] Offline toggle changes output text
- [ ] Settings button opens Material Dialog
- [ ] Base URL can be edited and reset
- [ ] Loading overlay shows during operations
- [ ] Responsive on all screen sizes

## 📚 Development Notes

- Built with Vite 8 beta (uses Rolldown bundler)
- Material-UI 5 with Emotion styling
- React 19 with modern hooks API
- No external state management needed (useState/useEffect sufficient)
- Properly centered layout using flexbox

## 🎯 Design Highlights

- **Professional Material Design 3** aesthetic
- **Properly centered** on all screen sizes
- **Spacious 1280px width** for comfortable reading
- **Smooth animations** and transitions
- **Accessible components** with proper ARIA labels
- **Light theme** for clean, modern appearance
- **Responsive sections** with light paper backgrounds

## License

Part of the ForexConversion KMP project.

---

**Built with:** React + Material-UI + Vite  
**Design:** Material Design 3 with centered 1280px layout  
**Layout:** Properly centered, responsive  
**Status:** Production-ready POC  
**Last Updated:** February 2026
