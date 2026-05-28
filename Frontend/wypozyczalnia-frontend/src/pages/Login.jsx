import React, { useState } from 'react';
import api from '../api/axios';

export const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await api.post('/auth/login', { username, password });
            
            // Odbieramy dane z JwtResponse, który napisaliśmy w Springu
            const { token, roles } = response.data;

            // Zapisujemy w pamięci przeglądarki
            localStorage.setItem('token', token);
            localStorage.setItem('role', roles[0]); // Zapisujemy główną rolę (np. ROLE_ADMIN)

            alert('Zalogowano pomyślnie!');
            // Tutaj przekierowanie na odpowiedni pulpit na podstawie roli
        } catch (error) {
            alert('Błąd logowania: ' + error.response?.data);
        }
    };

    return (
        <div>
            <form onSubmit={handleLogin}>
                <input type="username" placeholder="Nazwa użytkownika" onChange={e => setUsername(e.target.value)} />
                <input type="password" placeholder="Hasło" onChange={e => setPassword(e.target.value)} />
                <button type="submit">Zaloguj się</button>
            </form>
        </div>
    );
};