---
stepsCompleted: ['step-01-init', 'step-02-discovery', 'step-02b-vision', 'step-02c-executive-summary', 'step-03-success', 'step-04-journeys', 'step-05-domain-skipped', 'step-06-innovation-skipped', 'step-07-project-type', 'step-08-scoping', 'step-09-functional', 'step-10-nonfunctional', 'step-11-polish']
classification:
  projectType: web_app
  domain: edtech
  complexity: medium
  projectContext: greenfield
inputDocuments:
  - '_bmad-output/brainstorming/brainstorming-session-2026-03-20-1200.md'
  - 'docs/cahier_des_charges_1.md'
  - 'docs/product_backlog_1.md'
  - 'docs/sprint_planning.md'
documentCounts:
  briefs: 0
  research: 0
  brainstorming: 1
  projectDocs: 3
workflowType: 'prd'
---

# Product Requirements Document - SmartIntern AI

**Author:** Djazi
**Date:** 2026-03-20
**Projet:** PFA -- Iteam University (Tunisie)

## Executive Summary

**SmartIntern AI** est une plateforme web de gestion des stages universitaires développée dans le cadre d'un PFA à l'université Iteam (Tunisie). Elle remplace le circuit actuel -- papier, emails, Excel, WhatsApp -- par une application web centralisant les 5 acteurs (étudiant, entreprise, encadrant académique, encadrant entreprise, administrateur) autour du cycle complet de stage.

Le problème : à Iteam, les stages ne sont pas gérés de manière structurée. L'admin ne connaît pas l'état réel du placement, les encadrants suivent leurs étudiants par canaux informels, les étudiants ignorent l'avancement de leur dossier, et les documents officiels sont produits manuellement un par un.

SmartIntern AI apporte structure, visibilité et automatisation à ce processus.

**Nature du projet :** PFA universitaire -- l'objectif est une v1 fonctionnelle, bien structurée et démontrable lors de la soutenance. Stack imposée : Java/Spring Boot, HTML/CSS/JS, MySQL, JWT. Méthodologie Agile Scrum, 4 sprints, 138 points.

### Ce qui rend SmartIntern AI spécial

