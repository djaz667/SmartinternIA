const API_BASE_URL = '/api';
const JWT_KEY = 'smartintern_jwt';

async function apiRequest(endpoint, options = {}) {
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

    if (!response.ok) {
        const error = await response.json().catch(() => ({
            message: 'Erreur inconnue',
        }));
        throw { status: response.status, ...error };
    }

    if (response.status === 204) return null;
    return response.json();
}

function setToken(token) {
    localStorage.setItem(JWT_KEY, token);
}

function getToken() {
    return localStorage.getItem(JWT_KEY);
}

function removeToken() {
    localStorage.removeItem(JWT_KEY);
}
