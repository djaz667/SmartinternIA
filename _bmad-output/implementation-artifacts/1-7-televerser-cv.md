# Story 1.7: Téléverser son CV (ET-01)

Status: review

## Story

As a étudiant,
I want téléverser mon CV au format PDF,
so that les entreprises puissent consulter mon profil complet lors de ma candidature.

## Acceptance Criteria

1. **Endpoint POST /api/etudiants/me/cv** : Upload multipart d'un fichier PDF, accessible uniquement par ETUDIANT. Retourne 200 OK avec le chemin du fichier.
2. **Validation format** : Seuls les fichiers PDF sont acceptés (vérification du content-type `application/pdf`). Sinon 400 "Format non autorisé, seul le PDF est accepté".
3. **Validation taille** : Taille max 5 Mo. Sinon 400 "Fichier trop volumineux (max 5 Mo)".
4. **Stockage** : Le fichier est sauvegardé dans le dossier `uploads/cv/` avec un nom unique : `cv_{userId}_{timestamp}.pdf`.
5. **Mise à jour profil** : Le champ `cvPath` de l'entité Etudiant est mis à jour avec le chemin relatif du fichier.
6. **Remplacement** : Si l'étudiant a déjà un CV, l'ancien fichier est supprimé et remplacé par le nouveau.
7. **Consultation** : Endpoint GET `/api/etudiants/{id}/cv` pour télécharger le CV (accessible par ADMIN, ENTREPRISE, ENCADRANT).
8. **Profil étudiant complet** : Endpoint GET `/api/etudiants/me` retourne le profil complet de l'étudiant (nom, prénom, filière, compétences, cvPath, bio). Endpoint PUT `/api/etudiants/me` permet de modifier le profil.

## Tasks / Subtasks

- [x] Task 1 : Implémenter FileStorageService (AC: 4, 6)
  - [x] `service/FileStorageService.java`
  - [x] Méthode `storeFile(MultipartFile file, String subDir, String prefix)` : sauvegarde le fichier avec nom unique, retourne le chemin relatif
  - [x] Méthode `deleteFile(String filePath)` : supprime un fichier existant
  - [x] Méthode `loadFile(String filePath)` : charge un fichier comme Resource pour le download
  - [x] Créer le dossier `uploads/cv/` au démarrage si inexistant
  - [x] Configurer le chemin uploads dans application.properties : `app.upload.dir=uploads`
- [x] Task 2 : Implémenter upload CV dans EtudiantService (AC: 1, 2, 3, 5, 6)
  - [x] Créer `EtudiantService.java`
  - [x] Méthode `uploadCv(Long userId, MultipartFile file)`
  - [x] Valider content-type = application/pdf
  - [x] Valider taille <= 5 Mo
  - [x] Supprimer l'ancien CV si existant
  - [x] Sauvegarder via FileStorageService
  - [x] Mettre à jour Etudiant.cvPath
- [x] Task 3 : Implémenter profil étudiant (AC: 8)
  - [x] `EtudiantService.getMyProfile(Long userId)` → EtudiantResponse
  - [x] `EtudiantService.updateMyProfile(Long userId, UpdateEtudiantRequest)` → EtudiantResponse
- [x] Task 4 : Créer les DTOs (AC: 7, 8)
  - [x] `dto/etudiant/EtudiantResponse.java` : id, nom, prenom, filiere, niveauAcademique, cvPath, bio, email
  - [x] `dto/etudiant/UpdateEtudiantRequest.java` : nom, prenom, filiereId, niveauAcademique, bio
- [x] Task 5 : Créer EtudiantController (AC: 1, 7, 8)
  - [x] POST `/api/etudiants/me/cv` → uploadCv() [@PreAuthorize("hasRole('ETUDIANT')")]
  - [x] GET `/api/etudiants/me` → getMyProfile() [@PreAuthorize("hasRole('ETUDIANT')")]
  - [x] PUT `/api/etudiants/me` → updateMyProfile() [@PreAuthorize("hasRole('ETUDIANT')")]
  - [x] GET `/api/etudiants/{id}/cv` → downloadCv() [@PreAuthorize admin/entreprise/encadrant]
  - [x] GET `/api/etudiants/{id}` → getProfile() [@PreAuthorize admin/encadrant]
- [x] Task 6 : Configurer upload dans Spring Boot (AC: 2, 3)
  - [x] `application.properties` : `spring.servlet.multipart.max-file-size=5MB`, `spring.servlet.multipart.max-request-size=5MB`
  - [x] `app.upload.dir=uploads` configuré, test utilise `target/test-uploads`
- [x] Task 7 : Interface étudiant — profil et CV (AC: 1, 8)
  - [x] Section "Mon profil" dans dashboard-etudiant.html
  - [x] Formulaire de modification du profil (nom, prénom, filière dropdown, bio)
  - [x] Zone d'upload CV avec drag & drop ou bouton fichier
  - [x] Indicateur visuel si CV déjà uploadé (nom du fichier)
  - [x] Bouton "Remplacer le CV"

