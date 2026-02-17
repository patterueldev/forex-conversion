import { Box, CircularProgress, Typography } from '@mui/material';

/**
 * LoadingOverlay - Shows loading spinner during async operations
 */
export const LoadingOverlay = () => {
  return (
    <Box
      sx={{
        position: 'fixed',
        inset: 0,
        bgcolor: 'rgba(0, 0, 0, 0.4)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 1300,
      }}
    >
      <Box
        sx={{
          bgcolor: 'background.paper',
          borderRadius: 2,
          p: 4,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          gap: 2,
          boxShadow: 3,
        }}
      >
        <CircularProgress size={48} />
        <Typography variant="h6" fontWeight={600}>
          Processing...
        </Typography>
      </Box>
    </Box>
  );
};
