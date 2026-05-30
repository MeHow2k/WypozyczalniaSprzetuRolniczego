import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute } from './components/ProtectedRoute';
import { Home } from './pages/Home';
import { StaffPanel } from './pages/StaffPanel';
import { AdminPanel } from './pages/AdminPanel';
import { UserPanel } from './pages/UserPanel';

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          {/* Główna strona */}
          <Route path="/" element={<Home />} />

          {/* Chronione panele boczne */}
          <Route path="/client" element={
            <ProtectedRoute allowedRoles={['ROLE_CLIENT','ROLE_STAFF', 'ROLE_ADMIN']}>
              <UserPanel />
            </ProtectedRoute>
          } />

          {/* Chronione panele boczne */}
          <Route path="/staff" element={
            <ProtectedRoute allowedRoles={['ROLE_STAFF', 'ROLE_ADMIN']}>
              <StaffPanel />
            </ProtectedRoute>
          } />

          <Route path="/admin" element={
            <ProtectedRoute allowedRoles={['ROLE_ADMIN']}>
              <AdminPanel />
            </ProtectedRoute>
          } />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;