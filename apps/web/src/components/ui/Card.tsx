import React from 'react';

export interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  variant?: 'default' | 'glass';
}

/**
 * Reusable Card container widget with optional glass backdrop blur support.
 */
export function Card({
  children,
  variant = 'default',
  className = '',
  ...props
}: CardProps) {
  const baseStyle = 'rounded-lg border border-border p-6 transition-all duration-200';
  
  const variants = {
    default: 'bg-card text-card-foreground shadow-sm',
    glass: 'bg-card/60 backdrop-blur-md text-card-foreground shadow-sm',
  };

  return (
    <div
      className={`${baseStyle} ${variants[variant]} ${className}`}
      {...props}
    >
      {children}
    </div>
  );
}
