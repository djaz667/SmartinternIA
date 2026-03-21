# Story 1.1: Créer un compte (U-01)

Status: review

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

- [x] Task 1 : Initialiser le projet Spring Boot (AC: prérequis)
  - [x] Créer le projet via Spring Initializr (Spring Web, Spring Security, Spring Data JPA, MySQL Driver, Validation, Lombok)
  - [x] Configurer `pom.xml` avec les dépendances : spring-boot-starter-web, spring-boot-starter-security, spring-boot-starter-data-jpa, mysql-connector-j, jjwt (0.12.x), lombok, spring-boot-starter-validation
  - [x] Configurer `application.properties` : datasource MySQL, JPA ddl-auto=update, jwt.secret, jwt.expiration
  - [x] Créer `application-dev.properties` pour le profil dev local
- [x] Task 2 : Créer les entités de base (AC: prérequis)
  - [x] `model/User.java` : id, email (unique), motDePasse, role (enum), statutCompte (enum), dateCreation, actif
  - [x] `model/Etudiant.java` : id, user (OneToOne), nom, prenom, filiere (ManyToOne), niveauAcademique, cvPath, bio
  - [x] `model/Entreprise.java` : id, user (OneToOne), nom, secteur, adresse, telephone, description
  - [x] `model/Filiere.java` : id, nom (unique)
  - [x] `enums/Role.java` : ADMIN, ETUDIANT, ENTREPRISE, ENCADRANT_ACADEMIQUE, ENCADRANT_ENTREPRISE
  - [x] `enums/StatutCompte.java` : EN_ATTENTE, APPROUVE, REFUSE, SUSPENDU
- [x] Task 3 : Créer les repositories (AC: prérequis)
  - [x] `UserRepository.java` : findByEmail(String email), existsByEmail(String email)
  - [x] `EtudiantRepository.java` : findByUserId(Long userId)
  - [x] `EntrepriseRepository.java` : findByUserId(Long userId)
  - [x] `FiliereRepository.java` : findByNom(String nom)
- [x] Task 4 : Créer le DTO RegisterRequest (AC: 4)
  - [x] `dto/auth/RegisterRequest.java` : email (@Email @NotBlank), motDePasse (@Size min=8), role (@NotNull), nom, prenom, filiereId, nomEntreprise, secteur, adresse, telephone
- [x] Task 5 : Implémenter AuthService.register() (AC: 3, 5, 6, 7)
  - [x] Vérifier email unique (sinon 409)
  - [x] Vérifier rôle autorisé (ETUDIANT ou ENTREPRISE uniquement)
  - [x] Encoder mot de passe avec BCrypt (via PasswordEncoder bean)
  - [x] Créer User avec statutCompte = EN_ATTENTE
  - [x] Créer Etudiant ou Entreprise selon le rôle
  - [x] Sauvegarder et retourner message succès
- [x] Task 6 : Créer AuthController (AC: 4)
  - [x] POST `/api/auth/register` → appelle AuthService.register()
  - [x] Gestion erreurs : 409 doublon, 400 validation
