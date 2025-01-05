# Yoga App

![Java](https://img.shields.io/badge/Java-11%2B-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.5.4-green)
![Node.js](https://img.shields.io/badge/Node.js-14%2B-brightgreen)
![Angular](https://img.shields.io/badge/Angular-12%2B-red)
![License](https://img.shields.io/badge/License-MIT-yellow)

## Description
Yoga App est une application full-stack permettant de gérer les utilisateurs et les enseignants d'une école de yoga. Le projet comprend :
- Un backend Spring-Boot pour l'authentification, la gestion des utilisateurs, enseignants et sessions via une API REST sécurisée.
- Un frontend Angular pour une interface utilisateur intuitive.

## Prérequis

### Backend
- Java 11 ou supérieur
- Maven 3.6 ou supérieur
- Une instance de base de données MySQL/PostgreSQL en cours d'exécution (ou H2 pour les tests)

### Frontend
- Node.js (version 14 ou supérieure)
- npm ou yarn
- Angular CLI (version 12 ou supérieure)

## Installation et Configuration

Après avoir cloné ce dépôt,
 ```bash
   git clone https://github.com/TangiLC/OC-P5_Yoga
   ```
suivre les étapes suivantes :

### Installation de la Base de Données
1. **Installer MySQL**.
2. **Créer la base de données** :
   ```sql
   CREATE DATABASE yoga_app;
   ```
3. **Configurer les informations de connexion** :
   Mettez à jour `application.properties` dans `back/src/main/resources` :
   ```properties
   spring.datasource.url=[your_db_url]
   spring.datasource.username=[your_db_username]
   spring.datasource.password=[your_db_password]
   ```
4. **Importer le schéma** :
   Utilisez `script.sql` disponible dans `ressources/sql`.
   - **Option 1 :** Utiliser la ligne de commande SQL :
     ```bash
     mysql -u <username> -p yoga_app < chemin/vers/script.sql
     ```
   - **Option 2 :** Utiliser un outil tel que MySQL Workbench :
     - Ouvrir MySQL Workbench.
     - Connectez-vous à votre base de données.
     - Importer le fichier `script.sql` dans la base `yoga_app`.

### Installation Backend

1. **Allez dans le répertoire Backend** :
   ```bash
   cd backend
   ```
2. **Installer les dépendances Maven** :
   ```bash
   mvn clean install
   ```
3. **Lancer le serveur** :
   ```bash
   mvn spring-boot:run
   ```
   Le serveur par défaut sera sur le `port 8080`.

### Installation Frontend
1. **Allez dans le répertoire Frontend** :
   ```bash
   cd frontend
   ```
2. **Installer les dépendances** :
   ```bash
   npm install
   ```
3. **Lancer le serveur** :
   ```bash
   ng serve
   ```
   L'application sera disponible à `http://localhost:4200`.

## Exécution des Tests

### Backend
1. **Tests unitaires et d'intégration Junit5** :
   ```bash
   mvn clean test
   ```
2. **Rapports de couverture avec JaCoCo** :
   Les rapports seront disponibles dans `target/site/jacoco/index.html`.

### Frontend
1. **Tests unitaires et d'intégration avec Jest** :
   ```bash
   npm run test
   ```
    Les rapports de tests sont accessibles dans `coverage/jest/jest-stare/index.html`.
    Les rapports de coverage sont à consulter `coverage/jest/index.html`

2. **Tests End-to-End avec Cypress** :
   ```bash
   npm run e2e
   ```
   Les rapports sont générés avec
   ```bash
   npm run e2e:coverage
   ```


## Contribution
Les contributions sont les bienvenues ! Forkez le dépôt et soumettez une pull request.

## Comptes de Démonstration
- **Admin** : yoga@studio.com / test!1234
