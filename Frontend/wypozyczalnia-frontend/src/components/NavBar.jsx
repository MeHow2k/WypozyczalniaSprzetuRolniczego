import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import '../styles/NavBar.css';

export const Navbar = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const location = useLocation(); // Pozwala sprawdzić, na której podstronie jesteśmy

    const handleLogoutClick = async () => {
        await logout(); // Wywołanie globalnej funkcji czyszczenia ze Springa i Contextu
        navigate('/');  // Wymuszenie powrotu na stronę główną
    };

    return (
        <header className="navbar-header">
            <div className="navbar-brand-section">
                <h2>🚜 Wypożyczalnia maszyn rolniczych</h2>
                
                {/* Jeśli użytkownik nie jest w home, pokazujemy przycisk powrotu */}
                {location.pathname !== '/' && (
                    <button className="navbar-home-btn" onClick={() => navigate('/')}>
                        🏠 Strona Główna
                    </button>
                )}
            </div>
            
            <div className="navbar-user-section">
                {user && (
                    <div className="navbar-logged-in-container">
                        <span color='black' >👤 Zalogowano jako: <strong>{user.username}</strong> ({user.roles.join(', ')})</span>
                        <button className="navbar-logout-btn" onClick={handleLogoutClick} >
                            Wyloguj się
                        </button>
                    </div>
                )}
            </div>
        </header>
    );
};