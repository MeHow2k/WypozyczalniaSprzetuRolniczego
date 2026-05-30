import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { Navbar } from '../components/NavBar';
import api from '../api/axios';
import '../styles/Panels.css'; // Zakładam, że korzystasz z tych samych stylów tabeli

export const UserPanel = () => {
    const { user } = useAuth();
    const navigate = useNavigate();
    
    const [myReservations, setMyReservations] = useState([]);
    const [error, setError] = useState('');
    const [resFilter, setResFilter] = useState('');

    // Funkcja pobierająca rezerwacje przypisane do zalogowanego konta
    const fetchMyReservations = async () => {
        try {
            const response = await api.get('/rental/my-reservations');
            setMyReservations(response.data);
            setError('');
        } catch (err) {
            setError("Nie udało się pobrać Twojej historii wypożyczeń.");
        }
    };

    // Pobierz dane od razu po załadowaniu komponentu
    useEffect(() => {
        fetchMyReservations();
    }, []);

    //przefiltrowana lista rezerwacji
    const filteredReservations = myReservations.filter(res => {
            if (resFilter === '') return true;
            return res.status === resFilter;
        });

    return (
        <div style={{ padding: '20px' }} className="admin-container">
            <Navbar/>
            <h2>Witaj w panelu użytkownika, {user?.username}</h2>
            
            {error && <p style={{ color: 'red', fontWeight: 'bold' }}>{error}</p>}

            <div style={{ marginTop: '30px' }}>
                <h3>🚜 Twoje rezerwacje i wypożyczenia sprzętu:</h3>
                <p>Filtruj:</p>
                    <select 
                                        name='resfilter' 
                                        value={resFilter} 
                                        onChange={(e) => setResFilter(e.target.value)}
                                        style={{ padding: '5px', minWidth: '150px', marginBottom: '10px' }}
                                    >
                                        {/* Opcja domyślna */}
                                        <option value="">Pokaż wszystkie</option>
                                        {/* Opcje odpowiadające nazwom statusu  */}
                                        <option value="PENDING"> Oczekuje na akceptację </option>
                                        <option value="APPROVED">Zaakceptowane (Do odbioru)</option>
                                        <option value="LENT">Na wypożyczeniu </option>
                                        <option value="COMPLETED">Zakończone (Sprzęt zwrócony)</option>
                                        <option value="REJECTED">Odrzucone</option>
                                    </select> 
                    <button type="submit" > Filtruj </button><p></p>               
                {myReservations.length === 0 ? (
                    <p style={{ fontStyle: 'italic', color: '#666' }}>Nie dokonałeś jeszcze żadnych rezerwacji maszyn.</p>
                ) : filteredReservations.length === 0 ? (
                    <p style={{ fontStyle: 'italic', color: '#ffc107', fontWeight: 'bold' }}>Brak rezerwacji o statusie "{resFilter}" w Twojej historii.</p>
                ) : (
                    <table className="admin-table">
                        <thead>
                            <tr>
                                <th>ID rezerwacji</th>
                                <th>Maszyna</th>
                                <th>Kategoria</th>
                                <th>Od kiedy</th>
                                <th>Do kiedy</th>
                                <th>Status zgłoszenia</th>
                            </tr>
                        </thead>
                        <tbody>
                            {myReservations.map(res => (
                                <tr key={res.id}>
                                    <td>#{res.id}</td>
                                    <td><strong>{res.machineName}</strong></td>
                                    <td>{res.machineCategory}</td>
                                    <td>{res.startDate}</td>
                                    <td>{res.endDate}</td>
                                    <td>
                                        <span style={{ 
                                            fontWeight: 'bold',
                                            color: res.status === 'PENDING' ? '#ffc107' : 
                                                   res.status === 'APPROVED' ? '#28a745' : 
                                                    res.status === 'LENT' ? '#cc00ff' : 
                                                   res.status === 'COMPLETED' ? '#007bff' : '#dc3545'
                                        }}>
                                            {res.status === 'PENDING' && '⏳ Oczekuje na akceptację'}
                                            {res.status === 'APPROVED' && '🟢 Zaakceptowane (Do odbioru)'}
                                            {res.status === 'REJECTED' && '🔴 Odrzucone'}
                                            {res.status === 'LENT' && '🚜 Na wypożyczeniu'}
                                            {res.status === 'COMPLETED' && '🔵 Zakończone (Sprzęt zwrócony)'}
                                        </span>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
            </div>

            <hr className="admin-hr" />
            
            <button 
                type="button" className="admin-btn admin-btn-back"
                onClick={() => navigate('/')} 
            >
                ⬅️ Powrót do strony głównej
            </button>
        </div>
    );
};