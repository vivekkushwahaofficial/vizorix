import { useExecutionStore } from '@/stores/executionStore';

/**
 * Selector hook for active program execution tracking.
 * Isolates component subscription so they don't trigger unnecessary re-renders.
 */
export function useExecution() {
  const executionId = useExecutionStore((s) => s.executionId);
  const totalSteps = useExecutionStore((s) => s.totalSteps);
  const currentStep = useExecutionStore((s) => s.currentStep);
  const stepCache = useExecutionStore((s) => s.stepCache);
  const currentStepData = useExecutionStore((s) => s.currentStepData);
  const isExecuting = useExecutionStore((s) => s.isExecuting);
  const isLoadingStep = useExecutionStore((s) => s.isLoadingStep);
  const isPlaying = useExecutionStore((s) => s.isPlaying);
  const playIntervalMs = useExecutionStore((s) => s.playIntervalMs);
  const error = useExecutionStore((s) => s.error);

  const startExecution = useExecutionStore((s) => s.startExecution);
  const goToStep = useExecutionStore((s) => s.goToStep);
  const nextStep = useExecutionStore((s) => s.nextStep);
  const prevStep = useExecutionStore((s) => s.prevStep);
  const cancel = useExecutionStore((s) => s.cancel);
  const reset = useExecutionStore((s) => s.reset);
  const togglePlay = useExecutionStore((s) => s.togglePlay);
  const setPlaySpeed = useExecutionStore((s) => s.setPlaySpeed);

  return {
    executionId,
    totalSteps,
    currentStep,
    stepCache,
    currentStepData,
    isExecuting,
    isLoadingStep,
    isPlaying,
    playIntervalMs,
    error,
    startExecution,
    goToStep,
    nextStep,
    prevStep,
    cancel,
    reset,
    togglePlay,
    setPlaySpeed,
  };
}
