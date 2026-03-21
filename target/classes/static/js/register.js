document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('registerForm');
    const roleSelect = document.getElementById('role');
    const etudiantFields = document.getElementById('etudiantFields');
    const entrepriseFields = document.getElementById('entrepriseFields');
    const messageDiv = document.getElementById('message');

    loadFilieres();

    roleSelect.addEventListener('change', function () {
        etudiantFields.classList.add('hidden');
        entrepriseFields.classList.add('hidden');

        if (this.value === 'ETUDIANT') {
            etudiantFields.classList.remove('hidden');
        } else if (this.value === 'ENTREPRISE') {
            entrepriseFields.classList.remove('hidden');
        }
    });

    form.addEventListener('submit', async function (e) {
        e.preventDefault();
        clearErrors();

        if (!validateForm()) return;

        const data = buildRequestData();

        try {
            await apiRequest('/auth/register', {
                method: 'POST',
                body: JSON.stringify(data),
            });
            showMessage('Compte créé avec succès ! En attente d\'approbation. Redirection...', 'success');
            setTimeout(() => {
                window.location.href = '/pages/login.html';
            }, 2000);
        } catch (error) {
            if (error.status === 409) {
                showMessage(error.message || 'Un compte avec cet email existe déjà.', 'error');
            } else if (error.status === 400) {
                showMessage(error.message || 'Données invalides.', 'error');
            } else {
                showMessage('Erreur lors de l\'inscription. Veuillez réessayer.', 'error');
            }
        }
    });

    function validateForm() {
        let valid = true;
        const email = document.getElementById('email').value.trim();
        const motDePasse = document.getElementById('motDePasse').value;
        const confirmMotDePasse = document.getElementById('confirmMotDePasse').value;
        const role = roleSelect.value;

        if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            showError('emailError', 'Veuillez entrer un email valide.');
            valid = false;
        }

        if (!motDePasse || motDePasse.length < 8) {
            showError('motDePasseError', 'Le mot de passe doit contenir au moins 8 caractères.');
            valid = false;
        }

        if (motDePasse !== confirmMotDePasse) {
            showError('confirmMotDePasseError', 'Les mots de passe ne correspondent pas.');
            valid = false;
        }

        if (!role) {
            showError('roleError', 'Veuillez sélectionner un rôle.');
            valid = false;
        }

        return valid;
    }

    function buildRequestData() {
        const data = {
            email: document.getElementById('email').value.trim(),
            motDePasse: document.getElementById('motDePasse').value,
            role: roleSelect.value,
        };

        if (roleSelect.value === 'ETUDIANT') {
            data.nom = document.getElementById('nom').value.trim();
            data.prenom = document.getElementById('prenom').value.trim();
            const filiereId = document.getElementById('filiereId').value;
            if (filiereId) data.filiereId = parseInt(filiereId);
        } else if (roleSelect.value === 'ENTREPRISE') {
            data.nomEntreprise = document.getElementById('nomEntreprise').value.trim();
            data.secteur = document.getElementById('secteur').value.trim();
            data.adresse = document.getElementById('adresse').value.trim();
            data.telephone = document.getElementById('telephone').value.trim();
        }

        return data;
    }

    async function loadFilieres() {
        try {
            const filieres = await apiRequest('/filieres');
            const select = document.getElementById('filiereId');
            filieres.forEach(function (filiere) {
                const option = document.createElement('option');
                option.value = filiere.id;
                option.textContent = filiere.nom;
                select.appendChild(option);
            });
        } catch (error) {
            console.error('Erreur lors du chargement des filières:', error);
        }
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
