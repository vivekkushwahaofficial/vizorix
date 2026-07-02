import { useRef, useCallback } from 'react';
import MonacoReact, { type Monaco, type OnMount } from '@monaco-editor/react';
import { useTheme } from '@/hooks/useTheme';
import { useEditorSettings } from '@/hooks/useEditorSettings';
import { useEditorState } from '@/hooks/useEditorState';

interface MonacoEditorProps {
  language?: string;
}

/**
 * Monaco editor surface with full theme-bridging, settings sync, and cursor tracking.
 *
 * - Theme is derived from the global app theme (dark → vs-dark, light → light).
 * - Editor options are driven by the persisted EditorSettings store.
 * - Cursor position is written back to the EditorState store on every change.
 * - Shows a styled skeleton loader while Monaco workers initialise.
 */
export default function MonacoEditor({ language = 'java' }: MonacoEditorProps) {
  const { isDark } = useTheme();
  const [settings] = useEditorSettings();
  const { code, setCode, updateCursor } = useEditorState();

  const monacoRef = useRef<Monaco | null>(null);

  const handleBeforeMount = useCallback((monaco: Monaco) => {
    monacoRef.current = monaco;

    // Define a custom dark theme that matches the Vizorix design system
    monaco.editor.defineTheme('vizorix-dark', {
      base: 'vs-dark',
      inherit: true,
      rules: [
        { token: 'comment', foreground: '6B7280', fontStyle: 'italic' },
        { token: 'keyword', foreground: '10B981', fontStyle: 'bold' },
        { token: 'string', foreground: 'A5F3D0' },
        { token: 'number', foreground: 'FCD34D' },
        { token: 'type', foreground: '67E8F9' },
      ],
      colors: {
        'editor.background': '#0b0f19',
        'editor.foreground': '#F8FAFC',
        'editorLineNumber.foreground': '#374151',
        'editorLineNumber.activeForeground': '#10B981',
        'editor.lineHighlightBackground': '#111827',
        'editor.selectionBackground': '#10B98130',
        'editor.inactiveSelectionBackground': '#10B98118',
        'editorCursor.foreground': '#10B981',
        'editorIndentGuide.background1': '#1F2937',
        'editorIndentGuide.activeBackground1': '#374151',
        'scrollbarSlider.background': '#1F293780',
        'scrollbarSlider.hoverBackground': '#374151',
        'scrollbarSlider.activeBackground': '#4B5563',
        'editorGutter.background': '#0b0f19',
        'minimap.background': '#111827',
      },
    });

    // Define a custom light theme
    monaco.editor.defineTheme('vizorix-light', {
      base: 'vs',
      inherit: true,
      rules: [
        { token: 'comment', foreground: '94A3B8', fontStyle: 'italic' },
        { token: 'keyword', foreground: '059669', fontStyle: 'bold' },
        { token: 'string', foreground: '065F46' },
        { token: 'number', foreground: 'D97706' },
        { token: 'type', foreground: '0369A1' },
      ],
      colors: {
        'editor.background': '#F8FAFC',
        'editor.foreground': '#0F172A',
        'editorLineNumber.foreground': '#CBD5E1',
        'editorLineNumber.activeForeground': '#059669',
        'editor.lineHighlightBackground': '#F1F5F9',
        'editor.selectionBackground': '#10B98125',
        'editorCursor.foreground': '#10B981',
        'editorIndentGuide.background1': '#E2E8F0',
        'editorGutter.background': '#F8FAFC',
        'minimap.background': '#F1F5F9',
      },
    });
  }, []);

  const handleMount: OnMount = useCallback(
    (editor) => {
      // Sync cursor position to store
      editor.onDidChangeCursorPosition((e) => {
        updateCursor(e.position.lineNumber, e.position.column);
      });
    },
    [updateCursor],
  );

  return (
    <div className="relative flex-1 overflow-hidden" style={{ minHeight: 0 }}>
      <MonacoReact
        height="100%"
        language={language}
        value={code}
        theme={isDark ? 'vizorix-dark' : 'vizorix-light'}
        beforeMount={handleBeforeMount}
        onMount={handleMount}
        onChange={(value) => setCode(value ?? '')}
        loading={<EditorSkeleton />}
        options={{
          fontSize: settings.fontSize,
          wordWrap: settings.wordWrap,
          minimap: { enabled: settings.minimap },
          tabSize: settings.tabSize,
          // UI polish
          fontFamily: '"JetBrains Mono", "Fira Code", "Cascadia Code", monospace',
          fontLigatures: true,
          renderLineHighlight: 'all',
          smoothScrolling: true,
          cursorBlinking: 'phase',
          cursorSmoothCaretAnimation: 'on',
          scrollBeyondLastLine: false,
          padding: { top: 16, bottom: 16 },
          lineNumbersMinChars: 4,
          folding: true,
          formatOnPaste: true,
          automaticLayout: true,
          // Hide the command palette keybinding overlay
          contextmenu: true,
        }}
      />
    </div>
  );
}

// ─── Loading skeleton ─────────────────────────────────────────────────────────

function EditorSkeleton() {
  return (
    <div
      className="flex-1 w-full h-full flex flex-col gap-3 p-6 animate-pulse"
      style={{ background: 'var(--background)' }}
    >
      {[80, 60, 90, 45, 70, 55, 85, 40].map((width, i) => (
        <div
          key={i}
          className="h-4 rounded bg-muted"
          style={{ width: `${width}%`, opacity: 1 - i * 0.08 }}
        />
      ))}
    </div>
  );
}
