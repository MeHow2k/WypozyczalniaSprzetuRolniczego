import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export const ProtectedRoute = ({ children, allowedRoles }) => {
    const { user } = useAuth();

    // Sprawdzanie czy użytkownik jest w ogóle zalogowany
    if (!user && localStorage.getItem('roles') === null) {
        return <Navigate to="/" replace />;
    }

    // Sprawdzanie czy rola pasuje
if (user && allowedRoles) {
        const hasAccess = user.roles.some(role => allowedRoles.includes(role));
        if (!hasAccess) {
            return <div style={{ padding: '20px', color: 'red' }}>Brak uprawnień do tego panelu! (403 Frontend)</div>;
        }
    }

    return children;
};