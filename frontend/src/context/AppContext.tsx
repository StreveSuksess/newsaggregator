import React, { createContext, useContext, useState, useCallback, useEffect, ReactNode } from 'react';
interface AppContextType {
  isDarkMode: boolean;
  toggleTheme: () => void;
  error: string | null;
  setError: (message: string) => void;
  clearError: () => void;
  isLoading: boolean;
  setIsLoading: (loading: boolean) => void;
}
export const AppContext = createContext<AppContextType>({
  isDarkMode: false,
  toggleTheme: () => {},
  error: null,
  setError: () => {},
  clearError: () => {},
  isLoading: false,
  setIsLoading: () => {},
});
interface AppProviderProps {
  children: ReactNode;
}
export const AppProvider: React.FC<AppProviderProps> = ({ children }) => {
  const [isDarkMode, setIsDarkMode] = useState(() => {
    const saved = localStorage.getItem('darkMode');
    return saved ? JSON.parse(saved) : false;
  });
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  useEffect(() => {
    localStorage.setItem('darkMode', JSON.stringify(isDarkMode));
    document.documentElement.setAttribute('data-theme', isDarkMode ? 'dark' : 'light');
  }, [isDarkMode]);
  useEffect(() => {
    const handleError = (event: ErrorEvent) => {
      console.error('Global error:', event.error);
      setError(event.error?.message || 'Произошла непредвиденная ошибка');
    };
    const handleUnhandledRejection = (event: PromiseRejectionEvent) => {
      console.error('Unhandled promise rejection:', event.reason);
      setError(event.reason?.message || 'Произошла ошибка при выполнении операции');
    };
    window.addEventListener('error', handleError);
    window.addEventListener('unhandledrejection', handleUnhandledRejection);
    return () => {
      window.removeEventListener('error', handleError);
      window.removeEventListener('unhandledrejection', handleUnhandledRejection);
    };
  }, []);
  const toggleTheme = () => {
    setIsDarkMode((prev: boolean) => !prev);
  };
  const clearError = () => {
    setError(null);
  };
  return (
    <AppContext.Provider
      value={{
        isDarkMode,
        toggleTheme,
        error,
        setError,
        clearError,
        isLoading,
        setIsLoading,
      }}
    >
      {children}
    </AppContext.Provider>
  );
};
export const useApp = () => {
  const context = useContext(AppContext);
  if (context === undefined) {
    throw new Error('useApp must be used within an AppProvider');
  }
  return context;
}; 