import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { perfilApi } from '../api'

const STATUS_LABELS = {
  PENDENTE:   { label: 'Pendente',    color: '#94a3b8' },
  ENVIADA:    { label: 'Enviada',     color: '#3b82f6' },
  EM_ANALISE: { label: 'Em análise',  color: '#f59e0b' },
  ENTREVISTA: { label: 'Entrevista',  color: '#8b5cf6' },
  APROVADA:   { label: 'Aprovada',    color: '#10b981' },
  REPROVADA:  { label: 'Reprovada',   color: '#ef4444' },
  DESISTENCIA:{ label: 'Desistência', color: '#f97316' },
}

const NIVEL_COLOR = { ALTO: 'text-green-600', MEDIO: 'text-yellow-600', BAIXO: 'text-red-400' }
const NIVEL_BG    = { ALTO: 'bg-green-50',    MEDIO: 'bg-yellow-50',    BAIXO: 'bg-red-50'   }

function MiniBar({ pct, nivel }) {
  const color = nivel === 'ALTO' ? '#10b981' : nivel === 'MEDIO' ? '#f59e0b' : '#ef4444'
  return (
    <div className="w-24 h-2 bg-gray-100 rounded-full overflow-hidden">
      <div style={{ width: `${pct}%`, background: color }} className="h-full rounded-full transition-all" />
    </div>
  )
}

function DonutChart({ porStatus }) {
  const entries = Object.entries(porStatus).filter(([, v]) => v > 0)
  const total = entries.reduce((s, [, v]) => s + v, 0)
  if (total === 0) return <p className="text-gray-400 text-sm text-center py-4">Nenhuma candidatura ainda</p>

  let offset = 0
  const slices = entries.map(([k, v]) => {
    const pct = (v / total) * 100
    const slice = { key: k, pct, offset, color: STATUS_LABELS[k]?.color || '#cbd5e1', label: STATUS_LABELS[k]?.label || k, count: v }
    offset += pct
    return slice
  })

  const circumference = 2 * Math.PI * 40
  return (
    <div className="flex flex-col sm:flex-row items-center gap-6">
      <svg viewBox="0 0 100 100" className="w-32 h-32 flex-shrink-0">
        {slices.map(s => (
          <circle key={s.key} cx="50" cy="50" r="40"
            fill="transparent"
            stroke={s.color}
            strokeWidth="18"
            strokeDasharray={`${(s.pct / 100) * circumference} ${circumference}`}
            strokeDashoffset={-((s.offset / 100) * circumference)}
            transform="rotate(-90 50 50)"
          />
        ))}
        <text x="50" y="54" textAnchor="middle" fontSize="14" fontWeight="bold" fill="#1e293b">{total}</text>
      </svg>
      <ul className="space-y-1.5 text-sm">
        {slices.map(s => (
          <li key={s.key} className="flex items-center gap-2">
            <span className="w-3 h-3 rounded-full flex-shrink-0" style={{ background: s.color }} />
            <span className="text-gray-600">{s.label}</span>
            <span className="font-semibold text-gray-900 ml-auto pl-4">{s.count}</span>
          </li>
        ))}
      </ul>
    </div>
  )
}

