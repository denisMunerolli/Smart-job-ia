import { useEffect, useState } from 'react'
import { candidaturaApi } from '../api'

const STATUS_LABELS = {
  PENDENTE: { label: 'Pendente',    color: 'bg-gray-100 text-gray-700'   },
  ENVIADA:  { label: 'Enviada',     color: 'bg-blue-100 text-blue-700'   },
  EM_ANALISE:{ label: 'Em análise', color: 'bg-yellow-100 text-yellow-700'},
  ENTREVISTA:{ label: 'Entrevista', color: 'bg-purple-100 text-purple-700'},
  APROVADA: { label: 'Aprovada',    color: 'bg-green-100 text-green-700' },
  REPROVADA:{ label: 'Reprovada',   color: 'bg-red-100 text-red-700'     },
  DESISTENCIA:{ label: 'Desistência',color: 'bg-orange-100 text-orange-700'},
}

export default function CandidaturasPage() {
  const [cands, setCands]     = useState([])
  const [loading, setLoading] = useState(true)
  const [editId, setEditId]   = useState(null)
  const [novoStatus, setNovoStatus] = useState('')

  function carregar() {
    candidaturaApi.listar({ size: 50 })
      .then(r => setCands(r.data.content))
      .finally(() => setLoading(false))
  }

  useEffect(() => { carregar() }, [])

  async function atualizarStatus(id) {
    if (!novoStatus) return
    await candidaturaApi.atualizarStatus(id, novoStatus, '')
    setEditId(null)
    carregar()
  }

  async function remover(id) {
    if (!window.confirm('Remover candidatura pendente?')) return
    await candidaturaApi.remover(id)
    carregar()
  }

  if (loading) return <div className="p-8 text-gray-400">Carregando...</div>

  return (
    <div className="p-8">
      <h2 className="text-2xl font-bold mb-6">📋 Candidaturas</h2>

      {cands.length === 0
        ? <p className="text-gray-400">Nenhuma candidatura ainda. <a href="/vagas" className="text-brand-600 hover:underline">Buscar vagas →</a></p>
        : (
          <div className="space-y-3">
            {cands.map(c => {
              const s = STATUS_LABELS[c.status] || { label: c.status, color: 'bg-gray-100 text-gray-700' }
              return (
                <div key={c.id} className="card">
                  <div className="flex justify-between items-start">
                    <div>
                      <p className="font-semibold">{c.vagaTitulo}</p>
                      <p className="text-sm text-gray-500">{c.vagaEmpresa}</p>
                      {c.curriculoTitulo && <p className="text-xs text-gray-400 mt-0.5">Currículo: {c.curriculoTitulo}</p>}
                    </div>
                    <span className={`badge ${s.color}`}>{s.label}</span>
                  </div>

                  {editId === c.id ? (
                    <div className="mt-3 flex gap-2">
                      <select className="input flex-1" value={novoStatus} onChange={e => setNovoStatus(e.target.value)}>
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
                      <button className="btn-secondary text-sm" onClick={() => { setEditId(c.id); setNovoStatus(c.status) }}>
                        Atualizar status
                      </button>
                      {c.status === 'PENDENTE' && (
                        <button className="text-red-500 text-sm hover:underline" onClick={() => remover(c.id)}>
                          Remover
                        </button>
                      )}
                    </div>
                  )}
                </div>
              )
            })}
          </div>
        )
      }
    </div>
  )
}
