import React from 'react';

export interface PanelProps extends React.HTMLAttributes<HTMLDivElement> {
  header?: React.ReactNode;
  footer?: React.ReactNode;
}

/**
 * Presentational Panel container containing structured header, body, and footer content layers.
 */
export function Panel({
  children,
  header,
  footer,
  className = '',
  ...props
}: PanelProps) {
  return (
    <div
      className={`flex flex-col rounded-lg border border-border bg-card text-card-foreground shadow-sm overflow-hidden ${className}`}
      {...props}
    >
      {header && (
        <div className="border-b border-border px-6 py-4 bg-muted/40 font-semibold text-sm text-foreground">
          {header}
        </div>
      )}
      <div className="flex-1 p-6 overflow-y-auto">
        {children}
      </div>
      {footer && (
        <div className="border-t border-border px-6 py-4 bg-muted/20 text-xs text-muted-foreground">
          {footer}
        </div>
      )}
    </div>
  );
}
