import { Navigate, Route, Routes } from 'react-router-dom'
import { hasToken } from './api'
import SignupPage from './pages/SignupPage'
import SigninPage from './pages/SigninPage'
import DashboardPage from './pages/DashboardPage'
import './Campus.css'

function ProtectedRoute({ children }) {
  return hasToken() ? children : <Navigate to="/signin" replace />
}

export default function CampusApp() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to={hasToken() ? '/dashboard' : '/signup'} replace />} />
      <Route path="/signup" element={<SignupPage />} />
      <Route path="/signin" element={<SigninPage />} />
      <Route path="/dashboard" element={<ProtectedRoute><DashboardPage /></ProtectedRoute>} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
