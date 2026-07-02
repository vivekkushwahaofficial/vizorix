import { useState, useEffect } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import MonacoEditor from '@/components/editor/MonacoEditor';
import EditorStatusBar from '@/components/editor/EditorStatusBar';
import EditorToolbar from '@/components/editor/EditorToolbar';
import { useExecution } from '@/hooks/useExecution';
import ExecutionControls from '@/components/execution/ExecutionControls';
import VariablesPanel from '@/components/execution/VariablesPanel';
import ConsolePanel from '@/components/execution/ConsolePanel';
import CallStackPanel from '@/components/execution/CallStackPanel';
import HeapPanel from '@/components/execution/HeapPanel';
import { AlertCircle, ArrowLeft } from 'lucide-react';

/**
 * Full-bleed editor page composing Toolbar → Monaco surface → StatusBar.
 *
 * Implements a split screen layout that activates when code is executing,
 * displaying standard visualization panels (controls, variable values, console streams, stack frames).
 */
export default function EditorPage() {
  const [language, setLanguage] = useState('java');
  const [searchParams] = useSearchParams();
  const projectId = searchParams.get('projectId');

  const { executionId, currentStepData, error, reset } = useExecution();

  // Reset execution store when exiting the editor page
  useEffect(() => {
    return () => {
      reset();
    };
  }, []);

  const highlightLine = currentStepData?.lineNumber;

  return (
    <div className="flex flex-col h-full w-full overflow-hidden bg-background">
      {/* Alert / Info Bar if project is missing */}
      {!projectId && (
        <div className="bg-amber-500/10 border-b border-amber-500/20 px-4 py-2 flex items-center justify-between text-xs text-amber-700 dark:text-amber-400 select-none shrink-0 animate-fade-in">
          <div className="flex items-center gap-2">
            <AlertCircle size={14} />
            <span>Scratchpad Mode: select a project on the dashboard to execute and trace code.</span>
          </div>
          <Link
            to="/"
            className="flex items-center gap-1 hover:underline font-semibold text-amber-800 dark:text-amber-300"
          >
            <ArrowLeft size={12} />
            Dashboard
          </Link>
        </div>
      )}

      {/* Editor Main Toolbar */}
      <EditorToolbar language={language} onLanguageChange={setLanguage} projectId={projectId} />

      {/* Split Pane Area */}
      <div className="flex flex-1 w-full overflow-hidden min-h-0">
        {/* Left Side: Editor */}
        <div className="flex-1 flex flex-col h-full min-w-0">
          <MonacoEditor language={language} highlightLine={highlightLine} />
        </div>

        {/* Right Side: Debugger Panel (Visible only when session is active or compiling) */}
        {executionId && (
          <div
            className="w-[420px] min-w-[320px] max-w-[600px] border-l border-border h-full flex flex-col overflow-y-auto shrink-0 select-none bg-card select-none"
            style={{
              resize: 'horizontal',
              overflow: 'auto',
            }}
          >
            <ExecutionControls />
            {error && (
              <div className="p-4 mx-4 my-2 rounded bg-red-500/10 border border-red-500/20 text-xs text-red-600 dark:text-red-400 select-text whitespace-pre-wrap leading-relaxed font-mono">
                {error}
              </div>
            )}
            <div className="flex-1 flex flex-col min-h-0 overflow-y-auto">
              <VariablesPanel />
              <CallStackPanel />
              <ConsolePanel />
              <HeapPanel />
            </div>
          </div>
        )}
      </div>

      {/* Status Bar */}
      <EditorStatusBar language={language} />
    </div>
  );
}
