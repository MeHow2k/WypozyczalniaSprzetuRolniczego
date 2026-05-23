import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api' // Adres Spring Boota
});

// przechwycenie żądań wychodzące z Reacta do Backend
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            // BEZPIECZEŃSTWO: Automatyczne wstrzykiwanie paszportu JWT do nagłówka
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default api;