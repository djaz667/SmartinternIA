CAHIER DES CHARGES 🎓SmartIntern AI

Plateforme intelligente de gestion des stages universitaires

Table des matières

	Informations générales	2

	1.Introduction	2

	2. Description générale du projet	3

	3. Exigences fonctionnelles	4

	4. Exigences non fonctionnelles	6

	5. Architecture technique proposée	7

	6. Livrables attendus	8

	7. Planning prévisionnel	9

	8. Risques identifiés et stratégies de mitigation	11

	Critères de réussite	13

	Conclusion	14

Informations générales

- Titre du projet : Plateforme Intelligente de Gestion des Stages Universitaires « 🎓SmartIntern AI »

- Date de création :

- Formation :

- Matière : PFA

- Établissement : Iteam University

1.Introduction

- **Objectif du document**

Ce cahier des charges définit les spécifications fonctionnelles et techniques du système **SmartIntern**** AI**, une plateforme intelligente de gestion des stages universitaires.

Ce document présente les besoins, les contraintes ainsi que les livrables attendus pour la conception et le développement de cette solution, destinée à connecter les étudiants, les entreprises, les encadrants académiques et l’administration au sein d’un environnement numérique centralisé et sécurisé.

- **Portée du projet**

Le système vise à **informatiser et centraliser l’ensemble des processus de gestion des stages universitaires**, depuis la publication des offres de stage jusqu’au suivi académique, la validation finale et la génération des attestations, afin d’améliorer l’efficacité administrative, d’optimiser le matching entre étudiants et entreprises et d’augmenter la qualité du suivi pédagogique.

- **Définitions**

• SGSU : Système de Gestion des Stages Universitaires (SmartIntern AI)

• IHM : Interface Homme-Machine — Interface permettant l’interaction entre les utilisateurs (étudiants, entreprises, encadrants, administration) et le système

• BDD : Base de Données — Système de stockage structuré des informations (utilisateurs, offres, candidatures, rapports, statistiques)

• API : Interface de Programmation d’Application — Service permettant la communication entre le backend, l’application web et l’application mobile

• JWT : JSON Web Token — Mécanisme d’authentification sécurisé utilisé pour protéger l’accès au système

• BI : Business Intelligence — Ensemble des outils d’analyse et de visualisation des données statistiques

• Matching intelligent : Algorithme de calcul du score de compatibilité entre un étudiant et une offre de stage

2. Description générale du projet 

**2.1 Contexte**

	Dans les établissements d’enseignement supérieur, la gestion des stages universitaires constitue un processus complexe impliquant plusieurs acteurs : Étudiants, Entreprises et Administration. Les procédures actuelles sont souvent manuelles, fragmentées et peu optimisées, la mise en place d’une plateforme numérique intelligente permettra de centraliser, automatiser et améliorer la gestion complète du cycle de stage.

La plateforme **SmartIntern**** AI** est une solution numérique destinée à moderniser et optimiser la gestion des stages universitaires. Face à l’augmentation du nombre d’étudiants, à la diversité des entreprises partenaires et à la complexité du suivi académique, il devient nécessaire de disposer d’un système informatique performant, centralisé et intelligent.

Cette solution vise à améliorer la coordination entre les étudiants, les entreprises, les encadrants académiques et l’administration, tout en garantissant un suivi structuré, sécurisé et efficace du processus de stage.

**2.2 Vision du produit **

	Développer une solution logicielle complète, intelligente et intuitive permettant de gérer efficacement l’ensemble du processus de gestion des stages universitaires, d’améliorer l’expérience des différents acteurs (étudiants, entreprises, encadrants et administration) et de faciliter la prise de décision grâce à des outils d’analyse et de suivi avancés.

**2.3 Objectifs du projet**

- Digitaliser le processus de gestion des stages

- Automatiser le matching étudiant < -- > entreprise

- Améliorer le suivi pédagogique

- Optimiser la communication entre acteurs

- Fournir des indicateurs stratégiques à l’administration

- Faciliter l’insertion professionnelle des étudiants

**2.4 Utilisateurs cibles **

La solution consiste en une plateforme web intelligente permettant la mise en relation et le suivi collaboratif entre :

- Étudiants

- Entreprises

- Administration / Établissement de formation

3. Exigences fonctionnelles 

**3.1 Modules principaux **

	1. Gestion des utilisateurs

2. Gestion des offres de stage

3. Système de matching intelligent

4. Suivi pédagogique

5. Gestion documentaire

6. Business Intelligence & statistiques

**3.2 Fonctionnalités prioritaires (MVP)**

	**3.2.1 Gestion des offres de stage **

		**. F01 : **Création et dépôt d’offres par les entreprises

