import { useState } from 'react';
import { Copy, RotateCcw, Settings, Play, ChevronDown } from 'lucide-react';
import { useEditorState } from '@/hooks/useEditorState';
import { useExecution } from '@/hooks/useExecution';
import EditorSettingsPanel from './EditorSettingsPanel';

interface EditorToolbarProps {
  language: string;
  onLanguageChange: (language: string) => void;
  projectId?: string | null;
}

const SUPPORTED_LANGUAGES = [
  { value: 'java', label: 'Java' },
  { value: 'json', label: 'JSON' },
] as const;

/**
 * Top toolbar for the editor surface.
 * Provides: language selector, Run (placeholder for M10), Copy, Reset, and Settings toggle.
 */
export default function EditorToolbar({ language, onLanguageChange, projectId }: EditorToolbarProps) {
  const { code, isDirty, resetCode } = useEditorState();
  const { isExecuting, startExecution } = useExecution();
  const [isSettingsOpen, setIsSettingsOpen] = useState(false);
  const [copied, setCopied] = useState(false);
  const [langDropdownOpen, setLangDropdownOpen] = useState(false);

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(code);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch {
      // Clipboard API unavailable; fail silently
    }
  };

  const handleReset = () => {
    if (!isDirty) return;
    resetCode();
  };

  /**
   * Entry point for Milestone 10 execution engine binding.
   */
  const handleRun = () => {
    if (!projectId) {
      alert('Please select or create a project on the dashboard first to execute code.');
      return;
    }
    startExecution(code, projectId);
  };

  const selectedLang = SUPPORTED_LANGUAGES.find((l) => l.value === language) ?? SUPPORTED_LANGUAGES[0];

  return (
    <>
      <div
        className="flex items-center justify-between px-4 h-11 shrink-0 border-b"
        style={{ background: 'var(--card)', borderColor: 'var(--border)' }}
      >
        {/* Left: language selector */}
        <div className="relative">
          <button
            id="editor-language-selector"
            onClick={() => setLangDropdownOpen((p) => !p)}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded text-xs font-medium transition-colors duration-150 border"
            style={{
              background: 'var(--muted)',
              color: 'var(--foreground)',
              borderColor: 'var(--border)',
            }}
          >
            <span
              className="w-2 h-2 rounded-full"
              style={{ background: 'var(--primary)' }}
            />
            {selectedLang.label}
            <ChevronDown size={12} style={{ color: 'var(--muted-foreground)' }} />
          </button>

          {langDropdownOpen && (
            <>
              {/* Click-away */}
              <div
                className="fixed inset-0 z-10"
                onClick={() => setLangDropdownOpen(false)}
              />
              <div
                className="absolute left-0 top-full mt-1 z-20 rounded shadow-xl border py-1 min-w-[120px]"
                style={{ background: 'var(--card)', borderColor: 'var(--border)' }}
              >
                {SUPPORTED_LANGUAGES.map((lang) => (
                  <button
                    key={lang.value}
                    className="w-full text-left px-3 py-2 text-xs transition-colors duration-100"
                    style={{
                      color: lang.value === language ? 'var(--primary)' : 'var(--foreground)',
                      background: lang.value === language ? 'var(--muted)' : 'transparent',
                      fontWeight: lang.value === language ? 600 : 400,
                    }}
                    onClick={() => {
                      onLanguageChange(lang.value);
                      setLangDropdownOpen(false);
                    }}
                  >
                    {lang.label}
                  </button>
                ))}
              </div>
            </>
          )}
        </div>

        {/* Right: action buttons */}
        <div className="flex items-center gap-1.5">
          {/* Run — wires to execution engine */}
          <ToolbarButton
            id="editor-run-btn"
            onClick={handleRun}
            title={projectId ? "Run code" : "Select a project to run"}
            disabled={isExecuting || !projectId}
            className="gap-1.5 px-3"
            style={{
              background: projectId ? 'var(--primary)' : 'var(--muted)',
              color: projectId ? 'var(--primary-foreground)' : 'var(--muted-foreground)',
              opacity: isExecuting || !projectId ? 0.6 : 1,
            }}
          >
            <Play size={13} className={isExecuting ? "animate-pulse" : ""} />
            <span className="text-xs font-semibold">{isExecuting ? 'Running...' : 'Run'}</span>
          </ToolbarButton>

          <div className="w-px h-5 mx-1" style={{ background: 'var(--border)' }} />

          {/* Copy */}
          <ToolbarButton
            id="editor-copy-btn"
            onClick={handleCopy}
            title="Copy code to clipboard"
          >
            <Copy size={14} style={{ color: copied ? '#10B981' : 'var(--muted-foreground)' }} />
            {copied && (
              <span className="text-[10px] font-medium" style={{ color: 'var(--primary)' }}>
                Copied!
              </span>
            )}
          </ToolbarButton>

          {/* Reset */}
          <ToolbarButton
            id="editor-reset-btn"
            onClick={handleReset}
            title="Reset to starter code"
            disabled={!isDirty}
            style={{ opacity: isDirty ? 1 : 0.35 }}
          >
            <RotateCcw size={14} style={{ color: 'var(--muted-foreground)' }} />
          </ToolbarButton>

          {/* Settings */}
          <ToolbarButton
            id="editor-settings-btn"
            onClick={() => setIsSettingsOpen(true)}
            title="Open editor settings"
          >
            <Settings
              size={14}
              style={{ color: isSettingsOpen ? 'var(--primary)' : 'var(--muted-foreground)' }}
            />
          </ToolbarButton>
        </div>
      </div>

      <EditorSettingsPanel
        isOpen={isSettingsOpen}
        onClose={() => setIsSettingsOpen(false)}
      />
    </>
  );
}

// ─── Shared toolbar button ────────────────────────────────────────────────────

function ToolbarButton({
  children,
  id,
  onClick,
  title,
  disabled,
  className = '',
  style,
}: {
  children: React.ReactNode;
  id: string;
  onClick: () => void;
  title: string;
  disabled?: boolean;
  className?: string;
  style?: React.CSSProperties;
}) {
  return (
    <button
      id={id}
      onClick={onClick}
      title={title}
      disabled={disabled}
      className={`flex items-center justify-center h-7 px-2 rounded text-xs transition-colors duration-150 ${className}`}
      style={{
        background: 'transparent',
        cursor: disabled ? 'not-allowed' : 'pointer',
        ...style,
      }}
    >
      {children}
    </button>
  );
}
