import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { vagaApi } from '../api'

export default function VagasPage() {
  const [vagas, setVagas]     = useState([])
  const [total, setTotal]     = useState(0)
  const [page, setPage]       = useState(0)
  const [loading, setLoading] = useState(false)
  const [filtros, setFiltros] = useState({ titulo: '', empresa: '', localizacao: '' })

  function buscar(p = 0) {
    setLoading(true)
    vagaApi.listar({ ...filtros, page: p, size: 20 })
      .then(r => { setVagas(r.data.content); setTotal(r.data.totalElements); setPage(p) })
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  useEffect(() => { buscar() }, [])

  return (
    <div className="p-8">
      <h2 className="text-2xl font-bold mb-6">💼 Vagas</h2>

      {/* Filtros */}
      <div className="card mb-6">
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          {['titulo','empresa','localizacao'].map(f => (
            <input key={f} className="input" placeholder={f.charAt(0).toUpperCase()+f.slice(1)}
              value={filtros[f]} onChange={e => setFiltros(v => ({ ...v, [f]: e.target.value }))} />
          ))}
        </div>
        <button className="btn-primary mt-4" onClick={() => buscar(0)}>Buscar</button>
      </div>

      {loading && <p className="text-gray-400">Carregando...</p>}

      <p className="text-sm text-gray-500 mb-4">{total} vaga(s) encontrada(s)</p>

      <div className="space-y-3">
        {vagas.map(v => (
          <Link key={v.id} to={`/vagas/${v.id}`}
            className="card hover:shadow-md transition-shadow block">
            <div className="flex justify-between items-start">
              <div>
                <h3 className="font-semibold text-gray-900">{v.titulo}</h3>
                <p className="text-sm text-gray-500">{v.empresa} • {v.localizacao}</p>
              </div>
              <span className="badge bg-blue-100 text-blue-700">{v.fonte}</span>
            </div>
            {v.descricao && (
              <p className="text-sm text-gray-600 mt-2 line-clamp-2">{v.descricao}</p>
            )}
          </Link>
        ))}
      </div>

      {/* Paginação */}
      <div className="flex gap-2 mt-6">
        {page > 0 && <button className="btn-secondary" onClick={() => buscar(page - 1)}>← Anterior</button>}
        {(page + 1) * 20 < total && <button className="btn-primary" onClick={() => buscar(page + 1)}>Próxima →</button>}
      </div>
    </div>
  )
}
