import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import BrandPanel from '../components/BrandPanel'
import { api, hasToken } from '../api'

export default function SignupPage() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ fullName: '', email: '', password: '', confirmPassword: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [showPassword, setShowPassword] = useState(false)

  if (hasToken()) return <Navigate to="/dashboard" replace />
  const update = (event) => setForm({ ...form, [event.target.name]: event.target.value })

  const submit = async (event) => {
    event.preventDefault()
    setError('')
    if (form.password !== form.confirmPassword) return setError('Passwords do not match.')
    if (form.password.length < 8) return setError('Password must contain at least 8 characters.')
    setLoading(true)
    try {
      const result = await api('/auth/signup', {
        method: 'POST',
        body: JSON.stringify({ fullName: form.fullName, email: form.email, password: form.password }),
      })
      navigate('/signin', { replace: true, state: { message: result.message, email: form.email } })
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="auth-layout">
      <BrandPanel variant="signup" />
      <section className="auth-content">
        <div className="auth-card">
          <div className="mobile-brand"><span>CM</span> CampusCare</div>
          <span className="step-label">Get started</span>
          <h2>Create your account</h2>
          <p className="auth-intro">Join your campus maintenance community today.</p>
          {error && <div className="alert error-alert" role="alert">{error}</div>}
          <form onSubmit={submit} className="auth-form">
            <label>Full name<input name="fullName" value={form.fullName} onChange={update} placeholder="e.g. Ishara Perera" autoComplete="name" required /></label>
            <label>Email address<input name="email" type="email" value={form.email} onChange={update} placeholder="you@campus.edu" autoComplete="email" required /></label>
            <label>Password<span className="password-field"><input name="password" type={showPassword ? 'text' : 'password'} value={form.password} onChange={update} placeholder="Minimum 8 characters" autoComplete="new-password" required minLength="8" /><button type="button" onClick={() => setShowPassword(!showPassword)}>{showPassword ? 'Hide' : 'Show'}</button></span></label>
            <label>Confirm password<input name="confirmPassword" type="password" value={form.confirmPassword} onChange={update} placeholder="Enter password again" autoComplete="new-password" required /></label>
            <button className="primary-button" disabled={loading}>{loading ? 'Creating account…' : 'Create account'} <span>→</span></button>
          </form>
          <p className="auth-switch">Already have an account? <Link to="/signin">Sign in</Link></p>
        </div>
      </section>
    </main>
  )
}
