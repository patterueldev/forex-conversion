// SDK implementation using the forex-web-sdk package
// This uses callback-based methods instead of async/await
// because suspend functions cannot be exported to JavaScript.

import { useCallback, useRef, useEffect } from 'react';
import sdk from 'forex-web-sdk';

export const useForexService = () => {
  const serviceRef = useRef(null);
  
  // Initialize service once on mount
  useEffect(() => {
    console.log('📦 Initializing ForexWebService...');
    try {
      getService();
      console.log('✅ ForexWebService initialized successfully');
    } catch (e) {
      console.error('❌ Failed to initialize ForexWebService on mount:', e);
    }
  }, []);

  const getService = useCallback(() => {
    if (!serviceRef.current) {
      try {
        console.log('🔍 SDK object:', sdk);
        console.log('🔍 SDK.dev:', sdk.dev);
        console.log('🔍 SDK.dev?.patteruel:', sdk.dev?.patteruel);
        console.log('🔍 SDK.dev?.patteruel?.forexconversion:', sdk.dev?.patteruel?.forexconversion);
        console.log('🔍 SDK.dev?.patteruel?.forexconversion?.web:', sdk.dev?.patteruel?.forexconversion?.web);
        
        // Access ForexWebService from the compiled Kotlin/JS module
        const ForexWebService = sdk.dev?.patteruel?.forexconversion?.web?.ForexWebService;
        console.log('🔍 ForexWebService class:', ForexWebService);
        
        if (!ForexWebService) {
          throw new Error('ForexWebService not found in SDK exports');
        }
        
        serviceRef.current = new ForexWebService();
        console.log('✅ ForexWebService instance created:', serviceRef.current);
        console.log('🔍 Service methods:', Object.keys(serviceRef.current));
        console.log('🔍 convertCurrency method:', serviceRef.current.convertCurrency);
        console.log('🔍 getCurrencyUSD method:', serviceRef.current.getCurrencyUSD);
        console.log('🔍 getCurrencyPHP method:', serviceRef.current.getCurrencyPHP);
      } catch (e) {
        console.error('❌ Failed to initialize ForexWebService:', e);
        console.error('❌ Stack trace:', e.stack);
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
      try {
        console.log('🔍 convertCurrency called with:', { amount, from, to });
        const service = getService();
        console.log('🔍 Service instance:', service);
        
        console.log('🔍 Calling getCurrencyUSD...');
        const usdCurrency = service.getCurrencyUSD();
        console.log('🔍 USD Currency:', usdCurrency);
        
        console.log('🔍 Calling getCurrencyPHP...');
        const phpCurrency = service.getCurrencyPHP();
        console.log('🔍 PHP Currency:', phpCurrency);
        
        const fromCurrency = from === 'USD' ? usdCurrency : phpCurrency;
        const toCurrency = to === 'USD' ? usdCurrency : phpCurrency;
        
        console.log('🔍 From currency:', fromCurrency);
        console.log('🔍 To currency:', toCurrency);
        console.log('🔍 About to call service.convertCurrency with:', {
          amount,
          fromCurrency,
          toCurrency
        });
        
        service.convertCurrency(amount, fromCurrency, toCurrency,
          (converted) => {
            console.log('✅ Conversion success:', converted);
            resolve(converted);
          },
          (error) => {
            console.error('❌ Conversion error callback:', error);
            reject(new Error(error));
          }
        );
      } catch (e) {
        console.error('❌ Exception in convertCurrency:', e);
        console.error('❌ Stack trace:', e.stack);
        reject(e);
      }
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
