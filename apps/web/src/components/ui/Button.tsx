import React from 'react';

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost';
  size?: 'sm' | 'md' | 'lg';
  isIconOnly?: boolean;
  isLoading?: boolean;
}

/**
 * Reusable Button primitive with style variants, size configs, and dynamic states support.
 */
export function Button({
  children,
  variant = 'primary',
  size = 'md',
  isIconOnly = false,
  isLoading = false,
  className = '',
  disabled,
  ...props
}: ButtonProps) {
  const baseStyle = 'inline-flex items-center justify-center font-medium rounded transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:ring-offset-2 dark:focus:ring-offset-slate-900 disabled:opacity-50 disabled:cursor-not-allowed';
  
  const variants = {
    primary: 'bg-emerald-600 hover:bg-emerald-500 active:bg-emerald-700 text-white shadow-sm border border-emerald-600 hover:border-emerald-500',
    secondary: 'bg-slate-700 hover:bg-slate-600 active:bg-slate-800 text-white border border-slate-700 hover:border-slate-600',
    outline: 'border border-border text-foreground hover:bg-muted active:bg-muted/80',
    ghost: 'text-foreground hover:bg-muted border border-transparent',
  };

  const sizes = isIconOnly 
    ? {
        sm: 'h-8 w-8 text-sm p-0',
        md: 'h-10 w-10 text-base p-0',
        lg: 'h-12 w-12 text-lg p-0',
      }
    : {
        sm: 'px-3 py-1.5 text-xs',
        md: 'px-4 py-2 text-sm',
        lg: 'px-6 py-3 text-base',
      };

  return (
    <button
      className={`${baseStyle} ${variants[variant]} ${sizes[size]} ${className}`}
      disabled={disabled || isLoading}
      {...props}
    >
      {isLoading ? (
        <span className="animate-spin mr-2 h-4 w-4 border-2 border-current border-t-transparent rounded-full" />
      ) : null}
      {children}
    </button>
  );
}
