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

    const [machines, setMachines] = useState([]);

    // Stany dla formularza
    const [showAuthForm, setShowAuthForm] = useState(false);
    const [isRegisterMode, setIsRegisterMode] = useState(false);

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');

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
            //walidacja danych formularza rejestracji 
            if (username.length < 8) {
                setAlert("Login musi składać się z co najmniej 8 znaków.");
                return;
            }
            if (password.length < 8) {
                setAlert("Słabe hasło: Hasło musi składać się z co najmniej 8 znaków.");
                return;
            }
            const passwordRegex = /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
        
        if (!passwordRegex.test(password)) {
            setAlert("Błąd formularza: Hasło musi zawierać co najmniej jedną wielką literę, jedną cyfrę oraz jeden znak specjalny (@$!%*?&).");
            return; 
        }

            try {
                // request rejestracji
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
        //walidacja dat
    if (!startDate || !endDate) {
            setAlert("Proszę wybrać datę rozpoczęcia i zakończenia rezerwacji!");
            return;
    }
    if (new Date(endDate) < new Date(startDate)) {
        setAlert("Data zakończenia nie może być wcześniejsza niż data rozpoczęcia!");
        return;
    }


    try {
        
        const response = await api.post('/rental/reserve', {
            machineId: machineId,
            startDate: startDate,
            endDate: endDate
        });
        setAlert(response.data); 
        setStartDate('');
        setEndDate('');

    } catch (err) {
        setAlert("Błąd rezerwacji: " + (err.response?.data || "Brak autoryzacji/Maszyna niedostępna."));
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
                        {user.roles.includes('ROLE_STAFF') && (
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
                   
                </div>
            )}

            {/* FORMULARZ LOGOWANIA / REJESTRACJI (WARUNKOWY) */}
            {showAuthForm && !user && (
                <div className="home-auth-card">
                    <h3>{isRegisterMode ? "Tworzenie konta rolnika" : "Logowanie do systemu"}</h3>
                    <form onSubmit={handleAuthSubmit} className="home-auth-form">
                        <input type="username" placeholder="Nazwa użytkownika" value={username} onChange={e => setUsername(e.target.value)} required className="home-auth-input" />
                        <input type="password" placeholder="Hasło" value={password} onChange={e => setPassword(e.target.value)} required className="home-auth-input" />
                        {isRegisterMode && (
                        <p style={{ fontSize: '0.75rem', color: '#d84141', marginTop: '-5px', marginBottom: '15px', textAlign: 'left', lineHeight: '1.2' }}>
                            💡 Hasło musi mieć min. 8 znaków, zawierać dużą literę, cyfrę oraz znak specjalny (@$!%*?&).
                        </p>
                    )}
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
            {/* Sekcja z wyborem terminu */}  
                {user && user.roles.includes("ROLE_CLIENT") && (
                    <div style={{ background: '#f8f9fa', padding: '15px', borderRadius: '5px', border: '1px solid #ddd', marginBottom: '20px', maxWidth: '600px' }}>
                        <h3 style={{ marginTop: 0, fontSize: '1.1rem', color: '#333' }}>🗓️ Wybierz termin wynajmu sprzętu:</h3>
                        <div style={{ display: 'flex', gap: '15px', flexWrap: 'wrap' }}>
                            <div>
                                <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 'bold', marginBottom: '3px' }}>Data odbioru:</label>
                                <input 
                                    type="date" 
                                    value={startDate} 
                                    min={new Date().toISOString().split('T')[0]} // Blokuje wybór dat z przeszłości w kalendarzu systemowym
                                    onChange={e => setStartDate(e.target.value)}
                                    style={{ padding: '6px', borderRadius: '4px', border: '1px solid #ccc' }}
                                />
                            </div>
                            <div>
                                <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 'bold', marginBottom: '3px' }}>Data zwrotu:</label>
                                <input 
                                    type="date" 
                                    value={endDate} 
                                    min={startDate || new Date().toISOString().split('T')[0]} // Blokuje zwrot przed dniem odbioru
                                    onChange={e => setEndDate(e.target.value)}
                                    style={{ padding: '6px', borderRadius: '4px', border: '1px solid #ccc' }}
                                />
                            </div>
                        </div>
                    </div>
                )}



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