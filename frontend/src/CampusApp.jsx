import { Navigate, Route, Routes } from 'react-router-dom'
import { hasToken } from './api'
import SignupPage from './pages/SignupPage'
import SigninPage from './pages/SigninPage'
import RoleDashboardPage from './pages/RoleDashboardPage'
import AuthLinkPage from './pages/AuthLinkPage'
import LandingPage from './pages/LandingPage'
import './Campus.css'

function ProtectedRoute({ children }) {
  return hasToken() ? children : <Navigate to="/signin" replace />
}

export default function CampusApp() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/signup" element={<SignupPage />} />
      <Route path="/signin" element={<SigninPage />} />
      <Route path="/dashboard" element={<ProtectedRoute><RoleDashboardPage /></ProtectedRoute>} />
      <Route path="/verify-email" element={<AuthLinkPage mode="verify" />} />
      <Route path="/forgot-password" element={<AuthLinkPage mode="forgot" />} />
      <Route path="/reset-password" element={<AuthLinkPage mode="reset" />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
