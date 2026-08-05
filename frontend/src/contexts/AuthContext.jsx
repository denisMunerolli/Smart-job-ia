import { createContext, useContext, useState, useEffect } from 'react'
import { authApi } from '../api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser]       = useState(null)
  const [token, setToken]     = useState(localStorage.getItem('token'))
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (token) localStorage.setItem('token', token)
    else localStorage.removeItem('token')
  }, [token])

  async function login(email, senha) {
    setLoading(true)
    try {
      const { data } = await authApi.login(email, senha)
      setToken(data.accessToken)
      setUser({ email })
      return { ok: true }
    } catch (err) {
      return { ok: false, msg: err.response?.data?.message || 'Credenciais inválidas' }
    } finally {
      setLoading(false)
    }
  }

  async function register(email, senha, nome) {
    setLoading(true)
    try {
      await authApi.register(email, senha, nome)
      return { ok: true }
    } catch (err) {
      return { ok: false, msg: err.response?.data?.message || 'Erro ao criar conta' }
    } finally {
      setLoading(false)
    }
  }

  function logout() {
    setToken(null)
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, token, loading, login, logout, register }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
