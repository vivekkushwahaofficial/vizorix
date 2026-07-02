import { BrowserRouter, Routes, Route } from 'react-router-dom';
import AppLayout from '@/layouts/AppLayout';
import Home from '@/pages/Home';
import EditorPage from '@/pages/EditorPage';

/**
 * Main application routing configurations.
 * Wraps root viewport nodes inside the AppLayout shell.
 */
export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<AppLayout />}>
          <Route path="/" element={<Home />} />
          <Route path="/editor" element={<EditorPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
