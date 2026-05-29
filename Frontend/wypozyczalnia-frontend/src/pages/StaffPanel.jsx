import React,{ useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Navbar } from '../components/NavBar';
import '../styles/Panels.css';

export const StaffPanel = () => {
    const navigate = useNavigate();
    const [alert, setAlert] = useState('');
    
    return (
        <div className="admin-container">
            <Navbar/>
            <h2>🛠️ Panel Zarządzania dla Staffu (Pracowników)</h2>
            <p style={{ color: 'red', fontWeight: 'bold' }}>{alert}</p>
            <p>z</p>
            <div className="admin-menu-box">
                <button 
                    className={`admin-btn `}
                    
                >
                    👥 Requesty
                </button>
                <button 
                    className={`admin-btn`}
                    
                >
                    🚜 Pożyczone
                </button>

                
            </div>




            <button className= "admin-btn admin-btn-back" onClick={() => navigate('/')}>⬅️ Powrót do strony głównej</button>
        </div>
    );
};