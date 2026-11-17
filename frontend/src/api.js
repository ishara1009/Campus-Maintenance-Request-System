const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'
const TOKEN_KEY = 'campus_maintenance_token'

export const hasToken = () => Boolean(localStorage.getItem(TOKEN_KEY))
export const saveToken = (token) => localStorage.setItem(TOKEN_KEY, token)
export const clearToken = () => localStorage.removeItem(TOKEN_KEY)

export async function api(path, options = {}) {
  const token = localStorage.getItem(TOKEN_KEY)
  const isForm = options.body instanceof FormData
  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: {
      ...(isForm ? {} : { 'Content-Type': 'application/json' }),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
  })

  if (!response.ok) {
    let message = 'Something went wrong. Please try again.'
    try {
      const data = await response.json()
      message = data.message || data.detail || message
    } catch {
      // Keep the friendly fallback for non-JSON responses.
    }
    const error = new Error(message)
    error.status = response.status
    throw error
  }

  return response.status === 204 ? null : response.json()
}

export async function apiBlob(path) {
  const token = localStorage.getItem(TOKEN_KEY)
  const response = await fetch(`${API_URL}${path}`, { headers: token ? { Authorization: `Bearer ${token}` } : {} })
  if (!response.ok) { const error = new Error('Unable to load image.'); error.status = response.status; throw error }
  return response.blob()
}
