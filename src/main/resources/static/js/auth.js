document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('loginForm');
    const messageDiv = document.getElementById('message');

    form.addEventListener('submit', async function (e) {
        e.preventDefault();
        clearErrors();

        const email = document.getElementById('email').value.trim();
        const motDePasse = document.getElementById('motDePasse').value;

        if (!validateForm(email, motDePasse)) return;

        try {
            const response = await apiFetch('/auth/login', {
                method: 'POST',
                body: JSON.stringify({ email, motDePasse }),
            });

            setToken(response.token);
            setRole(response.role);

            showMessage('Connexion réussie ! Redirection...', 'success');

            setTimeout(function () {
                redirectToDashboard(response.role);
            }, 1000);
        } catch (error) {
            if (error.status === 401) {
                showMessage('Identifiants invalides.', 'error');
            } else if (error.status === 403) {
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
            'ENCADRANT_ENTREPRISE': '/pages/dashboard-encadrant.html'
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
