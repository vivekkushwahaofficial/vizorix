import { Home, Code2 } from 'lucide-react';
import { NavLink } from 'react-router-dom';

/**
 * Collapsible side panel presenting workspace navigation hooks.
 */
export default function Sidebar() {
  const navLinkClass = ({ isActive }: { isActive: boolean }) =>
    `flex items-center gap-3 px-3 py-2 text-sm font-medium rounded transition-all duration-150 ${
      isActive
        ? 'bg-primary text-primary-foreground font-semibold shadow-sm'
        : 'text-foreground hover:bg-muted'
    }`;

  return (
    <aside className="w-64 border-r border-border bg-sidebar px-4 py-6 shadow-sm transition-all duration-200 hidden md:block">
      <div className="mb-6 px-2 text-xs font-bold uppercase tracking-wider text-muted-foreground">
        Workspace
      </div>
      <nav className="flex flex-col gap-1.5">
        <NavLink to="/" end className={navLinkClass}>
          <Home size={18} />
          Home
        </NavLink>
        <NavLink to="/editor" className={navLinkClass}>
          <Code2 size={18} />
          Editor
        </NavLink>
      </nav>
    </aside>
  );
}