**. F02 : **Validation des offres par l’administration

**. F03 :**** **Gestion des candidatures

**. F04 : **Historique des offres

	**3.2.2 Filtrage des stages et cv **

		**. ****F06 :**** **Filtrage selon (domaine/spécialité, compétences requises, localisation, durée, type de stage, niveau académique)

	**3.2.3 Matching automatique étudiant-entreprise**

	** **	**. F07 : **Extraction automatique des données du CV

		. **F08** **:** Reformulation du cv selon model prédéfinie

		. **F09** **: **Analyse des compétences (limité)

		. **F10**** :** Proposition automatique des correspondances (baser sur les compétences uniquement)

	**3.2.4 Suivi des rapports **

		**. F11 :** Dépôt numérique des rapports

		**. F12 :** Validation par encadrants 

		**. F13 :** Historique des versions 

		**. F14 :** Notifications automatiques

	**3.2.4 Signature électronique**

		**. F15 :** Signature des conventions 

		**. F16 :** Validation administrative numérique 

	3.2.5 Tableau de bord statistiques 

		**.  F17 :** Indicateurs (Nombre de stages actifs, Taux de placement, Répartition par filière, Nombre d’entreprises partenaires)

**3.3 Fonctionnalités secondaires ****&**** version avancée **

	**3.3.1 ****Système de recommanda****ti****on basé sur compétences**

**. F18 : **Suggestions automatiques de stages

**. F19 : **** **Recommandations de profils aux entreprises

	**3.3.2 ****Matching intelligent avancé**

		**. F20 : **Score de compatibilité (%) (Calcul basé sur les compétences techniques, soft skills, moyenne académique (facultatif), expériences passées, localisation)

	**3.3.3 Détection des étudiants à risque**

		**. F21 : **Alertes automatiques envoyées aux encadrants (Critères : absence d’activité, retard de dépôt, évaluations faibles, non-respect des échéances)

	**3.3.4 Messagerie interne **

		**. F22 : **Chat intégré (messagerie interne sécurisée, notifications en temps réel, historique des conversations)

	**3.3.5 ****Génération documents **

		**. F23 : **Génération automatique d’attestation PDF (attestation de stage, certificat de fin de stage, signature numérique intégrée)

	**3.3.6 ****Tableau de bord BI (Business Intelligence)**

		**.  F****24**** :** Indicateurs (taux d’insertion professionnelle, statistiques par filière, entreprises les plus actives, top compétences demandées, graphiques dynamiques interactifs)

	**3.3.7 ****Espace collabora****ti****f intelligent**

		**. F25 : **Journal de bord hebdomadaire (déclaration des tâches, validation par le tuteur)	

		**. F26 **: Évaluation tripartite (entreprise, encadrant académique, auto-évaluation étudiant)

		**. F27 : **Alertes automatiques (retard, inactivité, absence de validation)	

	**3****.****3****.8 Soutenance virtuelle intégrée**

**. F28 : **Visioconférence

**. F29 : **** **Dépôt numérique du PFE

**. F30 : **Notes et observations du jury

**. F31 **:  Archivage de la soutenance

	**3****.****3****.9 Ges****ti****on documentaire intelligente**

**. F3****2**** **:  Modèles automatiques de conventions

**. F3****3 **:  Archivage sécurisé

**. F3****4**** **:  Versioning des rapports

**. F3****5**** **:  Vérification anti-plagiat

	4. Exigences non fonctionnelles

**4****.1 Sécurité**

**. NF01 : **Authentification sécurisée

**. NF02 :  **Gestion des rôles et permissions

**. NF03 : **Chiffrement des données

**. NF04 : **Traçabilité des actions (optionnel)

	**4****.2 Performance**

**.NF05 **: Support multi-utilisateurs

**.NF06 : **Temps de réponse optimisé

**.NF07 : **** **Architecture évolutive

**4****.3 Accessibilité**

**.NF08 : **Interface responsive

.**NF09 :** Compatible mobile & desktop

**.NF10 : **Interface intuitive

	**4****.4 Protec****ti****on des données**

**.NF11 : **Sauvegardes automatiques et base redondante

**.NF12 : **Protection des données personnelles

**.NF13 : **Archivage sécurisé

5. Architecture technique proposée

	

	**5.1 ****Architecture globale**

- Application web responsive basée sur une architecture **client–serveur** avec séparation Frontend / BackendFrontend développé en HTML CSS et JS 

- Backend développé en **Java (Spring Boot)** avec architecture RESTful et sécurisation via JWT

- Frontend web développé en **HTML, CSS, JavaScript**

