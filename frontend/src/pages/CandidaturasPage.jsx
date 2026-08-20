import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { candidaturaApi } from '../api'

const STATUS_LABELS = {
  PENDENTE:    { label: 'Pendente',    color: 'bg-gray-100 text-gray-700',     dot: '#94a3b8' },
  ENVIADA:     { label: 'Enviada',     color: 'bg-blue-100 text-blue-700',     dot: '#3b82f6' },
  EM_ANALISE:  { label: 'Em análise',  color: 'bg-yellow-100 text-yellow-700', dot: '#f59e0b' },
  ENTREVISTA:  { label: 'Entrevista',  color: 'bg-purple-100 text-purple-700', dot: '#8b5cf6' },
  APROVADA:    { label: 'Aprovada',    color: 'bg-green-100 text-green-700',   dot: '#10b981' },
  REPROVADA:   { label: 'Reprovada',   color: 'bg-red-100 text-red-700',       dot: '#ef4444' },
  DESISTENCIA: { label: 'Desistência', color: 'bg-orange-100 text-orange-700', dot: '#f97316' },
}

const FUNIL = ['PENDENTE','ENVIADA','EM_ANALISE','ENTREVISTA','APROVADA']

export default function CandidaturasPage() {
  const [cands, setCands]           = useState([])
  const [todasCands, setTodasCands] = useState([]) // todas para o funil
  const [total, setTotal]           = useState(0)
  const [page, setPage]             = useState(0)
  const [filtroStatus, setFiltroStatus] = useState('')
  const [loading, setLoading]       = useState(true)
  const [editId, setEditId]         = useState(null)
  const [novoStatus, setNovoStatus] = useState('')
  const [msgId, setMsgId]           = useState(null)

  function carregar(p = 0, status = filtroStatus) {
    setLoading(true)
    // Buscar todas para o funil
    candidaturaApi.listar({ page: 0, size: 200, sort: 'dataCriacao,desc' })
      .then(r => {
        const all = r.data.content || []
        setTodasCands(all)
        setTotal(r.data.totalElements ?? 0)
        // Filtrar por status se selecionado
        const filtradas = status ? all.filter(c => c.status === status) : all
        setCands(filtradas.slice(p * 15, (p + 1) * 15))
        setPage(p)
      })
      .finally(() => setLoading(false))
  }

  useEffect(() => { carregar(0) }, [])

  async function atualizarStatus(id) {
    if (!novoStatus) return
    try {
      await candidaturaApi.atualizarStatus(id, novoStatus, '')
      setEditId(null)
      setMsgId(null)
      carregar(page)
    } catch (e) {
      alert(e.response?.data?.message || 'Erro ao atualizar status')
    }
  }

  async function remover(id) {
    if (!window.confirm('Remover candidatura pendente?')) return
    try {
      await candidaturaApi.remover(id)
      carregar(page)
    } catch (e) {
      alert(e.response?.data?.message || 'Erro ao remover')
    }
  }

  // Contagens do funil usando TODAS as candidaturas
  const contagens = {}
  todasCands.forEach(c => { contagens[c.status] = (contagens[c.status] || 0) + 1 })
  const maxFunil = Math.max(...FUNIL.map(s => contagens[s] || 0), 1)

  return (
    <div className="p-4 md:p-8 space-y-6 max-w-4xl mx-auto">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-gray-900">📋 Candidaturas</h2>
        <span className="text-sm text-gray-500">{total} total</span>
      </div>

      {/* Funil — baseado em TODAS as candidaturas */}
      <div className="card">
        <h3 className="text-xs font-semibold text-gray-500 mb-4 uppercase tracking-wide">
          Funil de progresso {filtroStatus && `· filtro: ${STATUS_LABELS[filtroStatus]?.label}`}
        </h3>
        <div className="flex items-end gap-1 md:gap-2">
          {FUNIL.map(s => {
            const count = contagens[s] || 0
            const h = Math.max((count / maxFunil) * 80, 4)
            const info = STATUS_LABELS[s]
            const ativo = filtroStatus === s
            return (
              <button key={s}
                onClick={() => {
                  const novo = ativo ? '' : s
                  setFiltroStatus(novo)
                  carregar(0, novo)
                }}
                className={`flex flex-col items-center gap-1 flex-1 transition-all ${ativo ? 'opacity-100 scale-105' : 'opacity-70 hover:opacity-100'}`}>
                <span className="text-xs font-bold text-gray-700">{count}</span>
                <div className="w-full rounded-t-md" style={{ height: `${h}px`, background: info.dot }} />
                <span className="text-xs text-gray-500 text-center leading-tight hidden sm:block">{info.label}</span>
              </button>
            )
          })}
        </div>
        {filtroStatus && (
          <button onClick={() => { setFiltroStatus(''); carregar(0, '') }}
            className="mt-3 text-xs text-brand-600 hover:underline">
            Limpar filtro ×
          </button>
        )}
      </div>

      {/* Lista */}
      {loading ? (
        <p className="text-gray-400 animate-pulse text-center py-8">Carregando candidaturas...</p>
      ) : cands.length === 0 ? (
        <div className="card text-center py-10">
          <p className="text-gray-400 text-lg mb-2">
            {filtroStatus ? `Nenhuma candidatura com status "${STATUS_LABELS[filtroStatus]?.label}"` : 'Nenhuma candidatura ainda.'}
          </p>
          {!filtroStatus && (
            <Link to="/vagas" className="text-brand-600 hover:underline text-sm">
              Buscar vagas →
            </Link>
          )}
        </div>
      ) : (
        <div className="space-y-3">
          {cands.map(c => {
            const s = STATUS_LABELS[c.status] || { label: c.status, color: 'bg-gray-100 text-gray-700' }
            return (
              <div key={c.id} className="card">
                <div className="flex justify-between items-start gap-3">
                  <div className="min-w-0 flex-1">
                    <p className="font-semibold text-gray-900 truncate">{c.vagaTitulo || 'Vaga sem título'}</p>
                    <p className="text-sm text-gray-500">{c.vagaEmpresa || ''}</p>
                    {c.curriculoTitulo && (
                      <p className="text-xs text-gray-400 mt-0.5">📄 {c.curriculoTitulo}</p>
                    )}
                  </div>
                  <div className="flex flex-col items-end gap-1 flex-shrink-0">
                    <span className={`badge ${s.color}`}>{s.label}</span>
                    <span className="text-xs text-gray-400">
                      {new Date(c.dataCriacao).toLocaleDateString('pt-BR')}
                    </span>
                  </div>
                </div>

                {editId === c.id ? (
                  <div className="mt-3 flex flex-col sm:flex-row gap-2">
                    <select className="input flex-1" value={novoStatus}
                      onChange={e => setNovoStatus(e.target.value)}>
                      <option value="">Selecione o novo status</option>
                      {Object.entries(STATUS_LABELS).map(([k, v]) => (
                        <option key={k} value={k}>{v.label}</option>
                      ))}
                    </select>
                    <div className="flex gap-2">
                      <button className="btn-primary flex-1 sm:flex-none" onClick={() => atualizarStatus(c.id)}>Salvar</button>
                      <button className="btn-secondary flex-1 sm:flex-none" onClick={() => setEditId(null)}>Cancelar</button>
                    </div>
                  </div>
                ) : (
                  <div className="mt-3 flex gap-2 flex-wrap">
                    <button className="btn-secondary text-xs"
                      onClick={() => { setEditId(c.id); setNovoStatus(c.status) }}>
                      Atualizar status
                    </button>
                    {c.status === 'PENDENTE' && (
                      <button className="text-red-400 text-xs hover:text-red-600 transition-colors"
                        onClick={() => remover(c.id)}>
                        Remover
                      </button>
                    )}
                  </div>
                )}
              </div>
            )
          })}
        </div>
      )}

      {/* Paginação */}
      {cands.length > 0 && (
        <div className="flex gap-2 items-center">
          {page > 0 && (
            <button className="btn-secondary" onClick={() => carregar(page - 1)}>← Anterior</button>
          )}
          {(page + 1) * 15 < (filtroStatus
            ? todasCands.filter(c => c.status === filtroStatus).length
            : total) && (
            <button className="btn-primary" onClick={() => carregar(page + 1)}>Próxima →</button>
          )}
        </div>
      )}
    </div>
  )
}
