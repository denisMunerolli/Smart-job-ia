import { createContext, useContext, useState, useEffect, useCallback } from 'react'
import { authApi } from '../api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [token,    setToken]    = useState(() => localStorage.getItem('token'))
  const [usuario,  setUsuario]  = useState(null)
  const [loading,  setLoading]  = useState(false)

  // Renova o access token automaticamente usando o refresh token
  const renovarToken = useCallback(async () => {
    const refreshToken = localStorage.getItem('refreshToken')
    if (!refreshToken) return false
    try {
      const { data } = await authApi.refresh(refreshToken)
      localStorage.setItem('token', data.accessToken)
      if (data.refreshToken) localStorage.setItem('refreshToken', data.refreshToken)
      setToken(data.accessToken)
      return true
    } catch {
      return false
    }
  }, [])

  // Verifica se o token está prestes a expirar (menos de 1 hora) e renova
  useEffect(() => {
    const verificar = async () => {
      const t = localStorage.getItem('token')
      if (!t) return

      try {
        const payload = JSON.parse(atob(t.split('.')[1]))
        const expMs   = payload.exp * 1000
        const agoraMs = Date.now()
        const umHora  = 60 * 60 * 1000

        if (expMs - agoraMs < umHora) {
          // Token expira em menos de 1 hora — renova agora
          await renovarToken()
        }
      } catch {}
    }

    verificar()
    // Verifica a cada 15 minutos
    const intervalo = setInterval(verificar, 15 * 60 * 1000)
    return () => clearInterval(intervalo)
  }, [renovarToken])

  async function login(email, senha) {
    setLoading(true)
    try {
      const { data } = await authApi.login(email, senha)
      localStorage.setItem('token', data.accessToken)
      if (data.refreshToken) localStorage.setItem('refreshToken', data.refreshToken)
      setToken(data.accessToken)
      return { ok: true }
    } catch (e) {
      return { ok: false, msg: e.response?.data?.message || 'Credenciais inválidas' }
    } finally {
      setLoading(false)
    }
  }

  async function register(email, senha, nome) {
    setLoading(true)
    try {
      await authApi.register(email, senha, nome)
      return await login(email, senha)
    } catch (e) {
      return { ok: false, msg: e.response?.data?.message || 'Erro ao cadastrar' }
    } finally {
      setLoading(false)
    }
  }

  function logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    setToken(null)
    setUsuario(null)
  }

  return (
    <AuthContext.Provider value={{ token, usuario, loading, login, register, logout, renovarToken }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