- Base de données **My****SQL** pour le stockage des données (utilisateurs, offres, candidatures, rapports, statistiques)

- Communication en temps réel via **WebSocket** (chat et notifications)

**5.2 Modèles de données **

	**Description des principales entités et leurs relations :**

- **Étudiants : **représentent les utilisateurs qui postulent aux stages. Ils possèdent un profil, un CV, des compétences et un suivi de leurs rapports de stage.

- **Entreprises** : représentent les organisations proposant des offres de stage. Elles peuvent publier, modifier ou supprimer des offres et valider les candidatures.

- **Encadrants académiques** : enseignants responsables du suivi des étudiants durant leur stage, de la validation des rapports et de la notation.

- **Administration** : utilisateurs ayant un rôle global pour gérer les comptes, superviser les stages et générer des statistiques ou documents officiels.

- **Offres de stage** : propositions de stage publiées par les entreprises. Elles sont liées aux étudiants via le processus de candidature et de matching.

- **Candidatures** : représentent le lien entre un étudiant et une offre de stage. Contiennent le statut (accepté, refusé, en attente) et éventuellement le score de compatibilité.

- **Rapports de stage** : documents déposés par les étudiants pour suivi académique et validation par l’encadrant et l’entreprise.

- **Attestations / Conventions** : documents officiels générés automatiquement par le système pour chaque stage validé.

- **Notifications** : messages automatiques envoyés aux utilisateurs (rappels, alertes, communications importantes).

- Matching intelligent : entité virtuelle qui calcule le score de compatibilité étudiant–offre et permet le classement des candidatures.

6. Livrables attendus 

**6.1 Documentation**

**Pour assurer la qualité, la maintenabilité et la bonne utilisation de la plateforme ****SmartIntern**** AI, la documentation comprendra les éléments suivants : **

- **Spécifications fonctionnelles détaillées** : description précise de toutes les fonctionnalités du système (gestion des utilisateurs, offres de stage, candidatures, matching intelligent, suivi des rapports, génération de documents, notifications, dashboards, etc.).

- **Document d’architecture technique :** présentation de l’architecture globale (frontend, backend Java/Spring Boot, base de données MySQL, application mobile Flutter, API REST, WebSocket pour temps réel), diagrammes UML, et schéma des relations entre entités.

- **Manuel utilisateur** : guide pratique pour chaque acteur du système (étudiant, entreprise, encadrant académique, administration), expliquant comment utiliser les fonctionnalités principales et accéder à leurs espaces respectifs.

- **Plan de tests et rapports** : description des scénarios de tests fonctionnels, tests unitaires, tests d’intégration et validation des performances, ainsi que les résultats attendus pour chaque fonctionnalité.

- **Document de maintenance** : instructions pour la mise à jour, la gestion des bugs, l’extension des fonctionnalités, et la sauvegarde/restauration des données afin d’assurer la pérennité du système.

**6.2 Modules logiciels**

	**La plateforme ****SmartIntern**** AI sera composée des modules logiciels suivants :**

- **Backend API complète** : développé en **Java (Spring Boot)**, exposant des services REST sécurisés pour gérer les utilisateurs, offres de stage, candidatures, rapports, notifications, génération de documents et le module de matching intelligent.

- **Frontend administrateur** : interface web responsive permettant à l’administration de gérer les comptes, les offres, les stages, de visualiser les statistiques et de générer les documents officiels.

- **Frontend entreprise** : interface web permettant aux entreprises de publier et gérer leurs offres de stage, consulter les candidatures classées par score de compatibilité, valider et évaluer les stagiaires.

- **Frontend encadrant académique** : interface web permettant de suivre le journal de bord des étudiants, commenter les rapports et valider les stages.

- **Application étudiante**** **: interface web responsive permettant aux étudiants de créer leur profil, postuler aux offres, suivre l’avancement de leur stage, déposer des rapports et recevoir des notifications.

- **Scripts de déploiement et configuration** : scripts pour installer et configurer l’environnement backend, frontend, base de données et application mobile.

- **Base de données avec données initiales** : **MySQL** contenant les tables principales (Étudiants, Entreprises, Encadrants, Offres, Candidatures, Rapports, Notifications, Attestations) et des données de test pour validation et démonstration.

7. Planning prévisionnel

	**7.1 ****Phases du projet – ****SmartIntern**** AI**

**Le projet ****SmartIntern**** AI sera réalisé en plusieurs phases successives, permettant de structurer le travail, d’assurer un suivi régulier et de garantir la qualité du produit final.**

**Phase 1 : Analyse et conception (2 semaines)**

- Recueil des besoins auprès des différents acteurs (étudiants, entreprises, encadrants, administration).

