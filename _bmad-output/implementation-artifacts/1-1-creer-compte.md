# Story 1.1: Créer un compte (U-01)

Status: ready-for-dev

## Story

As a utilisateur (étudiant ou entreprise),
I want créer un compte avec mon email et mot de passe,
so that je puisse accéder à la plateforme SmartIntern AI.

## Acceptance Criteria

1. **Formulaire d'inscription visible** : La page `/pages/register.html` affiche un formulaire avec les champs email, mot de passe, confirmation mot de passe, et sélection du rôle (ETUDIANT ou ENTREPRISE uniquement).
2. **Validation côté client** : Email valide, mot de passe >= 8 caractères, confirmation identique au mot de passe.
3. **Validation côté serveur** : Email unique en BDD, mot de passe hashé avec BCrypt avant persistance.
4. **Endpoint POST /api/auth/register** : Accepte `RegisterRequest {email, motDePasse, role}`, retourne 201 Created avec message de succès.
5. **Rôles autorisés à l'inscription** : Seuls ETUDIANT et ENTREPRISE peuvent s'inscrire publiquement. Les rôles ADMIN, ENCADRANT_ACADEMIQUE, ENCADRANT_ENTREPRISE sont créés uniquement par l'admin.
6. **Statut initial du compte** : `EN_ATTENTE` (l'admin doit approuver avant que l'utilisateur puisse se connecter).
7. **Doublon email** : Retourne 409 Conflict avec message explicite.
8. **Champs profil étendus** : Selon le rôle choisi, des champs supplémentaires sont collectés :
   - ETUDIANT : nom, prénom, filière (dropdown depuis `/api/filieres`)
   - ENTREPRISE : nom de l'entreprise, secteur, adresse, téléphone

## Tasks / Subtasks

- [ ] Task 1 : Initialiser le projet Spring Boot (AC: prérequis)
  - [ ] Créer le projet via Spring Initializr (Spring Web, Spring Security, Spring Data JPA, MySQL Driver, Validation, Lombok)
  - [ ] Configurer `pom.xml` avec les dépendances : spring-boot-starter-web, spring-boot-starter-security, spring-boot-starter-data-jpa, mysql-connector-j, jjwt (0.12.x), lombok, spring-boot-starter-validation
  - [ ] Configurer `application.properties` : datasource MySQL, JPA ddl-auto=update, jwt.secret, jwt.expiration
  - [ ] Créer `application-dev.properties` pour le profil dev local
- [ ] Task 2 : Créer les entités de base (AC: prérequis)
  - [ ] `model/User.java` : id, email (unique), motDePasse, role (enum), statutCompte (enum), dateCreation, actif
  - [ ] `model/Etudiant.java` : id, user (OneToOne), nom, prenom, filiere (ManyToOne), niveauAcademique, cvPath, bio
  - [ ] `model/Entreprise.java` : id, user (OneToOne), nom, secteur, adresse, telephone, description
  - [ ] `model/Filiere.java` : id, nom (unique)
  - [ ] `enums/Role.java` : ADMIN, ETUDIANT, ENTREPRISE, ENCADRANT_ACADEMIQUE, ENCADRANT_ENTREPRISE
  - [ ] `enums/StatutCompte.java` : EN_ATTENTE, APPROUVE, REFUSE, SUSPENDU
- [ ] Task 3 : Créer les repositories (AC: prérequis)
  - [ ] `UserRepository.java` : findByEmail(String email), existsByEmail(String email)
  - [ ] `EtudiantRepository.java` : findByUserId(Long userId)
  - [ ] `EntrepriseRepository.java` : findByUserId(Long userId)
  - [ ] `FiliereRepository.java` : findByNom(String nom)
- [ ] Task 4 : Créer le DTO RegisterRequest (AC: 4)
  - [ ] `dto/auth/RegisterRequest.java` : email (@Email @NotBlank), motDePasse (@Size min=8), role (@NotNull), nom, prenom, filiereId, nomEntreprise, secteur, adresse, telephone
