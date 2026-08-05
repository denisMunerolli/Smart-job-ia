import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'

export default function LoginPage() {
  const { login, loading } = useAuth()
  const navigate = useNavigate()
  const [form, setForm]   = useState({ email: '', senha: '' })
  const [error, setError] = useState('')

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    const result = await login(form.email, form.senha)
    if (result.ok) navigate('/')
    else setError(result.msg)
  }

  return (
    <div className="min-h-screen bg-brand-50 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-brand-900">SmartJobAI</h1>
          <p className="text-gray-500 mt-1">Faça login para continuar</p>
        </div>
        <div className="card">
          <form onSubmit={handleSubmit} className="space-y-4">
            {error && <p className="text-red-600 text-sm bg-red-50 p-3 rounded-lg">{error}</p>}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">E-mail</label>
              <input className="input" type="email" required
                value={form.email} onChange={e => setForm(f => ({ ...f, email: e.target.value }))} />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Senha</label>
              <input className="input" type="password" required
                value={form.senha} onChange={e => setForm(f => ({ ...f, senha: e.target.value }))} />
            </div>
            <button className="btn-primary w-full" type="submit" disabled={loading}>
              {loading ? 'Entrando...' : 'Entrar'}
            </button>
          </form>
          <p className="text-center text-sm text-gray-500 mt-4">
            Não tem conta?{' '}
            <Link to="/register" className="text-brand-600 font-medium hover:underline">Cadastre-se</Link>
          </p>
        </div>
      </div>
    </div>
  )
}
