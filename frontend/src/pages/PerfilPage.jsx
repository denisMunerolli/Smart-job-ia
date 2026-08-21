import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { perfilApi } from '../api'
import { useAuth } from '../contexts/AuthContext'
import api from '../api'

export default function PerfilPage() {
  const { logout } = useAuth()
  const navigate   = useNavigate()

  const [form, setForm]           = useState({ nome: '', linkedinUrl: '', githubUrl: '', portfolioUrl: '' })
  const [loading, setLoading]     = useState(true)
  const [msg, setMsg]             = useState('')
  const [salvando, setSalvando]   = useState(false)

  // Modal de exclusão
  const [modalExcluir, setModalExcluir] = useState(false)
  const [senhaExcluir, setSenhaExcluir] = useState('')
  const [verSenha, setVerSenha]         = useState(false)
  const [excluindo, setExcluindo]       = useState(false)
  const [erroExcluir, setErroExcluir]   = useState('')

  useEffect(() => {
    perfilApi.buscar()
      .then(r => setForm({
        nome:         r.data.nome         || '',
        linkedinUrl:  r.data.linkedinUrl  || '',
        githubUrl:    r.data.githubUrl    || '',
        portfolioUrl: r.data.portfolioUrl || '',
      }))
      .finally(() => setLoading(false))
  }, [])

  async function salvar(e) {
    e.preventDefault()
    setSalvando(true); setMsg('')
    try {
      await perfilApi.atualizar(form)
      setMsg('✅ Perfil atualizado com sucesso!')
    } catch (err) {
      setMsg('❌ ' + (err.response?.data?.message || 'Erro ao salvar'))
    } finally { setSalvando(false) }
  }

  async function excluirConta() {
    if (!senhaExcluir) { setErroExcluir('Digite sua senha para confirmar.'); return }
    setExcluindo(true); setErroExcluir('')
    try {
      await api.delete('/api/usuarios/me', { data: { senha: senhaExcluir } })
      logout()
      navigate('/login')
    } catch (err) {
      setErroExcluir(err.response?.data?.message || 'Senha incorreta. Tente novamente.')
      setExcluindo(false)
    }
  }

  if (loading) return <div className="p-8 text-gray-400 animate-pulse">Carregando perfil...</div>

  return (
    <div className="p-4 md:p-8 max-w-2xl mx-auto space-y-6">
      <h2 className="text-2xl font-bold text-gray-900">👤 Perfil</h2>

      {/* Formulário de perfil */}
      <form onSubmit={salvar} className="card space-y-4">
        <h3 className="font-semibold text-gray-900">Informações pessoais</h3>

        {msg && (
          <p className={`text-sm p-3 rounded-lg ${
            msg.startsWith('✅') ? 'bg-green-50 text-green-700 border border-green-200' :
            'bg-red-50 text-red-700 border border-red-200'}`}>
            {msg}
          </p>
        )}

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Nome completo</label>
          <input className="input" value={form.nome}
            onChange={e => setForm(f => ({ ...f, nome: e.target.value }))} />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">LinkedIn</label>
          <input className="input" type="url" placeholder="https://linkedin.com/in/seu-perfil"
            value={form.linkedinUrl}
            onChange={e => setForm(f => ({ ...f, linkedinUrl: e.target.value }))} />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">GitHub</label>
          <input className="input" type="url" placeholder="https://github.com/seu-usuario"
            value={form.githubUrl}
            onChange={e => setForm(f => ({ ...f, githubUrl: e.target.value }))} />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Portfólio</label>
          <input className="input" type="url" placeholder="https://seu-portfolio.com"
            value={form.portfolioUrl}
            onChange={e => setForm(f => ({ ...f, portfolioUrl: e.target.value }))} />
        </div>

        <button className="btn-primary w-full" type="submit" disabled={salvando}>
          {salvando ? 'Salvando...' : 'Salvar alterações'}
        </button>
      </form>

      {/* Zona de perigo */}
      <div className="card border border-red-200 bg-red-50">
        <h3 className="font-semibold text-red-700 mb-1">⚠️ Zona de perigo</h3>
        <p className="text-sm text-red-600 mb-4">
          A exclusão da conta é permanente e irreversível. Todos os seus dados —
          currículos, candidaturas e histórico — serão removidos definitivamente.
        </p>
        <button
          className="bg-red-600 hover:bg-red-700 active:bg-red-800 text-white font-medium py-2.5 px-4 rounded-lg transition-colors text-sm"
          onClick={() => { setModalExcluir(true); setSenhaExcluir(''); setErroExcluir('') }}>
          🗑️ Excluir minha conta
        </button>
      </div>

      {/* Modal de confirmação */}
      {modalExcluir && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm p-6 space-y-4">
            <div className="text-center">
              <div className="text-4xl mb-2">🗑️</div>
              <h3 className="text-xl font-bold text-gray-900">Excluir conta</h3>
              <p className="text-sm text-gray-500 mt-1">
                Esta ação é permanente e não pode ser desfeita.
                Digite sua senha para confirmar.
              </p>
            </div>

            {erroExcluir && (
              <p className="text-sm text-red-600 bg-red-50 p-3 rounded-lg border border-red-200">
                ⚠️ {erroExcluir}
              </p>
            )}

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Sua senha atual
              </label>
              <div className="relative">
                <input
                  className="input pr-10"
                  type={verSenha ? 'text' : 'password'}
                  placeholder="Digite sua senha"
                  value={senhaExcluir}
                  onChange={e => setSenhaExcluir(e.target.value)}
                  onKeyDown={e => e.key === 'Enter' && excluirConta()}
                  autoFocus
                />
                <button type="button"
                  onClick={() => setVerSenha(!verSenha)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600">
                  {verSenha ? '🙈' : '👁️'}
                </button>
              </div>
            </div>

            <div className="flex gap-3">
              <button
                className="btn-secondary flex-1"
                onClick={() => setModalExcluir(false)}
                disabled={excluindo}>
                Cancelar
              </button>
              <button
                className="flex-1 bg-red-600 hover:bg-red-700 text-white font-medium py-2.5 px-4 rounded-lg transition-colors text-sm disabled:opacity-50"
                onClick={excluirConta}
                disabled={excluindo || !senhaExcluir}>
                {excluindo ? 'Excluindo...' : 'Excluir conta'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
