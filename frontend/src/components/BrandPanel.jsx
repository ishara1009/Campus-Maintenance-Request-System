export default function BrandPanel({ variant = 'signup' }) {
  return (
    <aside className="brand-panel">
      <div className="brand-mark"><span>CM</span> CampusCare</div>
      <div className="brand-copy">
        <span className="eyebrow">Smart campus support</span>
        <h1>{variant === 'signup' ? 'A better campus starts with you.' : 'Welcome back to CampusCare.'}</h1>
        <p>{variant === 'signup'
          ? 'Report maintenance issues in seconds and follow every update from one simple workspace.'
          : 'Sign in to track your requests, view progress, and keep your campus running smoothly.'}</p>
      </div>
      <div className="feature-strip"><span>✓ Fast reporting</span><span>✓ Live progress</span></div>
      <div className="orb orb-one" /><div className="orb orb-two" />
    </aside>
  )
}
