export interface ExecutionInitResponse {
  executionId: string;
  totalSteps: number;
}

export interface VariableSnapshot {
  name: string;
  value: string;
  type: string;
  scope: 'LOCAL' | 'HEAP';
}

export interface StackFrame {
  className: string;
  methodName: string;
  lineNumber: number;
}

export interface ExecutionStepResponse {
  step: number;
  totalSteps: number;
  lineNumber: number;
  className: string;
  methodName: string;
  stdout: string;
  stderr: string;
  variables: VariableSnapshot[];
  callStack: StackFrame[];
}

export interface ExecutionSessionSummary {
  executionId: string;
  totalSteps: number;
  status: 'PENDING' | 'RUNNING' | 'SUCCESS' | 'FAILED';
  compileTimeMs: number;
  executionTimeMs: number;
}
