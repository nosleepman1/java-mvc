-- Script de création de la base de données et des tables
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
