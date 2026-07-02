import { useEditorStore } from '@/stores/editorStore';
import type { EditorState } from '@/types/editor';

/**
 * Selector hook for volatile editor runtime state (code content, cursor, dirty flag).
 * Settings changes do not trigger re-renders here.
 */
export function useEditorState(): EditorState & {
  setCode: (code: string) => void;
  updateCursor: (line: number, column: number) => void;
  resetCode: () => void;
} {
  const state = useEditorStore((s) => s.state);
  const setCode = useEditorStore((s) => s.setCode);
  const updateCursor = useEditorStore((s) => s.updateCursor);
  const resetCode = useEditorStore((s) => s.resetCode);

  return { ...state, setCode, updateCursor, resetCode };
}
