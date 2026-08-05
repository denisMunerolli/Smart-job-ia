import { useEffect, useState } from 'react'
import { curriculoApi } from '../api'

export default function CurriculosPage() {
  const [curriculos, setCurriculos] = useState([])
  const [form, setForm] = useState({ titulo: '', conteudoJson: '' })
  const [editId, setEditId] = useState(null)
  const [loading, setLoading] = useState(true)
  const [msg, setMsg] = useState('')

  function carregar() {
    curriculoApi.listar()
      .then(r => setCurriculos(r.data))
      .finally(() => setLoading(false))
  }

  useEffect(() => { carregar() }, [])

  async function salvar() {
    try {
      if (editId) await curriculoApi.atualizar(editId, form)
      else        await curriculoApi.criar(form)
      setForm({ titulo: '', conteudoJson: '' })
      setEditId(null)
      setMsg('✅ Salvo com sucesso!')
      carregar()
    } catch (e) {
      setMsg('❌ ' + (e.response?.data?.message || 'Erro ao salvar'))
    }
  }

  async function ativar(id) {
    await curriculoApi.ativar(id)
    carregar()
  }

  async function remover(id) {
    try {
      await curriculoApi.remover(id)
      carregar()
    } catch (e) {
      setMsg('❌ ' + (e.response?.data?.message || 'Erro ao remover'))
    }
  }

  return (
    <div className="p-8 max-w-3xl">
      <h2 className="text-2xl font-bold mb-6">📄 Currículos</h2>

      {/* Formulário */}
      <div className="card mb-6">
        <h3 className="font-semibold mb-3">{editId ? 'Editar currículo' : 'Novo currículo'}</h3>
        {msg && <p className="text-sm mb-3 p-2 rounded bg-gray-50">{msg}</p>}
        <input className="input mb-3" placeholder="Título" value={form.titulo}
          onChange={e => setForm(f => ({ ...f, titulo: e.target.value }))} />
        <textarea className="input mb-3 h-32" placeholder="Conteúdo / habilidades (texto livre)"
          value={form.conteudoJson}
          onChange={e => setForm(f => ({ ...f, conteudoJson: e.target.value }))} />
        <div className="flex gap-2">
          <button className="btn-primary" onClick={salvar}>Salvar</button>
          {editId && <button className="btn-secondary" onClick={() => { setEditId(null); setForm({ titulo:'', conteudoJson:'' }) }}>Cancelar</button>}
        </div>
      </div>

      {/* Lista */}
      {loading ? <p className="text-gray-400">Carregando...</p> : (
        <div className="space-y-3">
          {curriculos.map(c => (
            <div key={c.id} className={`card ${c.ativo ? 'border-brand-400 border-2' : ''}`}>
              <div className="flex justify-between items-start">
                <div>
                  <p className="font-semibold">{c.titulo}</p>
                  <p className="text-xs text-gray-400">Versão {c.versao} {c.ativo ? '★ Ativo' : ''}</p>
                </div>
                <div className="flex gap-2">
                  {!c.ativo && <button className="btn-secondary text-sm" onClick={() => ativar(c.id)}>Ativar</button>}
                  <button className="btn-secondary text-sm" onClick={() => { setEditId(c.id); setForm({ titulo: c.titulo, conteudoJson: c.conteudoJson || '' }) }}>Editar</button>
                  {!c.ativo && <button className="text-red-500 text-sm hover:underline" onClick={() => remover(c.id)}>Remover</button>}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
