# Story 1.3: Réinitialisation du mot de passe (U-03)

Status: review

## Story

As a utilisateur ayant oublié son mot de passe,
I want réinitialiser mon mot de passe via un lien sécurisé,
so that je puisse retrouver l'accès à mon compte.

## Acceptance Criteria

1. **Lien "Mot de passe oublié"** : Visible sur la page login.html, redirige vers un formulaire de saisie d'email.
2. **Endpoint POST /api/auth/reset-password** : Accepte `{email}`, génère un token de réinitialisation unique (UUID), le stocke en BDD avec expiration (1h), retourne 200 OK avec message générique (ne pas révéler si l'email existe).
3. **Endpoint POST /api/auth/reset-password/confirm** : Accepte `{token, nouveauMotDePasse}`, vérifie la validité du token (existence + non expiré), met à jour le mot de passe (BCrypt), invalide le token.
4. **Page de réinitialisation** : `/pages/reset-password.html` avec 2 étapes — saisie email, puis saisie nouveau mot de passe (avec le token en paramètre URL).
5. **Sécurité** : Token à usage unique, expiré après 1h. Mot de passe >= 8 caractères. Message réponse identique que l'email existe ou non (anti-énumération).
6. **Contexte PFA** : Pas d'envoi d'email réel. Le token est affiché dans la console serveur (log) ou retourné directement dans la réponse pour faciliter la démo.

## Tasks / Subtasks

- [x] Task 1 : Ajouter champ reset token dans User (AC: 2)
  - [x] Ajouter à `User.java` : `resetToken` (String, nullable), `resetTokenExpiry` (LocalDateTime, nullable)
- [x] Task 2 : Implémenter AuthService.requestPasswordReset() (AC: 2, 5, 6)
  - [x] Chercher user par email
  - [x] Si trouvé : générer UUID, stocker dans resetToken avec expiry = now + 1h
  - [x] Logger le token dans la console (pour la démo PFA)
  - [x] Retourner toujours 200 OK avec message "Si l'email existe, un lien de réinitialisation a été envoyé"
  - [x] Pour la démo : inclure le token dans la réponse JSON aussi
- [x] Task 3 : Implémenter AuthService.confirmPasswordReset() (AC: 3, 5)
  - [x] Chercher user par resetToken
  - [x] Vérifier que le token n'est pas expiré (resetTokenExpiry > now)
  - [x] Encoder le nouveau mot de passe avec BCrypt
  - [x] Mettre à jour motDePasse, nullifier resetToken et resetTokenExpiry
  - [x] Retourner 200 OK
  - [x] Si token invalide ou expiré → 400 "Token invalide ou expiré"
- [x] Task 4 : Ajouter les endpoints dans AuthController (AC: 2, 3)
  - [x] POST `/api/auth/reset-password` → requestPasswordReset()
  - [x] POST `/api/auth/reset-password/confirm` → confirmPasswordReset()
- [x] Task 5 : Ajouter à UserRepository (AC: 2, 3)
  - [x] `findByResetToken(String token)` : Optional<User>
- [x] Task 6 : Créer DTOs (AC: 2, 3)
  - [x] `dto/auth/ResetPasswordRequest.java` : email (@Email @NotBlank)
  - [x] `dto/auth/ConfirmResetRequest.java` : token (@NotBlank), nouveauMotDePasse (@Size min=8)
- [x] Task 7 : Créer reset-password.html + JS (AC: 1, 4)
  - [x] Étape 1 : formulaire email → appel POST /api/auth/reset-password
  - [x] Étape 2 : formulaire nouveau mot de passe (token récupéré depuis l'URL ?token=xxx ou depuis la réponse)
  - [x] Messages de feedback utilisateur
  - [x] Lien retour vers login

## Dev Notes

### Dépendance
Dépend de 1-1 (User entity, AuthService, AuthController) et 1-2 (BCrypt encoder, SecurityConfig endpoints publics).

### Simplification PFA
Pas de service d'email — le token est :
1. Loggé dans la console Spring Boot (`log.info("Reset token: {}", token)`)
2. Retourné dans la réponse JSON pour faciliter le test

En production, on remplacerait par un envoi d'email avec lien `https://app/pages/reset-password.html?token=xxx`.

### Sécurité anti-énumération
La réponse est TOUJOURS la même que l'email existe ou non. Cela empêche un attaquant de découvrir quels emails sont inscrits.

### Architecture
- Pas de nouvelle entité — on ajoute 2 colonnes nullable à `User`
- Endpoints sous `/api/auth/` donc publics (déjà configuré dans SecurityConfig)

### References
- [Source: architecture.md#API REST Authentification] - POST /api/auth/reset-password
- [Source: prd.md#FR3] - Réinitialisation mot de passe
- [Source: prd.md#NFR2] - Mots de passe hashés BCrypt

## Dev Agent Record

### Agent Model Used
Claude Opus 4.6

### Debug Log References
- Aucun problème rencontré

### Completion Notes List
- Task 1: Ajouté resetToken (String) et resetTokenExpiry (LocalDateTime) dans User.java, colonnes nullable.
- Task 2: requestPasswordReset() génère un UUID, stocke en BDD avec expiry +1h, log le token dans la console, retourne un message générique anti-énumération + le token pour la démo PFA.
- Task 3: confirmPasswordReset() vérifie token existence + expiration, encode le nouveau mot de passe BCrypt, nullifie le token. Token expiré → nettoyé + 400.
- Task 4: POST /api/auth/reset-password et POST /api/auth/reset-password/confirm ajoutés dans AuthController.
- Task 5: findByResetToken(String) ajouté à UserRepository.
- Task 6: ResetPasswordRequest et ConfirmResetRequest DTOs créés avec validation Jakarta.
- Task 7: reset-password.html avec 2 étapes (email puis nouveau MDP), reset-password.js avec gestion token URL ou réponse, lien "Mot de passe oublié" ajouté sur login.html.
- 5 tests unitaires ajoutés couvrant : génération token, anti-énumération, confirmation valide, token invalide, token expiré.

### File List
- src/main/java/com/smartintern/model/User.java (MODIFIED)
- src/main/java/com/smartintern/repository/UserRepository.java (MODIFIED)
- src/main/java/com/smartintern/service/AuthService.java (MODIFIED)
- src/main/java/com/smartintern/controller/AuthController.java (MODIFIED)
- src/main/java/com/smartintern/dto/auth/ResetPasswordRequest.java (NEW)
- src/main/java/com/smartintern/dto/auth/ConfirmResetRequest.java (NEW)
- src/main/resources/static/pages/reset-password.html (NEW)
- src/main/resources/static/js/reset-password.js (NEW)
- src/main/resources/static/pages/login.html (MODIFIED)
- src/test/java/com/smartintern/service/AuthServiceResetPasswordTest.java (NEW)

## Change Log
- 2026-03-21: Implémentation complète de la story 1-3 Réinitialisation mot de passe — ajout champs resetToken/resetTokenExpiry dans User, endpoints reset-password et reset-password/confirm, page frontend 2 étapes, anti-énumération, token loggé + retourné pour démo PFA. 38 tests passent (5 nouveaux + 33 existants).
