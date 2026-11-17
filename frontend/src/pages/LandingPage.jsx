import { Link } from 'react-router-dom'
import { hasToken } from '../api'
import './LandingPage.css'

const Arrow = () => <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M5 12h13M13 6l6 6-6 6" /></svg>
const Check = () => <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m5 12 4 4 10-10" /></svg>

export default function LandingPage() {
  const signedIn = hasToken()
  return <div className="landing-page">
    <header className="landing-nav">
      <Link className="landing-logo" to="/"><span>CM</span><strong>CampusCare</strong></Link>
      <nav aria-label="Main navigation"><a href="#how-it-works">How it works</a><a href="#roles">For everyone</a><a href="#why-campuscare">Why CampusCare</a></nav>
      <div className="landing-nav-actions">{!signedIn&&<Link className="nav-signin" to="/signin">Sign in</Link>}<Link className="nav-cta" to={signedIn?'/dashboard':'/signup'}>{signedIn?'Open dashboard':'Get started'} <Arrow/></Link></div>
    </header>

    <main>
      <section className="landing-hero">
        <div className="hero-grid" aria-hidden="true" />
        <div className="hero-glow hero-glow-one" aria-hidden="true" /><div className="hero-glow hero-glow-two" aria-hidden="true" />
        <div className="hero-copy">
          <div className="landing-kicker"><span /> Built for a better campus</div>
          <h1>Campus repairs,<br/><em>without the runaround.</em></h1>
          <p>Report maintenance issues in seconds, follow every update, and help your campus team resolve what matters—clearly and securely.</p>
          <div className="hero-actions"><Link className="hero-primary" to={signedIn?'/dashboard':'/signup'}>{signedIn?'Go to your dashboard':'Report your first issue'} <Arrow/></Link><a className="hero-secondary" href="#how-it-works"><span>▶</span> See how it works</a></div>
          <div className="hero-trust"><span><Check/> Role-based access</span><span><Check/> Private photo uploads</span><span><Check/> Clear status history</span></div>
        </div>

        <div className="hero-product" aria-label="CampusCare dashboard preview">
          <div className="preview-orbit orbit-one"/><div className="preview-orbit orbit-two"/>
          <div className="preview-window">
            <div className="preview-sidebar"><div className="preview-logo">CM</div><i className="active"/><i/><i/><i/><b/></div>
            <div className="preview-main">
              <div className="preview-top"><div><small>STUDENT WORKSPACE</small><strong>Good morning, Maya.</strong></div><span>MP</span></div>
              <div className="preview-stats"><article><small>Total requests</small><b>04</b></article><article><small>In progress</small><b>02</b></article><article><small>Completed</small><b>01</b></article></div>
              <div className="preview-list"><div className="preview-list-head"><div><b>Recent requests</b><small>Track every campus issue</small></div><button>+ New request</button></div>
                <PreviewRow icon="⌁" title="Leaking tap in Block B" meta="Science Building · Room 204" tone="progress" label="In progress"/>
                <PreviewRow icon="⌘" title="Library Wi-Fi outage" meta="Main Library · Level 2" tone="pending" label="Pending"/>
                <PreviewRow icon="◇" title="Broken hallway light" meta="Arts Wing · East corridor" tone="done" label="Completed"/>
              </div>
            </div>
          </div>
          <div className="floating-card assignment-card"><span>✓</span><div><b>Request assigned</b><small>Facilities team notified</small></div></div>
          <div className="floating-card progress-card"><small>Resolution progress</small><div><i/><i/><i/></div><b>2 of 3 steps</b></div>
        </div>
      </section>

      <section className="landing-intro" id="why-campuscare">
        <div><span className="section-number">01</span><p className="section-label">One shared workspace</p></div>
        <h2>From “something’s broken”<br/>to <em>“it’s been handled.”</em></h2>
        <p className="section-lead">CampusCare replaces scattered messages and uncertain follow-ups with one simple, accountable maintenance journey.</p>
      </section>

      <section className="journey-section" id="how-it-works">
        <div className="journey-rail"><span>REPORT</span><i/><span>ASSIGN</span><i/><span>RESOLVE</span></div>
        <div className="journey-cards">
          <article><span className="card-number">01</span><div className="journey-icon report-icon">↗</div><h3>Report it clearly</h3><p>Add the location, priority, description, and an optional photo so the right team has the full picture.</p><ul><li><Check/> Guided request form</li><li><Check/> Secure image attachment</li></ul></article>
          <article className="featured"><span className="card-number">02</span><div className="journey-icon assign-icon">◎</div><h3>Route it responsibly</h3><p>Administrators review priorities and assign open requests to active staff without mixing assignment and status.</p><ul><li><Check/> Staff assignment</li><li><Check/> Role-aware visibility</li></ul></article>
          <article><span className="card-number">03</span><div className="journey-icon resolve-icon">✓</div><h3>Close the loop</h3><p>Students see when work starts, when it finishes, and the repair note that explains what was completed.</p><ul><li><Check/> Persistent notifications</li><li><Check/> Complete event history</li></ul></article>
        </div>
      </section>

      <section className="roles-section" id="roles">
        <div className="roles-copy"><span className="section-number light">02</span><p className="section-label light">Designed around real roles</p><h2>One campus.<br/><em>Three focused views.</em></h2><p>Everyone gets the tools they need—and only the access they should have.</p></div>
        <div className="role-stack">
          <article><div className="role-top"><span>ST</span><small>01</small></div><h3>Students</h3><p>Report issues, manage eligible requests, and follow progress from submission to completion.</p><Link to="/signup">Create student account <Arrow/></Link></article>
          <article><div className="role-top"><span>SF</span><small>02</small></div><h3>Staff</h3><p>See only assigned work, start repairs, and complete each job with a clear repair note.</p><Link to="/signin">Open staff sign in <Arrow/></Link></article>
          <article><div className="role-top"><span>AD</span><small>03</small></div><h3>Administrators</h3><p>Coordinate requests, priorities, assignments, and active maintenance staff from one overview.</p><Link to="/signin">Open admin sign in <Arrow/></Link></article>
        </div>
      </section>

      <section className="landing-cta">
        <div className="cta-mark">CM</div><div><span>Ready when your campus is</span><h2>Make every report count.</h2></div><Link to={signedIn?'/dashboard':'/signup'}>{signedIn?'Open CampusCare':'Create your account'} <Arrow/></Link>
      </section>
    </main>

    <footer className="landing-footer"><Link className="landing-logo footer-logo" to="/"><span>CM</span><strong>CampusCare</strong></Link><p>Clear reports. Accountable repairs. A better campus.</p><div><Link to="/signin">Sign in</Link><Link to="/signup">Create account</Link></div><small>© {new Date().getFullYear()} CampusCare</small></footer>
  </div>
}

function PreviewRow({icon,title,meta,tone,label}) { return <div className="preview-row"><span>{icon}</span><div><b>{title}</b><small>{meta}</small></div><em className={tone}>{label}</em></div> }
