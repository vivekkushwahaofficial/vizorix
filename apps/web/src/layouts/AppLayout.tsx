import { Outlet } from 'react-router-dom';
import Header from './Header';
import Sidebar from './Sidebar';

/**
 * Top level layout wrapper, organizing sticky Header, Sidebar, and child outlet viewports.
 */
export default function AppLayout() {
  return (
    <div className="flex h-screen w-full flex-col overflow-hidden bg-background">
      <Header />
      <div className="flex flex-1 overflow-hidden">
        <Sidebar />
        <main className="flex-1 overflow-y-auto px-6 py-8 transition-colors duration-200">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
