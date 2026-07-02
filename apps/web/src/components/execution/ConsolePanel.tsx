import { useEffect, useRef } from 'react';
import { useExecution } from '@/hooks/useExecution';

export default function ConsolePanel() {
  const { currentStepData } = useExecution();
  const consoleEndRef = useRef<HTMLDivElement>(null);

  const stdout = currentStepData?.stdout || '';
  const stderr = currentStepData?.stderr || '';

  // Auto-scroll to the bottom of the console when stdout changes
  useEffect(() => {
    consoleEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [stdout, stderr]);

  if (!currentStepData) {
    return null;
  }

  const hasOutput = stdout.trim() || stderr.trim();

  return (
    <div className="flex flex-col flex-1 min-h-[140px] overflow-hidden bg-card border-b border-border">
      <div className="px-4 py-2 border-b border-border bg-muted/30 select-none flex items-center justify-between">
        <span className="text-xs font-bold text-foreground uppercase tracking-wider">
          Console Output
        </span>
        {hasOutput && (
          <span className="text-[10px] bg-primary/10 text-primary px-1.5 py-0.5 rounded font-mono uppercase tracking-wider font-semibold">
            Interactive
          </span>
        )}
      </div>

      <div className="flex-1 overflow-auto p-4 font-mono text-xs bg-slate-950 text-slate-100 selection:bg-primary/30 leading-relaxed">
        {!hasOutput ? (
          <div className="text-slate-500 italic select-none">No console output recorded yet.</div>
        ) : (
          <div className="whitespace-pre-wrap break-all">
            {stdout && <div className="text-slate-100">{stdout}</div>}
            {stderr && <div className="text-red-400 font-bold">{stderr}</div>}
            <div ref={consoleEndRef} />
          </div>
        )}
      </div>
    </div>
  );
}
