/**
 * Persisted user preferences controlling Monaco Editor display and behaviour.
 */
export interface EditorSettings {
  /** Font size in pixels (10–24). */
  fontSize: number;
  /** Whether lines longer than the viewport should wrap. */
  wordWrap: 'on' | 'off';
  /** Whether the minimap column gutter is visible. */
  minimap: boolean;
  /** Number of spaces per tab stop. */
  tabSize: 2 | 4 | 8;
}

/**
 * Runtime state of the active editor session.
 */
export interface EditorState {
  /** Current code content inside the editor. */
  code: string;
  /** Whether the code has been modified since the last reset. */
  isDirty: boolean;
  /** Current cursor line (1-indexed). */
  line: number;
  /** Current cursor column (1-indexed). */
  column: number;
}
