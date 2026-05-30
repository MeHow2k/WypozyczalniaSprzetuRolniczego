import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Navbar } from '../components/NavBar';
import api from '../api/axios';
import '../styles/Home.css';

export const Home = () => {
    const { user, login, logout } = useAuth();
    const navigate = useNavigate();
    const [alert, setAlert] = useState('');

    // Stany dla danych biznesowych
    const [machines, setMachines] = useState([]);
    const [weather, setWeather] = useState('');//TODO

    // Stany dla formularza
    const [showAuthForm, setShowAuthForm] = useState(false);
    const [isRegisterMode, setIsRegisterMode] = useState(false);

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');



    // Pobieranie maszyn (zawsze publiczne)
    useEffect(() => {
        api.get('/rental/machines')
            .then(res => setMachines(res.data))
            .catch(err => setAlert("Błąd pobierania maszyn", err));
    }, []);

    useEffect(() => {
        // Jeśli użytkownik nie jest zalogowany (user === null), ustaw formularz na otwarty
        if (!user) {
            setShowAuthForm(true); // Otwiera formularz
            setIsRegisterMode(false); // Domyślnie ustawia tryb logowania, nie rejestracji
        }
    }, [user]);

    // Obsługa Logowania / Rejestracji
    const handleAuthSubmit = async (e) => {
        e.preventDefault();
        if (isRegisterMode) {
            try {
                // Domyślnie rejestrujemy jako zwykły user
                await api.post('/auth/register', { username, password });
                setAlert("Zarejestrowano pomyślnie! Teraz możesz się zalogować.");
                setIsRegisterMode(false);
            } catch (err) {
                setAlert("Błąd rejestracji: " + (err.response?.data || "Nastąpił problem z serwerem. Serdecznie przepraszamy."));
            }
        } else {
            try {
                const res = await api.post('/auth/login', { username, password });
                const { token, roles } = res.data;
                login({ token, roles, username });
                setShowAuthForm(false); // Ukryj formularz po zalogowaniu
                setUsername('');
                setPassword('');
                setAlert('');
            } catch (err) {
                setAlert("Błąd logowania: " + (err.response?.data || "Złe dane"));
            }
        }
    };


    const handleReserveMachine = async (machineId) => {
    try {
        // Symulujemy rezerwację od dzisiaj na kolejne 3 dni (w pełnej wersji można dać kalendarz <input type="date">)
        const today = new Date().toISOString().split('T')[0];
        const nextWeek = new Date();
        nextWeek.setDate(nextWeek.getDate() + 3);
        const endDate = nextWeek.toISOString().split('T')[0];

        const response = await api.post('/rental/reserve', {
            machineId: machineId,
            startDate: today,
            endDate: endDate
        });

        setAlert(response.data); 
    } catch (err) {
        setAlert("Błąd rezerwacji: " + (err.response?.data || "Brak autoryzacji"));
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
        <div className="home-container">
            <Navbar/>
            <p> {alert} </p>
            {/* DYNAMICZNE MENU DLA RÓL (RBAC) */}
            {user && (
                <div className="home-role-menu">
                    <h3>Dostępne akcje:</h3>
                    <div className="home-actions-row">
                        {/* Przzycisk widoczny dla zalogowanego użytkonika */}
                        <button onClick={() => navigate('/client')} className="home-btn-action">Panel użytkownika</button>
                        
                        {/* Przycisk Staff - widoczny dla ROLE_STAFF oraz ROLE_ADMIN */}
                        {(user.roles.includes('ROLE_STAFF') || user.roles.includes( 'ROLE_ADMIN')) && (
                            <button onClick={() => navigate('/staff')} className="home-btn-action home-btn-staff">
                                🛠️ Przejdź do Panelu Staff
                            </button>
                        )}

                        {/* Przycisk Admin - widoczny tylko dla ROLE_ADMIN */}
                        {user.roles.includes('ROLE_ADMIN') && (
                            <button onClick={() => navigate('/admin')} className="home-btn-action home-btn-admin">
                                ⚙️ Przejdź do Panelu Administratora
                            </button>
                        )}
                    </div>
                    {weather && <blockquote className="home-weather-quote">{weather}</blockquote>}
                </div>
            )}

            {/* FORMULARZ LOGOWANIA / REJESTRACJI (WARUNKOWY) */}
            {showAuthForm && !user && (
                <div className="home-auth-card">
                    <h3>{isRegisterMode ? "Tworzenie konta rolnika" : "Logowanie do systemu"}</h3>
                    <form onSubmit={handleAuthSubmit} className="home-auth-form">
                        <input type="username" placeholder="Nazwa użytkownika" value={username} onChange={e => setUsername(e.target.value)} required className="home-auth-input" />
                        <input type="password" placeholder="Hasło" value={password} onChange={e => setPassword(e.target.value)} required className="home-auth-input" />
                        <button type="submit" className="home-auth-submit-btn">
                            {isRegisterMode ? "Zarejestruj się" : "Zaloguj się"}
                        </button>
                    </form>
                    <p className="home-auth-toggle-text">
                        {isRegisterMode ? "Masz już konto?" : "Nie masz konta?"} {' '}
                        <span onClick={() => setIsRegisterMode(!isRegisterMode)} style={{ color: 'blue', cursor: 'pointer', textDecoration: 'underline' }}>
                            {isRegisterMode ? "Zaloguj się" : "Załóż konto rolnika"}
                        </span>
                    </p>
                </div>
            )}

            <hr className="home-divider" />

            {/* KATALOG MASZYN (PUBLICZNY) */}
            <h2>🚜 Dostępny sprzęt rolniczy na stanie:</h2>
            <div className="home-machines-grid">
                {machines.map(m => (
                    <div key={m.id} className="home-machine-card">
                        <h3>{m.name}</h3>
                        <p className="home-machine-category">Kategoria: {m.category}</p>
                        <p><strong>Cena: {m.pricePerDay} PLN / doba</strong></p>
                        <p>{m.available ? (
                                <span className="home-status-free">🟢 Dostępny</span>
                            ) : (
                                <span className="home-status-busy">🔴 Niedostępny</span>
                            )}</p>
                        {user && user.roles.includes("ROLE_CLIENT") && m.available && (
                            <button className="home-btn-reserve" onClick={() => handleReserveMachine(m.id)}>Zarezerwuj</button>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
};