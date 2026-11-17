import { useCallback, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api, clearToken } from '../api'

const emptyForm = { title: '', category: 'Electrical', location: '', description: '', priority: 'MEDIUM' }

export default function DashboardPage() {
  const navigate = useNavigate()
  const [user, setUser] = useState(null)
  const [summary, setSummary] = useState({ total: 0, pending: 0, inProgress: 0, completed: 0 })
  const [requests, setRequests] = useState([])
  const [form, setForm] = useState(emptyForm)
  const [showForm, setShowForm] = useState(false)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  const signOut = useCallback(async () => {
    try { await api('/auth/logout', { method: 'POST' }) } catch { /* Clear local session regardless. */ }
    clearToken()
    navigate('/signin', { replace: true })
  }, [navigate])

  const loadDashboard = useCallback(async () => {
    try {
      const [me, stats, items] = await Promise.all([
        api('/auth/me'), api('/requests/summary'), api('/requests'),
      ])
      setUser(me); setSummary(stats); setRequests(items)
    } catch (requestError) {
      if (requestError.status === 401) return signOut()
      setError(requestError.message)
    } finally {
      setLoading(false)
    }
  }, [signOut])

  useEffect(() => {
    const loadTimer = window.setTimeout(() => void loadDashboard(), 0)
    return () => window.clearTimeout(loadTimer)
  }, [loadDashboard])

  const createRequest = async (event) => {
    event.preventDefault(); setSaving(true); setError('')
    try {
      await api('/requests', { method: 'POST', body: JSON.stringify(form) })
      setForm(emptyForm); setShowForm(false); await loadDashboard()
    } catch (requestError) {
      setError(requestError.message)
    } finally { setSaving(false) }
  }

  const initials = user?.fullName?.split(' ').map((part) => part[0]).slice(0, 2).join('').toUpperCase() || 'U'

  if (loading) return <div className="page-loader"><div className="loader-mark">CM</div><p>Preparing your dashboard…</p></div>

  return (
    <div className="dashboard-shell">
      <aside className="sidebar">
        <div className="sidebar-brand"><span>CM</span><strong>CampusCare</strong></div>
        <nav><a className="active" href="#overview">⌂ <span>Overview</span></a><a href="#requests">≡ <span>My requests</span></a></nav>
        <div className="sidebar-help"><strong>Need urgent help?</strong><p>Contact campus security for immediate hazards.</p><span>011 234 5678</span></div>
        <button className="logout-button" onClick={signOut}>↪ <span>Sign out</span></button>
      </aside>

      <main className="dashboard-main">
        <header className="topbar"><div className="mobile-dashboard-brand">CampusCare</div><div className="profile"><div><strong>{user?.fullName}</strong><span>{user?.email}</span></div><b>{initials}</b></div></header>
        <div className="dashboard-content" id="overview">
          <section className="welcome-row"><div><span className="eyebrow">Your workspace</span><h1>Good to see you, {user?.fullName?.split(' ')[0]}.</h1><p>Here is what is happening with your maintenance requests.</p></div><button className="new-request-button" onClick={() => setShowForm(true)}>＋ New request</button></section>
          {error && <div className="alert error-alert dashboard-alert">{error}</div>}

          <section className="stats-grid">
            <StatCard label="Total requests" value={summary.total} tone="blue" icon="▦" />
            <StatCard label="Pending" value={summary.pending} tone="amber" icon="◷" />
            <StatCard label="In progress" value={summary.inProgress} tone="purple" icon="↻" />
            <StatCard label="Completed" value={summary.completed} tone="green" icon="✓" />
          </section>

          <section className="requests-card" id="requests">
            <div className="section-heading"><div><h2>Recent requests</h2><p>Your latest maintenance reports and their progress.</p></div>{requests.length > 0 && <button onClick={() => setShowForm(true)}>Add request</button>}</div>
            {requests.length === 0 ? (
              <div className="empty-state"><div>⌁</div><h3>No requests yet</h3><p>Report your first campus maintenance issue and track it here.</p><button onClick={() => setShowForm(true)}>Create first request</button></div>
            ) : (
              <div className="request-list">{requests.map((request) => <RequestRow key={request.id} request={request} />)}</div>
            )}
          </section>
        </div>
      </main>

      {showForm && <div className="modal-backdrop" onMouseDown={() => setShowForm(false)}><section className="request-modal" onMouseDown={(e) => e.stopPropagation()}><div className="modal-heading"><div><span className="step-label">New report</span><h2>Submit a maintenance request</h2></div><button onClick={() => setShowForm(false)} aria-label="Close">×</button></div><form onSubmit={createRequest} className="request-form"><label>Issue title<input value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} placeholder="e.g. Leaking tap in Block B" required maxLength="120" /></label><div className="form-row"><label>Category<select value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })}><option>Electrical</option><option>Plumbing</option><option>Furniture</option><option>IT & Network</option><option>Cleaning</option><option>Other</option></select></label><label>Priority<select value={form.priority} onChange={(e) => setForm({ ...form, priority: e.target.value })}><option value="LOW">Low</option><option value="MEDIUM">Medium</option><option value="HIGH">High</option></select></label></div><label>Campus location<input value={form.location} onChange={(e) => setForm({ ...form, location: e.target.value })} placeholder="Building, floor and room" required maxLength="150" /></label><label>Description<textarea value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} placeholder="Describe the problem clearly…" required maxLength="1200" rows="4" /></label><div className="modal-actions"><button type="button" onClick={() => setShowForm(false)}>Cancel</button><button className="primary-button" disabled={saving}>{saving ? 'Submitting…' : 'Submit request'} <span>→</span></button></div></form></section></div>}
    </div>
  )
}

function StatCard({ label, value, tone, icon }) {
  return <article className={`stat-card ${tone}`}><div className="stat-icon">{icon}</div><div><strong>{value}</strong><span>{label}</span></div></article>
}

function RequestRow({ request }) {
  const status = request.status.replace('_', ' ')
  return <article className="request-row"><div className="request-symbol">{request.category.slice(0, 1)}</div><div className="request-info"><strong>{request.title}</strong><span>{request.location} · {new Date(request.createdAt).toLocaleDateString()}</span></div><span className={`priority priority-${request.priority.toLowerCase()}`}>{request.priority}</span><span className={`status status-${request.status.toLowerCase().replace('_', '-')}`}>{status}</span></article>
}
