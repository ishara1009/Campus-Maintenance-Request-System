import { useState } from 'react'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import BrandPanel from '../components/BrandPanel'
import { api, hasToken, saveToken } from '../api'

export default function SigninPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const [form, setForm] = useState({ email: location.state?.email || '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [showPassword, setShowPassword] = useState(false)

  if (hasToken()) return <Navigate to="/dashboard" replace />

  const submit = async (event) => {
    event.preventDefault()
    setError('')
    setLoading(true)
    try {
      const result = await api('/auth/signin', { method: 'POST', body: JSON.stringify(form) })
      saveToken(result.token)
      navigate('/dashboard', { replace: true })
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="auth-layout">
      <BrandPanel variant="signin" />
      <section className="auth-content">
        <div className="auth-card signin-card">
          <div className="mobile-brand"><span>CM</span> CampusCare</div>
          <span className="step-label">Welcome back</span>
          <h2>Sign in to your account</h2>
          <p className="auth-intro">Enter your details to open your dashboard.</p>
          {location.state?.message && <div className="alert success-alert">✓ {location.state.message}</div>}
          {error && <div className="alert error-alert" role="alert">{error}</div>}
          <form onSubmit={submit} className="auth-form">
            <label>Email address<input name="email" type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} placeholder="you@campus.edu" autoComplete="email" required /></label>
            <label>Password<span className="password-field"><input name="password" type={showPassword ? 'text' : 'password'} value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} placeholder="Enter your password" autoComplete="current-password" required /><button type="button" onClick={() => setShowPassword(!showPassword)}>{showPassword ? 'Hide' : 'Show'}</button></span></label>
            <button className="primary-button" disabled={loading}>{loading ? 'Signing in…' : 'Sign in'} <span>→</span></button>
          </form>
          <p className="auth-switch">New to CampusCare? <Link to="/signup">Create an account</Link></p>
        </div>
      </section>
    </main>
  )
}
