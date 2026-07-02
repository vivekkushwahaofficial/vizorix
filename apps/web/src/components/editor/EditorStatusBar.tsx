import { useEditorState } from '@/hooks/useEditorState';
import { useEditorSettings } from '@/hooks/useEditorSettings';

interface EditorStatusBarProps {
  language: string;
}

/**
 * Bottom status strip mirroring VS Code conventions.
 * Displays: language, cursor position, dirty indicator, tab size, word wrap.
 */
export default function EditorStatusBar({ language }: EditorStatusBarProps) {
  const { line, column, isDirty } = useEditorState();
  const [settings] = useEditorSettings();

  return (
    <div
      className="flex items-center justify-between px-4 h-7 text-[11px] font-mono select-none shrink-0 border-t border-border"
      style={{
        background: 'var(--card)',
        color: 'var(--muted-foreground)',
      }}
    >
      {/* Left section */}
      <div className="flex items-center gap-4">
        {/* Language badge */}
        <span
          className="px-2 py-0.5 rounded text-[10px] font-semibold uppercase tracking-wider"
          style={{
            background: 'var(--primary)',
            color: 'var(--primary-foreground)',
          }}
        >
          {language}
        </span>

        {/* Dirty indicator */}
        {isDirty && (
          <span
            className="flex items-center gap-1 text-amber-400"
            title="Unsaved changes"
          >
            <span className="text-sm leading-none">●</span>
            <span>Modified</span>
          </span>
        )}
      </div>

      {/* Right section */}
      <div className="flex items-center gap-5">
        <span title="Cursor position">
          Ln {line}, Col {column}
        </span>
        <span title="Tab size">
          Spaces: {settings.tabSize}
        </span>
        <span title="Word wrap">
          Wrap: {settings.wordWrap === 'on' ? 'On' : 'Off'}
        </span>
        <span title="Font size">
          {settings.fontSize}px
        </span>
      </div>
    </div>
  );
}
