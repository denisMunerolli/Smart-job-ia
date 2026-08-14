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
      .then(r => {
        setVagas(r.data.content || [])
        setTotal(r.data.totalElements || 0)
        setPage(p)
      })
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
          {[
            { key: 'titulo',      placeholder: 'Título (ex: Java Developer)' },
            { key: 'empresa',     placeholder: 'Empresa' },
            { key: 'localizacao', placeholder: 'Localização (ex: Brasil, Remoto)' },
          ].map(f => (
            <input key={f.key} className="input" placeholder={f.placeholder}
              value={filtros[f.key]}
              onChange={e => setFiltros(v => ({ ...v, [f.key]: e.target.value }))}
              onKeyDown={e => e.key === 'Enter' && buscar(0)}
            />
          ))}
        </div>
        <button className="btn-primary mt-4 w-full sm:w-auto" onClick={() => buscar(0)}>
          🔍 Buscar
        </button>
      </div>

      <p className="text-sm text-gray-500 mb-4">{total} vaga(s) encontrada(s)</p>

      {loading && <p className="text-gray-400 animate-pulse">Carregando...</p>}

      {!loading && vagas.length === 0 && (
        <div className="card text-center py-10">
          <p className="text-gray-400 text-lg">Nenhuma vaga encontrada.</p>
          <p className="text-gray-400 text-sm mt-1">
            Tente outros filtros ou aguarde a importação automática (6h e 18h).
          </p>
        </div>
      )}

      <div className="space-y-3">
        {vagas.map(v => (
          <Link key={v.id} to={`/vagas/${v.id}`}
            className="card hover:shadow-md transition-shadow block group">
            <div className="flex justify-between items-start gap-3">
              <div className="min-w-0 flex-1">
                <div className="flex items-center gap-2 mb-0.5">
                  <h3 className="font-semibold text-gray-900 group-hover:text-blue-700 transition-colors truncate">
                    {v.titulo || 'Sem título'}
                  </h3>
                  <span className="text-xs text-gray-400 flex-shrink-0">#{v.id}</span>
                </div>
                <p className="text-sm text-gray-500">
                  {v.empresa || 'Empresa não informada'}
                  {v.localizacao ? ` • ${v.localizacao}` : ''}
                </p>
              </div>
              <div className="flex flex-col items-end gap-1 flex-shrink-0">
                {v.fonte && (
                  <span className="px-2 py-0.5 bg-blue-100 text-blue-700 rounded-full text-xs font-medium">
                    {v.fonte}
                  </span>
                )}
              </div>
            </div>

            {v.descricao ? (
              <p className="text-sm text-gray-600 mt-2 line-clamp-2">{v.descricao}</p>
            ) : (
              <p className="text-xs text-gray-400 mt-2 italic">
                Sem descrição — clique para ver detalhes e calcular matching.
              </p>
            )}
          </Link>
        ))}
      </div>

      {/* Paginação */}
      <div className="flex items-center gap-3 mt-6">
        {page > 0 && (
          <button className="btn-secondary" onClick={() => buscar(page - 1)}>← Anterior</button>
        )}
        {(page + 1) * 20 < total && (
          <button className="btn-primary" onClick={() => buscar(page + 1)}>Próxima →</button>
        )}
        {total > 0 && (
          <span className="text-sm text-gray-400">
            Página {page + 1} de {Math.ceil(total / 20)}
          </span>
        )}
      </div>
    </div>
  )
}
