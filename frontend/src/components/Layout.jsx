import { Outlet, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'

const nav = [
  { to: '/',            label: '🏠 Dashboard'    },
  { to: '/vagas',       label: '💼 Vagas'         },
  { to: '/candidaturas',label: '📋 Candidaturas'  },
  { to: '/curriculos',  label: '📄 Currículos'    },
  { to: '/matching',    label: '🎯 Matching IA'   },
  { to: '/perfil',      label: '👤 Perfil'        },
]

export default function Layout() {
  const { logout } = useAuth()
  const navigate   = useNavigate()

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <div className="min-h-screen flex">
      {/* Sidebar */}
      <aside className="w-56 bg-brand-900 text-white flex flex-col">
        <div className="p-5 border-b border-brand-700">
          <h1 className="text-xl font-bold tracking-tight">SmartJobAI</h1>
          <p className="text-xs text-brand-100 mt-0.5">Sua carreira inteligente</p>
        </div>
        <nav className="flex-1 p-3 space-y-1">
          {nav.map(({ to, label }) => (
            <NavLink
              key={to}
              to={to}
              end={to === '/'}
              className={({ isActive }) =>
                `block px-3 py-2 rounded-lg text-sm transition-colors ${
                  isActive
                    ? 'bg-brand-600 text-white font-medium'
                    : 'text-brand-100 hover:bg-brand-700'
                }`
              }
            >
              {label}
            </NavLink>
          ))}
        </nav>
        <div className="p-3 border-t border-brand-700">
          <button
            onClick={handleLogout}
            className="w-full text-left px-3 py-2 rounded-lg text-sm text-brand-100 hover:bg-brand-700 transition-colors"
          >
            🚪 Sair
          </button>
        </div>
      </aside>

      {/* Main */}
      <main className="flex-1 overflow-auto">
        <Outlet />
      </main>
    </div>
  )
}
