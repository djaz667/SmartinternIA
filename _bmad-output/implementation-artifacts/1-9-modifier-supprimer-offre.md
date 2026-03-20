# Story 1.9: Modifier ou supprimer une offre (EN-02)

Status: ready-for-dev

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

- [ ] Task 1 : Implémenter OffreService.updateOffre() (AC: 1, 3, 4, 6)
  - [ ] Charger l'offre par id (sinon 404)
  - [ ] Vérifier que l'offre appartient à l'entreprise connectée (sinon 403)
  - [ ] Vérifier qu'aucun stage actif n'est lié (sinon 400) — requête sur StageRepository si existant, sinon skip
  - [ ] Mettre à jour les champs modifiables
  - [ ] Mettre à jour les compétences (clear + re-add via competenceIds)
  - [ ] Sauvegarder et retourner OffreResponse
- [ ] Task 2 : Implémenter OffreService.deleteOffre() (AC: 2, 3, 5)
  - [ ] Charger l'offre par id (sinon 404)
  - [ ] Vérifier ownership (sinon 403)
  - [ ] Marquer `active = false` (soft delete, pas de suppression physique)
  - [ ] TODO Sprint 2 : appeler CandidatureService.annulerCandidaturesEnAttente(offreId) + NotificationService
  - [ ] Sauvegarder
- [ ] Task 3 : Ajouter les endpoints dans OffreController (AC: 1, 2)
  - [ ] PUT `/api/offres/{id}` → updateOffre() [@PreAuthorize("hasRole('ENTREPRISE')")]
  - [ ] DELETE `/api/offres/{id}` → deleteOffre() [@PreAuthorize("hasRole('ENTREPRISE')")]
- [ ] Task 4 : Interface entreprise — modifier/retirer (AC: 7)
  - [ ] Dans "Mes offres" : bouton "Modifier" ouvre un formulaire pré-rempli (réutiliser le formulaire de création)
  - [ ] Bouton "Retirer" avec confirmation ("Êtes-vous sûr ?") avant appel DELETE
  - [ ] Offres retirées (active=false) affichées en grisé ou dans une section séparée
  - [ ] Feedback visuel après modification/retrait

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

## Dev Agent Record

### Agent Model Used
### Debug Log References
### Completion Notes List
### File List
