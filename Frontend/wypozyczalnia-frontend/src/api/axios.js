import axios from 'axios';

const api = axios.create({
    baseURL: 'https://localhost:8443/api', // Adres Spring Boota
    withCredentials: true
});


export default api;