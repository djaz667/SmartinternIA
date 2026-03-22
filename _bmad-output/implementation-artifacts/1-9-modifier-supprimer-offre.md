# Story 1.9: Modifier ou supprimer une offre (EN-02)

Status: review

## Story

As a entreprise,
I want modifier ou retirer une offre de stage publiée,
so that les informations restent à jour et que je puisse retirer une offre pourvue.

## Acceptance Criteria

1. **Endpoint PUT /api/offres/{id}** : Modifie une offre existante. Accessible uniquement par l'ENTREPRISE propriétaire de l'offre. Retourne `OffreResponse` mis à jour.
2. **Endpoint DELETE /api/offres/{id}** : Retire une offre (la marque comme `active = false`). Accessible uniquement par l'ENTREPRISE propriétaire.
3. **Vérification ownership** : L'entreprise ne peut modifier/supprimer que ses propres offres. Sinon 403.
4. **Modification** : Tous les champs de l'offre sont modifiables (titre, domaine, description, durée, lieu, niveauRequis, compétences, rémunération).
5. **Retrait et annulations** : Le retrait d'une offre déclenche l'annulation automatique de toutes les candidatures `EN_ATTENTE` associées. Chaque étudiant concerné reçoit une notification. (Note : les candidatures et notifications seront créées au Sprint 2 — pour l'instant, implémenter la logique de désactivation de l'offre uniquement, et préparer l'appel au service de candidatures quand il existera.)
6. **Offre non modifiable si stage en cours** : Si un stage actif est lié à cette offre, la modification est bloquée (400 "Offre liée à un stage en cours").
7. **Interface** : Dans la section "Mes offres" du dashboard-entreprise.html, boutons "Modifier" et "Retirer" sur chaque offre.

## Tasks / Subtasks

- [x] Task 1 : Implémenter OffreService.updateOffre() (AC: 1, 3, 4, 6)
  - [x] Charger l'offre par id (sinon 404)
  - [x] Vérifier que l'offre appartient à l'entreprise connectée (sinon 403)
  - [x] Vérifier qu'aucun stage actif n'est lié (sinon 400) — requête sur StageRepository si existant, sinon skip
  - [x] Mettre à jour les champs modifiables
  - [x] Mettre à jour les compétences (clear + re-add via competenceIds)
  - [x] Sauvegarder et retourner OffreResponse
- [x] Task 2 : Implémenter OffreService.deleteOffre() (AC: 2, 3, 5)
  - [x] Charger l'offre par id (sinon 404)
  - [x] Vérifier ownership (sinon 403)
  - [x] Marquer `active = false` (soft delete, pas de suppression physique)
  - [x] TODO Sprint 2 : appeler CandidatureService.annulerCandidaturesEnAttente(offreId) + NotificationService
  - [x] Sauvegarder
- [x] Task 3 : Ajouter les endpoints dans OffreController (AC: 1, 2)
  - [x] PUT `/api/offres/{id}` → updateOffre() [@PreAuthorize("hasRole('ENTREPRISE')")]
  - [x] DELETE `/api/offres/{id}` → deleteOffre() [@PreAuthorize("hasRole('ENTREPRISE')")]
- [x] Task 4 : Interface entreprise — modifier/retirer (AC: 7)
  - [x] Dans "Mes offres" : bouton "Modifier" ouvre un formulaire pré-rempli (réutiliser le formulaire de création)
  - [x] Bouton "Retirer" avec confirmation ("Êtes-vous sûr ?") avant appel DELETE
  - [x] Offres retirées (active=false) affichées en grisé ou dans une section séparée
  - [x] Feedback visuel après modification/retrait

## Dev Notes

### Dépendance
Dépend de 1-8 (Offre entity, OffreService, OffreController, OffreRepository, dashboard-entreprise.html).

### Soft Delete
On ne supprime JAMAIS physiquement une offre. On passe `active = false`. Cela permet de garder l'historique et les références des candidatures/stages futurs.

### Ownership check pattern
```java
private void checkOwnership(Offre offre, Long userId) {
    Entreprise entreprise = entrepriseRepository.findByUserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException("Entreprise non trouvée"));
    if (!offre.getEntreprise().getId().equals(entreprise.getId())) {
        throw new UnauthorizedException("Vous ne pouvez modifier que vos propres offres");
    }
}
```

### Préparation Sprint 2
Le service de suppression doit être prêt à appeler l'annulation des candidatures (FR15). Pour l'instant, ajouter un commentaire TODO :
```java
// TODO Sprint 2: candidatureService.annulerParOffre(offre.getId());
// TODO Sprint 2: notificationService.notifierAnnulation(candidatures);
```

### Architecture
- Réutiliser OffreRequest pour la modification (mêmes champs)
- Le pattern ownership check sera réutilisé dans beaucoup de services — on peut créer un helper method dans le service

### References
- [Source: architecture.md#API REST Offres] - PUT /api/offres/{id}, DELETE /api/offres/{id}
- [Source: prd.md#FR14] - Modifier ou retirer une offre
- [Source: prd.md#FR15] - Retrait annule candidatures + notification
- [Source: prd.md#NFR4] - Isolation des données (ownership)

## Change Log

- 2026-03-22: Implemented all 4 tasks — updateOffre, deleteOffre (soft delete), controller endpoints, dashboard UI with edit modal and retire confirmation. 7 new unit tests added. Total: 91 tests passing, 0 failures.

## Dev Agent Record

### Agent Model Used
Claude Opus 4.6

### Debug Log References
N/A — no debug issues encountered

### Completion Notes List
- All 7 acceptance criteria met
- `updateOffre()`: loads offre, checks ownership via `checkOwnership()` helper, validates competences, updates all fields including competences (clear + re-add), saves and returns OffreResponse
- `deleteOffre()`: soft delete — sets `active = false`, ownership check, TODO comments for Sprint 2 candidature annulation + notifications
- AC6 (stage actif check): Stage model doesn't exist yet — TODO comment added for future implementation
- Controller: PUT `/api/offres/{id}` returns 200 with updated OffreResponse, DELETE `/api/offres/{id}` returns 204 No Content
- Dashboard UI: "Modifier" button opens modal with pre-filled form + competence chips, "Retirer" button with confirm() dialog, inactive offres shown greyed out (`.tr-inactive`), success/error feedback messages
- 7 new tests in OffreServiceTest: updateOffre (valid, 404, 403 ownership, invalid competences) + deleteOffre (valid sets active=false, 404, 403 ownership)

### File List
- `src/main/java/com/smartintern/service/OffreService.java` (MODIFIED — added updateOffre, deleteOffre, checkOwnership)
- `src/main/java/com/smartintern/controller/OffreController.java` (MODIFIED — added PUT and DELETE endpoints)
- `src/main/resources/static/pages/dashboard-entreprise.html` (MODIFIED — edit modal, action buttons, retirer with confirm)
- `src/test/java/com/smartintern/service/OffreServiceTest.java` (MODIFIED — added 7 tests for update/delete)
