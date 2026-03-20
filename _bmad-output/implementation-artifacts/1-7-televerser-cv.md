# Story 1.7: Téléverser son CV (ET-01)

Status: ready-for-dev

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

- [ ] Task 1 : Implémenter FileStorageService (AC: 4, 6)
  - [ ] `service/FileStorageService.java`
  - [ ] Méthode `storeFile(MultipartFile file, String subDir, String prefix)` : sauvegarde le fichier avec nom unique, retourne le chemin relatif
  - [ ] Méthode `deleteFile(String filePath)` : supprime un fichier existant
  - [ ] Méthode `loadFile(String filePath)` : charge un fichier comme Resource pour le download
  - [ ] Créer le dossier `uploads/cv/` au démarrage si inexistant
  - [ ] Configurer le chemin uploads dans application.properties : `app.upload.dir=uploads`
- [ ] Task 2 : Implémenter upload CV dans UserService ou EtudiantService (AC: 1, 2, 3, 5, 6)
  - [ ] Créer `EtudiantService.java` (ou ajouter dans UserService)
  - [ ] Méthode `uploadCv(Long userId, MultipartFile file)`
  - [ ] Valider content-type = application/pdf
  - [ ] Valider taille <= 5 Mo
  - [ ] Supprimer l'ancien CV si existant
  - [ ] Sauvegarder via FileStorageService
  - [ ] Mettre à jour Etudiant.cvPath
- [ ] Task 3 : Implémenter profil étudiant (AC: 8)
  - [ ] `EtudiantService.getMyProfile(Long userId)` → EtudiantResponse
  - [ ] `EtudiantService.updateMyProfile(Long userId, UpdateEtudiantRequest)` → EtudiantResponse
- [ ] Task 4 : Créer les DTOs (AC: 7, 8)
  - [ ] `dto/etudiant/EtudiantResponse.java` : id, nom, prenom, filiere, niveauAcademique, cvPath, bio, competences
  - [ ] `dto/etudiant/UpdateEtudiantRequest.java` : nom, prenom, filiereId, niveauAcademique, bio, competenceIds
- [ ] Task 5 : Créer EtudiantController (AC: 1, 7, 8)
  - [ ] POST `/api/etudiants/me/cv` → uploadCv() [@PreAuthorize("hasRole('ETUDIANT')")]
  - [ ] GET `/api/etudiants/me` → getMyProfile() [@PreAuthorize("hasRole('ETUDIANT')")]
  - [ ] PUT `/api/etudiants/me` → updateMyProfile() [@PreAuthorize("hasRole('ETUDIANT')")]
  - [ ] GET `/api/etudiants/{id}/cv` → downloadCv() [@PreAuthorize admin/entreprise/encadrant]
  - [ ] GET `/api/etudiants/{id}` → getProfile() [@PreAuthorize admin/encadrant]
- [ ] Task 6 : Configurer upload dans Spring Boot (AC: 2, 3)
  - [ ] `application.properties` : `spring.servlet.multipart.max-file-size=5MB`, `spring.servlet.multipart.max-request-size=5MB`
  - [ ] Servir les fichiers statiques depuis uploads/ (ResourceHandler dans AppConfig)
- [ ] Task 7 : Interface étudiant — profil et CV (AC: 1, 8)
  - [ ] Section "Mon profil" dans dashboard-etudiant.html
  - [ ] Formulaire de modification du profil (nom, prénom, filière dropdown, bio)
  - [ ] Zone d'upload CV avec drag & drop ou bouton fichier
  - [ ] Indicateur visuel si CV déjà uploadé (nom du fichier, date)
  - [ ] Bouton "Remplacer le CV"

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
### Debug Log References
### Completion Notes List
### File List
