import React, { createContext, useState, useContext, useEffect } from 'react';
import api from '../api/axios';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);

    useEffect(() => {
        const token = localStorage.getItem('token');
        const rolesList = localStorage.getItem('roles');
        const username = localStorage.getItem('username');
        if (token && rolesList && username) {
            const roles = JSON.parse(rolesList);
            setUser({ token, roles, username });
        }
    }, []);

    const login = (userData) => {
        setUser(userData);
        localStorage.setItem('token', userData.token);
        localStorage.setItem('roles', JSON.stringify(userData.roles));
        localStorage.setItem('username', userData.username);
    };

    const logout = async () => {
    try {
        await api.post('/auth/logout'); // Czyści ciastko po stronie serwera
    } catch (err) {
        console.error(err);
    }
    setUser(null);
    localStorage.clear();
};

    return (
        <AuthContext.Provider value={{ user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);