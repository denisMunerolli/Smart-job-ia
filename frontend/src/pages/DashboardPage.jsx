import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { vagaApi, candidaturaApi, curriculoApi } from '../api'

export default function DashboardPage() {
  const [stats, setStats] = useState({ vagas: 0, candidaturas: 0, curriculos: 0 })
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([
      vagaApi.listar({ size: 1 }),
      candidaturaApi.listar({ size: 1 }),
      curriculoApi.listar(),
    ]).then(([vagas, cands, curriculos]) => {
      setStats({
        vagas:        vagas.data.totalElements ?? 0,
        candidaturas: cands.data.totalElements ?? 0,
        curriculos:   curriculos.data.length   ?? 0,
      })
    }).catch(() => {}).finally(() => setLoading(false))
  }, [])

  const cards = [
    { label: 'Vagas disponíveis',  value: stats.vagas,        to: '/vagas',        color: 'bg-blue-50 text-blue-700'   },
    { label: 'Candidaturas',       value: stats.candidaturas, to: '/candidaturas', color: 'bg-green-50 text-green-700' },
    { label: 'Currículos',         value: stats.curriculos,   to: '/curriculos',   color: 'bg-purple-50 text-purple-700'},
  ]

  return (
    <div className="p-8">
      <h2 className="text-2xl font-bold text-gray-900 mb-6">Dashboard</h2>
      {loading ? (
        <p className="text-gray-400">Carregando...</p>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-6 mb-10">
          {cards.map(c => (
            <Link key={c.label} to={c.to}
              className={`card hover:shadow-md transition-shadow ${c.color}`}>
              <p className="text-sm font-medium mb-1">{c.label}</p>
              <p className="text-3xl font-bold">{c.value}</p>
            </Link>
          ))}
        </div>
      )}

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
        <Link to="/vagas" className="card hover:shadow-md transition-shadow">
          <h3 className="font-semibold text-lg mb-1">💼 Buscar vagas</h3>
          <p className="text-gray-500 text-sm">Explore vagas importadas de múltiplas fontes.</p>
        </Link>
        <Link to="/matching" className="card hover:shadow-md transition-shadow">
          <h3 className="font-semibold text-lg mb-1">🎯 Matching IA</h3>
          <p className="text-gray-500 text-sm">Compare seu currículo com uma vaga e veja o score TF-IDF.</p>
        </Link>
      </div>
    </div>
  )
}
