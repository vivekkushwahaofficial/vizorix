import { Outlet, useMatch } from 'react-router-dom';
import Header from './Header';
import Sidebar from './Sidebar';

/**
 * Top level layout wrapper, organizing sticky Header, Sidebar, and child outlet viewports.
 * On the /editor route the main area is full-bleed (no padding, no scroll) so Monaco
 * can claim 100% of the available height.
 */
export default function AppLayout() {
  const isEditorRoute = useMatch('/editor');

  return (
    <div className="flex h-screen w-full flex-col overflow-hidden bg-background">
      <Header />
      <div className="flex flex-1 overflow-hidden">
        <Sidebar />
        <main
          className={`flex-1 overflow-hidden transition-colors duration-200 ${
            isEditorRoute ? '' : 'overflow-y-auto px-6 py-8'
          }`}
        >
          <Outlet />
        </main>
      </div>
    </div>
  );
}

