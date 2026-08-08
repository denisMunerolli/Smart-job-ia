import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '',
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  res => res,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

export const authApi = {
  login:    (email, senha)       => api.post('/api/auth/login', { email, senha }),
  register: (email, senha, nome) => api.post('/api/auth/register', { email, senha, nome }),
}

export const perfilApi = {
  buscar:    ()     => api.get('/api/usuarios/me'),
  atualizar: (data) => api.put('/api/usuarios/me', data),
  stats:     ()     => api.get('/api/usuarios/me/stats'),
  recomendadas: (limite = 10) => api.get('/api/usuarios/me/vagas/recomendadas', { params: { limite } }),
}

export const curriculoApi = {
  listar:    ()           => api.get('/api/usuarios/me/curriculos'),
  buscar:    (id)         => api.get(`/api/usuarios/me/curriculos/${id}`),
  criar:     (data)       => api.post('/api/usuarios/me/curriculos', data),
  atualizar: (id, data)   => api.put(`/api/usuarios/me/curriculos/${id}`, data),
  ativar:    (id)         => api.put(`/api/usuarios/me/curriculos/${id}/ativar`),
  remover:   (id)         => api.delete(`/api/usuarios/me/curriculos/${id}`),
}

export const vagaApi = {
  listar:  (params) => api.get('/api/vagas', { params }),
  buscar:  (id)     => api.get(`/api/vagas/${id}`),
}

export const candidaturaApi = {
  listar:          (params)           => api.get('/api/usuarios/me/candidaturas', { params }),
  listarPorStatus: (status)           => api.get(`/api/usuarios/me/candidaturas/status/${status}`),
  buscar:          (id)               => api.get(`/api/usuarios/me/candidaturas/${id}`),
  candidatar:      (data)             => api.post('/api/usuarios/me/candidaturas', data),
  atualizarStatus: (id, status, obs)  => api.put(`/api/usuarios/me/candidaturas/${id}/status`, { status, observacao: obs }),
  remover:         (id)               => api.delete(`/api/usuarios/me/candidaturas/${id}`),
}

export const matchingApi = {
  comparar: (data) => api.post('/api/matching', data),
}

export const experienciaApi = {
  listar:    ()         => api.get('/api/usuarios/me/experiencias'),
  criar:     (data)     => api.post('/api/usuarios/me/experiencias', data),
  atualizar: (id, data) => api.put(`/api/usuarios/me/experiencias/${id}`, data),
  remover:   (id)       => api.delete(`/api/usuarios/me/experiencias/${id}`),
}

export default api
