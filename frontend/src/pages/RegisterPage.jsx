import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'

export default function RegisterPage() {
  const { register, loading } = useAuth()
  const navigate = useNavigate()

  const [form, setForm] = useState({
    nome: '', email: '', telefone: '', area: '', senha: '', confirmarSenha: ''
  })
  const [verSenha, setVerSenha]           = useState(false)
  const [verConfirmar, setVerConfirmar]   = useState(false)
  const [error, setError]                 = useState('')

  function set(k) { return e => setForm(f => ({ ...f, [k]: e.target.value })) }

  function validar() {
    if (!form.nome.trim())   return 'Nome é obrigatório.'
    if (!form.email.trim())  return 'E-mail é obrigatório.'
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) return 'E-mail inválido.'
    if (form.senha.length < 8) return 'Senha deve ter ao menos 8 caracteres.'
    if (form.senha !== form.confirmarSenha) return 'As senhas não coincidem.'
    return null
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    const err = validar()
    if (err) { setError(err); return }

    const result = await register(form.email, form.senha, form.nome)
    if (result.ok) navigate('/')
    else setError(result.msg)
  }

  const senhaForte = form.senha.length >= 8
  const senhaIguais = form.senha && form.confirmarSenha && form.senha === form.confirmarSenha

  return (
    <div className="min-h-screen bg-gradient-to-br from-brand-50 to-brand-100 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <div className="text-5xl mb-3">🎯</div>
          <h1 className="text-3xl font-bold text-brand-900">SmartJobAI</h1>
          <p className="text-gray-500 mt-1">Crie sua conta gratuita</p>
        </div>

        <div className="card shadow-lg">
          <h2 className="text-xl font-semibold text-gray-800 mb-6">Criar conta</h2>

          <form onSubmit={handleSubmit} className="space-y-4">
            {error && (
              <div className="flex items-center gap-2 text-red-600 text-sm bg-red-50 p-3 rounded-lg border border-red-200">
                <span>⚠️</span><span>{error}</span>
              </div>
            )}

            {/* Nome completo */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Nome completo <span className="text-red-500">*</span>
              </label>
              <input className="input" type="text" required autoComplete="name"
                placeholder="Seu nome completo"
                value={form.nome} onChange={set('nome')} />
            </div>

            {/* E-mail */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                E-mail <span className="text-red-500">*</span>
              </label>
              <input className="input" type="email" required autoComplete="email"
                placeholder="seu@email.com"
                value={form.email} onChange={set('email')} />
            </div>

            {/* Telefone (opcional) */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Telefone / WhatsApp <span className="text-gray-400 font-normal">(opcional)</span>
              </label>
              <input className="input" type="tel" autoComplete="tel"
                placeholder="(48) 99999-9999"
                value={form.telefone} onChange={set('telefone')} />
            </div>

            {/* Área de atuação */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Área de atuação <span className="text-gray-400 font-normal">(opcional)</span>
              </label>
              <select className="input" value={form.area} onChange={set('area')}>
                <option value="">Selecione sua área...</option>
                <option value="backend">Backend Developer</option>
                <option value="frontend">Frontend Developer</option>
                <option value="fullstack">Fullstack Developer</option>
                <option value="mobile">Mobile Developer</option>
                <option value="devops">DevOps / SRE</option>
                <option value="dados">Dados / BI</option>
                <option value="ia">IA / Machine Learning</option>
                <option value="qa">QA / Testes</option>
                <option value="outro">Outro</option>
              </select>
            </div>

            {/* Senha */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Senha <span className="text-red-500">*</span>
              </label>
              <div className="relative">
                <input className="input pr-10"
                  type={verSenha ? 'text' : 'password'}
                  required autoComplete="new-password"
                  placeholder="Mínimo 8 caracteres"
                  value={form.senha} onChange={set('senha')} />
                <button type="button" onClick={() => setVerSenha(!verSenha)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600">
                  {verSenha ? '🙈' : '👁️'}
                </button>
              </div>
              {form.senha && (
                <p className={`text-xs mt-1 ${senhaForte ? 'text-green-600' : 'text-red-500'}`}>
                  {senhaForte ? '✓ Senha com tamanho adequado' : '✗ Mínimo 8 caracteres'}
                </p>
              )}
            </div>

            {/* Confirmar senha */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Confirmar senha <span className="text-red-500">*</span>
              </label>
              <div className="relative">
                <input className="input pr-10"
                  type={verConfirmar ? 'text' : 'password'}
                  required autoComplete="new-password"
                  placeholder="Repita a senha"
                  value={form.confirmarSenha} onChange={set('confirmarSenha')} />
                <button type="button" onClick={() => setVerConfirmar(!verConfirmar)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600">
                  {verConfirmar ? '🙈' : '👁️'}
                </button>
              </div>
              {form.confirmarSenha && (
                <p className={`text-xs mt-1 ${senhaIguais ? 'text-green-600' : 'text-red-500'}`}>
                  {senhaIguais ? '✓ Senhas coincidem' : '✗ Senhas não coincidem'}
                </p>
              )}
            </div>

            <button className="btn-primary w-full py-3" type="submit" disabled={loading}>
              {loading ? (
                <span className="flex items-center justify-center gap-2">
                  <svg className="animate-spin h-4 w-4" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
                  </svg>
                  Criando conta...
                </span>
              ) : 'Criar conta grátis'}
            </button>
          </form>

          <div className="mt-6 pt-4 border-t border-gray-100 text-center">
            <p className="text-sm text-gray-500">
              Já tem conta?{' '}
              <Link to="/login" className="text-brand-600 font-medium hover:underline">Entrar</Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}
