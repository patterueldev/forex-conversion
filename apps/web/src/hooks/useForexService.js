import { useState, useCallback } from 'react';

/**
 * Mock implementation of ForexService
 * Simulates the KMP ForexMobileService functionality
 */
export const useForexService = () => {
  const [baseURL, setBaseURL] = useState('http://localhost:8080');
  const [simulatedOffline, setSimulatedOffline] = useState(false);
  
  // Mock cached rates storage
  const [cachedRates] = useState({
    'USD-PHP': {
      rate: '56.78',
      updatedAt: 'Feb 11, 2026, 10:00 PM'
    }
  });

  /**
   * Fetch rates on startup - simulates initial data load
   */
  const fetchOnStartUpOnce = useCallback(async () => {
    // Simulate network delay
    await new Promise(resolve => setTimeout(resolve, 800));
    // In real implementation, this would fetch from KMP service
    return true;
  }, []);

  /**
   * Fetch and store latest rate from server
   */
  const fetchAndStoreLatestRate = useCallback(async () => {
    // Simulate network delay
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    if (simulatedOffline) {
      throw new Error('Network offline');
    }
    
    // Mock: Update cached rate timestamp
    cachedRates['USD-PHP'].updatedAt = new Date().toLocaleString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
      hour12: true
    });
    
    return true;
  }, [simulatedOffline, cachedRates]);

  /**
   * Get cached rate for currency pair
   */
  const getCachedRate = useCallback(async (from, to) => {
    await new Promise(resolve => setTimeout(resolve, 200));
    const key = `${from}-${to}`;
    const cached = cachedRates[key] || { rate: '56.78', updatedAt: 'N/A' };
    return cached;
  }, [cachedRates]);

  /**
   * Convert currency using cached or fetched rate
   */
  const convertCurrency = useCallback(async (amount, from, to) => {
    await new Promise(resolve => setTimeout(resolve, 500));
    
    const rate = parseFloat(cachedRates['USD-PHP'].rate);
    const convertedAmount = amount * rate;
    
    return {
      rate: {
        fromCurrency: from,
        toCurrency: to,
        amount: rate
      },
      originalAmount: amount,
      convertedAmount: convertedAmount
    };
  }, [cachedRates]);

  /**
   * Toggle simulated offline mode
   */
  const toggleSimulatedNetwork = useCallback((shouldSimulate) => {
    setSimulatedOffline(shouldSimulate);
  }, []);

  /**
   * Check if currently in simulated offline mode
   */
  const isSimulatedOffline = useCallback(() => {
    return simulatedOffline;
  }, [simulatedOffline]);

  /**
   * Get current base URL
   */
  const getBaseURL = useCallback(() => {
    return baseURL;
  }, [baseURL]);

  /**
   * Update base URL
   */
  const updateBaseURL = useCallback((url) => {
    setBaseURL(url);
  }, []);

  return {
    fetchOnStartUpOnce,
    fetchAndStoreLatestRate,
    getCachedRate,
    convertCurrency,
    toggleSimulatedNetwork,
    isSimulatedOffline,
    getBaseURL,
    setBaseURL: updateBaseURL
  };
};
