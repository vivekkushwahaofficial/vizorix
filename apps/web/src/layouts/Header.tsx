import { Button } from '@/components/ui/Button';
import { useTheme } from '@/hooks/useTheme';
import { Sun, Moon, Terminal } from 'lucide-react';

/**
 * Global navigation header, housing product branding title and the dark mode switch.
 */
export default function Header() {
  const { isDark, toggleTheme } = useTheme();

  return (
    <header className="sticky top-0 z-40 flex h-16 w-full items-center justify-between border-b border-border bg-card px-6 shadow-sm transition-all duration-200">
      <div className="flex items-center gap-2.5">
        <div className="flex h-10 w-10 items-center justify-center rounded bg-primary text-primary-foreground">
          <Terminal size={22} className="stroke-[2.5]" />
        </div>
        <div>
          <h1 className="text-xl font-bold tracking-tight text-foreground leading-none">Vizorix</h1>
          <span className="text-[10px] text-muted-foreground uppercase font-semibold tracking-wider">
            Execution Visualizer
          </span>
        </div>
      </div>

      <div className="flex items-center gap-4">
        <Button
          variant="ghost"
          size="sm"
          isIconOnly
          onClick={toggleTheme}
          title={isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
        >
          {isDark ? <Sun size={18} className="text-amber-400" /> : <Moon size={18} className="text-slate-700" />}
        </Button>
      </div>
    </header>
  );
}
