# StreamCore - Plateforme de Streaming Vidéo

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-ready-blue.svg)](https://www.docker.com/)

Application de streaming vidéo backend basée sur une architecture microservices avec API REST.

## 📋 Table des matières

- [Architecture](#architecture)
- [Technologies](#technologies)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Démarrage](#démarrage)
- [Endpoints API](#endpoints-api)
- [Tests](#tests)
- [Structure du projet](#structure-du-projet)
- [Diagramme de classes](#diagramme-de-classes)

## 🏗️ Architecture

L'application est composée de **5 microservices** :

### Microservices Fonctionnels
1. **video-service** (Port 8081) - Gestion des contenus vidéo
2. **user-service** (Port 8082) - Gestion des utilisateurs, watchlist et historique

### Microservices Infrastructure
3. **gateway-service** (Port 8080) - Point d'entrée unique (Spring Cloud Gateway)
4. **discovery-service** (Port 8761) - Service discovery (Eureka Server)
5. **config-service** (Port 8888) - Configuration centralisée (Git repository)

### Bases de données
- **postgres-video** (Port 5432) - Base de données pour video-service
- **postgres-user** (Port 5433) - Base de données pour user-service

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       ▼
┌─────────────────────┐
│  Gateway Service    │  Port 8080
│  (Spring Cloud      │
│   Gateway)          │
└──────┬──────────────┘
       │
       ├──────────────────┬───────────────────┐
       ▼                  ▼                   ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│Video Service │   │User Service  │   │Discovery     │
│Port 8081     │   │Port 8082     │   │Port 8761     │
└──────┬───────┘   └──────┬───────┘   └──────────────┘
       │                  │
       ▼                  ▼
┌──────────────┐   ┌──────────────┐
│postgres-video│   │postgres-user │
│Port 5432     │   │Port 5433     │
└──────────────┘   └──────────────┘
```

## 💻 Technologies

### Backend Framework
- **Spring Boot 3.2.0** - Framework principal
- **Spring Data JPA** - Couche d'accès aux données
- **Spring Cloud** - Microservices (Eureka, Config, Gateway)
- **OpenFeign** - Communication inter-services
- **Hibernate** - ORM

### Base de données
- **PostgreSQL 16** - SGBD relationnel

### Conteneurisation
- **Docker** - Conteneurisation
- **Docker Compose** - Orchestration multi-conteneurs

### Tests
- **JUnit 5** - Framework de tests
- **Mockito** - Mocking pour tests unitaires
- **AssertJ** - Assertions fluides

### Autres
- **Lombok** - Réduction du boilerplate code
- **Jakarta Validation** - Validation des données
- **Resilience4j** - Circuit Breaker pour OpenFeign

## 📦 Prérequis

- **Java 17** ou supérieur
- **Maven 3.8+** (ou utiliser le wrapper Maven inclus)
- **Docker** et **Docker Compose**
- **Git**

## 🚀 Installation

### 1. Cloner le repository

```bash
git clone https://github.com/YounesBousfiha/StreamCore.git
cd StreamCore
```

### 2. Vérifier les prérequis

```bash
java -version    # Doit afficher Java 17+
docker --version # Doit afficher Docker 20.10+
docker-compose --version
```

## ▶️ Démarrage

### Démarrage avec Docker Compose (Recommandé)

```bash
# Construire et démarrer tous les services
docker-compose up --build

# Ou en arrière-plan
docker-compose up -d --build
```

### Ordre de démarrage
Les services démarrent automatiquement dans le bon ordre grâce aux dépendances Docker Compose :

1. Bases de données (postgres-video, postgres-user)
2. Discovery Service (Eureka)
3. Config Service
4. Video Service & User Service
5. Gateway Service

### Vérification du démarrage

- **Eureka Dashboard** : http://localhost:8761
- **Gateway Service** : http://localhost:8080
- **Video Service** : http://localhost:8081/api/videos
- **User Service** : http://localhost:8082/api/users

### Arrêter les services

```bash
docker-compose down

# Avec suppression des volumes (données)
docker-compose down -v
```

## 📡 Endpoints API

Tous les endpoints sont accessibles via le **Gateway Service** sur le port **8080**.

### Video Service (`/api/videos`)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/videos` | Récupérer tous les vidéos |
| `GET` | `/api/videos/{id}` | Récupérer une vidéo par ID |
| `POST` | `/api/videos` | Créer une nouvelle vidéo |
| `PUT` | `/api/videos/{id}` | Mettre à jour une vidéo |
| `DELETE` | `/api/videos/{id}` | Supprimer une vidéo |
| `GET` | `/api/videos/type/{type}` | Filtrer par type (FILM/SERIE) |
| `GET` | `/api/videos/category/{category}` | Filtrer par catégorie |
| `GET` | `/api/videos/search?title={title}` | Rechercher par titre |

### User Service (`/api/users`)

#### Gestion des utilisateurs

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/users` | Récupérer tous les utilisateurs |
| `GET` | `/api/users/{id}` | Récupérer un utilisateur par ID |
| `POST` | `/api/users` | Créer un nouvel utilisateur |
| `PUT` | `/api/users/{id}` | Mettre à jour un utilisateur |
| `DELETE` | `/api/users/{id}` | Supprimer un utilisateur |
| `GET` | `/api/users/{id}/statistics` | Statistiques de visionnage |

#### Watchlist

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/users/{userId}/watchlist` | Récupérer la watchlist |
| `POST` | `/api/users/{userId}/watchlist` | Ajouter une vidéo à la watchlist |
| `DELETE` | `/api/users/{userId}/watchlist/{videoId}` | Retirer une vidéo de la watchlist |

#### Historique de visionnage

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/users/{userId}/watch-history?limit={limit}` | Récupérer l'historique |
| `POST` | `/api/users/{userId}/watch-history` | Enregistrer un visionnage |

## 📝 Exemples d'utilisation avec Postman

### 1. Créer une vidéo

```http
POST http://localhost:8080/api/videos
Content-Type: application/json

{
  "title": "Inception",
  "description": "Un voleur qui s'infiltre dans les rêves",
  "thumbnailUrl": "https://example.com/inception-thumb.jpg",
  "trailerUrl": "https://www.youtube.com/embed/YoHD9XEInc0",
  "duration": 148,
  "releaseYear": 2010,
  "type": "FILM",
  "category": "SCIENCE_FICTION",
  "rating": 8.8,
  "director": "Christopher Nolan",
  "cast": ["Leonardo DiCaprio", "Marion Cotillard", "Tom Hardy"]
}
```

### 2. Créer un utilisateur

```http
POST http://localhost:8080/api/users
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john.doe@example.com",
  "password": "password123"
}
```

### 3. Ajouter une vidéo à la watchlist

```http
POST http://localhost:8080/api/users/1/watchlist
Content-Type: application/json

{
  "videoId": 1
}
```

### 4. Enregistrer un visionnage

```http
POST http://localhost:8080/api/users/1/watch-history
Content-Type: application/json

{
  "videoId": 1,
  "progressTime": 3600,
  "completed": true
}
```

### 5. Obtenir les statistiques de visionnage

```http
GET http://localhost:8080/api/users/1/statistics
```

**Réponse :**
```json
{
  "totalVideosWatched": 15,
  "totalWatchTimeSeconds": 54000,
  "mostWatchedCategory": "SCIENCE_FICTION"
}
```

## 🧪 Tests

### Exécuter tous les tests

```bash
# Pour video-service
cd video-service
mvn test

# Pour user-service
cd user-service
mvn test
```

### Couverture des tests

- **video-service** : Tests unitaires complets (Mapper, Service)
- **user-service** : Tests unitaires complets (Mapper, Service)
- Technologies : JUnit 5, Mockito, AssertJ

## 📂 Structure du projet

```
StreamCore/
├── config-service/          # Configuration centralisée
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── discovery-service/       # Eureka Server
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── gateway-service/         # API Gateway
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── video-service/           # Gestion des vidéos
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/streamcore/videoservice/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── entity/
│   │   │   │   ├── dto/
│   │   │   │   ├── mapper/
│   │   │   │   └── exception/
│   │   │   └── resources/
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
├── user-service/            # Gestion des utilisateurs
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/streamcore/userservice/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── entity/
│   │   │   │   ├── dto/
│   │   │   │   ├── mapper/
│   │   │   │   ├── client/
│   │   │   │   └── exception/
│   │   │   └── resources/
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml       # Orchestration Docker
└── README.md               # Ce fichier
```

## 🎨 Diagramme de classes

Voir le fichier [class-diagram.png](./class-diagram.png) pour le diagramme UML complet.

### Entités principales

#### Video Service
- **Video** : id, title, description, thumbnailUrl, trailerUrl, duration, releaseYear, type, category, rating, director, cast

#### User Service
- **User** : id, username, email, password
- **Watchlist** : id, userId, videoId, addedAt
- **WatchHistory** : id, userId, videoId, watchedAt, progressTime, completed

### Relations
- Un utilisateur peut avoir plusieurs vidéos dans sa **watchlist** (relation 1:N)
- Un utilisateur peut avoir plusieurs entrées dans son **historique** (relation 1:N)
- La communication entre services utilise **OpenFeign** avec fallback

## 🔧 Configuration

### Variables d'environnement

Les services utilisent des variables d'environnement définies dans `docker-compose.yml` :

```yaml
SPRING_DATASOURCE_URL      # URL de la base de données
SPRING_DATASOURCE_USERNAME # Utilisateur PostgreSQL
SPRING_DATASOURCE_PASSWORD # Mot de passe PostgreSQL
EUREKA_SERVER              # URL du serveur Eureka
SERVER_PORT                # Port du service
```

### Configuration centralisée

Le **config-service** récupère les configurations depuis un repository Git :
- Repository : https://github.com/YounesBousfiha/StreamCore-config-service
- Format : YAML
- Fichiers : `video-service.yml`, `user-service.yml`, `gateway-service.yml`

## 🛠️ Design Patterns utilisés

- **Repository Pattern** : Abstraction de l'accès aux données
- **DTO Pattern** : Séparation entre entités et objets de transfert
- **Mapper Pattern** : Conversion entre entités et DTOs
- **Circuit Breaker** : Gestion des défaillances inter-services (Resilience4j)
- **API Gateway Pattern** : Point d'entrée unique pour tous les services
- **Service Discovery** : Découverte dynamique des services (Eureka)

## 🚧 Fonctionnalités bonus (non implémentées)

- ❌ Microservice de sécurité avec authentification stateless (JWT)
- ❌ Microservice de monitoring (Spring Boot Admin / Prometheus)

## 📊 Gestion de projet

- **Méthodologie** : Agile
- **Gestion des tâches** : Jira
- **Versioning** : Git

## 👥 Contributeurs

- Younes Bousfiha

## 📄 Licence

Ce projet est développé dans un cadre académique.

---

**Note** : Cette application est 100% backend. Les tests se font exclusivement via Postman ou tout autre client HTTP.

