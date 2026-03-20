---
stepsCompleted: [1, 2, 3]
inputDocuments: []
session_topic: 'SmartIntern AI — Plateforme intelligente de gestion des stages universitaires (Iteam, Tunisie)'
session_goals: 'Prioriser fonctionnalités v1, cartographier flux utilisateurs par acteur, définir périmètre v1, préparer base PRD'
selected_approach: 'AI-Recommended Techniques'
techniques_used: ['Actor-by-Actor Gap Analysis', 'Decision Matrix', 'Edge Case Analysis', 'State Machine Design']
ideas_generated: []
context_file: ''
---

# Brainstorming Session Results

**Facilitateur:** Djazi
**Date:** 2026-03-20

## Session Overview

**Sujet:** SmartIntern AI — Plateforme multi-acteurs (5 rôles) couvrant le cycle complet de stage universitaire pour l'université Iteam (Tunisie)

**Objectifs:**
- Clarifier les fonctionnalités prioritaires pour une v1 universitaire
- Identifier les flux utilisateurs les plus importants par acteur
- Définir les limites du système (v1 scope)
- Préparer une base solide pour rédiger le PRD

### Contraintes et Contexte

**Stack technique imposée:** Java/Spring Boot, HTML/CSS/JS, MySQL, JWT, WebSocket, Agile Scrum
**Existant:** Product Backlog MoSCoW (138 pts), Sprint Planning (4 sprints), Cahier des Charges
**Nature du projet:** PFA universitaire — v1 fonctionnelle, bien structurée, démontrable

### Périmètre v1

**MUST HAVE:**
- Authentification & gestion des rôles (5 acteurs)
- Publication et gestion des offres de stage
- Candidatures + suivi de statut
- Matching intelligent basé sur compétences CV
- Suivi pédagogique : rapports hebdomadaires + rapport final
- Gestion documentaire : génération PDF (convention, demande, attestation, fiche évaluation, autorisation soutenance)
- Signature / tampon numérique sur documents
- Tableau de bord statistique admin
- Détection étudiants à risque (IA simple)
- Notifications / alertes

**SHOULD HAVE:**
- Messagerie temps réel (WebSocket)
- Score de matching affiché en %

**WON'T HAVE v1:**
- Visioconférence / soutenance virtuelle
- Anti-plagiat
- Application mobile
- Recommandations IA avancées

---

## Brief Final — SmartIntern AI v1

_Document prêt à être transmis à l'agent PM pour rédaction du PRD_

---

### 1. Vision Produit

**SmartIntern AI** est une plateforme web de gestion intelligente des stages universitaires pour l'université Iteam (Tunisie). Elle digitalise et centralise l'ensemble du cycle de stage — de la recherche d'offre jusqu'à l'autorisation de soutenance — en connectant 5 acteurs dans un écosystème unifié.

**Nature du projet :** PFA universitaire — v1 fonctionnelle, bien structurée, démontrable. Petite équipe. Pas d'exigence de scalabilité à grande échelle.

---

### 2. Acteurs et Modèle de Comptes

| Acteur | Mode d'inscription | Validation |
|---|---|---|
| **Étudiant** | Inscription libre | Approbation par l'admin |
| **Entreprise** | Inscription libre | Validation admin obligatoire avant toute publication |
| **Encadrant Académique** | Créé par l'admin | Affectation manuelle par l'admin |
| **Encadrant Entreprise** | Créé par l'entreprise | Lié à un stage spécifique en v1 |
| **Administrateur** | Pré-configuré | Accès système complet |

**Sécurité :** Authentification JWT, gestion des rôles (5 niveaux).

---

### 3. Flux Utilisateurs Détaillés par Acteur

#### 3.1 Étudiant

