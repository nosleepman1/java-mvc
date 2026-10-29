# Java Native MVC avec JDBC

Ce projet est une application Java native développée selon le modèle architectural MVC, avec JDBC pour l'accès aux données et MySQL comme système de gestion de base de données.

L'objectif est de mettre en pratique les principes de la programmation orientée objet, la séparation des responsabilités et la structuration d'une application Java en plusieurs couches, sans utiliser de framework comme Spring Boot.

## Technologies utilisées

- **Java** : langage de programmation.
- **JDBC** : connexion à la base de données et exécution des requêtes SQL.
- **MySQL** : système de gestion de base de données relationnelle.
- **Maven** : gestion des dépendances et compilation du projet.

## Architecture du projet

Le code source est organisé en plusieurs packages, chacun ayant une responsabilité précise.

- **Controllers** : gèrent les interactions entre les entrées de l'application et les services.
- **Services** : contiennent la logique métier et coordonnent les opérations.
- **Repositories** : gèrent l'accès aux données avec JDBC et les requêtes SQL.
- **Entities** : représentent les objets métier manipulés par l'application.

## Structure du projet

```text
src/
└── main/
    └── java/
        └── ...
            ├── controllers/
            ├── services/
            ├── repositories/
            └── entities/
                ├── BaseEntity.java
                └── UserEntity.java
```

## Concepts et fonctionnalités

### 1. Architecture MVC et séparation des responsabilités

Le projet sépare les contrôleurs, les services, les repositories et les entités afin de faciliter la maintenance, les tests et l'évolution du code.

### 2. Classe abstraite BaseEntity

La classe abstraite `BaseEntity` définit un contrat commun aux entités de l'application.

Elle contient notamment la méthode `mapToEntity`, utilisée pour transformer les résultats issus de la base de données en objets Java.

Les entités concrètes, comme `UserEntity`, héritent de `BaseEntity` et implémentent leur propre logique de mapping.

### 3. Abstraction des repositories

Les repositories encapsulent les opérations d'accès aux données et permettent de centraliser les interactions avec la base de données.

Le modèle prévoit notamment les méthodes suivantes :

- `getById` : récupérer une entité à partir de son identifiant.
- `getAll` : récupérer l'ensemble des entités.
- `delete` : supprimer une entité.

Les repositories spécifiques, comme celui des utilisateurs, peuvent étendre cette abstraction et ajouter des méthodes adaptées à leurs besoins.

### 4. Accès aux données avec JDBC

JDBC est utilisé pour établir les connexions à MySQL, préparer et exécuter les requêtes SQL, puis convertir les résultats en objets Java.

### 5. Programmation orientée objet

Le projet met en pratique plusieurs concepts fondamentaux :

- Encapsulation.
- Héritage.
- Abstraction.
- Polymorphisme.
- Séparation des responsabilités.
- Réutilisation du code.

## Objectifs pédagogiques

- Comprendre le fonctionnement de JDBC.
- Concevoir une architecture Java organisée en couches.
- Appliquer les principes de la programmation orientée objet.
- Utiliser des classes abstraites pour mutualiser les comportements.
- Structurer l'accès aux données avec le Repository Pattern.
- Implémenter les opérations CRUD.
- Comprendre le mapping entre les résultats SQL et les objets Java.

## Installation et exécution

### Prérequis

- JDK compatible avec le projet.
- Maven.
- MySQL.

### Étapes

1. Cloner le dépôt :

   ```bash
   git clone <URL_DU_DEPOT>
   ```

2. Créer la base de données MySQL et configurer les paramètres de connexion.

3. Charger les dépendances Maven.

4. Compiler le projet :

   ```bash
   mvn compile
   ```

   Sous Windows, si le Maven Wrapper est disponible :

   ```powershell
   .\mvnw.cmd compile
   ```

5. Exécuter la classe principale depuis IntelliJ IDEA ou avec la commande adaptée à la configuration du projet.

## Auteur

Abdallah Diouf

GitHub : [nosleepman1](https://github.com/nosleepman1)
