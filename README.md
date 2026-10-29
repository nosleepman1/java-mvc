# Gestionnaire de Projets et de Tâches (Java MVC)

Application console de gestion de projets et de tâches implémentant le patron d'architecture **MVC (Modèle - Vue - Contrôleur)** avec **authentification sécurisée (mots de passe hachés en SHA-256)** et persistance des données sous **MySQL (JDBC)**.

---

## 🏗️ Architecture du Projet

Le projet respecte scrupuleusement la séparation des responsabilités :

```
src/
├── database/
│   ├── Database.java         # Gestion de la connexion JDBC et auto-initialisation des tables
│   └── schema.sql            # Script SQL de création de la base et des tables
├── Entities/                 # Modèles de données
│   ├── User.java             # Entité Utilisateur
│   ├── Project.java          # Entité Projet (titre, description, date, tâches, userId)
│   ├── Task.java             # Entité Tâche (titre, description, statut, date échéance, projectId)
│   └── TaskStatus.java       # Énumération des statuts (A_FAIRE, EN_COURS, TERMINE)
├── repositories/             # Couche d'accès aux données (Data Access Object / DAO)
│   ├── BaseRepository.java   # Classe générique abstraite (CRUD de base)
│   ├── UserRepository.java   # Opérations CRUD et authentification des utilisateurs
│   ├── ProjectRepository.java# Opérations sur les projets par utilisateur
│   └── TaskRepository.java   # Opérations sur les tâches par projet/utilisateur
├── utils/
│   └── PasswordHasher.java   # Utilitaire de hachage SHA-256 des mots de passe
├── Controllers/              # Logique métier et orchestration
│   ├── AuthController.java   # Inscription, connexion, session active et changement de mot de passe
│   ├── ProjectController.java# Gestion des projets de l'utilisateur connecté
│   ├── TaskController.java   # Gestion des tâches et des statuts
│   └── UserController.java   # Administration des utilisateurs
├── views/                    # Couche Présentation / Menus interactifs en console
│   ├── ConsoleUtils.java     # Saisie sécurisée, barres de progression et mise en forme
│   ├── AuthView.java         # Menus de connexion et d'inscription
│   ├── ProjectView.java      # Menu et affichages de gestion des projets
│   ├── TaskView.java         # Menu et affichages de gestion des tâches
│   └── MainView.java         # Tableau de bord principal et navigation
├── Main.java                 # Point d'entrée de l'application (instancie les contrôleurs et les vues)
└── TestApp.java              # Suite de tests d'intégration automatisés
```

---

## 🔐 Sécurité & Hachage des Mots de Passe

- Les mots de passe ne sont **jamais stockés en clair** en base de données.
- Utilisation de `PasswordHasher` basé sur `java.security.MessageDigest` avec l'algorithme standard **SHA-256** (empreinte hexadécimale de 64 caractères).
- Lors de l'inscription : le mot de passe est haché avant l'insertion SQL.
- Lors de la connexion : le mot de passe saisi est haché puis comparé au hash stocké en base.

---

## 🗄️ Schéma de la Base de Données (`java_shop`)

1. **Table `users`** :
   - `id` INT AUTO_INCREMENT PRIMARY KEY
   - `nom` VARCHAR(100) NOT NULL
   - `email` VARCHAR(150) NOT NULL UNIQUE
   - `mdp` VARCHAR(255) NOT NULL *(hash SHA-256)*
   - `age` INT

2. **Table `projects`** :
   - `id` INT AUTO_INCREMENT PRIMARY KEY
   - `titre` VARCHAR(150) NOT NULL
   - `description` TEXT
   - `date_creation` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   - `user_id` INT NOT NULL (clé étrangère vers `users(id)` avec `ON DELETE CASCADE`)

3. **Table `tasks`** :
   - `id` INT AUTO_INCREMENT PRIMARY KEY
   - `titre` VARCHAR(150) NOT NULL
   - `description` TEXT
   - `statut` VARCHAR(30) DEFAULT 'A_FAIRE'
   - `date_echeance` DATE
   - `project_id` INT NOT NULL (clé étrangère vers `projects(id)` avec `ON DELETE CASCADE`)

*Note : Les tables sont créées automatiquement au démarrage via `Database.initDatabase()`.*

---

## 🚀 Exécution

### 1. Avec IntelliJ IDEA
- Ouvrir le dossier du projet dans IntelliJ IDEA.
- Exécuter la classe `Main.java`.

### 2. En Ligne de Commande

**Compilation :**
```powershell
javac -cp "lib/mysql-connector-j-9.4.0.jar" -d out (Get-ChildItem -Path src -Filter *.java -Recurse | Select-Object -ExpandProperty FullName)
```

**Lancement de l'application :**
```powershell
java -cp "out;lib/mysql-connector-j-9.4.0.jar" Main
```

**Lancement des tests automatisés :**
```powershell
java -ea -cp "out;lib/mysql-connector-j-9.4.0.jar" TestApp
```
