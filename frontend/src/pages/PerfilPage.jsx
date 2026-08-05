import { useEffect, useState } from 'react'
import { perfilApi } from '../api'

export default function PerfilPage() {
  const [perfil, setPerfil] = useState(null)
  const [form, setForm]     = useState({ nome: '', linkedinUrl: '', githubUrl: '', portfolioUrl: '' })
  const [loading, setLoading] = useState(true)
  const [msg, setMsg]         = useState('')

  useEffect(() => {
    perfilApi.buscar().then(r => {
      setPerfil(r.data)
      setForm({
        nome:         r.data.nome         || '',
        linkedinUrl:  r.data.linkedinUrl  || '',
        githubUrl:    r.data.githubUrl    || '',
        portfolioUrl: r.data.portfolioUrl || '',
      })
    }).finally(() => setLoading(false))
  }, [])

  async function salvar() {
    try {
      await perfilApi.atualizar(form)
      setMsg('✅ Perfil atualizado com sucesso!')
    } catch (e) {
      setMsg('❌ ' + (e.response?.data?.message || 'Erro ao salvar'))
    }
  }

  if (loading) return <div className="p-8 text-gray-400">Carregando...</div>

  return (
    <div className="p-8 max-w-xl">
      <h2 className="text-2xl font-bold mb-6">👤 Perfil</h2>

      <div className="card">
        <p className="text-sm text-gray-400 mb-4">{perfil?.email}</p>
        {msg && <p className="text-sm mb-4 p-2 rounded bg-gray-50">{msg}</p>}

        {[
          { key: 'nome',         label: 'Nome completo'  },
          { key: 'linkedinUrl',  label: 'LinkedIn URL'   },
          { key: 'githubUrl',    label: 'GitHub URL'     },
          { key: 'portfolioUrl', label: 'Portfolio URL'  },
        ].map(({ key, label }) => (
          <div key={key} className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">{label}</label>
            <input className="input" value={form[key]}
              onChange={e => setForm(f => ({ ...f, [key]: e.target.value }))} />
          </div>
        ))}

        <button className="btn-primary" onClick={salvar}>Salvar</button>
      </div>
    </div>
  )
}
