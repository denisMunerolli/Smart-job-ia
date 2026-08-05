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

function PrivateRoute({ children }) {
  const { token } = useAuth()
  return token ? children : <Navigate to="/login" replace />
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
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
      </BrowserRouter>
    </AuthProvider>
  )
}
