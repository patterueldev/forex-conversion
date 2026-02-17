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

            {/* Output Section */}
            <Paper elevation={0} sx={{ p: 2, bgcolor: 'background.paper', borderRadius: 1 }}>
              <Typography variant="subtitle2" sx={{ fontWeight: 600, mb: 1.5, fontSize: '0.85rem', color: '#666' }}>
                Result
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