## Dev Notes

### Dépendance
Dépend de 1-1 (Etudiant entity), 1-2 (JWT auth), 1-4 (dashboard-etudiant.html squelette).

### Upload multipart Spring Boot
```java
@PostMapping("/me/cv")
@PreAuthorize("hasRole('ETUDIANT')")
public ResponseEntity<?> uploadCv(
    @RequestParam("file") MultipartFile file,
    @AuthenticationPrincipal UserDetails userDetails) {
    // ...
}
```

### Sécurité fichiers
- TOUJOURS valider le content-type côté serveur (ne pas se fier à l'extension)
- Nom de fichier généré côté serveur (jamais le nom original) pour éviter les injections de path
- Le dossier `uploads/` ne doit PAS être dans le classpath — le servir via un ResourceHandler dédié

### Structure uploads
```
uploads/
├── cv/
│   ├── cv_1_1710892800.pdf
│   └── cv_3_1710893000.pdf
└── rapports/     (futur Sprint 3)
```

### Compétences — tables de jointure
L'entité `Competence` et la table de jointure `etudiant_competence` doivent être créées. L'endpoint PUT /api/etudiants/me accepte une liste de competenceIds pour mettre à jour les compétences de l'étudiant.

### References
- [Source: architecture.md#API REST Étudiants] - POST /api/etudiants/me/cv, GET /api/etudiants/me
- [Source: architecture.md#Modèle de Données] - Etudiant, Competence, etudiant_competence
- [Source: architecture.md#Considérations d'Implémentation] - Upload PDF, stockage local
- [Source: prd.md#FR10] - Téléverser CV format PDF
- [Source: prd.md#FR9] - Profil structuré étudiant
- [Source: prd.md#NFR5] - Uploads limités au PDF, taille raisonnable

## Dev Agent Record

### Agent Model Used
Claude Opus 4.6

### Debug Log References
- Aucun problème rencontré

### Completion Notes List
- Task 1: FileStorageService créé — storeFile() génère un nom unique (prefix_timestamp.pdf), deleteFile() supprime silencieusement, loadFile() retourne un UrlResource. @PostConstruct crée uploads/cv/ au démarrage.
- Task 2: EtudiantService.uploadCv() — validation content-type PDF et taille <= 5 Mo, suppression ancien CV via FileStorageService.deleteFile(), stockage via storeFile(), mise à jour Etudiant.cvPath.
- Task 3: getMyProfile() et updateMyProfile() implémentés — mise à jour partielle (champs non-null seulement), support changement de filière par filiereId.
- Task 4: EtudiantResponse (id, nom, prénom, filière, filiereId, niveauAcademique, cvPath, bio, email) et UpdateEtudiantRequest (nom, prénom, filiereId, niveauAcademique, bio) créés.
- Task 5: EtudiantController avec 5 endpoints — POST /me/cv (ETUDIANT), GET /me (ETUDIANT), PUT /me (ETUDIANT), GET /{id}/cv (ADMIN/ENTREPRISE/ENCADRANT), GET /{id} (ADMIN/ENCADRANT). @AuthenticationPrincipal utilisé pour résoudre le userId.
- Task 6: application.properties configuré (multipart 5MB, app.upload.dir=uploads). Test properties utilise target/test-uploads.
- Task 7: dashboard-etudiant.html enrichi — formulaire profil (nom, prénom, filière dropdown, niveau, bio), zone upload CV avec drag & drop, indicateur CV existant avec bouton "Remplacer le CV".

### File List
- src/main/java/com/smartintern/service/FileStorageService.java (NEW)
- src/main/java/com/smartintern/service/EtudiantService.java (NEW)
- src/main/java/com/smartintern/controller/EtudiantController.java (NEW)
- src/main/java/com/smartintern/dto/etudiant/EtudiantResponse.java (NEW)
- src/main/java/com/smartintern/dto/etudiant/UpdateEtudiantRequest.java (NEW)
- src/main/resources/application.properties (MODIFIED)
- src/test/resources/application-test.properties (MODIFIED)
- src/main/resources/static/pages/dashboard-etudiant.html (MODIFIED)
- src/test/java/com/smartintern/service/EtudiantServiceTest.java (NEW)
- src/test/java/com/smartintern/service/FileStorageServiceTest.java (NEW)

## Change Log
- 2026-03-21: Implémentation complète de la story 1-7 Téléverser son CV — FileStorageService (store/delete/load), EtudiantService (uploadCv, getMyProfile, updateMyProfile), EtudiantController (5 endpoints avec @PreAuthorize par rôle), DTOs, config multipart 5MB, dashboard étudiant avec profil + upload CV drag&drop. 70 tests passent (0 régression).
