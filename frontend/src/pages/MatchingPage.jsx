import { useEffect, useState } from 'react'
import { matchingApi, curriculoApi, vagaApi } from '../api'

export default function MatchingPage() {
  const [modo, setModo]           = useState('texto')
  const [curriculos, setCurriculos] = useState([])
  const [vagas, setVagas]         = useState([])
  const [form, setForm] = useState({ textoVaga: '', textoCurriculo: '', vagaId: '', curriculoId: '' })
  const [resultado, setResultado] = useState(null)
  const [loading, setLoading]     = useState(false)
  const [error, setError]         = useState('')

  useEffect(() => {
    curriculoApi.listar().then(r => setCurriculos(r.data))
    vagaApi.listar({ size: 50 }).then(r => setVagas(r.data.content))
  }, [])

  async function calcular() {
    setLoading(true); setError(''); setResultado(null)
    try {
      const payload = modo === 'texto'
        ? { textoVaga: form.textoVaga, textoCurriculo: form.textoCurriculo }
        : { vagaId: Number(form.vagaId), curriculoId: Number(form.curriculoId) }
      const { data } = await matchingApi.comparar(payload)
      setResultado(data)
    } catch (e) {
      setError(e.response?.data?.message || 'Erro ao calcular matching')
    } finally {
      setLoading(false)
    }
  }

  const nivelColor = resultado?.nivel === 'ALTO' ? 'text-green-600 bg-green-50'
                   : resultado?.nivel === 'MÉDIO' ? 'text-yellow-700 bg-yellow-50' : 'text-red-600 bg-red-50'

  return (
    <div className="p-8 max-w-2xl">
      <h2 className="text-2xl font-bold mb-2">🎯 Matching IA</h2>
      <p className="text-gray-500 mb-6">Compare seu currículo com uma vaga usando TF-IDF</p>

      {/* Modo */}
      <div className="flex gap-2 mb-6">
        {['texto','ids'].map(m => (
          <button key={m} onClick={() => setModo(m)}
            className={m === modo ? 'btn-primary' : 'btn-secondary'}>
            {m === 'texto' ? 'Texto livre' : 'Por ID (banco)'}
          </button>
        ))}
      </div>

      <div className="card mb-6">
        {modo === 'texto' ? (
          <>
            <textarea className="input mb-3 h-32" placeholder="Cole aqui a descrição da vaga..."
              value={form.textoVaga} onChange={e => setForm(f => ({ ...f, textoVaga: e.target.value }))} />
            <textarea className="input mb-3 h-32" placeholder="Cole aqui o conteúdo do seu currículo..."
              value={form.textoCurriculo} onChange={e => setForm(f => ({ ...f, textoCurriculo: e.target.value }))} />
          </>
        ) : (
          <>
            <select className="input mb-3" value={form.vagaId} onChange={e => setForm(f => ({ ...f, vagaId: e.target.value }))}>
              <option value="">Selecione uma vaga</option>
              {vagas.map(v => <option key={v.id} value={v.id}>{v.titulo} — {v.empresa}</option>)}
            </select>
            <select className="input mb-3" value={form.curriculoId} onChange={e => setForm(f => ({ ...f, curriculoId: e.target.value }))}>
              <option value="">Selecione um currículo</option>
              {curriculos.map(c => <option key={c.id} value={c.id}>{c.titulo} (v{c.versao}){c.ativo?' ★':''}</option>)}
            </select>
          </>
        )}

        {error && <p className="text-red-500 text-sm mb-3">{error}</p>}
        <button className="btn-primary" onClick={calcular} disabled={loading}>
          {loading ? 'Calculando...' : 'Calcular matching'}
        </button>
      </div>

      {resultado && (
        <div className={`card ${nivelColor}`}>
          <div className="flex items-center gap-4 mb-4">
            <div className="text-5xl font-bold">{resultado.scorePercentual}%</div>
            <div>
              <p className="font-semibold text-lg">{resultado.nivel}</p>
              <p className="text-sm opacity-75">Similaridade TF-IDF</p>
            </div>
          </div>
          {resultado.habilidadesFaltantes?.length > 0 && (
            <div>
              <p className="font-medium text-sm mb-2">Habilidades para desenvolver:</p>
              <div className="flex flex-wrap gap-1">
                {resultado.habilidadesFaltantes.map(h => (
                  <span key={h} className="badge bg-white bg-opacity-60 text-gray-700">{h}</span>
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
