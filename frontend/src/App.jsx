import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './contexts/AuthContext'
import Layout from './components/Layout'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import DashboardPage from './pages/DashboardPage'
import VagasPage from './pages/VagasPage'
import VagaDetalhe from './pages/VagaDetalhe'
import CandidaturasPage from './pages/CandidaturasPage'
import CurriculosPage from './pages/CurriculosPage'
import MatchingPage from './pages/MatchingPage'
import PerfilPage from './pages/PerfilPage'
import { Component } from 'react'

class ErrorBoundary extends Component {
  constructor(props) { super(props); this.state = { hasError: false, error: null } }
  static getDerivedStateFromError(error) { return { hasError: true, error } }
  render() {
    if (this.state.hasError) return (
      <div className="min-h-screen flex items-center justify-center p-8">
        <div className="text-center">
          <h2 className="text-xl font-bold text-gray-900 mb-2">Algo deu errado</h2>
          <p className="text-gray-500 text-sm mb-4">{this.state.error?.message}</p>
          <button onClick={() => window.location.href = '/Smart-job-ia/'}
            className="btn-primary">Voltar ao início</button>
        </div>
      </div>
    )
    return this.props.children
  }
}

function PrivateRoute({ children }) {
  const { token } = useAuth()
  return token ? children : <Navigate to="/login" replace />
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter basename="/Smart-job-ia">
        <ErrorBoundary>
          <Routes>
            <Route path="/login"    element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/" element={<PrivateRoute><Layout /></PrivateRoute>}>
              <Route index element={<DashboardPage />} />
              <Route path="vagas" element={<VagasPage />} />
              <Route path="vagas/:id" element={<VagaDetalhe />} />
              <Route path="candidaturas" element={<CandidaturasPage />} />
              <Route path="curriculos" element={<CurriculosPage />} />
              <Route path="matching" element={<MatchingPage />} />
              <Route path="perfil" element={<PerfilPage />} />
            </Route>
          </Routes>
        </ErrorBoundary>
      </BrowserRouter>
    </AuthProvider>
  )
}
