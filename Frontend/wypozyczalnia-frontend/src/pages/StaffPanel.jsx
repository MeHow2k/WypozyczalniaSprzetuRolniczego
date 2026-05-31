import React,{ useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Navbar } from '../components/NavBar';
import api from '../api/axios';
import '../styles/Panels.css';

export const StaffPanel = () => {
    const navigate = useNavigate();
    const [alert, setAlert] = useState('');
    
    const [activeView, setActiveView] = useState('none'); 

    const [reservations, setReservations] = useState([]);

    const [reservationIdToChange, setreservationIdToChange] = useState(''); 
    const [reservationStatusToChange, setreservationStatusToChange] = useState(''); 

    const fetchReservations = async () => {
        try {
            const response = await api.get('/rental/reservations');
            setReservations(response.data);
            //setAlert('');
        } catch (err) {
            const errorMsg = err.response?.data || "Błąd podczas pobierania rezerwacji.";
            setAlert(errorMsg);
        }
    };
    useEffect(() => {
        if (activeView !== "none") {
            fetchReservations();
        }
    }, [activeView]);

    const handleStatusUpdate = async (reservationId, newStatus) => {
        try {
            const response = await api.put(`/rental/updatestatus/${reservationId}`, {
                status: newStatus
            });
            setAlert(response.data);
            fetchReservations();
        } catch (err) {
            const errorMsg = err.response?.data || "Nie udało się zmienić statusu rezerwacji.";
            setAlert("Błąd: " + errorMsg);
        }
    };

    const handleSubmitChangeReservationStatus = async (e) => {
    e.preventDefault(); 
    setAlert(''); 

    if (!reservationIdToChange) {
        setAlert("Błąd: Proszę wpisać ID rezerwacji!");
        return;
    }
    if (!reservationStatusToChange) {
        setAlert("Błąd: Proszę wybrać status z listy!");
        return;
    }
    await handleStatusUpdate(reservationIdToChange, reservationStatusToChange);
    setreservationIdToChange('');
    setreservationStatusToChange('');
};

    return (
        <div className="admin-container">
            <Navbar/>
            <h2>🛠️ Panel Zarządzania dla Pracowników</h2>
            <p style={{ color: 'red', fontWeight: 'bold' }}>{alert}</p>
            
            <div className="admin-menu-box">
                <button 
                    className={`admin-btn ${activeView === 'requests' ? 'active' : ''}`}
                    onClick={() => setActiveView('requests')}
                >
                    👍🏻 Rezerwacje do akceptacji
                </button>

                <button 
                    className={`admin-btn ${activeView === 'requestsaccepted' ? 'active' : ''}`}
                    onClick={() => setActiveView('requestsaccepted')}
                >
                    🚜 Gotowe do wydania sprzętu
                </button>

                <button 
                    className={`admin-btn ${activeView === 'requestslent' ? 'active' : ''}`}
                    onClick={() => setActiveView('requestslent')}
                >
                    🕑 Rezerwacje w toku
                </button>

                <button 
                    className={`admin-btn ${activeView === 'requestscompleted' ? 'active' : ''}`}
                    onClick={() => setActiveView('requestscompleted')}
                >
                    🤝 Zakończone rezerwacje
                </button>

                <button 
                    className={`admin-btn ${activeView === 'requestsall' ? 'active' : ''}`}
                    onClick={() => setActiveView('requestsall')}
                >
                    📖 Wszystkie rezerwacje
                </button>

                
            </div>
            {/* TABELA: oczekujace rezerwacje */}
            {activeView === 'requests' && (
                <div>
                    <h3>Rezerwacje w Systemie:</h3>

            {reservations.length === 0 ? (
                <p style={{ fontStyle: 'italic', color: '#666', marginTop: '15px' }}>
                    Nie ma rezerwacji oczekujących na potwierdzenie
                </p>
            ) : (

                    <table className="admin-table">
                        <thead>
                            <tr>
                                <th >ID</th>
                                <th >User</th>
                                <th >Maszyna</th>
                                <th >Od kiedy</th>
                                <th >Do kiedy</th>
                                <th >Status</th>
                                <th >Akcje</th>
                            </tr>
                        </thead>
                        <tbody>
                            {reservations.filter(r => r.status === 'PENDING').map(r => (
                                <tr key={r.id}>
                                    <td >#{r.id}</td>
                                    <td >{r.user?.username}</td>
                                    <td ><strong>{r.machine?.name}</strong></td>
                                    <td >{r.startDate}</td>
                                    <td >{r.endDate}</td>
                                    <td>
                                            <span style={{ 
                                                fontWeight: 'bold',
                                                color: r.status === 'PENDING' ? '#ffc107' : 
                                                       r.status === 'APPROVED' ? '#28a745' : 
                                                       r.status === 'COMPLETED' ? '#007bff' : '#dc3545'
                                            }}>
                                                {r.status}
                                            </span>
                                    </td>

                                    <td style={{ padding: '10px' }}>
                                            {/* Przyciski aktywne tylko dla statusu PENDING */}
                                            {r.status === 'PENDING' ? (
                                                <div style={{ display: 'flex', gap: '5px' }}>
                                                    <button 
                                                        onClick={() => handleStatusUpdate(r.id, 'APPROVED')}
                                                        style={{ background: '#28a745', color: 'white', border: 'none', padding: '5px 10px', cursor: 'pointer', borderRadius: '3px' }}
                                                    >
                                                        Zatwierdź
                                                    </button>
                                                    <button 
                                                        onClick={() => handleStatusUpdate(r.id, 'REJECTED')}
                                                        style={{ background: '#dc3545', color: 'white', border: 'none', padding: '5px 10px', cursor: 'pointer', borderRadius: '3px' }}
                                                    >
                                                        Odrzuć
                                                    </button>
                                                </div>
                                            ) : (
                                                <span style={{ color: '#888', fontStyle: 'italic' }}>Rozpatrzono</span>
                                            )}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
            )}
                </div>
            )}

            {/* TABELA: zaakcepowane rezerwacje */}
            {activeView === 'requestsaccepted' && (
                <div>
                    <h3>Rezerwacje w Systemie:</h3>

            {reservations.length === 0 ? (
                <p style={{ fontStyle: 'italic', color: '#666', marginTop: '15px' }}>
                    Nie ma rezerwacji o statusie: zaakcepowane
                </p>
            ) : (

                    <table className="admin-table">
                        <thead>
                            <tr>
                                <th >ID</th>
                                <th >User</th>
                                <th >Maszyna</th>
                                <th >Od kiedy</th>
                                <th >Do kiedy</th>
                                <th >Status</th>
                                <th >Akcje</th>
                            </tr>
                        </thead>
                        <tbody>
                            {reservations.filter(r => r.status === 'APPROVED').map(r => (
                                <tr key={r.id}>
                                    <td >#{r.id}</td>
                                    <td >{r.user?.username}</td>
                                    <td ><strong>{r.machine?.name}</strong></td>
                                    <td >{r.startDate}</td>
                                    <td >{r.endDate}</td>
                                    <td>
                                            <span style={{ 
                                                fontWeight: 'bold',
                                                color: r.status === 'APPROVED' ? '#28a745' : 
                                                       r.status === 'COMPLETED' ? '#007bff' : '#dc3545'
                                            }}>
                                                {r.status}
                                            </span>
                                    </td>

                                    <td style={{ padding: '10px' }}>
                                            {/* Przyciski aktywne tylko dla statusu APPROVED */}
                                            {r.status === 'APPROVED' ? (
                                                <div style={{ display: 'flex', gap: '5px' }}>
                                                    <button 
                                                        onClick={() => handleStatusUpdate(r.id, 'LENT')}
                                                        style={{ background: '#28a745', color: 'white', border: 'none', padding: '5px 10px', cursor: 'pointer', borderRadius: '3px' }}
                                                    >
                                                        Potwierdź wydanie sprzętu
                                                    </button>
                                                    <button 
                                                        onClick={() => handleStatusUpdate(r.id, 'REJECTED')}
                                                        style={{ background: '#dc3545', color: 'white', border: 'none', padding: '5px 10px', cursor: 'pointer', borderRadius: '3px' }}
                                                    >
                                                        Odrzuć
                                                    </button>
                                                </div>
                                            ) : (
                                                <span style={{ color: '#888', fontStyle: 'italic' }}>Rozpatrzono</span>
                                            )}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
            )}
                </div>
            )}


            {/* TABELA: rezerwacje w toku ()wydany sprzet */}
            {activeView === 'requestslent' && (
                <div>
                    <h3>Rezerwacje w Systemie:</h3>

            {reservations.length === 0 ? (
                <p style={{ fontStyle: 'italic', color: '#666', marginTop: '15px' }}>
                    Nie ma rezerwacji w toku
                </p>
            ) : (

                    <table className="admin-table">
                        <thead>
                            <tr>
                                <th >ID</th>
                                <th >User</th>
                                <th >Maszyna</th>
                                <th >Od kiedy</th>
                                <th >Do kiedy</th>
                                <th >Status</th>
                                <th >Akcje</th>
                            </tr>
                        </thead>
                        <tbody>
                            {reservations.filter(r => r.status === 'LENT').map(r => (
                                <tr key={r.id}>
                                    <td >#{r.id}</td>
                                    <td >{r.user?.username}</td>
                                    <td ><strong>{r.machine?.name}</strong></td>
                                    <td >{r.startDate}</td>
                                    <td >{r.endDate}</td>
                                    <td>
                                            <span style={{ 
                                                fontWeight: 'bold',
                                                color: r.status === 'LENT' ? '#cc00ff' :
                                                       r.status === 'COMPLETED' ? '#007bff' : '#dc3545'
                                            }}>
                                                {r.status}
                                            </span>
                                    </td>

                                    <td style={{ padding: '10px' }}>
                                            {/* Przyciski aktywne tylko dla statusu LENT */}
                                            {r.status === 'LENT' ? (
                                                <div style={{ display: 'flex', gap: '5px' }}>
                                                    <button 
                                                        onClick={() => handleStatusUpdate(r.id, 'COMPLETED')}
                                                        style={{ background: '#28a745', color: 'white', border: 'none', padding: '5px 10px', cursor: 'pointer', borderRadius: '3px' }}
                                                    >
                                                        Zakończ rezerwację
                                                    </button>                                           
                                                </div>
                                            ) : (
                                                <span style={{ color: '#888', fontStyle: 'italic' }}>Rozpatrzono</span>
                                            )}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
            )}
                </div>
            )}

 {/* TABELA: rezerwacje zakonczone */}
            {activeView === 'requestscompleted' && (
                <div>
                    <h3>Rezerwacje w Systemie:</h3>

            {reservations.length === 0 ? (
                <p style={{ fontStyle: 'italic', color: '#666', marginTop: '15px' }}>
                    Nie ma zakończonych rezerwacji
                </p>
            ) : (

                    <table className="admin-table">
                        <thead>
                            <tr>
                                <th >ID</th>
                                <th >User</th>
                                <th >Maszyna</th>
                                <th >Od kiedy</th>
                                <th >Do kiedy</th>
                                <th >Status</th>
                                
                            </tr>
                        </thead>
                        <tbody>
                            {reservations.filter(r => r.status === 'COMPLETED').map(r => (
                                <tr key={r.id}>
                                    <td >#{r.id}</td>
                                    <td >{r.user?.username}</td>
                                    <td ><strong>{r.machine?.name}</strong></td>
                                    <td >{r.startDate}</td>
                                    <td >{r.endDate}</td>
                                    <td>
                                            <span style={{ 
                                                fontWeight: 'bold',
                                                color: r.status === 'COMPLETED' ? '#007bff' : '#dc3545'
                                            }}>
                                                {r.status}
                                            </span>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
            )}
                </div>
            )}

            {/* TABELA: lista wszystkich rezerwacji */}
             {activeView === 'requestsall' && (
                <div>
                    <div>
                        <h4>Manualna zmiana statusu rezerwacji</h4>
                        <form onSubmit={handleSubmitChangeReservationStatus}>
                                <label> ID rezerwacji </label>  <input type="text" name='reservationid' value={reservationIdToChange} onChange={(e) => setreservationIdToChange(e.target.value)} />  
                                <label> Zmień status na: </label>  
                                 <select 
                                    name='status' 
                                    value={reservationStatusToChange} 
                                    onChange={(e) => setreservationStatusToChange(e.target.value)}
                                    style={{ padding: '5px', minWidth: '150px', marginBottom: '10px' }}
                                >
                                    {/* Opcja domyślna */}
                                    <option value="" disabled>-- Wybierz status --</option>
                                    {/* Opcje odpowiadające kategoriom  */}
                                    <option value="PENDING">PENDING </option>
                                    <option value="APPROVED">APPROVED</option>
                                    <option value="LENT">LENT</option>
                                    <option value="COMPLETED">COMPLETED</option>
                                    <option value="REJECTED">REJECTED</option>                               
                                </select>                                                         
                                <button type="submit" > Zmień </button><p></p>                                                 
                            </form>
                    </div>
                    <h3>Wszystkie rezerwacje w systemie:</h3>
                    <table className="admin-table">
                        <thead>
                            <tr>
                                <th >ID</th>
                                <th >User</th>
                                <th >Maszyna</th>
                                <th >Od kiedy</th>
                                <th >Do kiedy</th>
                                <th >Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            {reservations.map(r => (
                                <tr key={r.id}>
                                    <td >#{r.id}</td>
                                    <td >{r.user?.username}</td>
                                    <td ><strong>{r.machine?.name}</strong></td>
                                    <td >{r.startDate}</td>
                                    <td >{r.endDate}</td>
                                    <td>
                                            <span style={{ 
                                                fontWeight: 'bold',
                                                color: r.status === 'PENDING' ? '#ffc107' : 
                                                       r.status === 'APPROVED' ? '#28a745' :
                                                        r.status === 'LENT' ? '#cc00ff' :
                                                       r.status === 'COMPLETED' ? '#007bff' : '#dc3545'
                                            }}>
                                                {r.status}
                                            </span>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}

            {activeView === 'none' && !alert && (
                <p className="admin-info-placeholder" >Wybierz jedną z opcji powyżej, aby załadować dane.</p>
            )}

            <hr className="admin-hr" />


            <button className= "admin-btn admin-btn-back" onClick={() => navigate('/')}>⬅️ Powrót do strony głównej</button>
        </div>
    );
};