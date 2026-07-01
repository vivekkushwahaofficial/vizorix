import { Home } from 'lucide-react';
import { NavLink } from 'react-router-dom';

/**
 * Collapsible side panel presenting workspace navigation hooks.
 */
export default function Sidebar() {
  return (
    <aside className="w-64 border-r border-border bg-sidebar px-4 py-6 shadow-sm transition-all duration-200 hidden md:block">
      <div className="mb-6 px-2 text-xs font-bold uppercase tracking-wider text-muted-foreground">
        Workspace
      </div>
      <nav className="flex flex-col gap-1.5">
        <NavLink
          to="/"
          className={({ isActive }) =>
            `flex items-center gap-3 px-3 py-2 text-sm font-medium rounded transition-all duration-150 ${
              isActive
                ? 'bg-primary text-primary-foreground font-semibold shadow-sm'
                : 'text-foreground hover:bg-muted'
            }`
          }
        >
          <Home size={18} />
          Home
        </NavLink>
      </nav>
    </aside>
  );
}
