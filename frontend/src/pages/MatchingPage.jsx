import { useEffect, useState } from 'react'
import { matchingApi, curriculoApi } from '../api'

const NIVEL_COLOR = { ALTO: 'text-green-600', MEDIO: 'text-yellow-600', BAIXO: 'text-red-500' }
const NIVEL_BG    = { ALTO: 'bg-green-50 border-green-200', MEDIO: 'bg-yellow-50 border-yellow-200', BAIXO: 'bg-red-50 border-red-200' }

function ScoreBar({ label, value, color = '#3b82f6', weight }) {
  return (
    <div className="space-y-1">
      <div className="flex justify-between items-center text-sm">
        <span className="text-gray-600">{label}</span>
        <div className="flex items-center gap-2">
          {weight && <span className="text-xs text-gray-400">{weight}</span>}
          <span className="font-bold text-gray-900">{value}%</span>
        </div>
      </div>
      <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
        <div className="h-full rounded-full transition-all duration-700"
          style={{ width: `${value}%`, background: color }} />
      </div>
    </div>
  )
}

function Badge({ ok, label }) {
  return ok
    ? <span className="px-2 py-0.5 bg-green-100 text-green-700 text-xs rounded-full font-medium">✓ {label}</span>
    : <span className="px-2 py-0.5 bg-red-100 text-red-600 text-xs rounded-full font-medium">✗ {label}</span>
}

