-- Filières de base
INSERT IGNORE INTO filieres (nom) VALUES ('Informatique');
INSERT IGNORE INTO filieres (nom) VALUES ('Génie Logiciel');
INSERT IGNORE INTO filieres (nom) VALUES ('Réseau et Télécommunications');
INSERT IGNORE INTO filieres (nom) VALUES ('Systèmes Embarqués');
INSERT IGNORE INTO filieres (nom) VALUES ('Intelligence Artificielle');
INSERT IGNORE INTO filieres (nom) VALUES ('Cybersécurité');
INSERT IGNORE INTO filieres (nom) VALUES ('Data Science');
INSERT IGNORE INTO filieres (nom) VALUES ('Génie Électrique');

-- Competences techniques de base
INSERT IGNORE INTO competences (nom, categorie) VALUES ('Java', 'Backend');
INSERT IGNORE INTO competences (nom, categorie) VALUES ('Spring Boot', 'Backend');
INSERT IGNORE INTO competences (nom, categorie) VALUES ('Python', 'Backend');
INSERT IGNORE INTO competences (nom, categorie) VALUES ('Node.js', 'Backend');
INSERT IGNORE INTO competences (nom, categorie) VALUES ('SQL', 'Base de donnees');
INSERT IGNORE INTO competences (nom, categorie) VALUES ('MySQL', 'Base de donnees');
INSERT IGNORE INTO competences (nom, categorie) VALUES ('MongoDB', 'Base de donnees');
INSERT IGNORE INTO competences (nom, categorie) VALUES ('HTML', 'Frontend');
INSERT IGNORE INTO competences (nom, categorie) VALUES ('CSS', 'Frontend');
INSERT IGNORE INTO competences (nom, categorie) VALUES ('JavaScript', 'Frontend');
INSERT IGNORE INTO competences (nom, categorie) VALUES ('React', 'Frontend');
INSERT IGNORE INTO competences (nom, categorie) VALUES ('Angular', 'Frontend');
INSERT IGNORE INTO competences (nom, categorie) VALUES ('Docker', 'DevOps');
INSERT IGNORE INTO competences (nom, categorie) VALUES ('Git', 'DevOps');
INSERT IGNORE INTO competences (nom, categorie) VALUES ('Machine Learning', 'IA');

-- Comptes de test pré-approuvés (mot de passe: admin123 pour tous)
-- Admin
INSERT INTO users (email, mot_de_passe, role, statut_compte, date_creation, actif)
VALUES ('admin@smartintern.tn', '$2a$10$5EqoBNMb9zwIEz34l3L1te8HpB3q7lKmPnSHEYsNr78DTIv.bd92y', 'ADMIN', 'APPROUVE', NOW(), true)
ON DUPLICATE KEY UPDATE mot_de_passe = '$2a$10$5EqoBNMb9zwIEz34l3L1te8HpB3q7lKmPnSHEYsNr78DTIv.bd92y';

-- Etudiant
INSERT INTO users (email, mot_de_passe, role, statut_compte, date_creation, actif)
VALUES ('etudiant@smartintern.tn', '$2a$10$5EqoBNMb9zwIEz34l3L1te8HpB3q7lKmPnSHEYsNr78DTIv.bd92y', 'ETUDIANT', 'APPROUVE', NOW(), true)
ON DUPLICATE KEY UPDATE mot_de_passe = '$2a$10$5EqoBNMb9zwIEz34l3L1te8HpB3q7lKmPnSHEYsNr78DTIv.bd92y';

INSERT INTO etudiants (user_id, nom, prenom, filiere_id)
SELECT u.id, 'Ben Ali', 'Sami', f.id
FROM users u, filieres f
WHERE u.email = 'etudiant@smartintern.tn' AND f.nom = 'Informatique'
AND NOT EXISTS (SELECT 1 FROM etudiants e WHERE e.user_id = u.id);

-- Entreprise
INSERT INTO users (email, mot_de_passe, role, statut_compte, date_creation, actif)
VALUES ('entreprise@smartintern.tn', '$2a$10$5EqoBNMb9zwIEz34l3L1te8HpB3q7lKmPnSHEYsNr78DTIv.bd92y', 'ENTREPRISE', 'APPROUVE', NOW(), true)
ON DUPLICATE KEY UPDATE mot_de_passe = '$2a$10$5EqoBNMb9zwIEz34l3L1te8HpB3q7lKmPnSHEYsNr78DTIv.bd92y';

INSERT INTO entreprises (user_id, nom, secteur, adresse, telephone)
SELECT u.id, 'TechCorp Tunisia', 'Technologies', 'Tunis', '+216 71 000 000'
FROM users u
WHERE u.email = 'entreprise@smartintern.tn'
AND NOT EXISTS (SELECT 1 FROM entreprises e WHERE e.user_id = u.id);