- [ ] Task 5 : Implémenter AuthService.register() (AC: 3, 5, 6, 7)
  - [ ] Vérifier email unique (sinon 409)
  - [ ] Vérifier rôle autorisé (ETUDIANT ou ENTREPRISE uniquement)
  - [ ] Encoder mot de passe avec BCrypt (via PasswordEncoder bean)
  - [ ] Créer User avec statutCompte = EN_ATTENTE
  - [ ] Créer Etudiant ou Entreprise selon le rôle
  - [ ] Sauvegarder et retourner message succès
- [ ] Task 6 : Créer AuthController (AC: 4)
  - [ ] POST `/api/auth/register` → appelle AuthService.register()
  - [ ] Gestion erreurs : 409 doublon, 400 validation
- [ ] Task 7 : Configurer Spring Security (AC: prérequis)
  - [ ] `SecurityConfig.java` : désactiver CSRF, mode STATELESS, permettre /api/auth/** et /pages/** et ressources statiques
  - [ ] Bean PasswordEncoder (BCryptPasswordEncoder)
- [ ] Task 8 : Créer GlobalExceptionHandler (AC: 7)
  - [ ] Handler pour les exceptions métier avec format JSON uniforme {status, message, timestamp}
- [ ] Task 9 : Endpoint GET /api/filieres (AC: 8)
  - [ ] Retourne la liste des filières pour le dropdown d'inscription
- [ ] Task 10 : Créer la page register.html + JS (AC: 1, 2)
  - [ ] Formulaire responsive avec champs dynamiques selon le rôle
  - [ ] Validation JS avant envoi
  - [ ] Appel fetch POST /api/auth/register
  - [ ] Feedback utilisateur (succès → redirection login, erreur → message)
- [ ] Task 11 : Données initiales (AC: prérequis)
  - [ ] `data.sql` : insérer filières de base (Informatique, Génie Logiciel, Réseau, etc.)
  - [ ] `data.sql` : insérer un compte admin par défaut (admin@smartintern.tn / admin123)

## Dev Notes

### Architecture obligatoire
- Package racine : `com.smartintern`
- Architecture 3 couches : Controller → Service → Repository (JAMAIS de Repository dans Controller)
- `@Transactional` sur toute méthode Service qui modifie des données
- DTOs pour entrées/sorties, JAMAIS exposer les entités JPA

### Stack technique exacte
- Java 17+, Spring Boot 3.x
- MySQL 8.x, JPA/Hibernate avec ddl-auto=update
- JWT via jjwt 0.12.x (io.jsonwebtoken)
- BCrypt pour le hachage des mots de passe
- Lombok pour réduire le boilerplate (@Data, @Builder, @NoArgsConstructor, @AllArgsConstructor)

### Conventions de nommage
- Tables MySQL : `snake_case` (users, etudiants, entreprises, filieres)
- Colonnes : `snake_case` (user_id, date_creation, statut_compte)
- Classes Java : `PascalCase` (AuthController, UserRepository)
- JSON : `camelCase` (motDePasse, statutCompte)
- API : `/api/auth/register` (kebab-case)

### Format réponse erreur uniforme
```json
{"status": 404, "message": "...", "timestamp": "2026-03-20T10:00:00"}
```

### ATTENTION - Story fondatrice
Cette story initialise TOUT le projet Spring Boot. Les stories suivantes en dépendent. Prendre le temps de bien structurer les packages et les configurations de base.

### Project Structure Notes
- Frontend : fichiers dans `src/main/resources/static/` (pages/, js/, css/, img/)
- `api.js` : module utilitaire centralisant les appels fetch avec gestion du JWT token (localStorage)
- `auth.js` : logique d'authentification côté client

### References
- [Source: architecture.md#Structure du Projet Spring Boot] - Structure complète des packages
- [Source: architecture.md#Modèle de Données MySQL] - Schéma User, Etudiant, Entreprise, Filiere
- [Source: architecture.md#API REST] - Endpoints /api/auth et /api/filieres
- [Source: architecture.md#Sécurité JWT] - SecurityConfig et BCrypt
- [Source: prd.md#FR1] - Création de compte
- [Source: prd.md#NFR2] - Mots de passe hashés BCrypt

## Dev Agent Record

### Agent Model Used
### Debug Log References
### Completion Notes List
### File List
