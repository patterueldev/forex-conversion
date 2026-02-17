// SDK implementation using the forex-web-sdk package
// This uses callback-based methods instead of async/await
// because suspend functions cannot be exported to JavaScript.

import { useCallback, useRef } from 'react';
import sdk from 'forex-web-sdk';

export const useForexService = () => {
  const serviceRef = useRef(null);

  const getService = useCallback(() => {
    if (!serviceRef.current) {
      try {
        // Access ForexWebService from the compiled Kotlin/JS module
        const ForexWebService = sdk.dev?.patteruel?.forexconversion?.web?.ForexWebService;
        if (!ForexWebService) {
          throw new Error('ForexWebService not found in SDK exports');
        }
        serviceRef.current = new ForexWebService();
      } catch (e) {
        console.error('Failed to initialize ForexWebService:', e);
        throw new Error('SDK not properly configured. Ensure forex-web-sdk is installed correctly.');
      }
    }
    return serviceRef.current;
  }, []);

  const fetchOnStartUpOnce = useCallback(async () => {
    return new Promise((resolve, reject) => {
      const service = getService();
      service.fetchLatestRate(
        (rate) => resolve(rate),
        (error) => reject(new Error(error))
      );
    });
  }, [getService]);

  const fetchAndStoreLatestRate = useCallback(async () => {
    return new Promise((resolve, reject) => {
      const service = getService();
      service.fetchLatestRate(
        (rate) => resolve(rate),
        (error) => reject(new Error(error))
      );
    });
  }, [getService]);

  const getCachedRate = useCallback(async (from, to) => {
    const service = getService();
    const rate = service.getCachedRate();
    return rate || { fromCurrency: from, toCurrency: to, amount: 0 };
  }, [getService]);

  const convertCurrency = useCallback(async (amount, from, to) => {
    return new Promise((resolve, reject) => {
      const service = getService();
      const fromCurrency = from === 'USD' ? service.getCurrencyUSD() : service.getCurrencyPHP();
      const toCurrency = to === 'USD' ? service.getCurrencyUSD() : service.getCurrencyPHP();
      
      service.convertCurrency(amount, fromCurrency, toCurrency,
        (converted) => resolve(converted),
        (error) => reject(new Error(error))
      );
    });
  }, [getService]);

  const toggleSimulatedNetwork = useCallback((shouldSimulate) => {
    const service = getService();
    service.toggleSimulatedNetwork(shouldSimulate);
  }, [getService]);

  const isSimulatedOffline = useCallback(() => {
    const service = getService();
    return service.isSimulatedOffline();
  }, [getService]);

  const getBaseURL = useCallback(() => {
    const service = getService();
    return service.getBaseURL();
  }, [getService]);

  const setBaseURL = useCallback((url) => {
    const service = getService();
    service.setBaseURL(url);
  }, [getService]);

  return {
    fetchOnStartUpOnce,
    fetchAndStoreLatestRate,
    getCachedRate,
    convertCurrency,
    toggleSimulatedNetwork,
    isSimulatedOffline,
    getBaseURL,
    setBaseURL,
  };
};
