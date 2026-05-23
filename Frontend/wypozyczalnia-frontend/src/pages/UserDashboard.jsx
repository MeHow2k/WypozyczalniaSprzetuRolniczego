import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';

export const UserDashboard = () => {
    const { user, logout } = useAuth();
    const [weather, setWeather] = useState('');

    const checkWeather = async () => {
        try {
            const res = await api.get('/rental/weather-check');
            setWeather(res.data);
        } catch (err) {
            setWeather("Błąd autoryzacji zewnętrznego API");
        }
    };

    return (
        <div style={{ padding: '20px' }}>
            <h2>Witaj w Panelu Rolnika, {user?.username}</h2>
            <button onClick={checkWeather}>Sprawdź pogodę przed żniwami (Zewnętrzne API)</button>
            {weather && <blockquote style={{background: '#eee', padding: '10px'}}>{weather}</blockquote>}
            <br /><br />
            <button onClick={logout} style={{background: 'red', color: 'white'}}>Wyloguj</button>
        </div>
    );
};