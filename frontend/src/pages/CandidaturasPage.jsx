import { useEffect, useState } from 'react'
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
  const [cands, setCands]   = useState([])
  const [total, setTotal]   = useState(0)
  const [page,  setPage]    = useState(0)
  const [filtroStatus, setFiltroStatus] = useState('')
  const [loading, setLoading] = useState(true)
  const [editId, setEditId]   = useState(null)
  const [novoStatus, setNovoStatus] = useState('')

  function carregar(p = 0, status = filtroStatus) {
    setLoading(true)
    candidaturaApi.listar({ page: p, size: 15, sort: 'dataCriacao,desc' })
      .then(r => {
        const content = r.data.content || []
        const filtered = status ? content.filter(c => c.status === status) : content
        setCands(filtered)
        setTotal(r.data.totalElements ?? 0)
        setPage(p)
      })
      .finally(() => setLoading(false))
  }

  useEffect(() => { carregar(0) }, [])

  async function atualizarStatus(id) {
    if (!novoStatus) return
    await candidaturaApi.atualizarStatus(id, novoStatus, '')
    setEditId(null)
    carregar(page)
  }

  async function remover(id) {
    if (!window.confirm('Remover candidatura pendente?')) return
    await candidaturaApi.remover(id)
    carregar(page)
  }

  // contagens para o funil
  const contagens = {}
  cands.forEach(c => { contagens[c.status] = (contagens[c.status] || 0) + 1 })
  const maxFunil = Math.max(...FUNIL.map(s => contagens[s] || 0), 1)

  return (
    <div className="p-8 space-y-6">
      <h2 className="text-2xl font-bold text-gray-900">📋 Candidaturas</h2>

      {/* Funil visual */}
      <div className="card">
        <h3 className="text-sm font-semibold text-gray-600 mb-4 uppercase tracking-wide">Funil de progresso</h3>
        <div className="flex items-end gap-2">
          {FUNIL.map(s => {
            const count = contagens[s] || 0
            const h = Math.max((count / maxFunil) * 80, 4)
            const info = STATUS_LABELS[s]
            return (
              <button key={s}
                onClick={() => { setFiltroStatus(filtroStatus === s ? '' : s); carregar(0, filtroStatus === s ? '' : s) }}
                className={`flex flex-col items-center gap-1 flex-1 group transition-all ${filtroStatus === s ? 'opacity-100' : 'opacity-80 hover:opacity-100'}`}>
                <span className="text-xs font-semibold text-gray-700">{count}</span>
                <div className="w-full rounded-t-md transition-all" style={{ height: `${h}px`, background: info.dot }} />
                <span className="text-xs text-gray-500 text-center leading-tight">{info.label}</span>
              </button>
            )
          })}
        </div>
        {filtroStatus && (
          <button onClick={() => { setFiltroStatus(''); carregar(0, '') }}
            className="mt-3 text-xs text-blue-600 hover:underline">
            Limpar filtro ×
          </button>
        )}
      </div>

      {/* Lista */}
      {loading ? (
        <p className="text-gray-400 animate-pulse">Carregando...</p>
      ) : cands.length === 0 ? (
        <div className="card text-center py-10">
          <p className="text-gray-400 text-lg">Nenhuma candidatura encontrada.</p>
          <a href="/vagas" className="text-blue-600 hover:underline text-sm mt-1 inline-block">Buscar vagas →</a>
        </div>
      ) : (
        <div className="space-y-3">
          {cands.map(c => {
            const s = STATUS_LABELS[c.status] || { label: c.status, color: 'bg-gray-100 text-gray-700' }
            return (
              <div key={c.id} className="card">
                <div className="flex justify-between items-start gap-3">
                  <div className="min-w-0">
                    <p className="font-semibold text-gray-900 truncate">{c.vagaTitulo}</p>
                    <p className="text-sm text-gray-500">{c.vagaEmpresa}</p>
                    {c.curriculoTitulo && (
                      <p className="text-xs text-gray-400 mt-0.5">📄 {c.curriculoTitulo}</p>
                    )}
                    {c.observacao && (
                      <p className="text-xs text-gray-400 mt-1 italic truncate">"{c.observacao}"</p>
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
                  <div className="mt-3 flex gap-2">
                    <select className="input flex-1" value={novoStatus}
                      onChange={e => setNovoStatus(e.target.value)}>
                      <option value="">Selecione o status</option>
                      {Object.entries(STATUS_LABELS).map(([k, v]) => (
                        <option key={k} value={k}>{v.label}</option>
                      ))}
                    </select>
                    <button className="btn-primary" onClick={() => atualizarStatus(c.id)}>Salvar</button>
                    <button className="btn-secondary" onClick={() => setEditId(null)}>Cancelar</button>
                  </div>
                ) : (
                  <div className="mt-3 flex gap-2">
                    <button className="btn-secondary text-sm"
                      onClick={() => { setEditId(c.id); setNovoStatus(c.status) }}>
                      Atualizar status
                    </button>
                    {c.status === 'PENDENTE' && (
                      <button className="text-red-500 text-sm hover:underline"
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
      <div className="flex gap-2">
        {page > 0 && (
          <button className="btn-secondary" onClick={() => carregar(page - 1)}>← Anterior</button>
        )}
        {(page + 1) * 15 < total && (
          <button className="btn-primary" onClick={() => carregar(page + 1)}>Próxima →</button>
        )}
        {total > 0 && (
          <span className="text-sm text-gray-400 self-center ml-2">
            {total} candidatura(s) no total
          </span>
        )}
      </div>
    </div>
  )
}
