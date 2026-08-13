import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { vagaApi, candidaturaApi, curriculoApi, matchingApi } from '../api'

const NIVEL_COLOR = { ALTO: 'text-green-600', MEDIO: 'text-yellow-600', BAIXO: 'text-red-500' }
const NIVEL_BG    = { ALTO: 'bg-green-50',    MEDIO: 'bg-yellow-50',    BAIXO: 'bg-red-50'   }

function ScoreBar({ label, value, color = '#3b82f6' }) {
  return (
    <div className="space-y-1">
      <div className="flex justify-between text-xs text-gray-500">
        <span>{label}</span>
        <span className="font-semibold text-gray-700">{value}%</span>
      </div>
      <div className="h-1.5 bg-gray-100 rounded-full overflow-hidden">
        <div className="h-full rounded-full" style={{ width: `${value}%`, background: color }} />
      </div>
    </div>
  )
}

export default function VagaDetalhe() {
  const { id }     = useParams()
  const navigate   = useNavigate()

  const [vaga, setVaga]               = useState(null)
  const [curriculos, setCurriculos]   = useState([])
  const [curriculoId, setCurriculoId] = useState('')
  const [matching, setMatching]       = useState(null)
  const [loading, setLoading]         = useState(true)
  const [loadingMatch, setLoadingMatch] = useState(false)
  const [msg, setMsg]                 = useState('')
  const [candidatado, setCandidatado] = useState(false)

  useEffect(() => {
    Promise.all([vagaApi.buscar(id), curriculoApi.listar()])
      .then(([v, c]) => {
        setVaga(v.data)
        setCurriculos(c.data)
        // Selecionar automaticamente o currículo ativo
        const ativo = c.data.find(cur => cur.ativo)
        if (ativo) setCurriculoId(String(ativo.id))
      })
      .finally(() => setLoading(false))
  }, [id])

  async function candidatar() {
    if (!curriculoId) {
      setMsg('⚠️ Selecione um currículo antes de se candidatar.')
      return
    }
    setMsg('')
    try {
      await candidaturaApi.candidatar({
        vagaId: Number(id),
        curriculoId: Number(curriculoId)
      })
      setMsg('✅ Candidatura realizada com sucesso!')
      setCandidatado(true)
    } catch (e) {
      const erro = e.response?.data?.message || 'Erro ao candidatar'
      if (erro.toLowerCase().includes('duplica') || erro.toLowerCase().includes('já')) {
        setMsg('⚠️ Você já se candidatou a esta vaga.')
      } else {
        setMsg('❌ ' + erro)
      }
    }
  }

  async function calcularMatching() {
    if (!curriculoId) {
      setMsg('⚠️ Selecione um currículo para calcular o matching.')
      return
    }
    setLoadingMatch(true)
    setMsg('')
    try {
      const { data } = await matchingApi.comparar({
        vagaId: Number(id),
        curriculoId: Number(curriculoId)
      })
      setMatching(data)
    } catch (e) {
      setMsg('❌ Erro ao calcular matching. Tente novamente.')
    } finally {
      setLoadingMatch(false)
    }
  }

  if (loading) return <div className="p-8 text-gray-400 animate-pulse">Carregando vaga...</div>
  if (!vaga)   return <div className="p-8 text-red-500">Vaga não encontrada.</div>

  const nivel = matching?.nivel || 'BAIXO'

  return (
    <div className="p-8 max-w-3xl space-y-6">
      <button className="text-blue-600 text-sm hover:underline" onClick={() => navigate(-1)}>← Voltar</button>

      {/* Cabeçalho da vaga */}
      <div className="card">
        <div className="flex justify-between items-start mb-3">
          <div>
            <h2 className="text-2xl font-bold text-gray-900">{vaga.titulo}</h2>
            <p className="text-gray-500 mt-1">{vaga.empresa}{vaga.localizacao ? ` • ${vaga.localizacao}` : ''}</p>
          </div>
          {vaga.fonte && (
            <span className="px-3 py-1 bg-blue-100 text-blue-700 rounded-full text-xs font-medium">
              {vaga.fonte}
            </span>
          )}
        </div>
        {vaga.descricao && (
          <p className="text-gray-700 whitespace-pre-line text-sm leading-relaxed mt-3">
            {vaga.descricao}
          </p>
        )}
      </div>

      {/* Ações */}
      <div className="card space-y-4">
        <h3 className="font-semibold text-gray-900">Currículo para esta vaga</h3>

        {curriculos.length === 0 ? (
          <div className="p-3 bg-yellow-50 border border-yellow-200 rounded-lg text-sm text-yellow-700">
            ⚠️ Você não tem currículos cadastrados.{' '}
            <a href="/Smart-job-ia/curriculos" className="underline font-medium">Criar currículo →</a>
          </div>
        ) : (
          <select
            className="input w-full"
            value={curriculoId}
            onChange={e => { setCurriculoId(e.target.value); setMatching(null); setMsg('') }}>
            <option value="">Selecione um currículo...</option>
            {curriculos.map(c => (
              <option key={c.id} value={c.id}>
                {c.titulo} (v{c.versao}){c.ativo ? ' ★ ativo' : ''}
              </option>
            ))}
          </select>
        )}

        {/* Resultado do matching */}
        {matching && (
          <div className={`p-4 rounded-lg border ${NIVEL_BG[nivel]}`}>
            <div className="flex items-center justify-between mb-3">
              <div>
                <p className="text-xs text-gray-500 mb-0.5">Compatibilidade geral</p>
                <p className={`text-3xl font-bold ${NIVEL_COLOR[nivel]}`}>{matching.scorePercentual}%</p>
              </div>
              <span className={`px-3 py-1 rounded-full text-sm font-semibold border ${NIVEL_BG[nivel]} ${NIVEL_COLOR[nivel]}`}>
                {nivel}
              </span>
            </div>
            <div className="space-y-2">
              <ScoreBar label="Hard Skills"            value={matching.hardSkills}             color="#3b82f6" />
              <ScoreBar label="Qualificações"          value={matching.qualificacoesRequeridas} color="#8b5cf6" />
              <ScoreBar label="Experiência"            value={matching.experiencia}             color="#f59e0b" />
              <ScoreBar label="Educação"               value={matching.educacao}                color="#10b981" />
              <ScoreBar label="Preferências"           value={matching.preferencias}            color="#6366f1" />
              <ScoreBar label="Similaridade textual"   value={matching.similaridadeTexto}       color="#94a3b8" />
            </div>
            {matching.hardSkillsFaltantes?.length > 0 && (
              <div className="mt-3 pt-3 border-t border-gray-200">
                <p className="text-xs text-gray-500 mb-2">Habilidades técnicas para desenvolver:</p>
                <div className="flex flex-wrap gap-1">
                  {matching.hardSkillsFaltantes.map(h => (
                    <span key={h} className="px-2 py-0.5 bg-white border border-red-200 text-red-600 rounded text-xs">
                      {h}
                    </span>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}

        {msg && (
          <p className={`text-sm p-3 rounded-lg ${
            msg.startsWith('✅') ? 'bg-green-50 text-green-700' :
            msg.startsWith('⚠️') ? 'bg-yellow-50 text-yellow-700' :
            'bg-red-50 text-red-700'
          }`}>{msg}</p>
        )}

        <div className="flex gap-3">
          <button
            className="btn-secondary flex-1"
            onClick={calcularMatching}
            disabled={!curriculoId || loadingMatch}>
            {loadingMatch ? 'Calculando...' : '🎯 Ver matching'}
          </button>
          <button
            className="btn-primary flex-1"
            onClick={candidatar}
            disabled={!curriculoId || candidatado}>
            {candidatado ? '✅ Candidatado' : 'Candidatar-se'}
          </button>
        </div>
      </div>
    </div>
  )
}
