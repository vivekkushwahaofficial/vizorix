import { apiClient } from '@/lib/apiClient';
import type {
  ExecutionInitResponse,
  ExecutionSessionSummary,
  ExecutionStepResponse,
} from '@/types/execution';

/** Service wrapping all backend execution endpoints. */
export const executionService = {
  /** Compiles and initiates source code execution. */
  async run(sourceCode: string, projectId: string): Promise<ExecutionInitResponse> {
    const response = await apiClient.post<ExecutionInitResponse>('/execute', {
      sourceCode,
      projectId,
    });
    return response.data;
  },

  /** Retrieves metadata and progress for a session. */
  async getSession(executionId: string): Promise<ExecutionSessionSummary> {
    const response = await apiClient.get<ExecutionSessionSummary>(`/execute/${executionId}`);
    return response.data;
  },

  /** Fetches a single step snapshot lazily. */
  async fetchStep(executionId: string, stepNumber: number): Promise<ExecutionStepResponse> {
    const response = await apiClient.get<ExecutionStepResponse>(
      `/execute/${executionId}/step/${stepNumber}`
    );
    return response.data;
  },

  /** Terminates any running tracer subprocess. */
  async cancel(executionId: string): Promise<void> {
    await apiClient.post(`/execute/${executionId}/cancel`);
  },
};
