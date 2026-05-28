import React,{ useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Navbar } from '../components/NavBar';

export const StaffPanel = () => {
    const navigate = useNavigate();
    const [error, setError] = useState('');
    
    return (
        <div style={{ padding: '20px' }}>
            <Navbar/>
            <h2>🛠️ Panel Zarządzania dla Staffu (Pracowników)</h2>
            <p style={{ color: 'red', fontWeight: 'bold' }}>{error}</p>
            <p>W tej sekcji pracownicy mogą weryfikować wnioski o wypożyczenie ciągników, zmieniać statusy rezerwacji oraz odnotowywać zwroty sprzętu rolniczego.</p>
            <button onClick={() => navigate('/')}>⬅️ Powrót do strony głównej</button>
        </div>
    );
};