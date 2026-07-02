import { useExecution } from '@/hooks/useExecution';

export default function CallStackPanel() {
  const { currentStepData } = useExecution();

  if (!currentStepData) {
    return null;
  }

  const stack = currentStepData.callStack || [];

  return (
    <div className="flex flex-col flex-1 min-h-[140px] overflow-hidden bg-card border-b border-border">
      <div className="px-4 py-2 border-b border-border bg-muted/30 select-none">
        <span className="text-xs font-bold text-foreground uppercase tracking-wider">
          Call Stack
        </span>
      </div>

      <div className="flex-1 overflow-auto">
        {stack.length === 0 ? (
          <div className="flex items-center justify-center h-full p-4 text-xs text-muted-foreground italic">
            Empty thread call stack trace.
          </div>
        ) : (
          <div className="flex flex-col font-mono text-xs">
            {stack.map((frame, index) => {
              const isTop = index === 0;
              return (
                <div
                  key={`${frame.className}-${frame.methodName}-${index}`}
                  className={`flex items-center justify-between px-4 py-2 border-b border-border transition-colors ${
                    isTop
                      ? 'bg-primary/5 border-l-2 border-l-primary font-bold text-primary'
                      : 'text-foreground hover:bg-muted/10'
                  }`}
                >
                  <div className="flex items-center gap-2 truncate">
                    <span className={isTop ? 'text-primary' : 'text-muted-foreground'}>
                      {stack.length - index}.
                    </span>
                    <span className="truncate">
                      {frame.className}.<span className="text-amber-600 dark:text-amber-400">{frame.methodName}</span>
                    </span>
                  </div>
                  <span className="text-[10px] font-sans px-1.5 py-0.5 rounded bg-muted text-muted-foreground shrink-0 select-none">
                    Line {frame.lineNumber}
                  </span>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
