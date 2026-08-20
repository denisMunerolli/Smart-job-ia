import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { vagaApi, candidaturaApi, curriculoApi, matchingApi } from '../api'

const FONTE_CONFIG = {
  adzuna:   { label: 'Adzuna',       color: 'bg-blue-100 text-blue-700',    icon: '🔍', site: 'adzuna.com.br' },
  remoteok: { label: 'RemoteOK',     color: 'bg-green-100 text-green-700',  icon: '🌍', site: 'remoteok.com' },
  rss:      { label: 'RSS Feed',     color: 'bg-orange-100 text-orange-700',icon: '📡', site: null },
  mock:     { label: 'Demonstração', color: 'bg-gray-100 text-gray-500',    icon: '🧪', site: null },
}

const NIVEL_COLOR = { ALTO: 'text-green-600', MEDIO: 'text-yellow-600', BAIXO: 'text-red-500' }
const NIVEL_BG    = { ALTO: 'bg-green-50 border-green-200', MEDIO: 'bg-yellow-50 border-yellow-200', BAIXO: 'bg-red-50 border-red-200' }

function ScoreBar({ label, value, color = '#3b82f6' }) {
  return (
    <div className="space-y-1">
      <div className="flex justify-between text-xs text-gray-500">
        <span>{label}</span>
        <span className="font-semibold">{value}%</span>
      </div>
      <div className="h-1.5 bg-gray-100 rounded-full overflow-hidden">
        <div className="h-full rounded-full" style={{ width: `${value}%`, background: color }} />
      </div>
    </div>
  )
}

