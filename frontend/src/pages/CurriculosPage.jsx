import { useEffect, useState } from 'react'
import { curriculoApi } from '../api'

export default function CurriculosPage() {
  const [curriculos, setCurriculos] = useState([])
  const [form, setForm]       = useState({ titulo: '', conteudoJson: '' })
  const [editId, setEditId]   = useState(null)
  const [loading, setLoading] = useState(true)
  const [msg, setMsg]         = useState('')

  function carregar() {
    curriculoApi.listar()
      .then(r => setCurriculos(r.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  useEffect(() => { carregar() }, [])

  async function salvar() {
    if (!form.titulo.trim()) { setMsg('Título obrigatório.'); return }
    try {
      if (editId) await curriculoApi.atualizar(editId, form)
      else        await curriculoApi.criar(form)
      setForm({ titulo: '', conteudoJson: '' })
      setEditId(null)
      setMsg('✅ Salvo!')
      carregar()
    } catch (e) {
      setMsg('❌ ' + (e.response?.data?.message || 'Erro ao salvar'))
    }
  }

  async function ativar(id) {
    try {
      await curriculoApi.ativar(id)
      setMsg('✅ Currículo ativado!')
      carregar()
    } catch (e) {
      setMsg('❌ ' + (e.response?.data?.message || 'Erro ao ativar'))
    }
  }

  async function remover(id) {
    if (!window.confirm('Remover este currículo?')) return
    try {
      await curriculoApi.remover(id)
      setMsg('✅ Removido.')
      carregar()
    } catch (e) {
      setMsg('❌ ' + (e.response?.data?.message || 'Não é possível remover o currículo ativo.'))
    }
  }

  // Detecta o ativo por comparação de id (não confia no campo ativo=null)
  const ativoId = curriculos.find(c => c.ativo === true)?.id

  return (
    <div className="p-8 max-w-3xl">
      <h2 className="text-2xl font-bold mb-6">📄 Currículos</h2>

      <div className="card mb-6">
        <h3 className="font-semibold mb-3">{editId ? 'Editar currículo' : 'Novo currículo'}</h3>
        {msg && (
          <p className={`text-sm mb-3 p-2 rounded ${msg.startsWith('✅') ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'}`}>
            {msg}
          </p>
        )}
        <input className="input mb-3 w-full"
          placeholder="Título (ex: Currículo Java Backend)"
          value={form.titulo}
          onChange={e => setForm(f => ({ ...f, titulo: e.target.value }))} />
        <textarea className="input mb-3 h-40 w-full resize-none"
          placeholder="Cole o conteúdo do seu currículo aqui (habilidades, experiências, tecnologias...)"
          value={form.conteudoJson}
          onChange={e => setForm(f => ({ ...f, conteudoJson: e.target.value }))} />
        <div className="flex gap-2">
          <button className="btn-primary" onClick={salvar}>
            {editId ? 'Salvar alterações' : 'Criar currículo'}
          </button>
          {editId && (
            <button className="btn-secondary"
              onClick={() => { setEditId(null); setForm({ titulo: '', conteudoJson: '' }); setMsg('') }}>
              Cancelar
            </button>
          )}
        </div>
      </div>

      {loading ? (
        <p className="text-gray-400 animate-pulse">Carregando...</p>
      ) : curriculos.length === 0 ? (
        <div className="card text-center py-8">
          <p className="text-gray-400">Nenhum currículo ainda. Crie um acima.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {curriculos.map(c => {
            const isAtivo = c.id === ativoId
            return (
              <div key={c.id}
                className={`card ${isAtivo ? 'border-2 border-green-400 bg-green-50' : 'border border-gray-200'}`}>
                <div className="flex justify-between items-start gap-3">
                  <div className="min-w-0 flex-1">
                    <div className="flex items-center gap-2 flex-wrap">
                      <p className="font-semibold text-gray-900">{c.titulo}</p>
                      {isAtivo && (
                        <span className="px-2 py-0.5 bg-green-100 text-green-700 text-xs rounded-full font-medium">
                          ★ Ativo
                        </span>
                      )}
                    </div>
                    <p className="text-xs text-gray-400 mt-0.5">Versão {c.versao}</p>
                    {c.conteudoJson && (
                      <p className="text-xs text-gray-500 mt-1 line-clamp-2">
                        {c.conteudoJson.slice(0, 120)}...
                      </p>
                    )}
                  </div>

                  <div className="flex flex-col gap-2 flex-shrink-0 min-w-[80px]">
                    {!isAtivo && (
                      <button
                        className="bg-blue-600 hover:bg-blue-700 text-white text-xs font-medium px-3 py-1.5 rounded-lg transition-colors"
                        onClick={() => ativar(c.id)}>
                        ★ Ativar
                      </button>
                    )}
                    <button
                      className="bg-gray-100 hover:bg-gray-200 text-gray-700 text-xs font-medium px-3 py-1.5 rounded-lg transition-colors"
                      onClick={() => {
                        setEditId(c.id)
                        setForm({ titulo: c.titulo, conteudoJson: c.conteudoJson || '' })
                        setMsg('')
                        window.scrollTo({ top: 0, behavior: 'smooth' })
                      }}>
                      Editar
                    </button>
                    {!isAtivo && (
                      <button
                        className="text-red-400 hover:text-red-600 text-xs transition-colors"
                        onClick={() => remover(c.id)}>
                        Remover
                      </button>
                    )}
                  </div>
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
