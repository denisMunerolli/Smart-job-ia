import { useState } from 'react'
import { Outlet, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'

const nav = [
  { to: '/',             label: 'Dashboard',    icon: '🏠' },
  { to: '/vagas',        label: 'Vagas',        icon: '💼' },
  { to: '/candidaturas', label: 'Candidaturas', icon: '📋' },
  { to: '/curriculos',   label: 'Currículos',   icon: '📄' },
  { to: '/matching',     label: 'Matching IA',  icon: '🎯' },
  { to: '/otimizar',     label: 'Otimizar CV',  icon: '🤖' },
  { to: '/perfil',       label: 'Perfil',       icon: '👤' },
]

// Nav items visíveis na bottom bar do mobile (máximo 5)
const navMobile = nav.slice(0, 5)

export default function Layout() {
  const { logout } = useAuth()
  const navigate   = useNavigate()
  const [menuAberto, setMenuAberto] = useState(false)

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <div className="min-h-screen flex flex-col md:flex-row bg-gray-50">

      {/* ── SIDEBAR — visível apenas em desktop (md+) ────────────────── */}
      <aside className="hidden md:flex w-60 bg-brand-900 text-white flex-col flex-shrink-0 min-h-screen">
        <div className="p-5 border-b border-brand-700">
          <h1 className="text-xl font-bold tracking-tight">SmartJobAI</h1>
          <p className="text-xs text-brand-100 mt-0.5">Sua carreira inteligente</p>
        </div>
        <nav className="flex-1 p-3 space-y-1 overflow-y-auto">
          {nav.map(({ to, label, icon }) => (
            <NavLink key={to} to={to} end={to === '/'}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-colors ${
                  isActive
                    ? 'bg-brand-600 text-white font-medium'
                    : 'text-brand-100 hover:bg-brand-700'
                }`}>
              <span className="text-base">{icon}</span>
              <span>{label}</span>
            </NavLink>
          ))}
        </nav>
        <div className="p-3 border-t border-brand-700">
          <button onClick={handleLogout}
            className="flex items-center gap-3 w-full px-3 py-2.5 rounded-lg text-sm text-brand-100 hover:bg-brand-700 transition-colors">
            <span>🚪</span>
            <span>Sair</span>
          </button>
        </div>
      </aside>

      {/* ── HEADER — visível apenas em mobile ────────────────────────── */}
      <header className="md:hidden bg-brand-900 text-white flex items-center justify-between px-4 py-3 flex-shrink-0">
        <div>
          <h1 className="text-lg font-bold">SmartJobAI</h1>
        </div>
        <div className="flex items-center gap-3">
          {/* Botão menu completo (hamburguer) */}
          <button onClick={() => setMenuAberto(!menuAberto)}
            className="p-2 rounded-lg hover:bg-brand-700 transition-colors text-xl">
            {menuAberto ? '✕' : '☰'}
          </button>
        </div>
      </header>

      {/* ── DRAWER — menu completo no mobile ─────────────────────────── */}
      {menuAberto && (
        <div className="md:hidden fixed inset-0 z-50 flex">
          {/* Overlay */}
          <div className="absolute inset-0 bg-black/50" onClick={() => setMenuAberto(false)} />
          {/* Drawer */}
          <div className="relative w-72 bg-brand-900 text-white flex flex-col h-full z-10">
            <div className="p-5 border-b border-brand-700 flex items-center justify-between">
              <div>
                <h1 className="text-xl font-bold">SmartJobAI</h1>
                <p className="text-xs text-brand-100 mt-0.5">Sua carreira inteligente</p>
              </div>
              <button onClick={() => setMenuAberto(false)}
                className="p-2 hover:bg-brand-700 rounded-lg transition-colors">✕</button>
            </div>
            <nav className="flex-1 p-3 space-y-1 overflow-y-auto">
              {nav.map(({ to, label, icon }) => (
                <NavLink key={to} to={to} end={to === '/'}
                  onClick={() => setMenuAberto(false)}
                  className={({ isActive }) =>
                    `flex items-center gap-3 px-4 py-3 rounded-lg text-sm transition-colors ${
                      isActive
                        ? 'bg-brand-600 text-white font-medium'
                        : 'text-brand-100 hover:bg-brand-700'
                    }`}>
                  <span className="text-lg">{icon}</span>
                  <span>{label}</span>
                </NavLink>
              ))}
            </nav>
            <div className="p-3 border-t border-brand-700">
              <button onClick={handleLogout}
                className="flex items-center gap-3 w-full px-4 py-3 rounded-lg text-sm text-brand-100 hover:bg-brand-700 transition-colors">
                <span>🚪</span>
                <span>Sair</span>
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ── CONTEÚDO PRINCIPAL ───────────────────────────────────────── */}
      <main className="flex-1 overflow-auto pb-20 md:pb-0">
        <Outlet />
      </main>

      {/* ── BOTTOM NAV — visível apenas em mobile ────────────────────── */}
      <nav className="md:hidden fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 flex z-40 safe-bottom">
        {navMobile.map(({ to, label, icon }) => (
          <NavLink key={to} to={to} end={to === '/'}
            className={({ isActive }) =>
              `flex-1 flex flex-col items-center justify-center py-2 gap-0.5 text-xs transition-colors ${
                isActive
                  ? 'text-brand-600 font-medium'
                  : 'text-gray-500 hover:text-gray-700'
              }`}>
            <span className="text-xl leading-none">{icon}</span>
            <span className="leading-tight">{label}</span>
          </NavLink>
        ))}
        {/* Mais → abre o drawer */}
        <button
          onClick={() => setMenuAberto(true)}
          className="flex-1 flex flex-col items-center justify-center py-2 gap-0.5 text-xs text-gray-500 hover:text-gray-700 transition-colors">
          <span className="text-xl leading-none">☰</span>
          <span className="leading-tight">Mais</span>
        </button>
      </nav>
    </div>
  )
}
