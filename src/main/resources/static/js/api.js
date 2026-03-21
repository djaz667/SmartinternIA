const API_BASE_URL = '/api';
const JWT_KEY = 'jwt_token';
const ROLE_KEY = 'user_role';

async function apiFetch(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const token = localStorage.getItem(JWT_KEY);

    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(url, {
        ...options,
        headers,
    });

    if (response.status === 401) {
        localStorage.removeItem(JWT_KEY);
        localStorage.removeItem(ROLE_KEY);
        window.location.href = '/pages/login.html';
        return;
    }

    if (!response.ok) {
        const error = await response.json().catch(() => ({
            message: 'Erreur inconnue',
        }));
        throw { status: response.status, ...error };
    }

    if (response.status === 204) return null;
    return response.json();
}

// Backward-compatible alias
const apiRequest = apiFetch;

function setToken(token) {
    localStorage.setItem(JWT_KEY, token);
}

function getToken() {
    return localStorage.getItem(JWT_KEY);
}

function removeToken() {
    localStorage.removeItem(JWT_KEY);
    localStorage.removeItem(ROLE_KEY);
}

function setRole(role) {
    localStorage.setItem(ROLE_KEY, role);
}

function getRole() {
    return localStorage.getItem(ROLE_KEY);
}
