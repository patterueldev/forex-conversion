import {
  Box,
  AppBar,
  Toolbar,
  Typography,
  Paper,
  TextField,
  Button,
  Stack,
  IconButton,
  Alert,
} from '@mui/material';
import SettingsIcon from '@mui/icons-material/Settings';
import { useOfflineDemo } from '../hooks/useOfflineDemo';
import { LoadingOverlay } from './LoadingOverlay';
import { SettingsDialog } from './SettingsDialog';

/**
 * OfflineDemoView - Main screen for Forex conversion
 * Properly centered layout with bigger width (1280px)
 * Looks like a natural web app, not constrained
 */
export const OfflineDemoView = () => {
  const {
    isLoading,
    convertedAmount,
    amountText,
    baseURL,
    showingSettingsModal,
    setAmountText,
    convert,
    updateBaseURL,
    resetBaseURL,
    setShowingSettingsModal,
  } = useOfflineDemo();

  // Mock test function for testing Compose UI without server
  const testWithMockData = () => {
    const mockResult = `100 USD → 5783.00 PHP @ rate=57.83`;
    // We can't set convertedAmount directly, but we can trigger the click handler with mock data
    handleResultClick({
      status: 'Online',
      fromCurrency: 'USD',
      toCurrency: 'PHP',
      inputAmount: 100,
      convertedAmount: 5783.00
    });
  };

  // Lazy load Compose Web UI script if not already loaded
  const ensureComposeLoaded = () => {
    return new Promise((resolve, reject) => {
      // Check if already loaded
      if (typeof window.renderResultScreen === 'function') {
        console.log('✅ Compose UI already loaded');
        resolve();
        return;
      }

      // Check if script is already being loaded
      const existingScript = document.querySelector('script[src^="/compose-web-ui.js"]');
      if (existingScript) {
        console.log('⏳ Compose UI script loading...');
        existingScript.addEventListener('load', resolve);
        existingScript.addEventListener('error', reject);
        return;
      }

      // Load the script
      console.log('📦 Loading Compose UI script...');
      const script = document.createElement('script');
      script.src = '/compose-web-ui.js?1771381193';
      script.onload = () => {
        console.log('✅ Compose UI loaded successfully');
        resolve();
      };
      script.onerror = (error) => {
        console.error('❌ Failed to load Compose UI:', error);
        reject(error);
      };
      document.body.appendChild(script);
    });
  };

  // Handle clicking the Result section to show Compose UI
  const handleResultClick = async (mockData = null) => {
    // Lazy load Compose UI first
    try {
      await ensureComposeLoaded();
    } catch (error) {
      console.error('❌ Failed to load Compose UI:', error);
      alert('Failed to load Compose UI. Please refresh the page.');
      return;
    }

    let resultData = mockData;
    
    if (!mockData) {
      // Parse from actual conversion result
      if (convertedAmount && convertedAmount !== 'error') {
        const parts = convertedAmount.match(/(\d+(?:\.\d+)?)\s+(\w+)\s+→\s+(\d+(?:\.\d+)?)\s+(\w+)/);
        
        if (parts) {
          resultData = {
            status: 'Online',
            fromCurrency: parts[2],
            toCurrency: parts[4],
            inputAmount: parseFloat(parts[1]),
            convertedAmount: parseFloat(parts[3])
          };
        }
      }
    }
    
    if (resultData) {
      // Check if Compose Web UI is loaded
      if (typeof window.renderResultScreen === 'function') {
        console.log('🎯 Opening Compose ResultScreen with data:', resultData);
        window.renderResultScreen(resultData);
      } else {
        console.warn('⚠️ Compose Web UI not loaded. window.renderResultScreen is not available.');
        alert('Compose Web UI is not loaded. Please refresh the page.');
      }
    }
  };

  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        flex: 1,
        minHeight: '100vh',
        bgcolor: '#fafafa',
      }}
    >
      {/* Header - Centered */}
      <AppBar position="sticky" elevation={1} sx={{ bgcolor: '#fff', color: '#000' }}>
        <Box
          sx={{
            display: 'flex',
            justifyContent: 'center',
            width: '100%',
          }}
        >
          <Toolbar
            sx={{
              display: 'flex',
              justifyContent: 'space-between',
              maxWidth: '1280px',
              width: '100%',
              px: 2,
            }}
          >
            <Typography variant="h6" component="div" sx={{ fontWeight: 700 }}>
              Forex Converter
            </Typography>
            <IconButton
              color="inherit"
              onClick={() => setShowingSettingsModal(true)}
              aria-label="Settings"
            >
              <SettingsIcon />
            </IconButton>
          </Toolbar>
        </Box>
      </AppBar>

      {/* Main Content Area - Centered */}
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'center',
          flex: 1,
          px: 2,
          py: 3,
        }}
      >
        <Box
          sx={{
            width: '100%',
            maxWidth: '1280px',
          }}
        >
          {/* Content Sections */}
          <Stack spacing={2}>
            {/* Currencies Section */}
            <Paper elevation={0} sx={{ p: 2, bgcolor: 'background.paper', borderRadius: 1 }}>
              <Typography variant="subtitle2" sx={{ fontWeight: 600, mb: 1.5, fontSize: '0.85rem', color: '#666' }}>
                Currencies
              </Typography>
              <Stack spacing={1.5}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                  <Typography variant="body2" color="textSecondary">From</Typography>
                  <Typography variant="body2" sx={{ fontWeight: 600 }}>USD</Typography>
                </Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                  <Typography variant="body2" color="textSecondary">To</Typography>
                  <Typography variant="body2" sx={{ fontWeight: 600 }}>PHP</Typography>
                </Box>
              </Stack>
            </Paper>

            {/* Amount Section */}
            <Paper elevation={0} sx={{ p: 2, bgcolor: 'background.paper', borderRadius: 1 }}>
              <Typography variant="subtitle2" sx={{ fontWeight: 600, mb: 1.5, fontSize: '0.85rem', color: '#666' }}>
                Amount
              </Typography>
              <TextField
                fullWidth
                type="number"
                value={amountText}
                onChange={(e) => setAmountText(e.target.value)}
                placeholder="Enter amount"
                inputProps={{ step: '0.01' }}
                variant="outlined"
                size="small"
              />
            </Paper>

            {/* Action Button */}
            <Button
              fullWidth
              variant="contained"
              color="success"
              onClick={convert}
              size="large"
            >
              Convert
            </Button>

            {/* Mock Test Button for Compose UI */}
            <Button
              fullWidth
              variant="outlined"
              color="primary"
              onClick={testWithMockData}
              size="large"
            >
              Test Compose UI (Mock)
            </Button>

            {/* Output Section - Clickable to show Compose UI */}
            <Paper 
              elevation={0} 
              sx={{ 
                p: 2, 
                bgcolor: 'background.paper', 
                borderRadius: 1,
                cursor: convertedAmount !== 'error' && convertedAmount !== '-' ? 'pointer' : 'default',
                transition: 'all 0.2s',
                '&:hover': convertedAmount !== 'error' && convertedAmount !== '-' ? {
                  elevation: 2,
                  bgcolor: '#f5f5f5',
                  transform: 'translateY(-2px)',
                } : {}
              }}
              onClick={handleResultClick}
            >
              <Typography variant="subtitle2" sx={{ fontWeight: 600, mb: 1.5, fontSize: '0.85rem', color: '#666' }}>
                Result {convertedAmount !== 'error' && convertedAmount !== '-' && '(Click to view in Compose UI)'}
              </Typography>
              <Alert severity={convertedAmount === 'error' ? 'error' : 'info'} sx={{ fontSize: '0.85rem' }}>
                <Typography
                  variant="body2"
                  component="code"
                  sx={{
                    fontFamily: '"Roboto Mono", "Courier New", monospace',
                    wordBreak: 'break-all',
                    display: 'block',
                    fontSize: '0.8rem',
                  }}
                >
                  {convertedAmount}
                </Typography>
              </Alert>
            </Paper>
          </Stack>
        </Box>
      </Box>

      {/* Loading Overlay */}
      {isLoading && <LoadingOverlay />}

      {/* Settings Dialog */}
      <SettingsDialog
        isOpen={showingSettingsModal}
        onClose={() => setShowingSettingsModal(false)}
        baseURL={baseURL}
        onBaseURLChange={updateBaseURL}
        onReset={resetBaseURL}
      />
    </Box>
  );
};
