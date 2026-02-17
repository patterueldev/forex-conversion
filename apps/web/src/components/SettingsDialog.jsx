import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Box,
} from '@mui/material';

/**
 * SettingsDialog - Modal for configuring base URL
 */
export const SettingsDialog = ({
  isOpen,
  onClose,
  baseURL,
  onBaseURLChange,
  onReset,
}) => {
  return (
    <Dialog
      open={isOpen}
      onClose={onClose}
      maxWidth="sm"
      fullWidth
    >
      <DialogTitle>Settings</DialogTitle>
      <DialogContent>
        <Box sx={{ pt: 2, display: 'flex', flexDirection: 'column', gap: 2 }}>
          <TextField
            fullWidth
            label="Base URL"
            type="url"
            value={baseURL}
            onChange={(e) => onBaseURLChange(e.target.value)}
            placeholder="http://localhost:8080"
            variant="outlined"
          />
          <Button
            variant="outlined"
            color="inherit"
            onClick={onReset}
            fullWidth
          >
            Reset to Default
          </Button>
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} color="primary" variant="contained">
          Done
        </Button>
      </DialogActions>
    </Dialog>
  );
};
