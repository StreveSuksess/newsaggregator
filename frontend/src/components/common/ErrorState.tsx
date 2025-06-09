import React from 'react';
interface ErrorStateProps {
  message: string;
  onRetry?: () => void;
  fullScreen?: boolean;
}
export const ErrorState: React.FC<ErrorStateProps> = ({ message, onRetry, fullScreen = false }) => {
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
      textAlign: 'center',
    }}>
      <div style={{
        color: 'var(--color-error)',
        fontSize: '3em',
        marginBottom: 'var(--spacing-md)',
      }}>
        ⚠️
      </div>
      <h3 style={{
        color: 'var(--color-text)',
        fontSize: '1.5em',
        marginBottom: 'var(--spacing-sm)',
      }}>
        Произошла ошибка
      </h3>
      <p style={{
        color: 'var(--color-text-light)',
        marginBottom: 'var(--spacing-lg)',
      }}>
        {message}
      </p>
      {onRetry && (
        <button
          onClick={onRetry}
          style={{
            padding: 'var(--spacing-sm) var(--spacing-lg)',
            background: 'var(--color-primary)',
            color: 'var(--color-text-inverse)',
            border: 'none',
            borderRadius: 'var(--radius)',
            cursor: 'pointer',
            fontSize: '1em',
            transition: 'background-color 0.2s',
          }}
          onMouseOver={(e) => {
            e.currentTarget.style.background = 'var(--color-primary-dark)';
          }}
          onMouseOut={(e) => {
            e.currentTarget.style.background = 'var(--color-primary)';
          }}
        >
          Повторить попытку
        </button>
      )}
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