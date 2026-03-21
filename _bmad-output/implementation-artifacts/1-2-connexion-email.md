# Story 1.2: Connexion email / mot de passe (U-02)

Status: review

## Story

As a utilisateur inscrit et approuvé,
I want me connecter avec mon email et mot de passe,
so that je puisse accéder à mon espace personnalisé sur la plateforme.

## Acceptance Criteria

1. **Page de connexion** : `/pages/login.html` affiche un formulaire email + mot de passe.
2. **Endpoint POST /api/auth/login** : Accepte `LoginRequest {email, motDePasse}`, retourne `AuthResponse {token, role, nom, email}`.
3. **Vérification du compte** : Seuls les comptes avec `statutCompte = APPROUVE` peuvent se connecter. Si EN_ATTENTE → 403 "Compte en attente d'approbation". Si REFUSE → 403 "Compte refusé". Si SUSPENDU → 403 "Compte suspendu".
4. **Token JWT généré** : Contient `sub` (email), `userId`, `role`, `iat`, `exp` (24h). Signé avec HMAC-SHA256.
5. **Stockage côté client** : Le token JWT est stocké dans `localStorage` sous la clé `jwt_token`. Le rôle sous `user_role`.
6. **Redirection après connexion** : Selon le rôle → dashboard correspondant (dashboard-admin.html, dashboard-etudiant.html, dashboard-entreprise.html, dashboard-encadrant.html).
7. **Filtre JWT** : Toute requête API (sauf /api/auth/**) doit inclure le header `Authorization: Bearer <token>`. Le filtre valide le token, extrait userId et role, et peuple le SecurityContext.
8. **Erreurs** : Email inconnu ou mot de passe incorrect → 401 "Identifiants invalides" (ne pas révéler lequel est faux).

## Tasks / Subtasks

- [x] Task 1 : Implémenter JwtTokenProvider (AC: 4)
  - [x] `security/JwtTokenProvider.java`
  - [x] Méthode `generateToken(User user)` : crée le JWT avec claims userId, role, sub=email, expiration 24h
  - [x] Méthode `validateToken(String token)` : vérifie signature et expiration
  - [x] Méthode `getUserIdFromToken(String token)` : extrait userId
  - [x] Méthode `getRoleFromToken(String token)` : extrait role
  - [x] Utiliser `io.jsonwebtoken.Jwts` (jjwt 0.12.x) — attention l'API a changé vs 0.9.x : utiliser `Jwts.builder().claims()...` et `Jwts.parser().verifyWith(key).build()`
  - [x] Clé secrète lue depuis `application.properties` : `jwt.secret`
  - [x] Expiration lue depuis `application.properties` : `jwt.expiration` (86400000 ms)
- [x] Task 2 : Implémenter JwtAuthenticationFilter (AC: 7)
  - [x] `security/JwtAuthenticationFilter.java` extends `OncePerRequestFilter`
  - [x] Extraire le token du header `Authorization: Bearer ...`
  - [x] Appeler `JwtTokenProvider.validateToken()`
  - [x] Charger le user depuis la BDD via `CustomUserDetailsService`
  - [x] Créer `UsernamePasswordAuthenticationToken` et le setter dans `SecurityContextHolder`
  - [x] Si pas de token ou token invalide → laisser passer (Spring Security refusera si endpoint protégé)
- [x] Task 3 : Implémenter CustomUserDetailsService (AC: 7)
  - [x] `security/CustomUserDetailsService.java` implements `UserDetailsService`
  - [x] `loadUserByUsername(String email)` : charge le User depuis UserRepository, retourne UserDetails avec authorities = role
- [x] Task 4 : Mettre à jour SecurityConfig (AC: 7)
  - [x] Ajouter `JwtAuthenticationFilter` avant `UsernamePasswordAuthenticationFilter`
  - [x] Endpoints publics : `/api/auth/**`, `/pages/**`, `/css/**`, `/js/**`, `/img/**`, `/index.html`
  - [x] `/api/users/**` → authentifié
  - [x] Reste → authentifié
- [x] Task 5 : Implémenter AuthService.login() (AC: 2, 3, 8)
  - [x] Chercher user par email (sinon 401)
  - [x] Vérifier mot de passe avec `passwordEncoder.matches()` (sinon 401)
  - [x] Vérifier `statutCompte == APPROUVE` (sinon 403 avec message adapté selon le statut)
  - [x] Générer token JWT
  - [x] Retourner `AuthResponse {token, role, nom, email}`
- [x] Task 6 : Créer AuthResponse DTO (AC: 2)
  - [x] `dto/auth/AuthResponse.java` : token, role, nom, email
- [x] Task 7 : Ajouter endpoint login dans AuthController (AC: 2)
  - [x] POST `/api/auth/login` → AuthService.login()
- [x] Task 8 : Créer login.html + auth.js (AC: 1, 5, 6)
  - [x] Formulaire de connexion
  - [x] Appel fetch POST /api/auth/login
  - [x] Stocker token et role dans localStorage
  - [x] Redirection vers le dashboard approprié selon le rôle
  - [x] Afficher messages d'erreur contextuels
- [x] Task 9 : Créer api.js utilitaire (AC: 5, 7)
  - [x] Fonction `apiFetch(url, options)` qui ajoute automatiquement le header Authorization Bearer
  - [x] Gestion du 401 : supprimer token et rediriger vers login
  - [x] Constante `API_BASE_URL`

## Dev Notes

### Dépendance story précédente
Cette story DÉPEND de 1-1 (créer un compte) : les entités User, les repositories, SecurityConfig de base, et le PasswordEncoder bean doivent exister.

### jjwt 0.12.x — API mise à jour
L'API jjwt a changé en version 0.12.x :
```java
// Génération
Jwts.builder()
    .subject(user.getEmail())
    .claim("userId", user.getId())
    .claim("role", user.getRole().name())
    .issuedAt(new Date())
    .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
    .signWith(getSigningKey())
    .compact();

// Validation
Jwts.parser()
    .verifyWith(getSigningKey())
    .build()
    .parseSignedClaims(token)
    .getPayload();

// Clé
private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
}
```

### Dépendances Maven pour JWT
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

### Architecture obligatoire
- Filtre JWT = `OncePerRequestFilter`, ajouté dans SecurityConfig
- Le nom du champ dans AuthResponse doit être cohérent : `nom` récupéré depuis Etudiant.nom ou Entreprise.nom selon le rôle
- `api.js` est le module central frontend — toutes les futures pages l'utiliseront

### Project Structure Notes
- `security/JwtTokenProvider.java` — gestion tokens
- `security/JwtAuthenticationFilter.java` — filtre HTTP
- `security/CustomUserDetailsService.java` — chargement user pour Spring Security
- `static/js/api.js` — utilitaire fetch centralisé
- `static/js/auth.js` — logique login/register/logout côté client
- `static/pages/login.html` — page de connexion

### References
- [Source: architecture.md#Sécurité JWT -- Flux d'Authentification] - Diagramme séquence complet
- [Source: architecture.md#Configuration JWT] - Propriétés jwt.secret, jwt.expiration
- [Source: architecture.md#Structure du Token JWT] - Claims sub, userId, role
- [Source: architecture.md#Sécurisation des Endpoints] - SecurityConfig pattern
- [Source: prd.md#FR2] - Connexion à la plateforme
- [Source: prd.md#NFR1] - JWT avec expiration

## Dev Agent Record

### Agent Model Used
Claude Opus 4.6

### Debug Log References
- Aucun problème majeur rencontré durant l'implémentation

### Completion Notes List
- Task 1: JwtTokenProvider implémenté avec jjwt 0.12.x API (generateToken, validateToken, getUserIdFromToken, getRoleFromToken, getEmailFromToken). 6 tests unitaires passent.
- Task 2: JwtAuthenticationFilter extends OncePerRequestFilter, extrait Bearer token, valide via JwtTokenProvider, peuple SecurityContext. 4 tests unitaires passent.
- Task 3: CustomUserDetailsService implements UserDetailsService, charge User depuis UserRepository par email, retourne UserDetails avec ROLE_ prefix. 2 tests unitaires passent.
- Task 4: SecurityConfig mis à jour avec injection de JwtAuthenticationFilter ajouté avant UsernamePasswordAuthenticationFilter. Endpoints publics configurés.
- Task 5: AuthService.login() implémenté avec vérification email/mot de passe (401), vérification statutCompte avec messages différenciés (403), génération JWT, résolution du nom depuis Etudiant ou Entreprise selon le rôle. 6 tests unitaires passent.
- Task 6: AuthResponse DTO créé (token, role, nom, email) + LoginRequest DTO avec validation.
- Task 7: POST /api/auth/login ajouté dans AuthController, retourne AuthResponse.
- Task 8: login.html créé avec formulaire email/mot de passe, auth.js gère la soumission, stockage jwt_token et user_role dans localStorage, redirection selon le rôle, messages d'erreur contextuels.
- Task 9: api.js mis à jour avec apiFetch(), gestion automatique du header Authorization Bearer, interception 401 avec suppression token et redirection login, alias apiRequest pour rétro-compatibilité avec register.js.
- ForbiddenException + handler ajoutés pour les erreurs 403 (statut compte non approuvé).

### File List
- src/main/java/com/smartintern/security/JwtTokenProvider.java (NEW)
- src/main/java/com/smartintern/security/JwtAuthenticationFilter.java (NEW)
- src/main/java/com/smartintern/security/CustomUserDetailsService.java (NEW)
- src/main/java/com/smartintern/config/SecurityConfig.java (MODIFIED)
- src/main/java/com/smartintern/service/AuthService.java (MODIFIED)
- src/main/java/com/smartintern/controller/AuthController.java (MODIFIED)
- src/main/java/com/smartintern/dto/auth/AuthResponse.java (NEW)
- src/main/java/com/smartintern/dto/auth/LoginRequest.java (NEW)
- src/main/java/com/smartintern/exception/ForbiddenException.java (NEW)
- src/main/java/com/smartintern/exception/GlobalExceptionHandler.java (MODIFIED)
- src/main/resources/static/pages/login.html (NEW)
- src/main/resources/static/js/auth.js (NEW)
- src/main/resources/static/js/api.js (MODIFIED)
- src/test/java/com/smartintern/security/JwtTokenProviderTest.java (NEW)
- src/test/java/com/smartintern/security/JwtAuthenticationFilterTest.java (NEW)
- src/test/java/com/smartintern/security/CustomUserDetailsServiceTest.java (NEW)
- src/test/java/com/smartintern/service/AuthServiceLoginTest.java (NEW)

## Change Log
- 2026-03-21: Implémentation complète de la story 1-2 Connexion email/mot de passe — backend JWT (JwtTokenProvider, JwtAuthenticationFilter, CustomUserDetailsService), AuthService.login() avec vérification statut compte, AuthController endpoint POST /api/auth/login, frontend login.html + auth.js + api.js utilitaire. 33 tests passent (13 nouveaux + 20 existants).
