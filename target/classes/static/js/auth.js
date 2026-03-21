// === Auth Guard & Utilities (utilisé par toutes les pages protégées) ===

function checkAuth() {
    var token = localStorage.getItem('jwt_token');
    if (!token) {
        window.location.href = '/pages/login.html';
        return false;
    }

    // Vérifier l'expiration du token
    try {
        var payload = parseJwt(token);
        if (payload.exp && payload.exp * 1000 < Date.now()) {
            localStorage.removeItem('jwt_token');
            localStorage.removeItem('user_role');
            window.location.href = '/pages/login.html';
            return false;
        }
    } catch (e) {
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user_role');
        window.location.href = '/pages/login.html';
        return false;
    }

    return true;
}

function getCurrentUser() {
    var token = localStorage.getItem('jwt_token');
    if (!token) return null;

    try {
        var payload = parseJwt(token);
        return {
            email: payload.sub,
            role: payload.role,
            userId: payload.userId
        };
    } catch (e) {
        return null;
    }
}

function logout() {
    // Fire-and-forget POST /api/auth/logout
    var token = localStorage.getItem('jwt_token');
    if (token) {
        fetch('/api/auth/logout', {
            method: 'POST',
            headers: { 'Authorization': 'Bearer ' + token }
        }).catch(function () {});
    }

    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user_role');
    window.location.href = '/pages/login.html';
}

function parseJwt(token) {
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    var jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload);
}

// === Login Form Logic (uniquement sur login.html) ===

document.addEventListener('DOMContentLoaded', function () {
    var form = document.getElementById('loginForm');
    if (!form) return; // Pas sur la page login

    var messageDiv = document.getElementById('message');

    form.addEventListener('submit', async function (e) {
        e.preventDefault();
        clearErrors();

        var email = document.getElementById('email').value.trim();
        var motDePasse = document.getElementById('motDePasse').value;

        if (!validateForm(email, motDePasse)) return;

        try {
            var response = await apiFetch('/auth/login', {
                method: 'POST',
                body: JSON.stringify({ email: email, motDePasse: motDePasse }),
            });

            setToken(response.token);
            setRole(response.role);

            showMessage('Connexion réussie ! Redirection...', 'success');

            setTimeout(function () {
                redirectToDashboard(response.role);
            }, 1000);
        } catch (error) {
            if (error && error.status === 401) {
                showMessage('Identifiants invalides.', 'error');
            } else if (error && error.status === 403) {
                showMessage(error.message || 'Accès refusé.', 'error');
            } else {
                showMessage('Erreur lors de la connexion. Veuillez réessayer.', 'error');
            }
        }
    });

    function validateForm(email, motDePasse) {
        var valid = true;

        if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            showError('emailError', 'Veuillez entrer un email valide.');
            valid = false;
        }

        if (!motDePasse) {
            showError('motDePasseError', 'Le mot de passe est obligatoire.');
            valid = false;
        }

        return valid;
    }

    function redirectToDashboard(role) {
        var dashboards = {
            'ADMIN': '/pages/dashboard-admin.html',
            'ETUDIANT': '/pages/dashboard-etudiant.html',
            'ENTREPRISE': '/pages/dashboard-entreprise.html',
            'ENCADRANT_ACADEMIQUE': '/pages/dashboard-encadrant.html',
            'ENCADRANT_ENTREPRISE': '/pages/dashboard-encadrant-entreprise.html'
        };

        var target = dashboards[role] || '/pages/dashboard-etudiant.html';
        window.location.href = target;
    }

    function showError(elementId, message) {
        document.getElementById(elementId).textContent = message;
    }

    function clearErrors() {
        document.querySelectorAll('.error-text').forEach(function (el) {
            el.textContent = '';
        });
        messageDiv.classList.add('hidden');
    }

    function showMessage(text, type) {
        messageDiv.textContent = text;
        messageDiv.className = 'message ' + type;
        messageDiv.classList.remove('hidden');
    }
});