- [x] Task 7 : Configurer Spring Security (AC: prérequis)
  - [x] `SecurityConfig.java` : désactiver CSRF, mode STATELESS, permettre /api/auth/** et /pages/** et ressources statiques
  - [x] Bean PasswordEncoder (BCryptPasswordEncoder)
- [x] Task 8 : Créer GlobalExceptionHandler (AC: 7)
  - [x] Handler pour les exceptions métier avec format JSON uniforme {status, message, timestamp}
- [x] Task 9 : Endpoint GET /api/filieres (AC: 8)
  - [x] Retourne la liste des filières pour le dropdown d'inscription
- [x] Task 10 : Créer la page register.html + JS (AC: 1, 2)
  - [x] Formulaire responsive avec champs dynamiques selon le rôle
  - [x] Validation JS avant envoi
  - [x] Appel fetch POST /api/auth/register
  - [x] Feedback utilisateur (succès → redirection login, erreur → message)
- [x] Task 11 : Données initiales (AC: prérequis)
  - [x] `data.sql` : insérer filières de base (Informatique, Génie Logiciel, Réseau, etc.)
  - [x] `data.sql` : insérer un compte admin par défaut (admin@smartintern.tn / admin123)

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
Claude Opus 4.6

### Debug Log References
- Compilation initiale : MockBean import incompatible avec Spring Boot 3.2.3, corrigé avec setup MockMvc standalone

### Completion Notes List
- Projet Spring Boot 3.2.3 initialisé avec toutes les dépendances (web, security, JPA, MySQL, JWT, Lombok, validation)
- 4 entités JPA créées : User, Etudiant, Entreprise, Filiere avec relations OneToOne/ManyToOne
- 2 enums : Role (5 valeurs), StatutCompte (4 valeurs)
- 4 repositories Spring Data JPA avec méthodes de recherche custom
- DTO RegisterRequest avec validation Jakarta (Email, Size, NotNull, NotBlank)
- AuthService.register() : vérification email unique, rôle autorisé, encodage BCrypt, statut EN_ATTENTE, création profil Etudiant/Entreprise
- AuthController POST /api/auth/register retournant 201 Created
- SecurityConfig : CSRF désactivé, mode STATELESS, endpoints publics configurés
- GlobalExceptionHandler : format JSON uniforme {status, message, timestamp} pour toutes les exceptions
- FiliereController + FiliereService : GET /api/filieres pour le dropdown d'inscription
- Page register.html avec formulaire dynamique (champs selon rôle), validation JS, appel API fetch
- data.sql : 8 filières de base + compte admin par défaut
- api.js : module utilitaire fetch avec gestion JWT token
- 8 tests unitaires AuthService (inscription étudiant/entreprise, doublon email, rôles interdits, encodage BCrypt, statut initial)
- 6 tests controller AuthController (201 succès, 409 doublon, 400 validation email/mdp/rôle, rôle admin interdit)
- 1 test contexte Spring Boot
- Total : 15 tests, 0 échecs

### File List
- pom.xml (nouveau)
- mvnw (nouveau)
- mvnw.cmd (nouveau)
- .mvn/wrapper/maven-wrapper.jar (nouveau)
- .mvn/wrapper/maven-wrapper.properties (nouveau)
- src/main/java/com/smartintern/SmartInternApplication.java (nouveau)
- src/main/java/com/smartintern/config/SecurityConfig.java (nouveau)
- src/main/java/com/smartintern/model/User.java (nouveau)
- src/main/java/com/smartintern/model/Etudiant.java (nouveau)
- src/main/java/com/smartintern/model/Entreprise.java (nouveau)
- src/main/java/com/smartintern/model/Filiere.java (nouveau)
- src/main/java/com/smartintern/enums/Role.java (nouveau)
- src/main/java/com/smartintern/enums/StatutCompte.java (nouveau)
- src/main/java/com/smartintern/repository/UserRepository.java (nouveau)
- src/main/java/com/smartintern/repository/EtudiantRepository.java (nouveau)
- src/main/java/com/smartintern/repository/EntrepriseRepository.java (nouveau)
- src/main/java/com/smartintern/repository/FiliereRepository.java (nouveau)
- src/main/java/com/smartintern/dto/auth/RegisterRequest.java (nouveau)
- src/main/java/com/smartintern/service/AuthService.java (nouveau)
- src/main/java/com/smartintern/service/FiliereService.java (nouveau)
- src/main/java/com/smartintern/controller/AuthController.java (nouveau)
- src/main/java/com/smartintern/controller/FiliereController.java (nouveau)
- src/main/java/com/smartintern/exception/GlobalExceptionHandler.java (nouveau)
- src/main/java/com/smartintern/exception/ResourceNotFoundException.java (nouveau)
- src/main/java/com/smartintern/exception/DuplicateResourceException.java (nouveau)
- src/main/java/com/smartintern/exception/UnauthorizedException.java (nouveau)
- src/main/resources/application.properties (nouveau)
- src/main/resources/application-dev.properties (nouveau)
- src/main/resources/data.sql (nouveau)
- src/main/resources/static/css/style.css (nouveau)
- src/main/resources/static/js/api.js (nouveau)
- src/main/resources/static/js/register.js (nouveau)
- src/main/resources/static/pages/register.html (nouveau)
- src/test/resources/application-test.properties (nouveau)
- src/test/java/com/smartintern/SmartInternApplicationTests.java (nouveau)
- src/test/java/com/smartintern/service/AuthServiceTest.java (nouveau)
- src/test/java/com/smartintern/controller/AuthControllerTest.java (nouveau)

## Change Log
- 2026-03-21 : Implémentation complète de la story 1-1-creer-compte. Initialisation du projet Spring Boot avec structure 3 couches, entités JPA, endpoint inscription, page frontend, données initiales, et suite de 15 tests.
