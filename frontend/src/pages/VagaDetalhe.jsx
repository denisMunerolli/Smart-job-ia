import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { vagaApi, candidaturaApi, curriculoApi, matchingApi } from '../api'

export default function VagaDetalhe() {
  const { id }       = useParams()
  const navigate     = useNavigate()
  const [vaga, setVaga]           = useState(null)
  const [curriculos, setCurriculos] = useState([])
  const [curriculoId, setCurriculoId] = useState('')
  const [matching, setMatching]   = useState(null)
  const [loading, setLoading]     = useState(true)
  const [msg, setMsg]             = useState('')

  useEffect(() => {
    Promise.all([vagaApi.buscar(id), curriculoApi.listar()])
      .then(([v, c]) => { setVaga(v.data); setCurriculos(c.data) })
      .finally(() => setLoading(false))
  }, [id])

  async function candidatar() {
    try {
      await candidaturaApi.candidatar({ vagaId: Number(id), curriculoId: curriculoId ? Number(curriculoId) : null })
      setMsg('✅ Candidatura realizada com sucesso!')
    } catch (e) {
      setMsg('❌ ' + (e.response?.data?.message || 'Erro ao candidatar'))
    }
  }

  async function calcularMatching() {
    if (!curriculoId) return
    try {
      const { data } = await matchingApi.comparar({ vagaId: Number(id), curriculoId: Number(curriculoId) })
      setMatching(data)
    } catch (e) {
      setMsg('❌ Erro ao calcular matching')
    }
  }

  if (loading) return <div className="p-8 text-gray-400">Carregando...</div>
  if (!vaga)   return <div className="p-8 text-red-500">Vaga não encontrada</div>

  const nivelColor = matching?.nivel === 'ALTO' ? 'text-green-600'
                   : matching?.nivel === 'MÉDIO' ? 'text-yellow-600' : 'text-red-500'

  return (
    <div className="p-8 max-w-3xl">
      <button className="text-brand-600 text-sm mb-4 hover:underline" onClick={() => navigate(-1)}>← Voltar</button>

      <div className="card mb-6">
        <div className="flex justify-between items-start mb-4">
          <div>
            <h2 className="text-2xl font-bold">{vaga.titulo}</h2>
            <p className="text-gray-500">{vaga.empresa} • {vaga.localizacao}</p>
          </div>
          <span className="badge bg-blue-100 text-blue-700">{vaga.fonte}</span>
        </div>
        {vaga.descricao && <p className="text-gray-700 whitespace-pre-line">{vaga.descricao}</p>}
      </div>

      {/* Ações */}
      <div className="card">
        <h3 className="font-semibold mb-3">Currículo para esta vaga</h3>
        <select className="input mb-4" value={curriculoId} onChange={e => setCurriculoId(e.target.value)}>
          <option value="">Sem currículo específico</option>
          {curriculos.map(c => (
            <option key={c.id} value={c.id}>{c.titulo} (v{c.versao}){c.ativo ? ' ★' : ''}</option>
          ))}
        </select>

        {msg && <p className="text-sm mb-3 p-2 rounded bg-gray-50">{msg}</p>}

        {matching && (
          <div className="mb-4 p-4 bg-gray-50 rounded-lg">
            <p className="font-medium">Score: <span className={`text-2xl font-bold ${nivelColor}`}>{matching.scorePercentual}%</span>
              <span className={`ml-2 badge ${nivelColor} bg-opacity-10`}>{matching.nivel}</span>
            </p>
            {matching.habilidadesFaltantes?.length > 0 && (
              <div className="mt-2">
                <p className="text-sm text-gray-600 mb-1">Habilidades para desenvolver:</p>
                <div className="flex flex-wrap gap-1">
                  {matching.habilidadesFaltantes.map(h => (
                    <span key={h} className="badge bg-orange-100 text-orange-700">{h}</span>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}

        <div className="flex gap-3">
          {curriculoId && (
            <button className="btn-secondary" onClick={calcularMatching}>🎯 Ver matching</button>
          )}
          <button className="btn-primary" onClick={candidatar}>Candidatar-se</button>
        </div>
      </div>
    </div>
  )
}
