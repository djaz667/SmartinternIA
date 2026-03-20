# Story 1.2: Connexion email / mot de passe (U-02)

Status: ready-for-dev

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

- [ ] Task 1 : Implémenter JwtTokenProvider (AC: 4)
  - [ ] `security/JwtTokenProvider.java`
  - [ ] Méthode `generateToken(User user)` : crée le JWT avec claims userId, role, sub=email, expiration 24h
  - [ ] Méthode `validateToken(String token)` : vérifie signature et expiration
  - [ ] Méthode `getUserIdFromToken(String token)` : extrait userId
  - [ ] Méthode `getRoleFromToken(String token)` : extrait role
  - [ ] Utiliser `io.jsonwebtoken.Jwts` (jjwt 0.12.x) — attention l'API a changé vs 0.9.x : utiliser `Jwts.builder().claims()...` et `Jwts.parser().verifyWith(key).build()`
  - [ ] Clé secrète lue depuis `application.properties` : `jwt.secret`
  - [ ] Expiration lue depuis `application.properties` : `jwt.expiration` (86400000 ms)
- [ ] Task 2 : Implémenter JwtAuthenticationFilter (AC: 7)
  - [ ] `security/JwtAuthenticationFilter.java` extends `OncePerRequestFilter`
  - [ ] Extraire le token du header `Authorization: Bearer ...`
  - [ ] Appeler `JwtTokenProvider.validateToken()`
  - [ ] Charger le user depuis la BDD via `CustomUserDetailsService`
  - [ ] Créer `UsernamePasswordAuthenticationToken` et le setter dans `SecurityContextHolder`
  - [ ] Si pas de token ou token invalide → laisser passer (Spring Security refusera si endpoint protégé)
- [ ] Task 3 : Implémenter CustomUserDetailsService (AC: 7)
  - [ ] `security/CustomUserDetailsService.java` implements `UserDetailsService`
  - [ ] `loadUserByUsername(String email)` : charge le User depuis UserRepository, retourne UserDetails avec authorities = role
- [ ] Task 4 : Mettre à jour SecurityConfig (AC: 7)
  - [ ] Ajouter `JwtAuthenticationFilter` avant `UsernamePasswordAuthenticationFilter`
  - [ ] Endpoints publics : `/api/auth/**`, `/pages/**`, `/css/**`, `/js/**`, `/img/**`, `/index.html`
  - [ ] `/api/users/**` → authentifié
  - [ ] Reste → authentifié
- [ ] Task 5 : Implémenter AuthService.login() (AC: 2, 3, 8)
  - [ ] Chercher user par email (sinon 401)
  - [ ] Vérifier mot de passe avec `passwordEncoder.matches()` (sinon 401)
  - [ ] Vérifier `statutCompte == APPROUVE` (sinon 403 avec message adapté selon le statut)
  - [ ] Générer token JWT
  - [ ] Retourner `AuthResponse {token, role, nom, email}`
- [ ] Task 6 : Créer AuthResponse DTO (AC: 2)
  - [ ] `dto/auth/AuthResponse.java` : token, role, nom, email
- [ ] Task 7 : Ajouter endpoint login dans AuthController (AC: 2)
  - [ ] POST `/api/auth/login` → AuthService.login()
- [ ] Task 8 : Créer login.html + auth.js (AC: 1, 5, 6)
  - [ ] Formulaire de connexion
  - [ ] Appel fetch POST /api/auth/login
  - [ ] Stocker token et role dans localStorage
  - [ ] Redirection vers le dashboard approprié selon le rôle
  - [ ] Afficher messages d'erreur contextuels
- [ ] Task 9 : Créer api.js utilitaire (AC: 5, 7)
  - [ ] Fonction `apiFetch(url, options)` qui ajoute automatiquement le header Authorization Bearer
  - [ ] Gestion du 401 : supprimer token et rediriger vers login
  - [ ] Constante `API_BASE_URL`

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
### Debug Log References
### Completion Notes List
### File List