export default function VagaDetalhe() {
  const { id }   = useParams()
  const navigate = useNavigate()

  const [vaga, setVaga]                 = useState(null)
  const [curriculos, setCurriculos]     = useState([])
  const [curriculoId, setCurriculoId]   = useState('')
  const [matching, setMatching]         = useState(null)
  const [loading, setLoading]           = useState(true)
  const [notFound, setNotFound]         = useState(false)
  const [loadingMatch, setLoadingMatch] = useState(false)
  const [loadingCand, setLoadingCand]   = useState(false)
  const [msg, setMsg]                   = useState('')
  const [candidatado, setCandidatado]   = useState(false)

  useEffect(() => {
    Promise.all([
      vagaApi.buscar(id).catch(e => {
        if (e.response?.status === 404) setNotFound(true)
        return null
      }),
      curriculoApi.listar().catch(() => ({ data: [] }))
    ]).then(([v, c]) => {
      if (v) {
        setVaga(v.data)
        const lista = c.data || []
        setCurriculos(lista)
        const ativo = lista.find(cur => cur.ativo === true)
        if (ativo) setCurriculoId(String(ativo.id))
        else if (lista.length > 0) setCurriculoId(String(lista[0].id))
      }
    }).finally(() => setLoading(false))
  }, [id])

  async function calcularMatching() {
    if (!curriculoId) { setMsg('⚠️ Selecione um currículo.'); return }
    setLoadingMatch(true); setMsg('')
    try {
      const { data } = await matchingApi.comparar({
        vagaId: Number(id), curriculoId: Number(curriculoId)
      })
      setMatching(data)
    } catch { setMsg('❌ Erro ao calcular matching.') }
    finally { setLoadingMatch(false) }
  }

  async function candidatar() {
    if (!curriculoId) { setMsg('⚠️ Selecione um currículo.'); return }
    setLoadingCand(true); setMsg('')
    try {
      await candidaturaApi.candidatar({ vagaId: Number(id), curriculoId: Number(curriculoId) })
      setMsg('✅ Candidatura registrada com sucesso!')
      setCandidatado(true)
    } catch (e) {
      const err = e.response?.data?.message || 'Erro ao candidatar'
      if (err.toLowerCase().includes('já') || err.toLowerCase().includes('duplica')) {
        setMsg('⚠️ Você já se candidatou a esta vaga.')
        setCandidatado(true)
      } else {
        setMsg('❌ ' + err)
      }
    } finally { setLoadingCand(false) }
  }

  if (loading)  return <div className="p-8 text-gray-400 animate-pulse">Carregando vaga...</div>
  if (notFound) return (
    <div className="p-8 text-center">
      <p className="text-4xl mb-4">🔍</p>
      <h2 className="text-xl font-bold text-gray-700 mb-2">Vaga não encontrada</h2>
      <p className="text-gray-500 mb-4">Esta vaga pode ter sido removida ou o ID é inválido.</p>
      <button className="btn-primary" onClick={() => navigate('/vagas')}>← Ver todas as vagas</button>
    </div>
  )
  if (!vaga) return <div className="p-8 text-red-500">Erro ao carregar vaga.</div>

  const nivel    = matching?.nivel || 'BAIXO'
  const fonteCfg = FONTE_CONFIG[vaga.fonte?.toLowerCase()] || { label: vaga.fonte || 'Desconhecida', color: 'bg-gray-100 text-gray-500', icon: '📋', site: null }

  return (
    <div className="p-4 md:p-8 max-w-3xl mx-auto space-y-4">
      <button className="text-brand-600 text-sm hover:underline flex items-center gap-1"
        onClick={() => navigate(-1)}>
        ← Voltar
      </button>

      {/* Cabeçalho */}
      <div className="card">
        <div className="flex justify-between items-start gap-3 mb-3">
          <div className="min-w-0 flex-1">
            <h2 className="text-xl md:text-2xl font-bold text-gray-900 leading-tight">{vaga.titulo}</h2>
            {vaga.empresa   && <p className="text-gray-600 font-medium mt-1">{vaga.empresa}</p>}
            {vaga.localizacao && <p className="text-gray-500 text-sm mt-0.5">📍 {vaga.localizacao}</p>}
          </div>
          <span className={`px-2 py-1 rounded-full text-xs font-medium flex items-center gap-1 flex-shrink-0 ${fonteCfg.color}`}>
            {fonteCfg.icon} {fonteCfg.label}
          </span>
        </div>

        {vaga.urlOrigem && (
          <a href={vaga.urlOrigem} target="_blank" rel="noopener noreferrer"
            className="flex items-center gap-2 p-3 bg-blue-50 border border-blue-200 rounded-lg text-sm text-blue-700 font-medium hover:bg-blue-100 transition-colors mb-3">
            🔗 Ver vaga original {fonteCfg.site ? `em ${fonteCfg.site}` : ''} ↗
          </a>
        )}

        {vaga.descricao ? (
          <div className="border-t pt-3">
            <h3 className="font-semibold text-gray-900 mb-2 text-sm">Descrição</h3>
            <p className="text-gray-700 whitespace-pre-line text-sm leading-relaxed">{vaga.descricao}</p>
          </div>
        ) : (
          <p className="text-gray-400 text-sm italic border-t pt-3">Sem descrição disponível para esta vaga.</p>
        )}
      </div>

      {/* Candidatura */}
      <div className="card space-y-3">
        <h3 className="font-semibold text-gray-900">Candidatar-se</h3>

        {curriculos.length === 0 ? (
          <div className="p-3 bg-yellow-50 border border-yellow-200 rounded-lg text-sm text-yellow-700">
            ⚠️ Você não tem currículos.{' '}
            <a href="/Smart-job-ia/curriculos" className="underline font-medium">Criar currículo →</a>
          </div>
        ) : (
          <select className="input" value={curriculoId}
            onChange={e => { setCurriculoId(e.target.value); setMatching(null); setMsg('') }}>
            {curriculos.map(c => (
              <option key={c.id} value={c.id}>
                {c.titulo} v{c.versao}{c.ativo === true ? ' ★' : ''}
              </option>
            ))}
          </select>
        )}

        {/* Resultado matching */}
        {matching && (
          <div className={`p-3 rounded-lg border ${NIVEL_BG[nivel]}`}>
            <div className="flex items-center justify-between mb-3">
              <div>
                <p className="text-xs text-gray-500">Compatibilidade</p>
                <p className={`text-3xl font-bold ${NIVEL_COLOR[nivel]}`}>{matching.scorePercentual}%</p>
                {matching.descricao && <p className="text-xs text-gray-500 mt-0.5 italic">{matching.descricao}</p>}
              </div>
              <span className={`px-3 py-1 rounded-full text-sm font-bold ${NIVEL_COLOR[nivel]}`}>{nivel}</span>
            </div>
            <div className="space-y-1.5">
              <ScoreBar label="Hard Skills"       value={matching.hardSkills}             color="#3b82f6" />
              <ScoreBar label="Qualificações"     value={matching.qualificacoesRequeridas} color="#8b5cf6" />
              <ScoreBar label="Experiência"       value={matching.experiencia}             color="#f59e0b" />
              <ScoreBar label="Educação"          value={matching.educacao}                color="#10b981" />
              <ScoreBar label="Preferências"      value={matching.preferencias}            color="#6366f1" />
              <ScoreBar label="Similaridade"      value={matching.similaridadeTexto}       color="#94a3b8" />
            </div>
            {matching.hardSkillsFaltantes?.length > 0 && (
              <div className="mt-2 pt-2 border-t border-gray-200">
                <p className="text-xs text-gray-500 mb-1.5">Para desenvolver:</p>
                <div className="flex flex-wrap gap-1">
                  {matching.hardSkillsFaltantes.map(h => (
                    <span key={h} className="px-2 py-0.5 bg-white border border-red-200 text-red-600 rounded text-xs">{h}</span>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}

        {msg && (
          <p className={`text-sm p-3 rounded-lg ${
            msg.startsWith('✅') ? 'bg-green-50 text-green-700 border border-green-200' :
            msg.startsWith('⚠️') ? 'bg-yellow-50 text-yellow-700 border border-yellow-200' :
            'bg-red-50 text-red-700 border border-red-200'}`}>
            {msg}
          </p>
        )}

        {/* Botões — sem repetição */}
        <div className="flex gap-2">
          {!matching && (
            <button className="btn-secondary flex-1"
              onClick={calcularMatching}
              disabled={!curriculoId || loadingMatch}>
              {loadingMatch ? 'Calculando...' : '🎯 Ver matching'}
            </button>
          )}
          {matching && (
            <button className="btn-secondary"
              onClick={() => setMatching(null)}>
              Recalcular
            </button>
          )}
          <button className="btn-primary flex-1"
            onClick={candidatar}
            disabled={!curriculoId || candidatado || loadingCand}>
            {loadingCand ? 'Enviando...' : candidatado ? '✅ Candidatado' : 'Candidatar-se'}
          </button>
        </div>
      </div>
    </div>
  )
}
