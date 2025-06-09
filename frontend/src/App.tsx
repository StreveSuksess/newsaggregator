import React, { useContext } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, useLocation } from 'react-router-dom';
import { AppContext } from './context/AppContext';
import { ArticleList } from './components/ArticleList';
import { ArticleDetail } from './components/ArticleDetail';
import { Analytics } from './components/Analytics';
import { LoadingState, ErrorState } from './components/common';
const NavLink: React.FC<{ to: string; children: React.ReactNode }> = ({ to, children }) => {
    const { pathname } = useLocation();
    const isActive = pathname === to;
    return (
        <Link
            to={to}
            style={{
                color: isActive ? 'var(--color-primary)' : 'var(--color-text)',
                textDecoration: 'none',
                padding: 'var(--spacing-sm) var(--spacing-md)',
                borderRadius: 'var(--radius)',
                transition: 'all 0.2s',
                background: isActive ? 'var(--color-bg-hover)' : 'transparent',
            }}
            onMouseOver={(e) => {
                if (!isActive) e.currentTarget.style.background = 'var(--color-bg-hover)';
            }}
            onMouseOut={(e) => {
                if (!isActive) e.currentTarget.style.background = 'transparent';
            }}
        >
            {children}
        </Link>
    );
};
const AppContent: React.FC = () => {
    const { isDarkMode, toggleTheme, error, clearError, isLoading } = useContext(AppContext);
    return (
        <div style={{ 
            minHeight: '100vh', 
            display: 'flex', 
            flexDirection: 'column',
            width: '100%',
            maxWidth: '100vw',
            overflowX: 'hidden'
        }} data-theme={isDarkMode ? 'dark' : 'light'}>
            <header style={{
                background: 'var(--color-bg-header)',
                padding: 'var(--spacing-md)',
                boxShadow: 'var(--shadow)',
                position: 'sticky',
                top: 0,
                zIndex: 100,
                width: '100%'
            }}>
                <nav style={{
                    maxWidth: 'var(--max-width)',
                    margin: '0 auto',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    width: '100%',
                    padding: '0 var(--spacing-md)'
                }}>
                    <div style={{ display: 'flex', gap: 'var(--spacing-md)', alignItems: 'center' }}>
                        <Link to="/" style={{ textDecoration: 'none', color: 'var(--color-text)' }}>
                            <h1 style={{ margin: 0, fontSize: '1.5em' }}>Новостной портал</h1>
                        </Link>
                        <NavLink to="/">Статьи</NavLink>
                        <NavLink to="/analytics">Аналитика</NavLink>
                    </div>
                    <button
                        onClick={toggleTheme}
                        style={{
                            background: 'transparent',
                            border: 'none',
                            cursor: 'pointer',
                            padding: 'var(--spacing-sm)',
                            borderRadius: 'var(--radius)',
                            color: 'var(--color-text)',
                            fontSize: '1.2em',
                        }}
                        onMouseOver={(e) => {
                            e.currentTarget.style.background = 'var(--color-bg-hover)';
                        }}
                        onMouseOut={(e) => {
                            e.currentTarget.style.background = 'transparent';
                        }}
                    >
                        {isDarkMode ? '☀️' : '🌙'}
                    </button>
                </nav>
            </header>
            <main style={{
                flex: 1,
                maxWidth: 'var(--max-width)',
                margin: '0 auto',
                width: '100%',
                boxSizing: 'border-box'
            }}>
                {isLoading && <LoadingState fullScreen />}
                {error && <ErrorState message={error} onRetry={clearError} fullScreen />}
                <Routes>
                    <Route path="/" element={<ArticleList />} />
                    <Route path="/article/:id" element={<ArticleDetail />} />
                    <Route path="/analytics" element={<Analytics />} />
                </Routes>
            </main>
            <footer style={{
                background: 'var(--color-bg-header)',
                padding: 'var(--spacing-md)',
                textAlign: 'center',
                color: 'var(--color-text-light)',
                marginTop: 'auto',
            }}>
                <p style={{ margin: 0 }}>
                    © {new Date().getFullYear()} Новостной портал. Все права защищены.
                </p>
            </footer>
        </div>
    );
};
export const App: React.FC = () => (
    <Router>
        <AppContent />
    </Router>
);
