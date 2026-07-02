import { create } from 'zustand';
import { executionService } from '@/services/executionService';
import type { ExecutionStepResponse } from '@/types/execution';

interface ExecutionStore {
  executionId: string | null;
  totalSteps: number;
  currentStep: number;
  stepCache: Record<number, ExecutionStepResponse>;
  currentStepData: ExecutionStepResponse | null;
  isExecuting: boolean;
  isLoadingStep: boolean;
  isPlaying: boolean;
  playIntervalMs: number;
  error: string | null;

  startExecution: (sourceCode: string, projectId: string) => Promise<void>;
  goToStep: (step: number) => Promise<void>;
  nextStep: () => void;
  prevStep: () => void;
  cancel: () => Promise<void>;
  reset: () => void;
  togglePlay: () => void;
  setPlaySpeed: (ms: number) => void;
}

export const useExecutionStore = create<ExecutionStore>((set, get) => {
  let playTimer: any = null;

  const clearPlayTimer = () => {
    if (playTimer) {
      clearInterval(playTimer);
      playTimer = null;
    }
  };

  return {
    executionId: null,
    totalSteps: 0,
    currentStep: 0,
    stepCache: {},
    currentStepData: null,
    isExecuting: false,
    isLoadingStep: false,
    isPlaying: false,
    playIntervalMs: 1000,
    error: null,

    startExecution: async (sourceCode, projectId) => {
      clearPlayTimer();
      set({
        isExecuting: true,
        isPlaying: false,
        error: null,
        executionId: null,
        totalSteps: 0,
        currentStep: 0,
        stepCache: {},
        currentStepData: null,
      });

      try {
        const init = await executionService.run(sourceCode, projectId);
        set({
          executionId: init.executionId,
          totalSteps: init.totalSteps,
          isExecuting: false,
        });

        if (init.totalSteps > 0) {
          await get().goToStep(1);
        }
      } catch (err: any) {
        const errMsg = err.response?.data?.message || err.message || 'Execution failed';
        set({ error: errMsg, isExecuting: false });
      }
    },

    goToStep: async (step) => {
      const { executionId, totalSteps, stepCache, isLoadingStep } = get();
      if (!executionId || step < 1 || step > totalSteps || isLoadingStep) {
        return;
      }

      // Check cache first
      if (stepCache[step]) {
        set({ currentStep: step, currentStepData: stepCache[step] });
        return;
      }

      set({ isLoadingStep: true, error: null });
      try {
        const stepData = await executionService.fetchStep(executionId, step);
        set((s) => ({
          currentStep: step,
          currentStepData: stepData,
          stepCache: { ...s.stepCache, [step]: stepData },
          isLoadingStep: false,
        }));
      } catch (err: any) {
        const errMsg = err.response?.data?.message || err.message || 'Failed to load step details';
        set({ error: errMsg, isLoadingStep: false, isPlaying: false });
        clearPlayTimer();
      }
    },

    nextStep: () => {
      const { currentStep, totalSteps, goToStep } = get();
      if (currentStep < totalSteps) {
        goToStep(currentStep + 1);
      } else {
        // Stop autoplay at end
        set({ isPlaying: false });
        clearPlayTimer();
      }
    },

    prevStep: () => {
      const { currentStep, goToStep } = get();
      if (currentStep > 1) {
        goToStep(currentStep - 1);
      }
    },

    cancel: async () => {
      const { executionId } = get();
      if (!executionId) return;

      clearPlayTimer();
      set({ isPlaying: false });

      try {
        await executionService.cancel(executionId);
      } catch (err: any) {
        console.error('Cancellation failed:', err);
      }
    },

    reset: () => {
      clearPlayTimer();
      set({
        executionId: null,
        totalSteps: 0,
        currentStep: 0,
        stepCache: {},
        currentStepData: null,
        isExecuting: false,
        isLoadingStep: false,
        isPlaying: false,
        error: null,
      });
    },

    togglePlay: () => {
      const { isPlaying, playIntervalMs, nextStep } = get();
      clearPlayTimer();

      if (isPlaying) {
        set({ isPlaying: false });
      } else {
        set({ isPlaying: true });
        playTimer = setInterval(() => {
          nextStep();
        }, playIntervalMs);
      }
    },

    setPlaySpeed: (ms) => {
      set({ playIntervalMs: ms });
      const { isPlaying } = get();
      if (isPlaying) {
        // Restart timer with new interval speed
        clearPlayTimer();
        playTimer = setInterval(() => {
          get().nextStep();
        }, ms);
      }
    },
  };
});
