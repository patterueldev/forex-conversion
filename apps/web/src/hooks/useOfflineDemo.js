import { useState, useCallback } from 'react';
import { useForexService } from './useForexService.sdk';

/**
 * Main hook for web app state management
 * Coordinates UI state and ForexService interactions
 */
export const useOfflineDemo = () => {
  const forexService = useForexService();
  
  const [isLoading, setIsLoading] = useState(false);
  const [convertedAmount, setConvertedAmount] = useState('-');
  const [amountText, setAmountText] = useState('1.0');
  const [baseURL, setBaseURL] = useState(forexService.getBaseURL());
  const [showingSettingsModal, setShowingSettingsModal] = useState(false);

  /**
   * Convert currency
   */
  const convert = useCallback(async () => {
    setIsLoading(true);
    try {
      const amount = parseFloat(amountText) || 0.0;
      console.log('🔄 Converting:', { amount, from: 'USD', to: 'PHP', baseURL });
      const converted = await forexService.convertCurrency(amount, 'USD', 'PHP');
      console.log('✅ Conversion result:', converted);
      
      const output = `${converted.originalAmount} USD → ${converted.convertedAmount.toFixed(2)} PHP @ rate=${converted.rate.amount.toFixed(2)}`;
      
      setConvertedAmount(output);
    } catch (error) {
      console.error('❌ Conversion failed:', error);
      setConvertedAmount('error');
    }
    setIsLoading(false);
  }, [amountText, forexService, baseURL]);

  /**
   * Update base URL
   */
  const updateBaseURL = useCallback((url) => {
    setBaseURL(url);
    if (url) {
      forexService.setBaseURL(url);
    }
  }, [forexService]);

  /**
   * Reset base URL to default
   */
  const resetBaseURL = useCallback(() => {
    const defaultURL = 'http://localhost:8080';
    setBaseURL(defaultURL);
    forexService.setBaseURL(defaultURL);
  }, [forexService]);

  return {
    // State
    isLoading,
    convertedAmount,
    amountText,
    baseURL,
    showingSettingsModal,
    
    // Actions
    setAmountText,
    convert,
    updateBaseURL,
    resetBaseURL,
    setShowingSettingsModal
  };
};
