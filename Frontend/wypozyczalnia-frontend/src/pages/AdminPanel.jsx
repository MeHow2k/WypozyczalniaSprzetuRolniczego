import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import { useNavigate } from 'react-router-dom';

export const AdminPanel = () => {
    
    const [users, setUsers] = useState([]);
    const navigate = useNavigate();
    useEffect(() => {
        api.get('/admin/users')
            .then(res => setUsers(res.data))
            .catch(err => alert("Błąd 403: Backend zablokował dostęp!"));
    }, []);


    
    return (
        <div style={{ padding: '20px' }}>
            <h2>Panel Zarządzania Administratora</h2>
            <h3>Zarejestrowani Użytkownicy w Systemie:</h3>
            <ul>
                {users.map(u => (
                    <li key={u.id}>{u.username} - Posiada hashowane hasło w Postgres</li>
                ))}
            </ul>
            
            <button type="button" onClick={() => navigate('/')}>⬅️ Powrót do strony głównej</button>
        </div>
    );
};