- Rédaction du cahier des charges fonctionnel et technique.

- Définition des **cas d’utilisation**, des **diagrammes UML** (use case, classe, séquence).

- Conception du **modèle de données** (tables MySQL, relations, clés primaires/étrangères).

- Choix de l’architecture technique et des technologies : Java/Spring Boot pour le backend, HTML/CSS/JS pour le frontend web.

**Phase 2 : Développement du backend (3 semaines)**

- Création de l’API REST sécurisée en **Java (Spring Boot)**.

- Implémentation des entités et services :

- Gestion utilisateurs et rôles

- Gestion des offres de stage et candidatures

- Gestion des rapports de stage et attestations

- Notifications et messagerie simple

- Intégration de la **logique du ****matching**** intelligent** étudiant ↔ entreprise.

- Tests unitaires des services backend avec JUnit.

**Phase 3 : Développement frontend web et mobile (3 semaines)**

- Développement du **frontend administrateur et entreprise** en HTML/CSS/JS 

- Développement du **frontend encadrant**.

- Connexion aux services REST du backend.

- Mise en place de l’authentification JWT côté frontend.

- Tests d’intégration frontend-backend.

**Phase 4 : Intégration et tests fonctionnels (2 semaines)**

- Déploiement de la solution sur un serveur local ou cloud.

- Tests fonctionnels globaux :

- Création de comptes et rôles

- Publication et candidature aux offres

- Matching intelligent

- Dépôt et validation des rapports

- Génération des attestations et PDF

- Vérification des notifications et messagerie.

- Correction des bugs et optimisation.

**Phase 5 : Déploiement et formation (1 semaine)**

- Mise en production de la plateforme.

- Préparation du **manuel utilisateur** pour chaque acteur.

- Formation rapide des utilisateurs pilotes (étudiants, entreprises, administration).

- Documentation finale du projet : cahier des charges, architecture, plan de tests, manuel de maintenance.

**Phase 6 : Suivi et maintenance (en continu)**

- Sauvegarde régulière de la base de données.

- Suivi des performances du système et corrections des anomalies éventuelles.

- Évolutions futures : micro services, amélioration du matching, ajout de statistiques avancées.

8. Risques identifiés et stratégies de mitigation

	**8.1 Risques techniques**

**R01 : Difficultés d’intégration entre le backend Java et le frontend web**

- **Mitigation : **Tests d’intégration précoces, définition claire des API REST, utilisation de Postman pour valider les endpoints.

**R02 : Performance insuffisante en cas de forte utilisation simultanée**

- **Mitigation** : Tests de charge, optimisation des requêtes MySQL, mise en cache des données fréquentes, architecture modulaire pour faciliter la scalabilité.

**R03 : Problèmes liés au module de ****matching**** intelligent**

- **Mitigation** : Développement incrémental de l’algorithme, validation avec des jeux de données de test, suivi des performances et ajustement des critères de scoring.

**R04 : Défaillance lors de la génération des documents PDF ou attestations**

- **Mitigation** : Utilisation de bibliothèques fiables pour PDF (ex. iText ou Apache PDFBox), tests unitaires sur tous les types de documents.

**8.2 Risques organisationnels**

- **R05 : Résistance au changement des utilisateurs (étudiants, entreprises, encadrants)**

- **Mitigation** : Formation et guide utilisateur détaillé, interface simple et intuitive, phase pilote pour recueillir des retours.

- **R06 : Évolution des besoins en cours de projet**

- **Mitigation** : Méthodologie agile avec backlog produit priorisé (MoSCoW), marges dans le planning pour intégrer les nouvelles fonctionnalités.

- **R07 : Manque de disponibilité des entreprises ou des encadrants pour tester le système**

- **Mitigation** : Planification anticipée des tests utilisateurs, utilisation de données de test si nécessaire, communication régulière avec les parties prenantes.

- **R08 : Contraintes légales liées à la protection des données personnelles (RGPD / Tunisie)**

- **Mitigation** : Anonymisation des données sensibles, sécurisation des accès, consultation juridique pour conformité, chiffrement des données sensibles.

**8.3 Diagrammes**

 • Diagramme de cas d'utilisation 

• Diagramme de classes 

• Diagramme de séquence pour les processus critiques

9. Critères de réussite

- Application fonctionnelle

- Matching intelligent opérationnel

- Génération automatique PDF

- Dashboard interactif

- Déploiement réussi

- Documentation complète

Conclusion

SmartIntern AI constitue une solution innovante combinant :

- Gestion académique

- Intelligence décisionnelle

- Architecture moderne Web + Mobile

- Sécurité et performance