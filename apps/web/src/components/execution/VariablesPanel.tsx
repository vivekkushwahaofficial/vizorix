import { useExecution } from '@/hooks/useExecution';

export default function VariablesPanel() {
  const { currentStep, currentStepData, stepCache } = useExecution();

  if (!currentStepData) {
    return null;
  }

  const variables = currentStepData.variables || [];
  const previousStepData = currentStep > 1 ? stepCache[currentStep - 1] : null;
  const previousVariables = previousStepData?.variables || [];

  // Lookup helper for previous variable values to highlight changes
  const getPreviousValue = (name: string) => {
    const prevVar = previousVariables.find((v) => v.name === name);
    return prevVar ? prevVar.value : null;
  };

  return (
    <div className="flex flex-col flex-1 min-h-[160px] overflow-hidden bg-card border-b border-border">
      <div className="px-4 py-2 border-b border-border bg-muted/30 select-none">
        <span className="text-xs font-bold text-foreground uppercase tracking-wider">
          Local Variables
        </span>
      </div>

      <div className="flex-1 overflow-auto">
        {variables.length === 0 ? (
          <div className="flex items-center justify-center h-full p-4 text-xs text-muted-foreground italic">
            No active local variables in current scope frame.
          </div>
        ) : (
          <table className="w-full text-left border-collapse text-xs font-mono">
            <thead>
              <tr className="border-b border-border bg-muted/20 text-muted-foreground font-sans select-none">
                <th className="px-4 py-1.5 font-semibold">Name</th>
                <th className="px-4 py-1.5 font-semibold">Type</th>
                <th className="px-4 py-1.5 font-semibold">Value</th>
              </tr>
            </thead>
            <tbody>
              {variables.map((v) => {
                const prevVal = getPreviousValue(v.name);
                const hasChanged = prevVal !== null && prevVal !== v.value;

                return (
                  <tr
                    key={v.name}
                    className={`border-b border-border hover:bg-muted/10 transition-colors ${
                      hasChanged ? 'bg-amber-500/10' : ''
                    }`}
                  >
                    <td className="px-4 py-2 font-semibold text-emerald-600 dark:text-emerald-400">
                      {v.name}
                    </td>
                    <td className="px-4 py-2 text-muted-foreground truncate max-w-[100px]" title={v.type}>
                      {v.type}
                    </td>
                    <td
                      className={`px-4 py-2 font-bold truncate max-w-[150px] ${
                        hasChanged
                          ? 'text-amber-600 dark:text-amber-400 animate-pulse'
                          : 'text-foreground'
                      }`}
                      title={v.value}
                    >
                      {v.value}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
