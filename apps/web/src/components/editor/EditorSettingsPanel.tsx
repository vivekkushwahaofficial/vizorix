import { useEffect, useRef } from 'react';
import { X, Type, AlignLeft, Map, AlignJustify } from 'lucide-react';
import { useEditorSettings } from '@/hooks/useEditorSettings';
import type { EditorSettings } from '@/types/editor';

interface EditorSettingsPanelProps {
  isOpen: boolean;
  onClose: () => void;
}

/**
 * Slide-in settings drawer (right side) for Monaco editor configuration.
 * All changes apply immediately to the editor via the Zustand store.
 * Uses pure CSS transitions — no third-party modal/drawer libraries.
 */
export default function EditorSettingsPanel({ isOpen, onClose }: EditorSettingsPanelProps) {
  const [settings, updateSettings] = useEditorSettings();
  const panelRef = useRef<HTMLDivElement>(null);

  // Close on Escape key
  useEffect(() => {
    const handleKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen) onClose();
    };
    document.addEventListener('keydown', handleKey);
    return () => document.removeEventListener('keydown', handleKey);
  }, [isOpen, onClose]);

  // Focus trap: focus the panel when it opens
  useEffect(() => {
    if (isOpen) panelRef.current?.focus();
  }, [isOpen]);

  const update = <K extends keyof EditorSettings>(key: K, value: EditorSettings[K]) => {
    updateSettings({ [key]: value });
  };

  return (
    <>
      {/* Backdrop */}
      <div
        className="fixed inset-0 z-40 transition-opacity duration-300"
        style={{
          background: 'rgba(0,0,0,0.4)',
          opacity: isOpen ? 1 : 0,
          pointerEvents: isOpen ? 'auto' : 'none',
        }}
        onClick={onClose}
        aria-hidden="true"
      />

      {/* Panel */}
      <div
        ref={panelRef}
        tabIndex={-1}
        role="dialog"
        aria-label="Editor Settings"
        aria-modal="true"
        className="fixed right-0 top-0 z-50 h-full w-80 flex flex-col outline-none shadow-2xl transition-transform duration-300 ease-in-out"
        style={{
          background: 'var(--card)',
          borderLeft: '1px solid var(--border)',
          transform: isOpen ? 'translateX(0)' : 'translateX(100%)',
        }}
      >
        {/* Header */}
        <div
          className="flex items-center justify-between px-5 py-4 shrink-0 border-b"
          style={{ borderColor: 'var(--border)' }}
        >
          <h2 className="text-sm font-semibold" style={{ color: 'var(--foreground)' }}>
            Editor Settings
          </h2>
          <button
            onClick={onClose}
            className="flex items-center justify-center w-7 h-7 rounded transition-colors"
            style={{ color: 'var(--muted-foreground)' }}
            aria-label="Close settings panel"
          >
            <X size={16} />
          </button>
        </div>

        {/* Body */}
        <div className="flex-1 overflow-y-auto px-5 py-6 flex flex-col gap-8">

          {/* Font Size */}
          <SettingSection icon={<Type size={15} />} label="Font Size" value={`${settings.fontSize}px`}>
            <input
              id="setting-font-size"
              type="range"
              min={10}
              max={24}
              step={1}
              value={settings.fontSize}
              onChange={(e) => update('fontSize', Number(e.target.value) as EditorSettings['fontSize'])}
              className="w-full accent-emerald-500 cursor-pointer"
            />
            <div className="flex justify-between text-[10px] mt-1" style={{ color: 'var(--muted-foreground)' }}>
              <span>10</span>
              <span>24</span>
            </div>
          </SettingSection>

          {/* Tab Size */}
          <SettingSection icon={<AlignLeft size={15} />} label="Tab Size">
            <div className="flex gap-2">
              {([2, 4, 8] as EditorSettings['tabSize'][]).map((size) => (
                <button
                  key={size}
                  onClick={() => update('tabSize', size)}
                  className="flex-1 py-1.5 rounded text-xs font-medium transition-all duration-150 border"
                  style={{
                    background: settings.tabSize === size ? 'var(--primary)' : 'var(--muted)',
                    color: settings.tabSize === size ? 'var(--primary-foreground)' : 'var(--foreground)',
                    borderColor: settings.tabSize === size ? 'var(--primary)' : 'var(--border)',
                  }}
                >
                  {size}
                </button>
              ))}
            </div>
          </SettingSection>

          {/* Word Wrap */}
          <SettingSection icon={<AlignJustify size={15} />} label="Word Wrap">
            <Toggle
              id="setting-word-wrap"
              enabled={settings.wordWrap === 'on'}
              onToggle={(on) => update('wordWrap', on ? 'on' : 'off')}
              label={settings.wordWrap === 'on' ? 'Enabled' : 'Disabled'}
            />
          </SettingSection>

          {/* Minimap */}
          <SettingSection icon={<Map size={15} />} label="Minimap">
            <Toggle
              id="setting-minimap"
              enabled={settings.minimap}
              onToggle={(on) => update('minimap', on)}
              label={settings.minimap ? 'Visible' : 'Hidden'}
            />
          </SettingSection>
        </div>

        {/* Footer hint */}
        <div
          className="px-5 py-3 text-[10px] border-t shrink-0"
          style={{ color: 'var(--muted-foreground)', borderColor: 'var(--border)' }}
        >
          Settings are saved automatically and persist across sessions.
        </div>
      </div>
    </>
  );
}

// ─── Sub-components ───────────────────────────────────────────────────────────

function SettingSection({
  icon,
  label,
  value,
  children,
}: {
  icon: React.ReactNode;
  label: string;
  value?: string;
  children: React.ReactNode;
}) {
  return (
    <div className="flex flex-col gap-3">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2" style={{ color: 'var(--muted-foreground)' }}>
          {icon}
          <span className="text-xs font-medium uppercase tracking-wider">{label}</span>
        </div>
        {value && (
          <span
            className="text-xs font-mono font-semibold"
            style={{ color: 'var(--primary)' }}
          >
            {value}
          </span>
        )}
      </div>
      {children}
    </div>
  );
}

function Toggle({
  id,
  enabled,
  onToggle,
  label,
}: {
  id: string;
  enabled: boolean;
  onToggle: (on: boolean) => void;
  label: string;
}) {
  return (
    <div className="flex items-center justify-between">
      <span className="text-xs" style={{ color: 'var(--foreground)' }}>
        {label}
      </span>
      <button
        id={id}
        role="switch"
        aria-checked={enabled}
        onClick={() => onToggle(!enabled)}
        className="relative w-10 h-5 rounded-full transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:ring-offset-2"
        style={{
          background: enabled ? 'var(--primary)' : 'var(--muted)',
          // @ts-expect-error CSS custom property
          '--tw-ring-offset-color': 'var(--card)',
        }}
      >
        <span
          className="absolute top-0.5 left-0.5 w-4 h-4 rounded-full bg-white shadow transition-transform duration-200"
          style={{ transform: enabled ? 'translateX(20px)' : 'translateX(0)' }}
        />
      </button>
    </div>
  );
}
