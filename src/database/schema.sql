-- Script de création de la base de données et des tables
-- Projet Java MVC - Gestionnaire de Projets et de Tâches

CREATE DATABASE IF NOT EXISTS java_shop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE java_shop;

-- 1. Table des utilisateurs (avec mot de passe haché SHA-256)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    mdp VARCHAR(255) NOT NULL,
    age INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Table des projets
CREATE TABLE IF NOT EXISTS projects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(150) NOT NULL,
    description TEXT,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id INT NOT NULL,
    CONSTRAINT fk_project_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Table des tâches (rattachées à un projet)
CREATE TABLE IF NOT EXISTS tasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(150) NOT NULL,
    description TEXT,
    statut VARCHAR(30) DEFAULT 'A_FAIRE',
    date_echeance DATE,
    project_id INT NOT NULL,
    CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

