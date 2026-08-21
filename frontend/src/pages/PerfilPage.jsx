import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { perfilApi } from '../api'
import { useAuth } from '../contexts/AuthContext'

function CampoSenha({ label, value, onChange, placeholder }) {
  const [ver, setVer] = useState(false)
  return (
    <div>
      <label className="block text-sm font-medium text-gray-700 mb-1">{label}</label>
      <div className="relative">
        <input className="input pr-10" type={ver ? 'text' : 'password'}
          placeholder={placeholder || '••••••••'}
          value={value} onChange={onChange} />
        <button type="button" onClick={() => setVer(!ver)}
          className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600">
          {ver ? '🙈' : '👁️'}
        </button>
      </div>
    </div>
  )
}

export default function PerfilPage() {
  const { logout } = useAuth()
  const navigate   = useNavigate()

  const [form, setForm]       = useState({ nome: '', linkedinUrl: '', githubUrl: '', portfolioUrl: '' })
  const [loading, setLoading] = useState(true)
  const [msgPerfil, setMsgPerfil] = useState('')
  const [salvando, setSalvando]   = useState(false)

  // Alterar senha
  const [senhaAtual, setSenhaAtual]   = useState('')
  const [novaSenha, setNovaSenha]     = useState('')
  const [confirmarSenha, setConfirmarSenha] = useState('')
  const [msgSenha, setMsgSenha]       = useState('')
  const [alterando, setAlterando]     = useState(false)

  // Excluir conta
  const [modalExcluir, setModalExcluir] = useState(false)
  const [senhaExcluir, setSenhaExcluir] = useState('')
  const [erroExcluir, setErroExcluir]   = useState('')
  const [excluindo, setExcluindo]       = useState(false)

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

  async function salvarPerfil(e) {
    e.preventDefault()
    setSalvando(true); setMsgPerfil('')
    try {
      await perfilApi.atualizar(form)
      setMsgPerfil('✅ Perfil atualizado!')
    } catch (err) {
      setMsgPerfil('❌ ' + (err.response?.data?.message || 'Erro ao salvar'))
    } finally { setSalvando(false) }
  }

  async function alterarSenha(e) {
    e.preventDefault()
    setMsgSenha('')
    if (novaSenha.length < 8) { setMsgSenha('❌ A nova senha deve ter pelo menos 8 caracteres.'); return }
    if (novaSenha !== confirmarSenha) { setMsgSenha('❌ As senhas não coincidem.'); return }
    setAlterando(true)
    try {
      await perfilApi.alterarSenha(senhaAtual, novaSenha)
      setMsgSenha('✅ Senha alterada com sucesso!')
      setSenhaAtual(''); setNovaSenha(''); setConfirmarSenha('')
    } catch (err) {
      setMsgSenha('❌ ' + (err.response?.data?.message || 'Erro ao alterar senha'))
    } finally { setAlterando(false) }
  }

  async function excluirConta() {
    if (!senhaExcluir) { setErroExcluir('Digite sua senha para confirmar.'); return }
    setExcluindo(true); setErroExcluir('')
    try {
      await perfilApi.deletarConta(senhaExcluir)
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

      {/* Informações pessoais */}
      <form onSubmit={salvarPerfil} className="card space-y-4">
        <h3 className="font-semibold text-gray-900">Informações pessoais</h3>
        {msgPerfil && (
          <p className={`text-sm p-3 rounded-lg border ${msgPerfil.startsWith('✅')
            ? 'bg-green-50 text-green-700 border-green-200'
            : 'bg-red-50 text-red-700 border-red-200'}`}>{msgPerfil}</p>
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

      {/* Alterar senha */}
      <form onSubmit={alterarSenha} className="card space-y-4">
        <h3 className="font-semibold text-gray-900">🔑 Alterar senha</h3>
        {msgSenha && (
          <p className={`text-sm p-3 rounded-lg border ${msgSenha.startsWith('✅')
            ? 'bg-green-50 text-green-700 border-green-200'
            : 'bg-red-50 text-red-700 border-red-200'}`}>{msgSenha}</p>
        )}
        <CampoSenha label="Senha atual" value={senhaAtual}
          onChange={e => setSenhaAtual(e.target.value)}
          placeholder="Digite sua senha atual" />
        <CampoSenha label="Nova senha" value={novaSenha}
          onChange={e => setNovaSenha(e.target.value)}
          placeholder="Mínimo 8 caracteres" />
        {novaSenha && (
          <p className={`text-xs -mt-3 ${novaSenha.length >= 8 ? 'text-green-600' : 'text-red-500'}`}>
            {novaSenha.length >= 8 ? '✓ Tamanho adequado' : '✗ Mínimo 8 caracteres'}
          </p>
        )}
        <CampoSenha label="Confirmar nova senha" value={confirmarSenha}
          onChange={e => setConfirmarSenha(e.target.value)}
          placeholder="Repita a nova senha" />
        {confirmarSenha && (
          <p className={`text-xs -mt-3 ${novaSenha === confirmarSenha ? 'text-green-600' : 'text-red-500'}`}>
            {novaSenha === confirmarSenha ? '✓ Senhas coincidem' : '✗ Senhas não coincidem'}
          </p>
        )}
        <button className="btn-primary w-full" type="submit" disabled={alterando}>
          {alterando ? 'Alterando...' : 'Alterar senha'}
        </button>
      </form>

      {/* Zona de perigo */}
      <div className="card border border-red-200 bg-red-50">
        <h3 className="font-semibold text-red-700 mb-1">⚠️ Zona de perigo</h3>
        <p className="text-sm text-red-600 mb-4">
          A exclusão é permanente e irreversível. Todos os seus dados —
          currículos, candidaturas e histórico — serão removidos definitivamente.
        </p>
        <button
          className="bg-red-600 hover:bg-red-700 text-white font-medium py-2.5 px-4 rounded-lg text-sm transition-colors"
          onClick={() => { setModalExcluir(true); setSenhaExcluir(''); setErroExcluir('') }}>
          🗑️ Excluir minha conta
        </button>
      </div>

      {/* Modal excluir conta */}
      {modalExcluir && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm p-6 space-y-4">
            <div className="text-center">
              <div className="text-4xl mb-2">🗑️</div>
              <h3 className="text-xl font-bold text-gray-900">Excluir conta</h3>
              <p className="text-sm text-gray-500 mt-1">
                Ação permanente e irreversível. Digite sua senha para confirmar.
              </p>
            </div>
            {erroExcluir && (
              <p className="text-sm text-red-600 bg-red-50 p-3 rounded-lg border border-red-200">
                ⚠️ {erroExcluir}
              </p>
            )}
            <CampoSenha label="Sua senha atual" value={senhaExcluir}
              onChange={e => setSenhaExcluir(e.target.value)}
              placeholder="Digite sua senha" />
            <div className="flex gap-3">
              <button className="btn-secondary flex-1"
                onClick={() => setModalExcluir(false)} disabled={excluindo}>
                Cancelar
              </button>
              <button
                className="flex-1 bg-red-600 hover:bg-red-700 text-white font-medium py-2.5 px-4 rounded-lg text-sm disabled:opacity-50 transition-colors"
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
