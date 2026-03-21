-- Filières de base
INSERT IGNORE INTO filieres (nom) VALUES ('Informatique');
INSERT IGNORE INTO filieres (nom) VALUES ('Génie Logiciel');
INSERT IGNORE INTO filieres (nom) VALUES ('Réseau et Télécommunications');
INSERT IGNORE INTO filieres (nom) VALUES ('Systèmes Embarqués');
INSERT IGNORE INTO filieres (nom) VALUES ('Intelligence Artificielle');
INSERT IGNORE INTO filieres (nom) VALUES ('Cybersécurité');
INSERT IGNORE INTO filieres (nom) VALUES ('Data Science');
INSERT IGNORE INTO filieres (nom) VALUES ('Génie Électrique');

-- Compte admin par défaut (mot de passe: admin123 hashé BCrypt)
INSERT IGNORE INTO users (email, mot_de_passe, role, statut_compte, date_creation, actif)
VALUES ('admin@smartintern.tn', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', 'APPROUVE', NOW(), true);
