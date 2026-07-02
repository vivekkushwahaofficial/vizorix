import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { EditorSettings, EditorState } from '@/types/editor';

// ─── Default values ──────────────────────────────────────────────────────────

const DEFAULT_SETTINGS: EditorSettings = {
  fontSize: 14,
  wordWrap: 'off',
  minimap: true,
  tabSize: 4,
};

const DEFAULT_CODE = `public class Main {
    public static void main(String[] args) {
        // Write your Java code here
        System.out.println("Hello, Vizorix!");
    }
}
`;

const DEFAULT_STATE: EditorState = {
  code: DEFAULT_CODE,
  isDirty: false,
  line: 1,
  column: 1,
};

// ─── Store shape ─────────────────────────────────────────────────────────────

interface EditorStore {
  settings: EditorSettings;
  state: EditorState;

  // Settings actions
  updateSettings: (patch: Partial<EditorSettings>) => void;

  // State actions
  setCode: (code: string) => void;
  markDirty: (isDirty: boolean) => void;
  updateCursor: (line: number, column: number) => void;
  resetCode: () => void;
}

// ─── Store ───────────────────────────────────────────────────────────────────

/**
 * Global Zustand store for editor session state and persisted user settings.
 * Settings are persisted to localStorage; runtime state (code, cursor) is not.
 */
export const useEditorStore = create<EditorStore>()(
  persist(
    (set) => ({
      settings: DEFAULT_SETTINGS,
      state: DEFAULT_STATE,

      updateSettings: (patch) =>
        set((s) => ({ settings: { ...s.settings, ...patch } })),

      setCode: (code) =>
        set((s) => ({
          state: { ...s.state, code, isDirty: code !== DEFAULT_CODE },
        })),

      markDirty: (isDirty) =>
        set((s) => ({ state: { ...s.state, isDirty } })),

      updateCursor: (line, column) =>
        set((s) => ({ state: { ...s.state, line, column } })),

      resetCode: () =>
        set({ state: DEFAULT_STATE }),
    }),
    {
      name: 'vizorix-editor-settings',
      // Only persist settings — not volatile runtime state
      partialize: (s) => ({ settings: s.settings }),
    },
  ),
);
