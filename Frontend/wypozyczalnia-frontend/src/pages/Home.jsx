import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';

export const Home = () => {
    const { user, login, logout } = useAuth();
    const navigate = useNavigate();

    // Stany dla danych biznesowych
    const [machines, setMachines] = useState([]);
    const [weather, setWeather] = useState('');

    // Stany dla formularza
    const [showAuthForm, setShowAuthForm] = useState(false);
    const [isRegisterMode, setIsRegisterMode] = useState(false);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    // Pobieranie maszyn (zawsze publiczne)
    useEffect(() => {
        api.get('/rental/machines')
            .then(res => setMachines(res.data))
            .catch(err => console.error("Błąd pobierania maszyn", err));
    }, []);

    // Obsługa Logowania / Rejestracji
    const handleAuthSubmit = async (e) => {
        e.preventDefault();
        if (isRegisterMode) {
            try {
                // Domyślnie rejestrujemy jako zwykły user
                await api.post('/auth/register', { username, password });
                alert("Zarejestrowano pomyślnie! Teraz możesz się zalogować.");
                setIsRegisterMode(false);
            } catch (err) {
                alert("Błąd rejestracji: " + (err.response?.data || "Nastąpił problem z serwerem. Serdecznie przepraszamy."));
            }
        } else {
            try {
                const res = await api.post('/auth/login', { username, password });
                const { token, roles } = res.data;
                login({ token, roles, username });
                setShowAuthForm(false); // Ukryj formularz po zalogowaniu
                setUsername('');
                setPassword('');
            } catch (err) {
                alert("Błąd logowania: " + (err.response?.data || "Złe dane"));
            }
        }
    };

    // Odpytywanie zewnętrznego API przez bezpieczny backend
    const checkWeather = async () => {
        try {
            const res = await api.get('/rental/weather-check');
            setWeather(res.data);
        } catch (err) {
            setWeather("Błąd pobierania danych pogodowych.");
        }
    };

    return (
        <div style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
            {/* PANEL GÓRNY / NAGŁÓWEK BEZPIECZEŃSTWA */}
            <header style={{ display: 'flex', justifyContent: 'between', alignItems: 'center', backgroundColor: '#f4f4f4', padding: '10px 20px', borderRadius: '5px' }}>
                <div>
                    <h2>🚜 AgroRent - Wypożyczalnia maszyn</h2>
                </div>
                
                <div style={{ marginLeft: 'auto' }}>
                    {!user ? (
                        <button onClick={() => setShowAuthForm(!showAuthForm)} style={{ padding: '8px 15px', cursor: 'pointer' }}>
                            {showAuthForm ? "Zamknij formularz" : "Zaloguj / Zarejestruj się"}
                        </button>
                    ) : (
                        <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                            <span>👤 Zalogowano jako: <strong>{user.username}</strong> ({user.roles.join(', ')})</span>
                            <button onClick={logout} style={{ padding: '6px 12px', background: '#dc3545', color: 'white', border: 'none', borderRadius: '3px', cursor: 'pointer' }}>Wyloguj się</button>
                        </div>
                    )}
                </div>
            </header>

            {/* DYNAMICZNE MENU DLA RÓL (RBAC na Frontendzie) */}
            {user && (
                <div style={{ marginTop: '20px', padding: '15px', background: '#e2f0d9', borderRadius: '5px' }}>
                    <h3>Dostępne akcje dla Twoich uprawnień:</h3>
                    <div style={{ display: 'flex', gap: '10px' }}>
                        {/* Każdy zalogowany (USER, STAFF, ADMIN) może sprawdzić pogodę */}
                        <button onClick={checkWeather} style={{ padding: '10px' }}>Sprawdź pogodę na żniwa (Zewnętrzne API)</button>
                        
                        {/* Przycisk Staff - widoczny dla ROLE_EMPLOYEE oraz ROLE_ADMIN */}
                        {(user.roles.includes('ROLE_STAFF') || user.roles.includes( 'ROLE_ADMIN')) && (
                            <button onClick={() => navigate('/staff')} style={{ padding: '10px', background: '#ffc107', border: 'none', fontWeight: 'bold' }}>
                                🛠️ Przejdź do Panelu Staff
                            </button>
                        )}

                        {/* Przycisk Admin - widoczny tylko dla ROLE_ADMIN */}
                        {user.roles.includes('ROLE_ADMIN') && (
                            <button onClick={() => navigate('/admin')} style={{ padding: '10px', background: '#007bff', color: 'white', border: 'none', fontWeight: 'bold' }}>
                                ⚙️ Przejdź do Panelu Administratora
                            </button>
                        )}
                    </div>
                    {weather && <blockquote style={{ marginTop: '10px', background: '#fff', padding: '10px', borderLeft: '5px solid green' }}>{weather}</blockquote>}
                </div>
            )}

            {/* FORMULARZ LOGOWANIA / REJESTRACJI (WARUNKOWY) */}
            {showAuthForm && !user && (
                <div style={{ border: '1px solid #ccc', padding: '20px', maxWidth: '350px', marginTop: '20px', borderRadius: '5px', background: '#fff' }}>
                    <h3>{isRegisterMode ? "Tworzenie konta rolnika" : "Logowanie do systemu"}</h3>
                    <form onSubmit={handleAuthSubmit}>
                        <input type="username" placeholder="Nazwa użytkownika" value={username} onChange={e => setUsername(e.target.value)} required style={{ width: '90%', padding: '8px', marginBottom: '10px' }} />
                        <input type="password" placeholder="Hasło" value={password} onChange={e => setPassword(e.target.value)} required style={{ width: '90%', padding: '8px', marginBottom: '10px' }} />
                        <button type="submit" style={{ width: '96%', padding: '10px', background: '#28a745', color: 'white', border: 'none', cursor: 'pointer' }}>
                            {isRegisterMode ? "Zarejestruj się" : "Zaloguj się"}
                        </button>
                    </form>
                    <p style={{ fontSize: '14px', marginTop: '10px' }}>
                        {isRegisterMode ? "Masz już konto?" : "Nie masz konta?"} {' '}
                        <span onClick={() => setIsRegisterMode(!isRegisterMode)} style={{ color: 'blue', cursor: 'pointer', textDecoration: 'underline' }}>
                            {isRegisterMode ? "Zaloguj się" : "Załóż konto rolnika"}
                        </span>
                    </p>
                </div>
            )}

            <hr style={{ margin: '30px 0' }} />

            {/* KATALOG MASZYN (PUBLICZNY) */}
            <h2>🚜 Dostępny sprzęt rolniczy na stanie:</h2>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '20px' }}>
                {machines.map(m => (
                    <div key={m.id} style={{ border: '1px solid #ddd', padding: '15px', borderRadius: '5px', width: '220px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)' }}>
                        <h3>{m.name}</h3>
                        <p style={{ color: '#666' }}>Kategoria: {m.category}</p>
                        <p><strong>Cena: {m.pricePerDay} PLN / doba</strong></p>
                        <p>{m.available ? "🟢 Dostępny od ręki" : "🔴 Aktualnie w polu (Zajęty)"}</p>
                        {user && user.role === 'ROLE_USER' && m.available && (
                            <button style={{ width: '100%', padding: '5px', background: '#28a745', color: 'white', border: 'none' }}>Zarezerwuj</button>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
};