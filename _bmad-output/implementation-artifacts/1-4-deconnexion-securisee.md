# Story 1.4: Déconnexion sécurisée (U-04)

Status: review

## Story

As a utilisateur connecté,
I want me déconnecter de manière sécurisée,
so that personne ne puisse utiliser ma session après mon départ.

## Acceptance Criteria

1. **Bouton de déconnexion** : Visible sur toutes les pages authentifiées (dans la barre de navigation ou le header), libellé "Déconnexion".
2. **Action côté client** : Suppression du `jwt_token` et `user_role` de `localStorage`, redirection vers `/pages/login.html`.
3. **Protection des pages** : Toute page authentifiée vérifie la présence du token JWT dans localStorage au chargement. Si absent → redirection vers login.
4. **Endpoint optionnel POST /api/auth/logout** : Retourne 200 OK. Côté serveur JWT stateless = pas de blacklist nécessaire pour le PFA. L'endpoint sert de convention pour le frontend.
5. **Navigation protégée** : Après déconnexion, le bouton "Retour" du navigateur ne doit pas permettre d'accéder aux pages protégées (le guard JS redirige vers login).

## Tasks / Subtasks

- [x] Task 1 : Créer le guard d'authentification JS (AC: 3, 5)
  - [x] Dans `auth.js` : fonction `checkAuth()` qui vérifie localStorage.jwt_token
  - [x] Si absent → `window.location.href = '/pages/login.html'`
  - [x] Si présent → décoder le token (base64 du payload) et vérifier l'expiration
  - [x] Fonction `getCurrentUser()` retournant {email, role, userId} depuis le token
  - [x] Appeler `checkAuth()` au chargement de chaque page protégée
- [x] Task 2 : Implémenter la fonction logout JS (AC: 2)
  - [x] Dans `auth.js` : fonction `logout()`
  - [x] `localStorage.removeItem('jwt_token')`
  - [x] `localStorage.removeItem('user_role')`
  - [x] Appel optionnel POST /api/auth/logout (fire-and-forget)
  - [x] `window.location.href = '/pages/login.html'`
- [x] Task 3 : Créer le layout / header commun (AC: 1)
  - [x] Créer un snippet HTML de navigation réutilisable (ou dans chaque page dashboard)
  - [x] Inclure le bouton "Déconnexion" avec `onclick="logout()"`
  - [x] Afficher le nom de l'utilisateur connecté dans le header
- [x] Task 4 : Ajouter endpoint logout (AC: 4)
  - [x] POST `/api/auth/logout` dans AuthController → retourne 200 OK
  - [x] Endpoint authentifié (valide que le token est bien présent)
- [x] Task 5 : Créer les pages dashboard squelettes (AC: 1, 3)
  - [x] `pages/dashboard-admin.html` — squelette avec header + bouton logout + zone contenu
  - [x] `pages/dashboard-etudiant.html` — squelette
  - [x] `pages/dashboard-entreprise.html` — squelette
  - [x] `pages/dashboard-encadrant.html` — squelette
  - [x] Chaque page appelle `checkAuth()` au chargement et affiche le nom du user
- [x] Task 6 : Créer index.html (AC: prérequis)
  - [x] Page d'accueil qui redirige vers login ou dashboard selon l'état de connexion

## Dev Notes

### Dépendance
Dépend de 1-1 (project setup) et 1-2 (JWT, auth.js, api.js, login.html).

### JWT Stateless — Pas de blacklist
Avec JWT stateless, la déconnexion est côté client uniquement (suppression du token). Pour un PFA, c'est suffisant. En production, on ajouterait une blacklist Redis ou un refresh token.

### Pattern de guard frontend
Toutes les pages protégées doivent inclure ce pattern au début du `<script>` :
```javascript
document.addEventListener('DOMContentLoaded', () => {
    checkAuth(); // Redirige vers login si pas de token
    const user = getCurrentUser();
    // ... initialiser la page
});
```

### Pages dashboard — squelettes seulement
Cette story crée des pages dashboard avec un layout de base (header, navigation, zone contenu). Le contenu réel des dashboards sera implémenté dans les sprints suivants.

### Architecture
- `auth.js` centralisé = un seul fichier pour toute la logique auth côté client
- Les dashboards seront des pages HTML distinctes par rôle (pas de SPA)

### References
- [Source: prd.md#FR4] - Déconnexion sécurisée
- [Source: prd.md#FR48] - Espace personnalisé par rôle après connexion
- [Source: architecture.md#Design et Navigation] - Dashboard personnalisé par rôle

## Dev Agent Record

### Agent Model Used
Claude Opus 4.6

### Debug Log References
- Aucun problème rencontré

### Completion Notes List
- Task 1: auth.js restructuré — checkAuth() vérifie présence + expiration du token JWT via décodage base64 du payload. getCurrentUser() retourne {email, role, userId}. parseJwt() utilitaire ajouté.
- Task 2: logout() supprime jwt_token et user_role du localStorage, fait un POST fire-and-forget vers /api/auth/logout, puis redirige vers login.html.
- Task 3: Header dashboard-header créé avec logo, badge rôle, email utilisateur, bouton Déconnexion. Styles CSS ajoutés (dashboard-header, user-info, btn-logout, dashboard-content, dashboard-card).
- Task 4: POST /api/auth/logout ajouté dans AuthController, retourne 200 OK. Endpoint sous /api/auth/** donc accessible publiquement (fire-and-forget pattern).
- Task 5: 4 pages dashboard squelettes créées (admin, etudiant, entreprise, encadrant), chacune avec header, bouton déconnexion, zone contenu placeholder. Chaque page appelle checkAuth() + getCurrentUser() au DOMContentLoaded.
- Task 6: index.html créé comme page d'accueil — vérifie le token, décode le rôle, redirige vers le bon dashboard ou vers login si pas connecté. Spring Boot le détecte automatiquement comme welcome page.

### File List
- src/main/resources/static/js/auth.js (MODIFIED)
- src/main/resources/static/css/style.css (MODIFIED)
- src/main/java/com/smartintern/controller/AuthController.java (MODIFIED)
- src/main/resources/static/index.html (NEW)
- src/main/resources/static/pages/dashboard-admin.html (NEW)
- src/main/resources/static/pages/dashboard-etudiant.html (NEW)
- src/main/resources/static/pages/dashboard-entreprise.html (NEW)
- src/main/resources/static/pages/dashboard-encadrant.html (NEW)

## Change Log
- 2026-03-21: Implémentation complète de la story 1-4 Déconnexion sécurisée — auth guard JS (checkAuth, getCurrentUser, parseJwt), logout(), endpoint POST /api/auth/logout, 4 dashboards squelettes avec header/bouton déconnexion, index.html avec redirection intelligente. 38 tests passent (0 régression).
