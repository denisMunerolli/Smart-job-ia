import { useEffect, useState } from 'react'
import { curriculoApi, vagaApi } from '../api'
import api from '../api'

const NIVEL_COLOR = { ALTO: 'text-green-600', MEDIO: 'text-yellow-600', BAIXO: 'text-red-500' }
const NIVEL_BG    = { ALTO: 'bg-green-50 border-green-200', MEDIO: 'bg-yellow-50 border-yellow-200', BAIXO: 'bg-red-50 border-red-200' }

export default function OtimizarCurriculoPage() {
  const [modo, setModo] = useState('ids') // 'ids' ou 'texto'
  const [curriculos, setCurriculos] = useState([])
  const [curriculoId, setCurriculoId] = useState('')
  const [vagaId, setVagaId] = useState('')
  const [textoVaga, setTextoVaga] = useState('')
  const [textoCurriculo, setTextoCurriculo] = useState('')
  const [resultado, setResultado] = useState(null)
  const [loading, setLoading] = useState(false)
  const [erro, setErro] = useState('')
  const [copiado, setCopiado] = useState(false)

  useEffect(() => {
    curriculoApi.listar().then(r => {
      setCurriculos(r.data)
      const ativo = r.data.find(c => c.ativo)
      if (ativo) setCurriculoId(String(ativo.id))
    })
  }, [])

  async function otimizar() {
    setLoading(true)
    setErro('')
    setResultado(null)
    try {
      let res
      if (modo === 'ids') {
        if (!vagaId || !curriculoId) {
          setErro('Preencha o ID da vaga e selecione um currículo.')
          return
        }
        res = await api.post('/api/curriculos/otimizar/ids', {
          vagaId: Number(vagaId),
          curriculoId: Number(curriculoId)
        })
      } else {
        if (!textoVaga || !textoCurriculo) {
          setErro('Preencha a descrição da vaga e o conteúdo do currículo.')
          return
        }
        res = await api.post('/api/curriculos/otimizar/texto', {
          textoVaga,
          textoCurriculo
        })
      }
      setResultado(res.data)
    } catch (e) {
      setErro(e.response?.data?.message || 'Erro ao otimizar. Verifique a chave da API do Claude.')
    } finally {
      setLoading(false)
    }
  }

  function copiarCurriculo() {
    navigator.clipboard.writeText(resultado.curriculoOtimizado)
    setCopiado(true)
    setTimeout(() => setCopiado(false), 2000)
  }

  const nivel = resultado?.scoreEstimado >= 70 ? 'ALTO' : resultado?.scoreEstimado >= 40 ? 'MEDIO' : 'BAIXO'

  return (
    <div className="p-8 space-y-6 max-w-4xl mx-auto">
      <div>
        <h2 className="text-2xl font-bold text-gray-900">🤖 Otimizar Currículo com IA</h2>
        <p className="text-gray-500 text-sm mt-1">
          O Claude AI analisa a vaga e reescreve seu currículo para maximizar as chances de aprovação.
        </p>
      </div>

      {/* Seletor de modo */}
      <div className="flex gap-2">
        <button
          onClick={() => setModo('ids')}
          className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
            modo === 'ids' ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
          }`}>
          Por ID de vaga
        </button>
        <button
          onClick={() => setModo('texto')}
          className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
            modo === 'texto' ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
          }`}>
          Texto livre
        </button>
      </div>

      {/* Formulário */}
      <div className="card space-y-4">
        {modo === 'ids' ? (
          <>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">ID da vaga</label>
              <input
                type="number"
                className="input w-full"
                placeholder="Ex: 42 (encontre na URL da vaga)"
                value={vagaId}
                onChange={e => setVagaId(e.target.value)}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Currículo</label>
              <select className="input w-full" value={curriculoId} onChange={e => setCurriculoId(e.target.value)}>
                <option value="">Selecione um currículo</option>
                {curriculos.map(c => (
                  <option key={c.id} value={c.id}>
                    {c.titulo} v{c.versao} {c.ativo ? '✅ ativo' : ''}
                  </option>
                ))}
              </select>
            </div>
          </>
        ) : (
          <>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Descrição da vaga</label>
              <textarea
                className="input w-full h-32 resize-none"
                placeholder="Cole aqui a descrição completa da vaga..."
                value={textoVaga}
                onChange={e => setTextoVaga(e.target.value)}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Seu currículo atual</label>
              <textarea
                className="input w-full h-40 resize-none"
                placeholder="Cole aqui o conteúdo do seu currículo..."
                value={textoCurriculo}
                onChange={e => setTextoCurriculo(e.target.value)}
              />
            </div>
          </>
        )}

        {erro && <p className="text-red-500 text-sm">{erro}</p>}

        <button
          onClick={otimizar}
          disabled={loading}
          className="btn-primary w-full flex items-center justify-center gap-2">
          {loading ? (
            <>
              <svg className="animate-spin h-4 w-4" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
              </svg>
              Otimizando com IA... (pode levar ~30s)
            </>
          ) : '🚀 Otimizar currículo'}
        </button>
      </div>

      {/* Resultado */}
      {resultado && (
        <div className="space-y-4">
          {/* Score */}
          <div className={`card border ${NIVEL_BG[nivel]}`}>
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Score estimado de compatibilidade</p>
                <p className={`text-4xl font-bold ${NIVEL_COLOR[nivel]}`}>{resultado.scoreEstimado}%</p>
              </div>
              <span className={`text-lg font-bold px-4 py-2 rounded-full border ${NIVEL_BG[nivel]} ${NIVEL_COLOR[nivel]}`}>
                {nivel}
              </span>
            </div>
          </div>

          {/* Habilidades destacadas */}
          {resultado.habilidadesDestacadas?.length > 0 && (
            <div className="card">
              <h3 className="font-semibold text-gray-900 mb-3">✨ Habilidades destacadas pela IA</h3>
              <div className="flex flex-wrap gap-2">
                {resultado.habilidadesDestacadas.map((h, i) => (
                  <span key={i} className="px-3 py-1 bg-blue-50 text-blue-700 rounded-full text-sm font-medium border border-blue-200">
                    {h}
                  </span>
                ))}
              </div>
            </div>
          )}

          {/* Mudanças feitas */}
          {resultado.mudancasFeitas?.length > 0 && (
            <div className="card">
              <h3 className="font-semibold text-gray-900 mb-3">📝 O que a IA ajustou</h3>
              <ul className="space-y-2">
                {resultado.mudancasFeitas.map((m, i) => (
                  <li key={i} className="flex items-start gap-2 text-sm text-gray-700">
                    <span className="text-green-500 mt-0.5 flex-shrink-0">✓</span>
                    {m}
                  </li>
                ))}
              </ul>
            </div>
          )}

          {/* Currículo otimizado */}
          <div className="card">
            <div className="flex items-center justify-between mb-3">
              <h3 className="font-semibold text-gray-900">📄 Currículo otimizado</h3>
              <button
                onClick={copiarCurriculo}
                className="btn-secondary text-sm flex items-center gap-1">
                {copiado ? '✅ Copiado!' : '📋 Copiar'}
              </button>
            </div>
            <pre className="whitespace-pre-wrap text-sm text-gray-700 bg-gray-50 rounded-lg p-4 max-h-96 overflow-y-auto font-sans">
              {resultado.curriculoOtimizado}
            </pre>
          </div>
        </div>
      )}
    </div>
  )
}
