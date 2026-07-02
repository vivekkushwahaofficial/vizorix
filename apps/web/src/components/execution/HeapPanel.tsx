import { Layers } from 'lucide-react';

export default function HeapPanel() {
  return (
    <div className="flex flex-col flex-1 min-h-[140px] overflow-hidden bg-card">
      <div className="px-4 py-2 border-b border-border bg-muted/30 select-none">
        <span className="text-xs font-bold text-foreground uppercase tracking-wider">
          Object Heap Graph
        </span>
      </div>

      <div className="flex-1 flex flex-col items-center justify-center p-6 text-center select-none text-muted-foreground bg-muted/5">
        <Layers size={22} className="mb-2 text-muted-foreground/60 stroke-[1.5] animate-pulse" />
        <h4 className="text-xs font-semibold text-foreground mb-0.5">Heap Visualizer Stub</h4>
        <p className="text-[10px] max-w-[200px] leading-normal text-muted-foreground/80">
          Timeline mapping of objects reference allocations is coming in Milestone 11.
        </p>
      </div>
    </div>
  );
}
