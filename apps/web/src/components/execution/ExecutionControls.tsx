import { Play, Pause, ChevronLeft, ChevronRight, RotateCcw, Square } from 'lucide-react';
import { useExecution } from '@/hooks/useExecution';

export default function ExecutionControls() {
  const {
    currentStep,
    totalSteps,
    isPlaying,
    isExecuting,
    playIntervalMs,
    togglePlay,
    nextStep,
    prevStep,
    reset,
    cancel,
    setPlaySpeed,
  } = useExecution();

  if (totalSteps === 0 && !isExecuting) {
    return null;
  }

  const speedMs = playIntervalMs;
  const speedLabel = speedMs === 2000 ? '0.5x' : speedMs === 1000 ? '1.0x' : speedMs === 500 ? '2.0x' : `${(1000 / speedMs).toFixed(1)}x`;

  const handleSpeedChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = Number(e.target.value);
    // Map slider values 1, 2, 3 to 2000ms, 1000ms, 500ms
    if (value === 1) setPlaySpeed(2000);
    else if (value === 2) setPlaySpeed(1000);
    else if (value === 3) setPlaySpeed(500);
  };

  const getSliderValue = () => {
    if (speedMs === 2000) return 1;
    if (speedMs === 1000) return 2;
    if (speedMs === 500) return 3;
    return 2;
  };

  return (
    <div className="flex flex-col gap-3 p-4 border-b border-border bg-card">
      <div className="flex items-center justify-between">
        <span className="text-xs font-semibold tracking-wider text-muted-foreground uppercase">
          Debugger Execution
        </span>
        <span className="text-xs font-mono font-bold text-primary">
          Step {currentStep} / {totalSteps}
        </span>
      </div>

      <div className="flex items-center justify-between gap-2">
        <div className="flex items-center gap-1">
          {/* Previous Step */}
          <button
            onClick={prevStep}
            disabled={currentStep <= 1 || isExecuting}
            className="flex items-center justify-center w-8 h-8 rounded hover:bg-muted text-foreground disabled:opacity-30 disabled:pointer-events-none transition-colors"
            title="Step Back (Ctrl+Left)"
          >
            <ChevronLeft size={16} />
          </button>

          {/* Play/Pause Toggle */}
          <button
            onClick={togglePlay}
            disabled={totalSteps <= 1 || isExecuting}
            className="flex items-center justify-center w-8 h-8 rounded bg-primary text-primary-foreground hover:opacity-90 disabled:opacity-50 disabled:pointer-events-none transition-all"
            title={isPlaying ? 'Pause Auto Play' : 'Auto Play (Space)'}
          >
            {isPlaying ? <Pause size={15} /> : <Play size={15} className="ml-0.5" />}
          </button>

          {/* Next Step */}
          <button
            onClick={nextStep}
            disabled={currentStep >= totalSteps || isExecuting}
            className="flex items-center justify-center w-8 h-8 rounded hover:bg-muted text-foreground disabled:opacity-30 disabled:pointer-events-none transition-colors"
            title="Step Forward (Ctrl+Right)"
          >
            <ChevronRight size={16} />
          </button>
        </div>

        <div className="flex items-center gap-1.5">
          {/* Cancel Run */}
          {isExecuting && (
            <button
              onClick={cancel}
              className="flex items-center gap-1.5 px-2.5 py-1.5 rounded bg-red-500/10 text-red-500 hover:bg-red-500/20 text-xs font-medium transition-colors"
              title="Terminate Execution Process"
            >
              <Square size={12} fill="currentColor" />
              Cancel
            </button>
          )}

          {/* Reset */}
          {!isExecuting && totalSteps > 0 && (
            <button
              onClick={reset}
              className="flex items-center gap-1 px-2.5 py-1.5 rounded hover:bg-muted text-muted-foreground hover:text-foreground text-xs font-medium transition-colors"
              title="Close Execution Viewer"
            >
              <RotateCcw size={12} />
              Reset
            </button>
          )}
        </div>
      </div>

      {/* Speed Slider */}
      {!isExecuting && totalSteps > 1 && (
        <div className="flex items-center gap-3 mt-1">
          <span className="text-[10px] uppercase font-bold text-muted-foreground select-none">
            Speed: {speedLabel}
          </span>
          <input
            type="range"
            min={1}
            max={3}
            step={1}
            value={getSliderValue()}
            onChange={handleSpeedChange}
            className="flex-1 h-1 bg-muted rounded-lg appearance-none cursor-pointer accent-primary"
          />
        </div>
      )}
    </div>
  );
}
