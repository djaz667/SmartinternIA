document.addEventListener('DOMContentLoaded', function () {
    var step1 = document.getElementById('step1');
    var step2 = document.getElementById('step2');
    var messageDiv = document.getElementById('message');
    var resetToken = null;

    // Vérifier si un token est dans l'URL (?token=xxx)
    var params = new URLSearchParams(window.location.search);
    var urlToken = params.get('token');
    if (urlToken) {
        resetToken = urlToken;
        step1.classList.add('hidden');
        step2.classList.remove('hidden');
    }

    // Étape 1 : Demande de réinitialisation
    document.getElementById('requestForm').addEventListener('submit', async function (e) {
        e.preventDefault();
        clearErrors();

        var email = document.getElementById('email').value.trim();
        if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            showError('emailError', 'Veuillez entrer un email valide.');
            return;
        }

        try {
            var response = await apiRequest('/auth/reset-password', {
                method: 'POST',
                body: JSON.stringify({ email: email }),
            });

            showMessage(response.message, 'success');

            // Pour la démo PFA : si le token est dans la réponse, passer à l'étape 2
            if (response.token) {
                resetToken = response.token;
                setTimeout(function () {
                    step1.classList.add('hidden');
                    step2.classList.remove('hidden');
                    showMessage('Token reçu. Saisissez votre nouveau mot de passe.', 'success');
                }, 1500);
            }
        } catch (error) {
            showMessage(error.message || 'Erreur lors de la demande. Veuillez réessayer.', 'error');
        }
    });

    // Étape 2 : Confirmation nouveau mot de passe
    document.getElementById('confirmForm').addEventListener('submit', async function (e) {
        e.preventDefault();
        clearErrors();

        var nouveauMotDePasse = document.getElementById('nouveauMotDePasse').value;
        var confirmMotDePasse = document.getElementById('confirmMotDePasse').value;

        if (!nouveauMotDePasse || nouveauMotDePasse.length < 8) {
            showError('nouveauMotDePasseError', 'Le mot de passe doit contenir au moins 8 caractères.');
            return;
        }

        if (nouveauMotDePasse !== confirmMotDePasse) {
            showError('confirmMotDePasseError', 'Les mots de passe ne correspondent pas.');
            return;
        }

        try {
            var response = await apiRequest('/auth/reset-password/confirm', {
                method: 'POST',
                body: JSON.stringify({ token: resetToken, nouveauMotDePasse: nouveauMotDePasse }),
            });

            showMessage(response.message + ' Redirection vers la connexion...', 'success');
            setTimeout(function () {
                window.location.href = '/pages/login.html';
            }, 2000);
        } catch (error) {
            showMessage(error.message || 'Token invalide ou expiré.', 'error');
        }
    });

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
