import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import { useNavigate } from 'react-router-dom';
import { Navbar } from '../components/NavBar';
import '../styles/Panels.css';

export const AdminPanel = () => {
    const [error, setError] = useState('');
    const [users, setUsers] = useState([]);
    const [machines, setMachines] = useState([]);

    const [UserId, setUserId] = useState("");
    const [userRole, setUserRole] = useState("");
    const [UserIdtoDeleteRole, setUserIdtoDeleteRole] = useState("");
    const [userRoletoDeleteRole, setUserRoletoDeleteRole] = useState("");

    const [machineName, setMachineName] = useState("");
    const [machineCategory, setMachineCategory] = useState("");
    const [machinePrice, setMachinePrice] = useState("");

    const [machineIdToDelete, setMachineIdToDelete] = useState("");
    
    // Nowy stan określający, co aktualnie wyświetlamy: 'users', 'machines' lub 'none'
    const [activeView, setActiveView] = useState('none'); 
    
    const navigate = useNavigate();

    // Funkcja pobierająca użytkowników (wywoływana dopiero po kliknięciu przycisku)
    const fetchUsers = () => {
        api.get('/admin/users')
            .then(res => {
                setUsers(res.data);
                setError(''); // Czyścimy ewentualne wcześniejsze błędy
                setActiveView('users');
            })
            .catch(err => {
                setError("Brak uprawnień do przeglądania listy użytkowników (Błąd 403 z backendu).");
                setActiveView('none');
            });
    };

    // Funkcja pobierająca maszyny
    const fetchMachines = () => {
        api.get('/rental/machines')
            .then(res => {
                setMachines(res.data);
                setError('');
                setActiveView('machines');
            })
            .catch(err => {
                setError("Błąd podczas pobierania listy maszyn.");
                setActiveView('none');
            });
    };

       // Funkcja obslugujaca przyznawanie ról
    const handleSubmitRole = async (e) => {
        e.preventDefault();
            if (!UserId || UserId=="") {
                setError("Proszę wpisać ID użytkownika!");
                return;
            }
            if (!userRole || userRole=="") {
                setError("Proszę wybrać rolę z listy!");
                return;
            }

        try {
            // Wysyłamy PUT na adres: /api/admin/addrole/{id}
            // Ciasteczko z tokenem JWT zostanie dołączone automatycznie dzięki 'withCredentials: true' w axios.js
            const response = await api.put(`/admin/users/addrole/${UserId}`, {
                roleName: userRole // To pole trafia do RoleAssignRequest na backendzie
            });
            setError(response.data); // Wyświetli komunikat ze Springa, np. "Pomyślnie nadano rolę..."
            setUserId('');
            setRole('');
        } catch (err) {    
            const errorMessage = err.response?.data || "Wystąpił nieoczekiwany błąd serwera.";
            setError("Błąd podczas nadawania roli: " + errorMessage);
        }
    };

     // Funkcja obslugujaca odbieranie ról
    const handleSubmitRoleDelete = async (e) => {
        e.preventDefault();
            if (!UserIdtoDeleteRole || UserIdtoDeleteRole=="") {
                setError("Proszę wpisać ID użytkownika!");
                return;
            }
            if (!userRoletoDeleteRole || userRoletoDeleteRole=="") {
                setError("Proszę wybrać rolę z listy!");
                return;
            }

        try {
            const response = await api.put(`/admin/users/deleterole/${UserIdtoDeleteRole}`, {
                roleName: userRoletoDeleteRole // To pole trafia do RoleDeleteRequest na backendzie
            });
            setError(response.data); 
            setUserIdtoDeleteRole('');
            setUserRoletoDeleteRole('');
        } catch (err) {    
            const errorMessage = err.response?.data || "Wystąpił nieoczekiwany błąd serwera.";
            setError("Błąd podczas nadawania roli: " + errorMessage);
        }
    };

    const handleSubmitMachineToDelete = async (e) => {
        e.preventDefault();
            if (!machineIdToDelete || machineIdToDelete=="") {
                setError("Proszę wpisać ID maszyny do usunięcia!");
                return;
            }
            
        const confirmDelete = window.confirm(`Czy na pewno chcesz usunąć maszynę o ID: ${machineIdToDelete}?`);
        if (!confirmDelete) return;

        try {
            const response = await api.delete(`/admin/machines/deletemachine/${machineIdToDelete}`);
            setError(response.data); 
            setMachineIdToDelete('');            
        } catch (err) {    
            const errorMessage = err.response?.data || "Wystąpił nieoczekiwany błąd serwera.";
            setError("Błąd usuwania maszyny: " + errorMessage);
        }
    };

    const handleSubmitAddMachine = async (e) => {
        e.preventDefault();
    if (!machineName || !machinePrice) {
            setError("Proszę uzupełnić nazwę i cenę maszyny!");
            return;
        }

        try {
            const response = await api.post('/admin/machines/addmachine', {
                name: machineName,
                category: machineCategory,
                pricePerDay: parseFloat(machinePrice) // Konwersja tekstu na liczbę zmiennoprzecinkową
            });

            setError(response.data);
            
            setMachineName('');
            setMachinePrice('');
            setMachineCategory('');
        } catch (err) {
            const errorMessage = err.response?.data || "Wystąpił nieoczekiwany błąd serwera.";
            setError("Błąd podczas dodawania maszyny: " + errorMessage);
        }
    };

    return (
        <div className="admin-container">
            <Navbar />
            
            <h2>⚙️ Panel Zarządzania Administratora</h2>
            
            
            {/* PRZYCISKI NAWIGACYJNE ADMINA */}
            <div className="admin-menu-box">
                <button 
                    className={`admin-btn ${activeView === 'users' ? 'active' : ''}`}
                    onClick={fetchUsers}
                >
                    👥 Użytkownicy i role
                </button>
                <button 
                    className={`admin-btn ${activeView === 'machines' ? 'active' : ''}`}
                    onClick={fetchMachines}
                >
                    🚜 Wyświetl Flotę Maszyn
                </button>

                
            </div>
            <p className="admin-error-msg">{error}</p>

            {/* TABELA: UŻYTKOWNICY */}
            {activeView === 'users' && (
                <div>
                    <div>
                        <form onSubmit={handleSubmitRole}>
                                <label> ID </label>  <input type="text" name='userid' value={UserId} onChange={(e) => setUserId(e.target.value)} />  
                                <label> Rola </label>  
                                 <select 
                                    name='userRole' 
                                    value={userRole} 
                                    onChange={(e) => setUserRole(e.target.value)}
                                    style={{ padding: '5px', minWidth: '150px', marginBottom: '10px' }}
                                >
                                    {/* Opcja domyślna */}
                                    <option value="" disabled>-- Wybierz rolę --</option>
                                    {/* Opcje odpowiadające nazwom ról w bazie danych  */}
                                    <option value="ROLE_USER">Klient </option>
                                    <option value="ROLE_STAFF">Pracownik</option>
                                    <option value="ROLE_ADMIN">Administrator</option>
                                </select>                               
                                <button type="submit" > Przyznaj rolę </button><p></p>                                                 
                            </form>
                    </div>
                    <div>
                        <form onSubmit={handleSubmitRoleDelete}>
                                <label> ID </label>  <input type="text" name='useridtoDeleteRole' value={UserIdtoDeleteRole} onChange={(e) => setUserIdtoDeleteRole(e.target.value)} />  
                                <label> Rola </label>  
                                 <select 
                                    name='userRoletoDeleteRole' 
                                    value={userRoletoDeleteRole} 
                                    onChange={(e) => setUserRoletoDeleteRole(e.target.value)}
                                    style={{ padding: '5px', minWidth: '150px', marginBottom: '10px' }}
                                >
                                    {/* Opcja domyślna */}
                                    <option value="" disabled>-- Wybierz rolę --</option>
                                    {/* Opcje odpowiadające nazwom ról w bazie danych  */}
                                    <option value="ROLE_USER">Klient </option>
                                    <option value="ROLE_STAFF">Pracownik</option>
                                   
                                </select>                               
                                <button type="submit" > Odbierz rolę </button><p></p>                                                 
                            </form>
                    </div>

                    <h3>Użytkownicy w Systemie:</h3>
                    <table className="admin-table">
                        <thead>
                            <tr>
                                <th >ID bazy</th>
                                <th >Adres E-mail (Username)</th>
                                <th >Role</th>
                            </tr>
                        </thead>
                        <tbody>
                            {users.map(u => (
                                <tr key={u.id}>
                                    <td >#{u.id}</td>
                                    <td ><strong>{u.username}</strong></td>
                                    <td >{u.roles && u.roles.map(r => typeof r === 'object' ? r.name : r).join(', ')}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}

            {/* TABELA: MASZYNY */}
            {activeView === 'machines' && (
                <div>

                    <div>
                        <form onSubmit={handleSubmitAddMachine}>
                                <label> Nazwa </label>  <input type="text" name='machineName' value={machineName} onChange={(e) => setMachineName(e.target.value)} />  
                                <label> Kategoria </label>  
                                 <select 
                                    name='machineCategory' 
                                    value={machineCategory} 
                                    onChange={(e) => setMachineCategory(e.target.value)}
                                    style={{ padding: '5px', minWidth: '150px', marginBottom: '10px' }}
                                >
                                    {/* Opcja domyślna */}
                                    <option value="" disabled>-- Wybierz kategorię --</option>
                                    {/* Opcje odpowiadające kategoriom  */}
                                    <option value="Traktory">Traktory </option>
                                    <option value="Kombajny">Kombajny</option>
                                    <option value="Rozsiewacze">Rozsiewacze</option>
                                    <option value="Siewniki">Siewniki</option>
                                    <option value="Pługi">Pługi</option>
                                    <option value="Kosiarki">Kosiarki</option>
                                    <option value="Ładowarki">Ładowarki</option>
                                </select> 
                                <label> Cena (doba) </label>  <input type="text" name='machineprice' value={machinePrice} onChange={(e) => setMachinePrice(e.target.value)} />                                
                                <button type="submit" > Dodaj </button><p></p>                                                 
                            </form>
                    </div>
                    <div>
                        <form onSubmit={handleSubmitMachineToDelete}>
                                <label> ID </label>  
                                <input type="text" name='machineidtoDelete' value={machineIdToDelete} onChange={(e) => setMachineIdToDelete(e.target.value)} />                                                            
                                <button type="submit" > Usuń maszynę </button><p></p>                                                 
                            </form>
                    </div>


                    <h3>Stan floty maszyn rolniczych:</h3>
                    <table className="admin-table">
                        <thead>
                            <tr>
                                <th >ID sprzętu</th>
                                <th >Nazwa maszyny</th>
                                <th >Kategoria</th>
                                <th >Cena (doba)</th>
                                <th >Dostępność</th>
                            </tr>
                        </thead>
                        <tbody>
                            {machines.map(m => (
                                <tr key={m.id}>
                                    <td >#{m.id}</td>
                                    <td ><strong>{m.name}</strong></td>
                                    <td >{m.category}</td>
                                    <td >{m.pricePerDay} PLN</td>
                                    <td >
                                        {m.available ? (
                                            <span style={{ color: 'green', fontWeight: 'bold' }}>🟢 Wolny</span>
                                        ) : (
                                            <span style={{ color: 'red', fontWeight: 'bold' }}>🔴 Wypożyczony</span>
                                        )}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}

            {activeView === 'none' && !error && (
                <p className="admin-info-placeholder" >Wybierz jedną z opcji powyżej, aby załadować dane relacyjne z bazy PostgreSQL.</p>
            )}

            <hr className="admin-hr" />
            
            <button 
                type="button" className= "admin-btn admin-btn-back"
                onClick={() => navigate('/')} 
            >
                ⬅️ Powrót do strony głównej
            </button>
        </div>
    );
};