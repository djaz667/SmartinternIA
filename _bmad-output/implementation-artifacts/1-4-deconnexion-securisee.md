# Story 1.4: Déconnexion sécurisée (U-04)

Status: ready-for-dev

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

- [ ] Task 1 : Créer le guard d'authentification JS (AC: 3, 5)
  - [ ] Dans `auth.js` : fonction `checkAuth()` qui vérifie localStorage.jwt_token
  - [ ] Si absent → `window.location.href = '/pages/login.html'`
  - [ ] Si présent → décoder le token (base64 du payload) et vérifier l'expiration
  - [ ] Fonction `getCurrentUser()` retournant {email, role, userId} depuis le token
  - [ ] Appeler `checkAuth()` au chargement de chaque page protégée
- [ ] Task 2 : Implémenter la fonction logout JS (AC: 2)
  - [ ] Dans `auth.js` : fonction `logout()`
  - [ ] `localStorage.removeItem('jwt_token')`
  - [ ] `localStorage.removeItem('user_role')`
  - [ ] Appel optionnel POST /api/auth/logout (fire-and-forget)
  - [ ] `window.location.href = '/pages/login.html'`
- [ ] Task 3 : Créer le layout / header commun (AC: 1)
  - [ ] Créer un snippet HTML de navigation réutilisable (ou dans chaque page dashboard)
  - [ ] Inclure le bouton "Déconnexion" avec `onclick="logout()"`
  - [ ] Afficher le nom de l'utilisateur connecté dans le header
- [ ] Task 4 : Ajouter endpoint logout (AC: 4)
  - [ ] POST `/api/auth/logout` dans AuthController → retourne 200 OK
  - [ ] Endpoint authentifié (valide que le token est bien présent)
- [ ] Task 5 : Créer les pages dashboard squelettes (AC: 1, 3)
  - [ ] `pages/dashboard-admin.html` — squelette avec header + bouton logout + zone contenu
  - [ ] `pages/dashboard-etudiant.html` — squelette
  - [ ] `pages/dashboard-entreprise.html` — squelette
  - [ ] `pages/dashboard-encadrant.html` — squelette
  - [ ] Chaque page appelle `checkAuth()` au chargement et affiche le nom du user
- [ ] Task 6 : Créer index.html (AC: prérequis)
  - [ ] Page d'accueil qui redirige vers login ou dashboard selon l'état de connexion

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
### Debug Log References
### Completion Notes List
### File List
