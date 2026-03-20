# Story 1.3: Réinitialisation du mot de passe (U-03)

Status: ready-for-dev

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

- [ ] Task 1 : Ajouter champ reset token dans User (AC: 2)
  - [ ] Ajouter à `User.java` : `resetToken` (String, nullable), `resetTokenExpiry` (LocalDateTime, nullable)
- [ ] Task 2 : Implémenter AuthService.requestPasswordReset() (AC: 2, 5, 6)
  - [ ] Chercher user par email
  - [ ] Si trouvé : générer UUID, stocker dans resetToken avec expiry = now + 1h
  - [ ] Logger le token dans la console (pour la démo PFA)
  - [ ] Retourner toujours 200 OK avec message "Si l'email existe, un lien de réinitialisation a été envoyé"
  - [ ] Pour la démo : inclure le token dans la réponse JSON aussi
- [ ] Task 3 : Implémenter AuthService.confirmPasswordReset() (AC: 3, 5)
  - [ ] Chercher user par resetToken
  - [ ] Vérifier que le token n'est pas expiré (resetTokenExpiry > now)
  - [ ] Encoder le nouveau mot de passe avec BCrypt
  - [ ] Mettre à jour motDePasse, nullifier resetToken et resetTokenExpiry
  - [ ] Retourner 200 OK
  - [ ] Si token invalide ou expiré → 400 "Token invalide ou expiré"
- [ ] Task 4 : Ajouter les endpoints dans AuthController (AC: 2, 3)
  - [ ] POST `/api/auth/reset-password` → requestPasswordReset()
  - [ ] POST `/api/auth/reset-password/confirm` → confirmPasswordReset()
- [ ] Task 5 : Ajouter à UserRepository (AC: 2, 3)
  - [ ] `findByResetToken(String token)` : Optional<User>
- [ ] Task 6 : Créer DTOs (AC: 2, 3)
  - [ ] `dto/auth/ResetPasswordRequest.java` : email (@Email @NotBlank)
  - [ ] `dto/auth/ConfirmResetRequest.java` : token (@NotBlank), nouveauMotDePasse (@Size min=8)
- [ ] Task 7 : Créer reset-password.html + JS (AC: 1, 4)
  - [ ] Étape 1 : formulaire email → appel POST /api/auth/reset-password
  - [ ] Étape 2 : formulaire nouveau mot de passe (token récupéré depuis l'URL ?token=xxx ou depuis la réponse)
  - [ ] Messages de feedback utilisateur
  - [ ] Lien retour vers login

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
### Debug Log References
### Completion Notes List
### File List
