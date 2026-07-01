import { useState } from 'react';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { Panel } from '@/components/ui/Panel';
import { SHARED_VERSION } from '@vizorix/shared';
import { Terminal } from 'lucide-react';

/**
 * Standard HomePage view.
 * Renders design system components to verify theme switches and styling configurations.
 */
export default function Home() {
  const [clickCount, setClickCount] = useState(0);

  return (
    <div className="mx-auto max-w-4xl space-y-8">
      {/* Welcome Section */}
      <section className="space-y-2">
        <h2 className="text-3xl font-extrabold tracking-tight text-foreground sm:text-4xl">
          Welcome to Vizorix
        </h2>
        <p className="text-muted-foreground text-base">
          Frontend and monorepo workspace configurations initialized successfully.
        </p>
      </section>

      {/* Workspaces Connection status */}
      <Card variant="glass" className="border-emerald-500/20 bg-emerald-500/5">
        <div className="flex items-center gap-3">
          <Terminal className="text-emerald-500 h-5 w-5" />
          <span className="text-sm font-medium text-foreground">
            Shared Package Connected:{' '}
            <code className="bg-muted px-1.5 py-0.5 rounded font-mono text-xs">
              @vizorix/shared@v{SHARED_VERSION}
            </code>
          </span>
        </div>
      </Card>

      {/* Primitives Showcase Grids */}
      <div className="grid gap-6 md:grid-cols-2">
        {/* Buttons Gallery */}
        <Panel header="Button Primitives" footer="Variants, sizes, and loading states are ready.">
          <div className="space-y-4">
            <div className="flex flex-wrap gap-2.5">
              <Button variant="primary" onClick={() => setClickCount((c) => c + 1)}>
                Primary Button
              </Button>
              <Button variant="secondary">Secondary</Button>
              <Button variant="outline">Outline</Button>
              <Button variant="ghost">Ghost</Button>
            </div>
            <div className="flex flex-wrap items-center gap-2.5">
              <Button variant="primary" size="sm">
                Small
              </Button>
              <Button variant="primary" size="md">
                Medium
              </Button>
              <Button variant="primary" size="lg">
                Large
              </Button>
            </div>
            <div className="flex flex-wrap gap-2.5">
              <Button variant="primary" isLoading>
                Loading
              </Button>
              <Button variant="outline" disabled>
                Disabled
              </Button>
            </div>
            <div className="text-sm text-muted-foreground font-mono">
              Action Count: {clickCount}
            </div>
          </div>
        </Panel>

        {/* Cards Gallery */}
        <Panel header="Card Primitives" footer="Simple boxes containing style tokens mappings.">
          <div className="space-y-4">
            <Card>
              <h4 className="font-semibold text-foreground mb-1">Standard Card</h4>
              <p className="text-xs text-muted-foreground">
                Flat background block containing standard layout padding and border metrics.
              </p>
            </Card>

            <Card variant="glass">
              <h4 className="font-semibold text-foreground mb-1">Glassmorphism Card</h4>
              <p className="text-xs text-muted-foreground">
                Transparent backdrop mapping custom transparent borders and shadows.
              </p>
            </Card>
          </div>
        </Panel>
      </div>
    </div>
  );
}
