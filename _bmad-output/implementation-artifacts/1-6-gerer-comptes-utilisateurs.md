# Story 1.6: Gérer les comptes utilisateurs (AD-02)

Status: ready-for-dev

## Story

As a administrateur,
I want approuver, refuser ou suspendre les comptes utilisateurs,
so that seuls les utilisateurs légitimes accèdent à la plateforme.

## Acceptance Criteria

1. **Endpoint PUT /api/users/{id}/statut** : Accepte `{statutCompte}` (APPROUVE, REFUSE, SUSPENDU), accessible uniquement par ADMIN. Retourne le profil mis à jour.
2. **Comptes en attente** : L'admin voit la liste des comptes avec `statutCompte = EN_ATTENTE` dans une section dédiée du dashboard admin.
3. **Approbation** : Passer un compte de EN_ATTENTE à APPROUVE permet à l'utilisateur de se connecter.
4. **Refus** : Passer un compte à REFUSE empêche la connexion avec message "Compte refusé".
5. **Suspension** : Passer un compte APPROUVE à SUSPENDU bloque la connexion avec message "Compte suspendu".
6. **Filtres** : L'admin peut filtrer les utilisateurs par statut (EN_ATTENTE, APPROUVE, REFUSE, SUSPENDU) et par rôle.
7. **Compteur** : Le dashboard admin affiche le nombre de comptes en attente d'approbation (badge visuel).

## Tasks / Subtasks

- [ ] Task 1 : Implémenter UserService.updateStatut() (AC: 1, 3, 4, 5)
  - [ ] Charger user par id (sinon 404)
  - [ ] Valider la transition de statut (pas de transition vers EN_ATTENTE)
  - [ ] Mettre à jour statutCompte
  - [ ] Sauvegarder et retourner UserResponse
- [ ] Task 2 : Implémenter UserService.getUsersByFilters() (AC: 2, 6)
  - [ ] GET /api/users?statut=EN_ATTENTE&role=ETUDIANT
  - [ ] Filtrage par statut et/ou rôle (paramètres optionnels)
  - [ ] Retourner List<UserResponse>
- [ ] Task 3 : Implémenter UserService.countPendingUsers() (AC: 7)
  - [ ] Compter les users avec statutCompte = EN_ATTENTE
  - [ ] GET /api/users/pending/count → retourne {count: N}
- [ ] Task 4 : Ajouter les endpoints dans UserController (AC: 1, 6, 7)
  - [ ] PUT `/api/users/{id}/statut` → updateStatut() [@PreAuthorize("hasRole('ADMIN')")]
  - [ ] GET `/api/users` avec @RequestParam statut, role → getUsersByFilters()
  - [ ] GET `/api/users/pending/count` → countPendingUsers()
- [ ] Task 5 : Ajouter à UserRepository (AC: 2, 6, 7)
  - [ ] `findByStatutCompte(StatutCompte statut)` : List<User>
  - [ ] `findByRoleAndStatutCompte(Role role, StatutCompte statut)` : List<User>
  - [ ] `countByStatutCompte(StatutCompte statut)` : Long
- [ ] Task 6 : Interface admin — gestion des comptes (AC: 2, 6, 7)
  - [ ] Section "Comptes en attente" dans dashboard-admin.html avec badge compteur
  - [ ] Tableau des comptes en attente avec boutons "Approuver" / "Refuser"
  - [ ] Filtres dropdown : par statut, par rôle
  - [ ] Bouton "Suspendre" sur les comptes approuvés
  - [ ] Feedback visuel après chaque action (succès/erreur)

## Dev Notes

### Dépendance
Dépend de 1-1 (User entity, StatutCompte enum), 1-2 (JWT auth), 1-5 (UserController, UserResponse DTO).

### Lien avec le login (Story 1-2)
La vérification du statut du compte est déjà implémentée dans `AuthService.login()` (story 1-2). Cette story complète le circuit en permettant à l'admin de changer les statuts.

### Transitions de statut autorisées
```
EN_ATTENTE → APPROUVE (approbation)
EN_ATTENTE → REFUSE (refus)
APPROUVE → SUSPENDU (suspension)
SUSPENDU → APPROUVE (réactivation)
```
Interdites : retour vers EN_ATTENTE, passage direct REFUSE → APPROUVE (doit recréer un compte).

### Architecture
- Réutiliser `UserController` créé dans story 1-5
- `UserResponse` DTO déjà créé dans story 1-5 — le réutiliser
- Les filtres sont des @RequestParam optionnels sur GET /api/users

### References
- [Source: architecture.md#API REST Utilisateurs] - PUT /api/users/{id}/statut
- [Source: prd.md#FR5] - Approuver ou refuser les comptes
- [Source: prd.md#NFR4] - Isolation des données

## Dev Agent Record

### Agent Model Used
### Debug Log References
### Completion Notes List
### File List
