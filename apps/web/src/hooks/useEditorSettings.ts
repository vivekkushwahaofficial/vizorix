import { useEditorStore } from '@/stores/editorStore';
import type { EditorSettings } from '@/types/editor';

/**
 * Selector hook for editor display settings and their update action.
 * Components subscribe only to the settings slice — no re-renders from cursor moves.
 */
export function useEditorSettings(): [EditorSettings, (patch: Partial<EditorSettings>) => void] {
  const settings = useEditorStore((s) => s.settings);
  const updateSettings = useEditorStore((s) => s.updateSettings);
  return [settings, updateSettings];
}
