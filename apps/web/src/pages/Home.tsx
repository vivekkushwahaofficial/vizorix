import { SHARED_VERSION } from '@vizorix/shared';

export default function Home() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-slate-900 text-white font-sans">
      <h1 className="text-4xl font-bold tracking-tight mb-2">Welcome to Vizorix</h1>
      <p className="text-slate-400 text-lg mb-4">Frontend Initialized Successfully</p>
      <div className="text-xs font-mono text-emerald-400 bg-slate-950 px-4 py-2 rounded border border-slate-800">
        Shared Package Connected: v{SHARED_VERSION}
      </div>
    </div>
  );
}