export default function DashboardPage() {
  const [stats, setStats]   = useState(null)
  const [recs,  setRecs]    = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([
      perfilApi.stats(),
      perfilApi.recomendadas(8),
    ]).then(([s, r]) => {
      setStats(s.data)
      setRecs(r.data)
    }).catch(() => {}).finally(() => setLoading(false))
  }, [])

  if (loading) return (
    <div className="p-8">
      <h2 className="text-2xl font-bold text-gray-900 mb-6">Dashboard</h2>
      <p className="text-gray-400 animate-pulse">Carregando dados...</p>
    </div>
  )

  const porStatus = stats?.candidaturasPorStatus || {}

  return (
    <div className="p-8 space-y-8">
      <h2 className="text-2xl font-bold text-gray-900">Dashboard</h2>

      {/* Cards de totais */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-5">
        <Link to="/vagas" className="card hover:shadow-md transition-shadow bg-blue-50">
          <p className="text-sm font-medium text-blue-600 mb-1">Vagas disponíveis</p>
          <p className="text-4xl font-bold text-blue-700">{stats?.totalVagas ?? 0}</p>
        </Link>
        <Link to="/candidaturas" className="card hover:shadow-md transition-shadow bg-purple-50">
          <p className="text-sm font-medium text-purple-600 mb-1">Candidaturas</p>
          <p className="text-4xl font-bold text-purple-700">{stats?.totalCandidaturas ?? 0}</p>
        </Link>
        <Link to="/curriculos" className="card hover:shadow-md transition-shadow bg-green-50">
          <p className="text-sm font-medium text-green-600 mb-1">Currículos</p>
          <p className="text-4xl font-bold text-green-700">{stats?.totalCurriculos ?? 0}</p>
        </Link>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Gráfico de candidaturas por status */}
        <div className="card">
          <h3 className="font-semibold text-gray-900 mb-4">📊 Candidaturas por status</h3>
          <DonutChart porStatus={porStatus} />
        </div>

        {/* Atalhos rápidos */}
        <div className="card flex flex-col gap-3">
          <h3 className="font-semibold text-gray-900 mb-1">⚡ Ações rápidas</h3>
          <Link to="/vagas" className="flex items-center gap-3 p-3 rounded-lg bg-blue-50 hover:bg-blue-100 transition-colors">
            <span className="text-2xl">💼</span>
            <div>
              <p className="font-medium text-blue-800">Buscar vagas</p>
              <p className="text-xs text-blue-600">Explore vagas de múltiplas fontes</p>
            </div>
          </Link>
          <Link to="/matching" className="flex items-center gap-3 p-3 rounded-lg bg-purple-50 hover:bg-purple-100 transition-colors">
            <span className="text-2xl">🎯</span>
            <div>
              <p className="font-medium text-purple-800">Matching IA</p>
              <p className="text-xs text-purple-600">Compare currículo com uma vaga</p>
            </div>
          </Link>
          <Link to="/curriculos" className="flex items-center gap-3 p-3 rounded-lg bg-green-50 hover:bg-green-100 transition-colors">
            <span className="text-2xl">📄</span>
            <div>
              <p className="font-medium text-green-800">Gerenciar currículos</p>
              <p className="text-xs text-green-600">Crie e ative versões do seu currículo</p>
            </div>
          </Link>
        </div>
      </div>

      {/* Vagas recomendadas */}
      {recs.length > 0 && (
        <div className="card">
          <div className="flex justify-between items-center mb-4">
            <h3 className="font-semibold text-gray-900">🏆 Vagas recomendadas para você</h3>
            <Link to="/vagas" className="text-sm text-blue-600 hover:underline">Ver todas →</Link>
          </div>
          <div className="space-y-3">
            {recs.map(v => (
              <Link key={v.id} to={`/vagas/${v.id}`}
                className="flex items-center justify-between gap-4 p-3 rounded-lg border border-gray-100 hover:border-blue-200 hover:bg-blue-50 transition-colors">
                <div className="min-w-0">
                  <p className="font-medium text-gray-900 truncate">{v.titulo}</p>
                  <p className="text-sm text-gray-500 truncate">{v.empresa} {v.localizacao ? `• ${v.localizacao}` : ''}</p>
                </div>
                <div className="flex flex-col items-end gap-1 flex-shrink-0">
                  <span className={`text-sm font-bold ${NIVEL_COLOR[v.nivel] || 'text-gray-500'}`}>
                    {v.scorePercentual}%
                  </span>
                  <MiniBar pct={v.scorePercentual} nivel={v.nivel} />
                  <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${NIVEL_BG[v.nivel]} ${NIVEL_COLOR[v.nivel]}`}>
                    {v.nivel}
                  </span>
                </div>
              </Link>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
