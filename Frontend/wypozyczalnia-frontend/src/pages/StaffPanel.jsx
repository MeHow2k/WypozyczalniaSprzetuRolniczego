import React from 'react';
import { useNavigate } from 'react-router-dom';

export const StaffPanel = () => {
    const navigate = useNavigate();

    return (
        <div style={{ padding: '20px' }}>
            <h2>🛠️ Panel Zarządzania dla Staffu (Pracowników)</h2>
            <p>W tej sekcji pracownicy mogą weryfikować wnioski o wypożyczenie ciągników, zmieniać statusy rezerwacji oraz odnotowywać zwroty sprzętu rolniczego.</p>
            <button onClick={() => navigate('/')}>⬅️ Powrót do strony głównej</button>
        </div>
    );
};