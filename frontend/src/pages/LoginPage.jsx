import { useState, useEffect } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'

export default function LoginPage() {
  const { login, loading } = useAuth()
  const navigate = useNavigate()

  const [form, setForm]         = useState({ email: '', senha: '' })
  const [verSenha, setVerSenha] = useState(false)
  const [lembrar, setLembrar]   = useState(false)
  const [error, setError]       = useState('')

  // Carregar e-mail salvo
  useEffect(() => {
    const emailSalvo = localStorage.getItem('smartjob_email')
    if (emailSalvo) {
      setForm(f => ({ ...f, email: emailSalvo }))
      setLembrar(true)
    }
  }, [])

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')

    if (lembrar) {
      localStorage.setItem('smartjob_email', form.email)
    } else {
      localStorage.removeItem('smartjob_email')
    }

    const result = await login(form.email, form.senha)
    if (result.ok) navigate('/')
    else setError(result.msg)
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-brand-50 to-brand-100 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <div className="text-5xl mb-3">🎯</div>
          <h1 className="text-3xl font-bold text-brand-900">SmartJobAI</h1>
          <p className="text-gray-500 mt-1">Sua carreira inteligente</p>
        </div>

        <div className="card shadow-lg">
          <h2 className="text-xl font-semibold text-gray-800 mb-6">Entrar na sua conta</h2>

          <form onSubmit={handleSubmit} className="space-y-4">
            {error && (
              <div className="flex items-center gap-2 text-red-600 text-sm bg-red-50 p-3 rounded-lg border border-red-200">
                <span>⚠️</span>
                <span>{error}</span>
              </div>
            )}

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">E-mail</label>
              <input className="input" type="email" required autoComplete="email"
                placeholder="seu@email.com"
                value={form.email}
                onChange={e => setForm(f => ({ ...f, email: e.target.value }))} />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Senha</label>
              <div className="relative">
                <input
                  className="input pr-10"
                  type={verSenha ? 'text' : 'password'}
                  required
                  autoComplete="current-password"
                  placeholder="••••••••"
                  value={form.senha}
                  onChange={e => setForm(f => ({ ...f, senha: e.target.value }))} />
                <button type="button"
                  onClick={() => setVerSenha(!verSenha)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors">
                  {verSenha ? '🙈' : '👁️'}
                </button>
              </div>
            </div>

            {/* Lembrar e-mail */}
            <label className="flex items-center gap-2 cursor-pointer select-none">
              <input type="checkbox" checked={lembrar}
                onChange={e => setLembrar(e.target.checked)}
                className="w-4 h-4 rounded border-gray-300 text-brand-600 accent-brand-600" />
              <span className="text-sm text-gray-600">Lembrar meu e-mail</span>
            </label>

            <button className="btn-primary w-full py-3" type="submit" disabled={loading}>
              {loading ? (
                <span className="flex items-center justify-center gap-2">
                  <svg className="animate-spin h-4 w-4" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
                  </svg>
                  Entrando...
                </span>
              ) : 'Entrar'}
            </button>
          </form>

          <div className="mt-6 pt-4 border-t border-gray-100 text-center">
            <p className="text-sm text-gray-500">
              Não tem conta?{' '}
              <Link to="/register" className="text-brand-600 font-medium hover:underline">
                Criar conta grátis
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}