export default function MatchingPage() {
  const [modo, setModo]           = useState('texto')
  const [curriculos, setCurriculos] = useState([])
  const [curriculoId, setCurriculoId] = useState('')
  const [vagaId, setVagaId]       = useState('')
  const [textoVaga, setTextoVaga] = useState('')
  const [textoCurriculo, setTextoCurriculo] = useState('')
  const [resultado, setResultado] = useState(null)
  const [loading, setLoading]     = useState(false)
  const [erro, setErro]           = useState('')

  useEffect(() => {
    curriculoApi.listar().then(r => {
      setCurriculos(r.data)
      const ativo = r.data.find(c => c.ativo)
      if (ativo) setCurriculoId(String(ativo.id))
    })
  }, [])

  async function calcular() {
    setLoading(true); setErro(''); setResultado(null)
    try {
      const payload = modo === 'ids'
        ? { vagaId: Number(vagaId), curriculoId: Number(curriculoId) }
        : { textoVaga, textoCurriculo }
      const res = await matchingApi.comparar(payload)
      setResultado(res.data)
    } catch (e) {
      setErro(e.response?.data?.message || 'Erro ao calcular matching.')
    } finally { setLoading(false) }
  }

  const nivel = resultado?.nivel || 'BAIXO'
  const exp   = resultado?.experienciaDetalhe
  const edu   = resultado?.educacaoDetalhe

  return (
    <div className="p-8 space-y-6 max-w-4xl mx-auto">
      <div>
        <h2 className="text-2xl font-bold text-gray-900">🎯 Matching IA</h2>
        <p className="text-gray-500 text-sm mt-1">
          Análise multidimensional v2 — experiência e educação comparadas com o que a vaga exige.
        </p>
      </div>

      <div className="flex gap-2">
        {['texto', 'ids'].map(m => (
          <button key={m} onClick={() => setModo(m)}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
              modo === m ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}`}>
            {m === 'texto' ? 'Texto livre' : 'Por ID de vaga'}
          </button>
        ))}
      </div>

      <div className="card space-y-4">
        {modo === 'texto' ? (
          <>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Descrição da vaga</label>
              <textarea className="input w-full h-36 resize-none"
                placeholder="Cole a descrição completa da vaga aqui..."
                value={textoVaga} onChange={e => setTextoVaga(e.target.value)} />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Seu currículo</label>
              <textarea className="input w-full h-36 resize-none"
                placeholder="Cole o conteúdo do seu currículo aqui..."
                value={textoCurriculo} onChange={e => setTextoCurriculo(e.target.value)} />
            </div>
          </>
        ) : (
          <>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">ID da vaga</label>
              <input type="number" className="input w-full" placeholder="Ex: 42"
                value={vagaId} onChange={e => setVagaId(e.target.value)} />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Currículo</label>
              <select className="input w-full" value={curriculoId} onChange={e => setCurriculoId(e.target.value)}>
                {curriculos.map(c => (
                  <option key={c.id} value={c.id}>{c.titulo} v{c.versao} {c.ativo ? '★' : ''}</option>
                ))}
              </select>
            </div>
          </>
        )}

        {erro && <p className="text-red-500 text-sm">{erro}</p>}
        <button onClick={calcular} disabled={loading} className="btn-primary w-full">
          {loading ? 'Calculando...' : '🎯 Calcular compatibilidade'}
        </button>
      </div>

      {resultado && (
        <div className="space-y-4">
          {/* Score geral */}
          <div className={`card border-2 ${NIVEL_BG[nivel]}`}>
            <div className="flex items-center justify-between mb-1">
              <div>
                <p className="text-sm text-gray-500">Compatibilidade geral</p>
                <p className={`text-5xl font-bold ${NIVEL_COLOR[nivel]}`}>{resultado.scorePercentual}%</p>
                <p className="text-sm text-gray-500 mt-1">{resultado.descricao}</p>
              </div>
              <span className={`text-xl font-bold px-5 py-2 rounded-full border-2 ${NIVEL_BG[nivel]} ${NIVEL_COLOR[nivel]}`}>
                {nivel}
              </span>
            </div>
          </div>

          {/* Breakdown */}
          <div className="card space-y-4">
            <h3 className="font-semibold text-gray-900">📊 Análise por dimensão</h3>
            <ScoreBar label="Hard Skills técnicas"       value={resultado.hardSkills}              color="#3b82f6" weight="25%" />
            <ScoreBar label="Qualificações requeridas"   value={resultado.qualificacoesRequeridas}  color="#8b5cf6" weight="25%" />
            <ScoreBar label="Experiência"                value={resultado.experiencia}              color="#f59e0b" weight="15%" />
            <ScoreBar label="Educação"                   value={resultado.educacao}                 color="#10b981" weight="15%" />
            <ScoreBar label="Qualificações preferidas"   value={resultado.preferencias}             color="#6366f1" weight="10%" />
            <ScoreBar label="Similaridade textual TF-IDF" value={resultado.similaridadeTexto}       color="#94a3b8" weight="10%" />
          </div>

          {/* Detalhe de Experiência */}
          {exp && (
            <div className="card">
              <h3 className="font-semibold text-gray-900 mb-3">⏱️ Experiência</h3>
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <p className="text-gray-500 text-xs mb-1">Exigido pela vaga</p>
                  <p className="font-medium text-gray-900">
                    {exp.anosExigidos > 0 ? `${exp.anosExigidos} ano(s)` : 'Não especificado'}
                    {exp.nivelExigido ? ` · ${exp.nivelExigido}` : ''}
                  </p>
                </div>
                <div>
                  <p className="text-gray-500 text-xs mb-1">Encontrado no CV</p>
                  <p className="font-medium text-gray-900">
                    {exp.anosEncontrados > 0 ? `${exp.anosEncontrados} ano(s)` : 'Não detectado'}
                    {exp.nivelEncontrado ? ` · ${exp.nivelEncontrado}` : ''}
                  </p>
                </div>
              </div>
              <div className="mt-3">
                <Badge ok={exp.satisfeito} label={exp.satisfeito ? 'Experiência suficiente' : 'Experiência insuficiente'} />
              </div>
            </div>
          )}

          {/* Detalhe de Educação */}
          {edu && edu.grauExigido && (
            <div className="card">
              <h3 className="font-semibold text-gray-900 mb-3">🎓 Educação</h3>
              <div className="flex flex-wrap gap-2">
                <Badge ok={edu.grauEncontrado}
                  label={edu.grauEncontrado
                    ? `Grau ${edu.grauExigido} encontrado`
                    : `Grau ${edu.grauExigido} não encontrado`} />
                {edu.areaExigida && (
                  <Badge ok={edu.areaEncontrada}
                    label={edu.areaEncontrada
                      ? `Área compatível`
                      : `Área ${edu.areaExigida} não encontrada`} />
                )}
              </div>
            </div>
          )}

          {/* Hard skills faltantes */}
          {resultado.hardSkillsFaltantes?.length > 0 && (
            <div className="card">
              <h3 className="font-semibold text-gray-900 mb-3">🎯 Habilidades técnicas para desenvolver</h3>
              <div className="flex flex-wrap gap-2">
                {resultado.hardSkillsFaltantes.map(h => (
                  <span key={h} className="px-3 py-1 bg-red-50 text-red-700 border border-red-200 rounded-full text-sm font-medium">
                    {h}
                  </span>
                ))}
              </div>
            </div>
          )}

          {/* Termos de contexto ignorados */}
          {resultado.termosContextoIgnorados?.length > 0 && (
            <div className="card bg-gray-50">
              <h3 className="text-sm font-semibold text-gray-600 mb-1">ℹ️ Termos de contexto ignorados no score técnico</h3>
              <div className="flex flex-wrap gap-1 mt-2">
                {resultado.termosContextoIgnorados.slice(0, 20).map(s => (
                  <span key={s} className="px-2 py-0.5 bg-gray-200 text-gray-500 rounded text-xs">{s}</span>
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
