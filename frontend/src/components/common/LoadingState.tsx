import React from 'react';
interface LoadingStateProps {
  fullScreen?: boolean;
  message?: string;
}
export const LoadingState: React.FC<LoadingStateProps> = ({ fullScreen = false, message = 'Загрузка...' }) => {
  const content = (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      gap: 'var(--spacing-md)',
      padding: 'var(--spacing-xl)',
      background: fullScreen ? 'var(--color-bg)' : 'var(--color-bg-card)',
      borderRadius: fullScreen ? 0 : 'var(--radius)',
      boxShadow: fullScreen ? 'none' : 'var(--shadow)',
      position: fullScreen ? 'fixed' : 'relative',
      top: fullScreen ? 0 : 'auto',
      left: fullScreen ? 0 : 'auto',
      right: fullScreen ? 0 : 'auto',
      bottom: fullScreen ? 0 : 'auto',
      width: fullScreen ? '100vw' : 'auto',
      height: fullScreen ? '100vh' : 'auto',
      zIndex: fullScreen ? 1000 : 'auto',
    }}>
      <div className="loading-spinner" />
      <p style={{
        color: 'var(--color-text-light)',
        fontSize: '1.1em',
        textAlign: 'center',
      }}>
        {message}
      </p>
    </div>
  );
  return fullScreen ? (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      background: 'rgba(0, 0, 0, 0.5)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      zIndex: 1000,
    }}>
      {content}
    </div>
  ) : content;
}; 