- **Matching par compétences** -- Correspondance automatique entre profil étudiant et offres de stage, évitant la recherche à l'aveugle
- **Détection des étudiants à risque** -- Alertes automatiques basées sur 4 critères simples (inactivité, retards, évaluations faibles, absence de candidature)
- **Génération documentaire** -- 5 types de documents PDF (convention, demande, attestation, fiche d'évaluation, autorisation de soutenance) générés avec tampon image
- **Espaces personnalisés par rôle** -- Chaque acteur a son dashboard avec les informations pertinentes à son périmètre

## Classification Projet

| Dimension | Valeur |
|---|---|
| **Type** | Application web (client-serveur, API REST) |
| **Domaine** | EdTech (gestion des stages universitaires) |
| **Complexité** | Medium (5 rôles, machine d'états, génération PDF) |
| **Contexte** | Greenfield, projet universitaire PFA |

## Critères de Succès

### Succès Utilisateur

- Un étudiant peut s'inscrire, compléter son profil, postuler à une offre et recevoir une notification de résultat sans intervention manuelle de l'admin
- Une entreprise peut publier une offre et voir les candidats classés par pertinence
- Un encadrant académique peut suivre ses étudiants assignés, valider leurs rapports et déclencher l'autorisation de soutenance depuis son dashboard
- L'admin peut visualiser l'état global des stages et générer n'importe quel document officiel en quelques clics

### Succès Technique

- Les 5 rôles fonctionnent avec isolation correcte des données (aucun acteur ne voit ce qui ne le concerne pas)
- La machine d'états du stage (12 transitions) fonctionne sans blocage ni incohérence
- Les 5 types de documents PDF se génèrent correctement avec tampon image
- L'authentification JWT sécurise l'accès et différencie les permissions par rôle
- L'application tourne de manière stable avec le jeu de données de démo

### Succès Académique (PFA)

- Le système est démontrable lors de la soutenance avec un scénario complet : offre publiée -> candidature -> matching -> convention -> suivi hebdo -> rapport final -> soutenance autorisée -> attestation
- Le code est structuré, documenté et suit les bonnes pratiques Spring Boot
- La documentation projet est complète (cahier des charges, architecture, plan de tests)

### Résultats Mesurables

- 100% des user stories MUST HAVE du backlog sont implémentées et fonctionnelles
- Les 5 types de documents PDF se génèrent sans erreur
- Le matching retourne des résultats pertinents sur le jeu de données de démo
- La détection à risque alerte correctement sur les 4 critères définis
- Le workflow complet de stage peut être déroulé de bout en bout sans intervention manuelle hors périmètre

## Périmètre & Planning

### MVP (v1 -- MUST HAVE)

- Authentification multi-rôles (5 acteurs) avec JWT
- Publication et gestion des offres de stage
- Candidatures avec suivi de statut et transitions automatiques
- Matching par compétences (algorithme simple, correspondance mots-clés)
- Suivi pédagogique : rapports hebdomadaires (formulaire) + rapport final (upload PDF)
- Gestion documentaire : 5 types de PDF avec tampon image
- Dashboard admin avec statistiques et alertes étudiants à risque
- Notifications in-app

### Fonctionnalités Secondaires (SHOULD HAVE)

- Messagerie temps réel via WebSocket
- Score de matching affiché en pourcentage côté entreprise

### Hors Périmètre v1 (WON'T HAVE)

- Visioconférence / soutenance virtuelle
- Anti-plagiat
- Application mobile
- Recommandations IA avancées
- Certificat cryptographique
- Multi-comptes par entreprise

### Découpage en Sprints

**Approche :** MVP fonctionnel couvrant le cycle complet de stage de bout en bout, démontrable en soutenance. Petite équipe étudiante, 138 points de complexité.

| Sprint | Objectif | Points |
|---|---|---|
| **Sprint 1** | Authentification & Profils de base | 20 pts |
| **Sprint 2** | Recherche de stage & Candidatures | 26 pts |
| **Sprint 3** | Suivi de stage & Documents officiels | 42 pts |
| **Sprint 4** | Évaluation, IA & Messagerie | 51 pts |

**Attention Sprint 3 (42 pts) :** Si la vélocité est limitée, les stories AD-08/AD-09/AD-10 peuvent glisser au Sprint 4.

**Attention Sprint 4 (51 pts) :** EN-04 (matching IA, 8 pts) et ET-06 (recommandations IA, 8 pts) sont facultatifs -- à prioriser selon le temps restant.

### Risques et Mitigations

| Risque | Impact | Mitigation |
|---|---|---|
| **Matching trop ambitieux** | Scope creep | Algorithme simple par correspondance mots-clés, pas de ML |
| **5 types de PDF** | Charge de dev | Bibliothèque PDF Java (iText/OpenPDF) avec templates réutilisables |
| **5 rôles = 5 interfaces** | Complexité UI | Layout commun avec vues conditionnelles par rôle |
| **WebSocket messagerie** | Non-livraison | Isolé en Sprint 4, fallback notification simple |
| **Machine d'états (12 transitions)** | Bugs logique métier | Documenter le diagramme, tester chaque transition |
| **Sprint 3 surchargé** | Retard | Stories déplaçables au Sprint 4 identifiées |

## Parcours Utilisateurs

### Parcours 1 : Amira, étudiante en 3ème année Informatique

**Situation :** Amira est en dernière année à Iteam. Elle doit trouver un stage de fin d'études mais ne sait pas par où commencer. Elle a envoyé des emails à quelques entreprises sans réponse, et ses camarades lui disent que "c'est chacun pour soi".

**Découverte :** Amira crée son compte sur SmartIntern AI, remplit son profil structuré (filière, compétences en Java, Spring Boot, SQL) et uploade son CV. La plateforme lui suggère immédiatement 3 offres qui correspondent à son profil.

**Action :** Elle consulte les détails des offres, postule aux deux qui l'intéressent le plus. Elle suit le statut de ses candidatures depuis son dashboard -- plus besoin de relancer par email.

**Moment clé :** Amira reçoit une notification : son stage est accepté. Elle ouvre la plateforme et voit sa convention de stage déjà générée. Pas de déplacement au bureau de scolarité, pas de formulaire papier.

**Suivi :** Chaque semaine, Amira remplit son rapport hebdomadaire via le formulaire structuré. Son encadrant académique commente et valide. Quand elle dépose son rapport final et qu'il est validé, l'autorisation de soutenance se débloque automatiquement.

**Résultat :** Amira a géré tout son stage -- de la candidature à la soutenance -- depuis une seule plateforme, sans courir après les signatures ni les documents.

### Parcours 2 : TechnoSoft, entreprise partenaire d'Iteam

**Situation :** TechnoSoft cherche 2 stagiaires en développement web pour un projet interne. Habituellement, le responsable RH publie l'offre par email à l'admin d'Iteam, reçoit des CV par lots, et passe des heures à les trier manuellement.

**Découverte :** TechnoSoft crée son compte entreprise sur SmartIntern AI. Après validation par l'admin, elle publie son offre avec les compétences requises (HTML, CSS, JavaScript, Spring Boot).

**Action :** Les candidatures arrivent directement dans la plateforme, classées par score de correspondance. Le responsable RH voit d'un coup d'oeil quels étudiants ont le profil le plus adapté, consulte leurs CV et profils détaillés.

**Moment clé :** TechnoSoft accepte 2 candidats en 2 clics. Les candidatures des étudiants non retenus passent automatiquement en "refusé" avec notification. TechnoSoft désigne un encadrant entreprise pour chaque stagiaire.

**Suivi :** L'encadrant entreprise valide les tâches du stagiaire, commente les rapports hebdomadaires, et remplit la fiche d'évaluation en fin de stage. La convention est signée numériquement via tampon image.

**Résultat :** TechnoSoft a trouvé des stagiaires pertinents en une fraction du temps habituel, avec un suivi structuré sans échanges d'emails interminables.

### Parcours 3 : Dr. Ben Ali, encadrant académique

**Situation :** Dr. Ben Ali encadre 8 étudiants en stage cette année. Jusqu'ici, il suivait leur progression par WhatsApp et emails dispersés. Il perdait le fil et ne savait pas toujours qui avait rendu quoi.

**Découverte :** L'admin lui assigne ses 8 étudiants dans SmartIntern AI. Dr. Ben Ali ouvre son dashboard et voit immédiatement l'état de chaque étudiant : qui a rendu son rapport hebdomadaire, qui est en retard, qui a été évalué positivement par l'entreprise.

**Action :** Il lit les rapports hebdomadaires directement dans la plateforme, ajoute ses commentaires et annotations, valide ou demande des corrections. Plus besoin de chercher dans ses emails.

**Moment clé :** Un de ses étudiants n'a pas soumis de rapport depuis 10 jours. La plateforme l'a signalé en "étudiant à risque". Dr. Ben Ali intervient avant que la situation ne dégénère.

**Suivi :** En fin de stage, il valide le rapport final d'Amira. L'autorisation de soutenance se génère automatiquement. Il remplit la fiche d'évaluation structurée (assiduité, qualité des rapports, progression, respect des délais).

**Résultat :** Dr. Ben Ali gère ses 8 étudiants depuis un seul endroit, avec des alertes proactives et une traçabilité complète.

### Parcours 4 : Mme Trabelsi, administratrice Iteam

**Situation :** Mme Trabelsi gère la coordination des stages pour toute la promotion. Elle jongle entre Excel, emails, signatures papier et relances téléphoniques. Elle ne sait jamais exactement combien d'étudiants ont un stage validé.

**Découverte :** Elle ouvre SmartIntern AI et accède à son dashboard admin. D'un coup d'oeil : 45 étudiants en stage, 12 en recherche, 3 signalés à risque, 8 entreprises partenaires actives.

**Action :** Elle approuve les nouveaux comptes étudiants et entreprises, affecte les encadrants académiques aux étudiants, et gère la taxonomie de compétences. Quand un stage est confirmé, elle déclenche sa mise en route.

**Moment clé :** Un étudiant a son rapport final refusé pour la 3ème fois -- l'admin reçoit une alerte automatique et peut intervenir. Plus tard, elle génère les 5 attestations de fin de stage en quelques clics -- plus de ressaisie manuelle.

**Scénario d'erreur :** Une entreprise retire une offre avec 3 candidatures en attente. La plateforme annule automatiquement les candidatures et notifie les étudiants -- pas d'intervention admin nécessaire.

**Résultat :** Mme Trabelsi a une visibilité totale et en temps réel sur la promotion, avec des documents générés automatiquement et des alertes proactives.

### Parcours 5 : M. Jaziri, encadrant entreprise chez TechnoSoft

**Situation :** M. Jaziri est développeur senior chez TechnoSoft. On lui assigne un stagiaire mais il n'a aucun outil pour suivre son travail formellement -- tout passe par des conversations informelles.

**Découverte :** TechnoSoft crée son compte encadrant entreprise dans SmartIntern AI. M. Jaziri voit le profil de son stagiaire et peut définir la mission (objectifs et livrables attendus).

**Action :** Chaque semaine, il valide les tâches effectuées par le stagiaire et commente les rapports hebdomadaires. Il signe la convention de stage via tampon numérique.

**Moment clé :** En fin de stage, il remplit la fiche d'évaluation structurée du stagiaire -- une première pour lui, car habituellement l'évaluation était un email informel.

**Scénario d'erreur :** M. Jaziri quitte l'entreprise en cours de stage. TechnoSoft désigne directement un remplaçant, et l'admin reçoit une notification du changement.

**Résultat :** M. Jaziri a un cadre clair pour encadrer son stagiaire, avec une trace formelle de son évaluation.

### Synthèse des Capacités par Parcours

| Parcours | Capacités clés requises |
|---|---|
| **Amira (Étudiante)** | Inscription, profil structuré, upload CV, recherche d'offres, candidature, suivi de statut, rapports hebdo, dépôt rapport final, notifications |
| **TechnoSoft (Entreprise)** | Publication d'offres, visualisation matching, gestion candidatures, acceptation/refus, désignation encadrant, signature convention |
| **Dr. Ben Ali (Enc. Académique)** | Dashboard étudiants, validation rapports, commentaires, évaluation structurée, validation rapport final, déclenchement soutenance |
| **Mme Trabelsi (Admin)** | Dashboard statistiques, approbation comptes, affectation encadrants, gestion taxonomie, génération documents PDF, alertes à risque |
| **M. Jaziri (Enc. Entreprise)** | Définition mission, validation tâches, commentaires rapports, signature convention, fiche évaluation, remplacement encadrant |

## Architecture Technique

| Couche | Choix | Justification |
|---|---|---|
| **Backend** | Java + Spring Boot (API REST) | Stack imposée, architecture MVC/REST classique |
| **Frontend** | HTML / CSS / JavaScript | Stack imposée, pages servies par le backend ou appels API |
| **Base de données** | MySQL | Stack imposée, modèle relationnel adapté aux entités métier |
| **Authentification** | JWT | Tokens stateless, gestion des 5 rôles via claims |
| **Temps réel** | WebSocket (SHOULD HAVE) | Messagerie uniquement, notifications par polling sinon |
| **Documents** | Bibliothèque PDF Java (iText/OpenPDF) | Génération des 5 types de documents avec templates |

### Design et Navigation

- **Desktop-first** -- la démo PFA se fait sur desktop, pas d'exigence responsive stricte
- **Dashboard personnalisé par rôle** -- chaque acteur accède à son espace après connexion
- **Navigation simple** -- menu latéral ou barre de navigation selon le rôle connecté
- **Pas de SEO** -- plateforme interne universitaire, pas indexée par les moteurs de recherche

### Considérations d'Implémentation

- **API REST** -- endpoints organisés par domaine métier (auth, offres, candidatures, rapports, documents, admin)
- **Sécurité des endpoints** -- filtres Spring Security par rôle sur chaque endpoint
- **Upload de fichiers** -- CV (PDF) et rapport final (PDF), stockage local sur le serveur
- **Génération PDF** -- templates prédéfinis avec injection des données dynamiques + tampon image
- **Pas d'exigence de charge** -- jeu de données de démo, requêtes MySQL directes suffisantes

## Exigences Fonctionnelles

### Gestion des Utilisateurs et Authentification

- **FR1 :** Tout utilisateur peut créer un compte avec email et mot de passe
- **FR2 :** Tout utilisateur peut se connecter à la plateforme via ses identifiants
- **FR3 :** Tout utilisateur peut réinitialiser son mot de passe
- **FR4 :** Tout utilisateur peut se déconnecter de manière sécurisée
- **FR5 :** L'administrateur peut approuver ou refuser les comptes étudiants et entreprises
- **FR6 :** L'administrateur peut attribuer des rôles aux utilisateurs (5 rôles : étudiant, entreprise, encadrant académique, encadrant entreprise, administrateur)
- **FR7 :** L'administrateur peut créer des comptes encadrants académiques
- **FR8 :** L'entreprise peut créer des comptes encadrants entreprise liés à un stage

### Profil et Compétences

- **FR9 :** L'étudiant peut remplir un profil structuré (filière, niveau académique, compétences depuis une taxonomie prédéfinie)
- **FR10 :** L'étudiant peut téléverser son CV au format PDF
- **FR11 :** L'administrateur peut définir et gérer la taxonomie de compétences
- **FR12 :** L'administrateur peut gérer la liste des filières et niveaux académiques

### Gestion des Offres de Stage

- **FR13 :** L'entreprise peut publier une offre de stage avec champs obligatoires (titre, domaine, compétences requises, durée, lieu, description, niveau académique requis) et champ optionnel (rémunération)
- **FR14 :** L'entreprise peut modifier ou retirer une offre publiée
- **FR15 :** Le retrait d'une offre annule automatiquement les candidatures en attente avec notification aux étudiants concernés
- **FR16 :** L'entreprise ne peut publier qu'après validation de son compte par l'admin

### Recherche et Candidature

- **FR17 :** L'étudiant peut rechercher et filtrer les offres de stage (domaine, compétences, durée, lieu, niveau)
- **FR18 :** L'étudiant peut consulter le détail d'une offre
- **FR19 :** L'étudiant peut postuler à une ou plusieurs offres simultanément
- **FR20 :** L'étudiant peut retirer une candidature tant que son statut est "en attente"
- **FR21 :** L'étudiant peut suivre le statut de ses candidatures (en attente, acceptée, refusée)
- **FR22 :** L'acceptation d'une offre passe l'étudiant en statut "placé" et annule automatiquement ses autres candidatures en attente
- **FR23 :** Un seul stage actif à la fois par étudiant

### Matching Intelligent

- **FR24 :** Le système peut calculer un score de correspondance entre le profil étudiant et les compétences requises d'une offre
- **FR25 :** L'étudiant peut recevoir des suggestions automatiques d'offres basées sur son profil
- **FR26 :** L'entreprise peut visualiser les candidatures triées par score de correspondance

### Suivi Pédagogique

- **FR27 :** L'étudiant peut rédiger et soumettre un rapport hebdomadaire via formulaire structuré (tâches réalisées, difficultés, objectifs semaine suivante)
- **FR28 :** L'encadrant académique peut valider ou refuser les rapports hebdomadaires
- **FR29 :** L'encadrant académique peut commenter et annoter les rapports
- **FR30 :** L'encadrant entreprise peut commenter les rapports hebdomadaires (sans pouvoir de validation)
- **FR31 :** L'encadrant entreprise peut valider les tâches effectuées par le stagiaire
- **FR32 :** L'étudiant peut déposer son rapport final au format PDF
- **FR33 :** L'encadrant académique peut valider ou refuser le rapport final (maximum 3 tentatives de soumission)
- **FR34 :** Au 3ème refus du rapport final, l'administrateur est notifié automatiquement
- **FR35 :** La validation du rapport final déclenche automatiquement l'autorisation de soutenance

### Évaluation

- **FR36 :** L'encadrant académique peut remplir une fiche d'évaluation structurée (assiduité, qualité des rapports, progression, respect des délais)
- **FR37 :** L'encadrant entreprise peut remplir la fiche d'évaluation du stagiaire
- **FR38 :** L'encadrant entreprise peut définir la mission du stagiaire (objectifs et livrables)

### Gestion Documentaire

- **FR39 :** Le système peut générer automatiquement une convention de stage (PDF) à la confirmation du stage
- **FR40 :** L'administrateur peut générer une demande de stage (PDF)
- **FR41 :** Le système peut générer automatiquement une attestation de stage (PDF) en fin de stage
- **FR42 :** Le système peut exporter la fiche d'évaluation en PDF
- **FR43 :** Le système peut générer l'autorisation de soutenance (PDF) après validation du rapport final
- **FR44 :** L'administrateur peut apposer un tampon image (signature numérique) sur les documents générés
- **FR45 :** L'entreprise et l'encadrant entreprise peuvent signer la convention de stage

### Dashboard et Statistiques

- **FR46 :** L'administrateur peut visualiser un tableau de bord avec indicateurs clés (stages en cours, candidatures en attente, taux de placement par filière, entreprises partenaires actives, étudiants sans stage, top compétences demandées)
- **FR47 :** L'encadrant académique peut visualiser un dashboard de ses étudiants assignés avec indicateurs de suivi
- **FR48 :** Chaque acteur accède à un espace personnalisé après connexion

### Détection des Étudiants à Risque

- **FR49 :** Le système peut détecter automatiquement les étudiants à risque selon 4 critères (aucun rapport depuis +7 jours, rapport refusé 2 fois, aucune candidature après 3 semaines, évaluation encadrant faible)
- **FR50 :** Le système envoie des alertes automatiques à l'étudiant à risque et à son encadrant académique

### Notifications

- **FR51 :** Le système peut envoyer des notifications in-app pour tous les événements clés (voir Matrice des Notifications en annexe)
- **FR52 :** Tout utilisateur peut consulter ses notifications reçues

### Gestion Administrative

- **FR53 :** L'administrateur peut affecter manuellement les encadrants académiques aux étudiants
- **FR54 :** L'entreprise peut remplacer un encadrant entreprise en cours de stage avec notification à l'admin
- **FR55 :** L'administrateur peut déclencher la mise en route d'un stage après confirmation

## Exigences Non Fonctionnelles

### Sécurité

- **NFR1 :** L'authentification utilise JWT avec expiration de token
- **NFR2 :** Les mots de passe sont hashés (bcrypt ou équivalent), jamais stockés en clair
- **NFR3 :** Chaque endpoint API vérifie le rôle de l'utilisateur avant d'autoriser l'accès
- **NFR4 :** L'isolation des données est stricte : aucun acteur ne peut accéder aux données hors de son périmètre (étudiant = ses données, entreprise = ses offres/candidatures, encadrant = ses étudiants assignés, admin = lecture totale)
- **NFR5 :** Les uploads de fichiers sont limités au format PDF et à une taille raisonnable

### Performance

- **NFR6 :** Les pages se chargent en moins de 3 secondes sur le jeu de données de démo
- **NFR7 :** La génération d'un document PDF se complète en moins de 5 secondes

### Fiabilité

- **NFR8 :** La machine d'états du stage ne permet aucune transition invalide (pas de retour en arrière non prévu)
- **NFR9 :** Les transitions automatiques (annulation candidatures, génération documents) s'exécutent de manière atomique -- en cas d'erreur, aucune modification partielle n'est persistée

### Maintenabilité

- **NFR10 :** Le code suit une architecture en couches (Controller / Service / Repository) conforme aux conventions Spring Boot
- **NFR11 :** La base de données utilise des migrations versionnées ou un schéma documenté

## Perspectives d'Évolution

Les axes suivants sont identifiés pour les versions futures de SmartIntern AI, au-delà du périmètre PFA :

### Intelligence Artificielle

- **Matching IA avancé** -- Remplacement de l'algorithme par mots-clés par un modèle ML/NLP capable d'analyser sémantiquement les compétences et l'expérience
- **Recommandations personnalisées** -- Suggestions proactives aux étudiants basées sur leur parcours, leurs évaluations et les tendances du marché
- **Anti-plagiat** -- Vérification automatique des rapports finaux contre une base de documents existants

### Mobilité et Accessibilité

- **Application mobile** -- Version mobile native (Flutter ou React Native) pour permettre le suivi de stage en déplacement
- **Notifications push** -- Alertes en temps réel sur mobile pour les événements critiques

### Intégration Système

- **Intégration SI universitaire** -- Import automatique des listes d'étudiants, filières et niveaux depuis le système d'information d'Iteam
- **Soutenance virtuelle** -- Module de visioconférence intégré pour les soutenances à distance

### Sécurité et Conformité

- **Certificat de signature numérique** -- Remplacement du tampon image par une signature cryptographique conforme aux standards légaux

### Administration Avancée

- **Tableau de bord BI** -- Graphiques interactifs, export de rapports statistiques, analyse des tendances par filière et par année
- **Multi-comptes entreprise** -- Gestion de plusieurs utilisateurs par entreprise avec droits différenciés (RH, managers, encadrants)

## Annexes

### Annexe A : Machine d'États du Stage

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

| Déclencheur | Action automatique |
|---|---|
| Acceptation d'une candidature | Annulation de toutes les autres candidatures "en attente" de l'étudiant |
| Retrait d'une offre par l'entreprise | Annulation des candidatures en attente + notification aux étudiants |
| Validation du rapport final par l'encadrant académique | Déblocage de l'autorisation de soutenance |
| Clôture du stage | Génération automatique de l'attestation de stage |

### Annexe B : Matrice des Notifications

| Événement | Destinataire(s) |
|---|---|
| Nouvelle candidature reçue | Entreprise |
| Candidature acceptée | Étudiant |
| Candidature refusée | Étudiant |
| Candidatures annulées (offre retirée) | Étudiants concernés |
| Candidatures annulées (étudiant placé) | Entreprises concernées |
| Rapport hebdomadaire soumis | Encadrant académique, Encadrant entreprise |
| Rapport validé / refusé | Étudiant |
| 3ème refus rapport final | Administrateur |
| Convention prête à signer | Entreprise, Encadrant entreprise |
| Alerte étudiant à risque | Étudiant, Encadrant académique |
| Encadrant entreprise remplacé | Administrateur |
| Stage clôturé | Tous les acteurs du stage |

**Canal :** In-app uniquement en v1. Notifications non configurables.

### Annexe C : Données de Référence et Initialisation

**Pré-chargement par l'admin (avant ouverture de la plateforme) :**

- Liste des filières
- Niveaux académiques
- Taxonomie de compétences

**Jeu de données de démo (pour la soutenance PFA) :**

- 3 étudiants fictifs avec profils complets
- 2 entreprises avec offres publiées
- 2 stages en cours à différents stades du cycle
- Quelques candidatures, rapports hebdomadaires et évaluations fictifs