**Profil & Compétences :**
- Formulaire structuré : filière, niveau académique, compétences (taxonomie prédéfinie par l'admin)
- Upload PDF du CV en complément
- Les deux sources alimentent le matching

**Recherche & Candidature :**
- Recherche manuelle parmi les offres publiées
- Suggestions automatiques basées sur le matching compétences/offre
- Possibilité de postuler même avec un score de matching faible
- Candidatures illimitées en simultané
- Retrait de candidature possible tant que le statut est "en attente"
- Dès qu'un étudiant accepte une offre : statut passe à "placé", toutes ses autres candidatures "en attente" sont automatiquement annulées
- Un seul stage actif à la fois par étudiant

**Suivi de stage :**
- Rapports hebdomadaires via formulaire structuré : tâches réalisées, difficultés rencontrées, objectifs semaine suivante
- Validation principale par l'encadrant académique ; l'encadrant entreprise peut commenter
- Upload PDF du rapport final
- Rapport final refusé : l'étudiant peut resoumettre, maximum 3 tentatives. Au 3ème refus, l'admin est notifié
- Autorisation de soutenance débloquée automatiquement lorsque l'encadrant académique valide le rapport final

#### 3.2 Entreprise

**Gestion des offres :**
- Un seul compte par entreprise en v1
- Publication d'offres avec champs obligatoires : titre, domaine, compétences requises, durée, lieu, description, niveau académique requis
- Rémunération : champ optionnel
- Publication possible uniquement après validation admin du compte
- Retrait d'offre : si l'entreprise retire une offre avec des candidatures en attente, celles-ci sont automatiquement annulées avec notification aux étudiants concernés (pas d'intervention admin)

**Sélection des candidats :**
- Visualisation du score de matching par candidat
- Tri et filtrage par score de matching
- Acceptation/refus directement dans la plateforme
- Après acceptation : désignation d'un encadrant entreprise

#### 3.3 Encadrant Académique

**Affectation :** Manuelle par l'admin (pas d'auto-affectation, pas d'automatisme par département).

**Suivi pédagogique :**
- Dashboard de tous ses étudiants encadrés
- Validation des rapports hebdomadaires
- Commentaires et annotations sur les rapports
- Validation du rapport final (déclenche l'autorisation de soutenance)

**Évaluation :**
- Fiche d'évaluation structurée dans la plateforme
- Critères : assiduité, qualité des rapports, progression, respect des délais

#### 3.4 Encadrant Entreprise

**Rôle (distinct de l'encadrant académique) :**
- Valide les tâches effectuées par le stagiaire
- Commente les rapports hebdomadaires (sans pouvoir de validation officielle)
- Remplit la fiche d'évaluation finale du stagiaire
- Signe la convention de stage

**Compte :** Créé par l'entreprise, lié à un stage spécifique en v1.
**Remplacement :** Si l'encadrant entreprise quitte en cours de stage, l'entreprise peut en désigner un nouveau directement ; l'admin reçoit une notification.

#### 3.5 Administrateur

**Gouvernance :**
- Approuve les comptes étudiants et entreprises
- Affecte manuellement les encadrants académiques aux étudiants
- Définit et gère la taxonomie de compétences

**Tableau de bord statistique :**
- Nombre de stages en cours
- Nombre de candidatures en attente
- Taux de placement par filière
- Nombre d'entreprises partenaires actives
- Nombre d'étudiants sans stage
- Alertes étudiants à risque
- Top compétences demandées par les entreprises

**Détection étudiants à risque (IA simple) :**
- Critères : aucun rapport depuis +7 jours, rapport refusé 2 fois, aucune candidature après 3 semaines, évaluation encadrant faible
- Actions : alerte envoyée à l'étudiant et à l'encadrant académique, annotation manuelle dans le dossier

---

### 4. Gestion Documentaire

| Document | Déclenchement | Format |
|---|---|---|
| Convention de stage | Auto-généré à la confirmation du stage | PDF |
| Demande de stage | Déclenché manuellement (admin) | PDF |
| Attestation de stage | Auto-généré en fin de stage | PDF |
| Fiche d'évaluation | Remplie dans la plateforme, exportable PDF | PDF |
| Autorisation de soutenance | Auto-débloquée après validation rapport final | PDF |

**Signature numérique :** Tampon image en v1 (pas de certificat cryptographique).

---

### 5. Matching Intelligent

- Basé sur les compétences du profil structuré de l'étudiant vs compétences requises de l'offre
- Score calculé et affiché en % (SHOULD HAVE — si le temps le permet côté entreprise)
- L'étudiant reçoit des suggestions automatiques mais peut aussi chercher manuellement
- Aucune restriction de candidature basée sur le score

---

### 6. Stack Technique (Imposée)

| Couche | Technologie |
|---|---|
| Backend | Java + Spring Boot |
| Frontend | HTML / CSS / JavaScript |
| Base de données | MySQL |
| Sécurité | JWT |
| Temps réel | WebSocket (SHOULD HAVE : messagerie) |
| Méthodologie | Agile Scrum |

---

### 7. Périmètre v1 — Résumé Exécutif

**IN (MUST HAVE) :** Auth multi-rôles, offres, candidatures, matching, suivi pédagogique (rapports hebdo + final), gestion documentaire PDF (5 types), tampon image, dashboard admin, détection à risque, notifications.

**SHOULD HAVE :** Messagerie temps réel (WebSocket), score de matching affiché en %.

**OUT (v1) :** Visioconférence, anti-plagiat, app mobile, IA avancée, certificat cryptographique, multi-comptes par entreprise.

---

### 8. Cycle de Vie d'un Stage (Machine d'États)

```
Offre publiée
  → Candidature soumise (par étudiant)
  → Candidature acceptée (par entreprise) / Candidature refusée
  → Convention générée (auto)
  → Convention signée (par entreprise + encadrant entreprise)
  → Stage en cours (déclenché par admin)
  → Rapports hebdomadaires soumis/validés (boucle)
  → Rapport final soumis (par étudiant)
  → Rapport final validé (par encadrant académique) [max 3 tentatives si refusé]
  → Soutenance autorisée (auto après validation)
  → Fiche d'évaluation remplie (par encadrants)
  → Attestation générée (auto)
  → Stage clôturé
```

**Transitions automatiques :**
- Acceptation candidature → annulation auto des autres candidatures de l'étudiant
- Retrait d'offre → annulation auto des candidatures en attente + notification
- Validation rapport final → déblocage auto autorisation soutenance
- Clôture stage → génération auto attestation

---

### 9. Notifications

**Canal :** In-app uniquement en v1 (pas d'email).
**Préférences :** Non configurable en v1, tout est imposé.

**Événements déclencheurs :**

| Événement | Destinataire(s) |
|---|---|
| Nouvelle candidature reçue | Entreprise |
| Candidature acceptée | Étudiant |
| Candidature refusée | Étudiant |
| Candidatures annulées (offre retirée) | Étudiants concernés |
| Candidatures annulées (étudiant placé) | Entreprises concernées |
| Rapport hebdomadaire soumis | Encadrant académique, Encadrant entreprise |
| Rapport validé / refusé | Étudiant |
| 3ème refus rapport final | Admin |
| Convention prête à signer | Entreprise, Encadrant entreprise |
| Alerte étudiant à risque | Étudiant, Encadrant académique |
| Encadrant entreprise remplacé | Admin |
| Stage clôturé | Tous les acteurs du stage |

---

### 10. Sécurité et Cloisonnement des Données

| Acteur | Périmètre de visibilité |
|---|---|
| **Étudiant** | Son profil, ses candidatures, ses rapports, ses documents |
| **Entreprise** | Ses offres, les candidatures sur ses offres uniquement |
| **Encadrant Académique** | Uniquement ses étudiants assignés (rapports, évaluations, documents) |
| **Encadrant Entreprise** | Uniquement le(s) stagiaire(s) qui lui sont assignés |
| **Admin** | Accès lecture totale sur tout (rapports, évaluations, documents, dossiers) |

**Isolation stricte** : aucun acteur ne voit les données d'un autre acteur hors de son périmètre.

---

### 11. UX et Navigation

- **Dashboard personnalisé par rôle** après connexion — chaque acteur a son propre espace
- **Étudiant :** vue de ses candidatures en liste avec badge de statut coloré (en attente / accepté / refusé / en cours)
- **Encadrant académique :** dashboard de ses étudiants avec indicateurs de suivi
- **Entreprise :** vue de ses offres + candidatures reçues triables par score
- **Admin :** tableau de bord statistique complet + alertes

---

### 12. Données de Référence et Initialisation

**Pré-chargement par l'admin (avant ouverture de la plateforme) :**
- Liste des filières
- Niveaux académiques
- Taxonomie de compétences

**Jeu de données de démo pour la soutenance PFA :**
- 3 étudiants fictifs
- 2 entreprises
- 2 stages en cours
- Quelques candidatures et rapports fictifs

---

### 13. Décisions Clés Tranchées

1. Inscription libre + validation admin (pas de provisionnement batch)
2. Double source pour le profil étudiant (formulaire + CV PDF)
3. Taxonomie de compétences gérée par l'admin
4. Candidatures illimitées, retrait possible en statut "en attente"
5. Rapports hebdo = formulaire structuré, rapport final = upload PDF
6. Rapport final : max 3 tentatives de soumission, au 3ème refus l'admin est notifié
7. Autorisation de soutenance = déclenchement automatique après validation encadrant académique
8. Un seul compte entreprise en v1
9. Encadrant entreprise créé par l'entreprise (pas l'admin), remplaçable directement par l'entreprise
10. Fiche d'évaluation structurée (4 critères : assiduité, qualité rapports, progression, délais)
11. Documents : mix auto-générés et manuels
12. Signature = tampon image (pas crypto)
13. Détection à risque = 4 critères basés sur les données de la plateforme
14. Acceptation d'offre = annulation automatique des autres candidatures, un seul stage actif
15. Retrait d'offre = annulation automatique des candidatures avec notification
16. Notifications in-app uniquement, non configurables en v1
17. Isolation stricte des données par acteur, admin en lecture totale
18. Dashboard personnalisé par rôle
19. Admin pré-charge filières, niveaux et compétences avant ouverture
20. Jeu de données de démo prévu pour la soutenance

---

### 14. Risques Identifiés pour le PRD

| Risque | Impact | Mitigation suggérée |
|---|---|---|
| Matching "intelligent" trop ambitieux pour un PFA | Scope creep | Algorithme simple de correspondance par mots-clés/compétences, pas de ML |
| 5 types de documents PDF à générer | Charge de développement | Utiliser une bibliothèque PDF Java (iText/OpenPDF) avec templates réutilisables |
| 5 rôles = 5 interfaces distinctes | Complexité UI | Privilégier un layout commun avec vues conditionnelles par rôle |
| WebSocket messagerie en SHOULD HAVE | Risque de non-livraison | Isoler dans un sprint dédié, prévoir un fallback notification simple |
| Tampon image comme "signature" | Crédibilité limitée | Documenter clairement que c'est un MVP universitaire, pas un système légal |
| Machine d'états complexe (12 transitions) | Bugs de logique métier | Bien documenter le diagramme d'états, tester chaque transition unitairement |
| Notifications in-app seulement | Étudiants pourraient rater des alertes | Mettre les alertes critiques en évidence sur le dashboard + badge non lu |

---

_Fin du brief — Prêt pour transmission à l'agent PM (bmad-create-prd)_
