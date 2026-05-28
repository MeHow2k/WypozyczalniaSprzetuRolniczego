import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api', // Adres Spring Boota
    withCredentials: true
});


export default api;