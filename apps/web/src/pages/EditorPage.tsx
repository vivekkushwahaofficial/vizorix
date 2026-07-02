import { useState } from 'react';
import MonacoEditor from '@/components/editor/MonacoEditor';
import EditorStatusBar from '@/components/editor/EditorStatusBar';
import EditorToolbar from '@/components/editor/EditorToolbar';

/**
 * Full-bleed editor page composing Toolbar → Monaco surface → StatusBar.
 *
 * The outer flex column is intentionally edge-to-edge (no padding) so
 * Monaco can claim the full available height between toolbar and status bar.
 *
 * M10 will bind this page to a specific project via /projects/:projectId/editor.
 */
export default function EditorPage() {
  const [language, setLanguage] = useState('java');

  return (
    <div className="flex flex-col h-full w-full overflow-hidden">
      <EditorToolbar language={language} onLanguageChange={setLanguage} />
      <MonacoEditor language={language} />
      <EditorStatusBar language={language} />
    </div>
  );
}
