# 🗺️ SIG Maps - Diagrammes d'Architecture

## Table des matières
1. [Diagramme de Composants](#1-diagramme-de-composants)
2. [Diagramme de Classes Backend](#2-diagramme-de-classes-backend)
3. [Diagramme de Séquence - Authentification JWT](#3-diagramme-de-séquence---authentification-jwt)
4. [Diagramme de Séquence - Création et Affichage d'une Carte](#4-diagramme-de-séquence---création-et-affichage-dune-carte)
5. [Diagramme de Déploiement Docker](#5-diagramme-de-déploiement-docker)
6. [Diagramme de Flux de Données Géospatiales](#6-diagramme-de-flux-de-données-géospatiales)
7. [Diagramme ERD - Modèle de Données](#7-diagramme-erd---modèle-de-données)
8. [Diagramme Architecture Frontend Vue/Nuxt](#8-diagramme-architecture-frontend-vuenuxt)
9. [Diagramme API REST](#9-diagramme-api-rest)
10. [Diagramme de Sécurité](#10-diagramme-de-sécurité)

---

## 1. Diagramme de Composants

### Vue d'ensemble de l'architecture système

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              ARCHITECTURE SIG MAPS                                       │
│                        Système d'Information Géographique                                │
└─────────────────────────────────────────────────────────────────────────────────────────┘

                                    ┌──────────────────┐
                                    │   NAVIGATEUR     │
                                    │   WEB CLIENT     │
                                    │  (Chrome/Edge)   │
                                    └────────┬─────────┘
                                             │
                                             │ HTTPS (Port 80/443)
                                             ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                   COUCHE PRÉSENTATION                                    │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │                          FRONTEND (Nuxt.js 2.x / Vue.js)                         │   │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐           │   │
│  │  │   Pages      │ │  Composants  │ │    Store     │ │   Plugins    │           │   │
│  │  │  (Nuxt)      │ │   (Vue)      │ │   (Vuex)     │ │ (Leaflet,UI) │           │   │
│  │  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘           │   │
│  │                                                                                   │   │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐           │   │
│  │  │  Dashboard   │ │   Viewer     │ │    Auth      │ │   Admin      │           │   │
│  │  │  (Admin)     │ │   (Maps)     │ │  (Login)     │ │  (Gestion)   │           │   │
│  │  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘           │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
│                                           │                                             │
│                                           │ Axios HTTP (REST API)                        │
│                                           ▼                                             │
└─────────────────────────────────────────────────────────────────────────────────────────┘
                                             │
                                             │ HTTP (Port 8080)
                                             ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                     COUCHE MÉTIER                                        │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │                      BACKEND (Spring Boot 3.2 / Jakarta EE 10)                   │   │
│  │                                                                                   │   │
│  │  ┌──────────────────────────────┐    ┌──────────────────────────────────┐       │   │
│  │  │      API REST CONTROLLERS    │    │       SÉCURITÉ (JWT)             │       │   │
│  │  │  ┌────────────────────────┐  │    │  ┌────────────────────────────┐  │       │   │
│  │  │  │ LoginController        │  │    │  │ AuthTokenFilter            │  │       │   │
│  │  │  │ MapController          │  │    │  │ JwtUtils                   │  │       │   │
│  │  │  │ LayerController        │  │    │  │ WebSecurityConfig          │  │       │   │
│  │  │  │ UserController         │  │    │  │ AuthEntryPointJwt          │  │       │   │
│  │  │  │ EntityElementController│  │    │  │ UserDetailsServiceImpl     │  │       │   │
│  │  │  │ GeoServerController    │  │    │  │ RedisUtil                  │  │       │   │
│  │  │  │ GeoProcessingController│  │    │  └────────────────────────────┘  │       │   │
│  │  │  │ FilterController       │  │    └──────────────────────────────────┘       │   │
│  │  │  └────────────────────────┘  │                                               │   │
│  │  └──────────────────────────────┘                                               │   │
│  │                                                                                   │   │
│  │  ┌──────────────────────────────┐    ┌──────────────────────────────────┐       │   │
│  │  │       SERVICES MÉTIER        │    │       UTILITAIRES                │       │   │
│  │  │  ┌────────────────────────┐  │    │  ┌────────────────────────────┐  │       │   │
│  │  │  │ MapService             │  │    │  │ GeoJsonReader/Writer       │  │       │   │
│  │  │  │ LayerService           │  │    │  │ ShapeFileReader/Writer     │  │       │   │
│  │  │  │ UserService            │  │    │  │ KMLReader/Writer           │  │       │   │
│  │  │  │ EntityElementService   │  │    │  │ GML2Writer/GML3Writer      │  │       │   │
│  │  │  │ GeoProcessingService   │  │    │  │ SLDGenerator               │  │       │   │
│  │  │  │ UploadFileService      │  │    │  │ GeoServerRest              │  │       │   │
│  │  │  │ FilterService          │  │    │  │ GeoToolsService            │  │       │   │
│  │  │  └────────────────────────┘  │    │  └────────────────────────────┘  │       │   │
│  │  └──────────────────────────────┘    └──────────────────────────────────┘       │   │
│  │                                                                                   │   │
│  │  ┌──────────────────────────────┐    ┌──────────────────────────────────┐       │   │
│  │  │       REPOSITORIES JPA       │    │       MAPPER (DTO)               │       │   │
│  │  │  ┌────────────────────────┐  │    │  ┌────────────────────────────┐  │       │   │
│  │  │  │ UserRepository         │  │    │  │ UserMapper                 │  │       │   │
│  │  │  │ LayerRepository        │  │    │  │ LayerMapper                │  │       │   │
│  │  │  │ MapRepository          │  │    │  │ MapMapper                  │  │       │   │
│  │  │  │ EntityElementRepository│  │    │  │ FieldMapper                │  │       │   │
│  │  │  │ GroupRepository        │  │    │  │ EntityElementMapper        │  │       │   │
│  │  │  └────────────────────────┘  │    │  └────────────────────────────┘  │       │   │
│  │  └──────────────────────────────┘    └──────────────────────────────────┘       │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────────────────┘
                                             │
                    ┌────────────────────────┼────────────────────────┐
                    │                        │                        │
                    ▼                        ▼                        ▼
┌──────────────────────────┐  ┌──────────────────────┐  ┌──────────────────────────────┐
│   BASE DE DONNÉES        │  │    CACHE REDIS       │  │    GEOSERVER (WMS/WFS)       │
│   PostgreSQL 15          │  │    (Sessions JWT)    │  │    Serveur Cartographique    │
│   + PostGIS 3.3          │  │                      │  │                              │
│  ┌────────────────────┐  │  │  ┌────────────────┐  │  │  ┌────────────────────────┐  │
│  │ Schema: sig        │  │  │  │ Tokens JWT     │  │  │  │ Workspaces             │  │
│  │ - user             │  │  │  │ Sessions       │  │  │  │ DataStores             │  │
│  │ - layer            │  │  │  │ Cache          │  │  │  │ Layers (WMS)           │  │
│  │ - map              │  │  │  └────────────────┘  │  │  │ Features (WFS)         │  │
│  │ - entity_element   │  │  │                      │  │  │ SLD Styles             │  │
│  │ - field            │  │  │  Port: 6379          │  │  └────────────────────────┘  │
│  │ - groups           │  │  │                      │  │                              │
│  │ - permissions      │  │  └──────────────────────┘  │  Port: 8080 (GeoServer)     │
│  └────────────────────┘  │                            │                              │
│  Port: 5432              │                            └──────────────────────────────┘
└──────────────────────────┘

```

### Technologies utilisées par composant

| Couche | Technologie | Version |
|--------|-------------|---------|
| **Frontend** | Nuxt.js / Vue.js | 2.x |
| **UI Framework** | Ant Design Vue / PrimeVue | Latest |
| **Cartographie** | Leaflet.js | Latest |
| **Backend** | Spring Boot | 3.2.x |
| **Java** | OpenJDK | 17/21 |
| **ORM** | Hibernate / Spring Data JPA | 6.x |
| **Base de données** | PostgreSQL + PostGIS | 15 / 3.3 |
| **Cache** | Redis | Latest |
| **Serveur SIG** | GeoServer | 2.x |
| **Sécurité** | Spring Security 6 + JWT | 6.x |
| **Conteneurisation** | Docker / Docker Compose | 3.8 |

---

## 2. Diagramme de Classes Backend

### Structure des entités JPA principales

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                          MODÈLE DE DOMAINES - ENTITÉS JPA                                │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────┐    ┌─────────────────────────────────────────┐
│              <<Entity>>                  │    │              <<Entity>>                  │
│                USER                      │    │                GROUP                     │
├─────────────────────────────────────────┤    ├─────────────────────────────────────────┤
│ - id: UUID (PK)                         │    │ - id: UUID (PK)                         │
│ - firstName: String                     │    │ - name: String (UK)                     │
│ - lastName: String                      │    │ - label: String                         │
│ - username: String (UK)                 │    │ - description: String                   │
│ - password: String                      │    ├─────────────────────────────────────────┤
│ - email: String (UK)                    │    │ + users: List<User>      (M:N)         │
│ - avatar: String                        │    │ + layers: List<Layer>    (M:N)         │
│ - mobile: String                        │    │ + maps: List<Map>        (M:N)         │
│ - enabled: Boolean                      │    │ + permissions: List<Permission> (M:N)  │
│ - activationDate: Date                  │    └───────────────────┬─────────────────────┘
│ - desactivationDate: Date               │                        │
├─────────────────────────────────────────┤                        │ M:N
│ + groups: List<Group>       (M:N)      │◄───────────────────────┘
│ + layers: List<Layer>       (M:N)      │
│ + maps: List<Map>           (M:N)      │    ┌─────────────────────────────────────────┐
│ + entityElements: List<EntityElement>   │    │              <<Entity>>                  │
│   (M:N)                                 │    │             PERMISSION                   │
│ + notifications: List<UserNotification> │    ├─────────────────────────────────────────┤
│ + userLayerFilters: List<UserLayerFilter│    │ - id: UUID (PK)                         │
└───────────────────┬─────────────────────┘    │ - name: String (UK)                     │
                    │                          │ - label: String                         │
                    │ M:N                      ├─────────────────────────────────────────┤
                    │                          │ + groups: List<Group>    (M:N)          │
                    ▼                          └─────────────────────────────────────────┘
┌─────────────────────────────────────────┐
│              <<Entity>>                  │
│                LAYER                     │
├─────────────────────────────────────────┤
│ - id: UUID (PK)                         │
│ - name: String (UK)                     │
│ - slug: String                          │
│ - topo: String                          │
│ - identifiant: String                   │
│ - type: LayerType (ENUM)                │
│ - typeLimit: TypeLimit (ENUM)           │
├─────────────────────────────────────────┤
│ + fields: List<Field>       (1:N)      │
│ + entityElements: List<EntityElement>   │
│   (1:N)                                 │
│ + viewElement: EntityElement (1:1)      │
│ + maps: List<MapLayer>      (1:N)       │
│ + users: List<User>         (M:N)      │
│ + groups: List<Group>       (M:N)      │
│ + tags: List<Tag>           (M:N)      │
│ + notification: Notification (1:1)      │
│ + userLayerFilters: List<UserLayerFilter│
└───────────────────┬─────────────────────┘
                    │
        ┌───────────┼───────────┐
        │ 1:N       │ 1:N       │ 1:N
        ▼           ▼           ▼
┌───────────────┐ ┌───────────────────┐ ┌─────────────────────────────────┐
│  <<Entity>>   │ │    <<Entity>>     │ │         <<Entity>>               │
│    FIELD      │ │  ENTITY_ELEMENT   │ │           MAP                    │
├───────────────┤ ├───────────────────┤ ├─────────────────────────────────┤
│ - id: UUID    │ │ - id: UUID (PK)   │ │ - id: UUID (PK)                 │
│ - name: String│ │ - geom: Geometry  │ │ - name: String (UK)             │
│ - slug: String│ │   (JTS Point/     │ │ - slug: String                  │
│ - type:       │ │    Polygon/       │ │ - image: String                 │
│   FieldType   │ │    LineString)    │ │ - privacy: Privacy (ENUM)       │
│ - required:   │ │ - properties:     │ ├─────────────────────────────────┤
│   Boolean     │ │   Map<String,     │ │ + layers: List<MapLayer> (1:N)  │
│ - visible:    │ │   String> (JSONB) │ │ + users: List<User>    (M:N)    │
│   Boolean     │ │ - order: int      │ │ + groups: List<Group>  (M:N)    │
│ - publique:   │ ├───────────────────┤ │ + tags: List<Tag>      (M:N)    │
│   Boolean     │ │ + layer: Layer    │ │ + themes: List<Theme>  (1:N)    │
│ - parent: UUID│ │   (M:1)           │ └───────────────┬─────────────────┘
│ - order: int  │ │ + tags: List<Tag> │                 │
├───────────────┤ │   (M:N)          │                 │ 1:N
│ + layer: Layer│ │ + users: List<User>               │
│   (M:1)       │ │   (M:N)          │                 ▼
│ + resource:   │ └───────────────────┘    ┌───────────────────────────────┐
│   Resource    │                          │       <<Entity>>              │
│   (1:1)       │                          │         MAP_LAYER             │
└───────────────┘                          │      (Table Associative)      │
        │                                  ├───────────────────────────────┤
        │ 1:1                              │ - mapLayerId: UUID            │
        ▼                                  │ - order: int                  │
┌───────────────────┐                      │ - isVisible: Boolean          │
│    <<Entity>>     │                      ├───────────────────────────────┤
│     RESOURCE      │                      │ + id: MapLayerId (Embedded)   │
├───────────────────┤                      │ + map: Map (M:1)              │
│ - id: UUID (PK)   │                      │ + layer: Layer (M:1)          │
│ - name: String    │                      │ + styles: List<Style> (1:N)   │
│ - code: String    │                      └───────────────────────────────┘
│ - parentResource: │
│   Resource (1:1)  │    ┌─────────────────────────────────────────────┐
├───────────────────┤    │              <<Entity>>                      │
│ + fields:         │    │                 TAG                          │
│   List<Field>     │    ├─────────────────────────────────────────────┤
│ + resourceValues: │    │ - id: UUID (PK)                             │
│   List<           │    │ - name: String                              │
│   ResourceValue>  │    ├─────────────────────────────────────────────┤
└───────────────────┘    │ + layers: List<Layer>         (M:N)        │
        │                │ + maps: List<Map>             (M:N)        │
        │ 1:N            │ + entityElements: List<       (M:N)        │
        ▼                │   EntityElement>                            │
┌───────────────────┐    └─────────────────────────────────────────────┘
│    <<Entity>>     │
│  RESOURCE_VALUE   │    ┌─────────────────────────────────────────────┐
├───────────────────┤    │              ENUMÉRATIONS                    │
│ - id: UUID (PK)   │    ├─────────────────────────────────────────────┤
│ - value: String   │    │ LayerType: VECTOR | RASTER | WMS | WFS     │
│ - parent:         │    │ FieldType: NUMBER | TEXT | IMAGE | DATE |   │
│   ResourceValue   │    │            SELECT | MULTISELECT             │
├───────────────────┤    │ Privacy: PRIVATE | PUBLIC | PUBLIC_WITH_LINK│
│ + resource:       │    │            | ARCHIVED                       │
│   Resource (M:1)  │    │ TypeLimit: LAYER | CLIENT | ADMIN           │
└───────────────────┘    └─────────────────────────────────────────────┘

```

### Légende des relations

| Symbole | Signification |
|---------|---------------|
| `1:N` | One-to-Many (Un vers Plusieurs) |
| `M:N` | Many-to-Many (Plusieurs vers Plusieurs) |
| `M:1` | Many-to-One (Plusieurs vers Un) |
| `1:1` | One-to-One (Un vers Un) |
| `PK` | Primary Key (Clé Primaire) |
| `UK` | Unique Key (Clé Unique) |

---

## 3. Diagramme de Séquence - Authentification JWT

### Flux complet d'authentification avec JWT et Refresh Token

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                     FLUX D'AUTHENTIFICATION JWT COMPLET                                  │
└─────────────────────────────────────────────────────────────────────────────────────────┘

    ┌─────────┐          ┌─────────────┐          ┌────────────────┐          ┌───────────┐
    │ Client  │          │  Frontend   │          │    Backend     │          │   Redis   │
    │Navigateur│          │   Nuxt.js   │          │  Spring Boot   │          │   Cache   │
    └────┬────┘          └──────┬──────┘          └───────┬────────┘          └─────┬─────┘
         │                      │                         │                         │
         │  1. Saisie login/mdp │                         │                         │
         │─────────────────────>│                         │                         │
         │                      │                         │                         │
         │                      │  2. POST /api/v1.0/login│                         │
         │                      │  {username, password}   │                         │
         │                      │────────────────────────>│                         │
         │                      │                         │                         │
         │                      │                         │  3. Authentification    │
         │                      │                         │  AuthenticationManager  │
         │                      │                         │  ┌─────────────────────┐│
         │                      │                         │  │ DaoAuthentication   ││
         │                      │                         │  │ Provider            ││
         │                      │                         │  │ - loadUserByUsername││
         │                      │                         │  │ - verify password   ││
         │                      │                         │  │   (BCrypt)          ││
         │                      │                         │  └─────────────────────┘│
         │                      │                         │                         │
         │                      │                         │  4. Générer Tokens JWT  │
         │                      │                         │  ┌─────────────────────┐│
         │                      │                         │  │ JwtUtils            ││
         │                      │                         │  │ - generateJwtToken()││
         │                      │                         │  │ - HS512 Signature   ││
         │                      │                         │  │ - Claims: sub, iat, ││
         │                      │                         │  │   exp               ││
         │                      │                         │  └─────────────────────┘│
         │                      │                         │                         │
         │                      │                         │  5. Stocker Token dans  │
         │                      │                         │     Redis pour invalid. │
         │                      │                         │────────────────────────>│
         │                      │                         │  SADD username: token   │
         │                      │                         │                         │
         │                      │                         │  6. Créer UserLog       │
         │                      │                         │  ┌─────────────────────┐│
         │                      │                         │  │ - loginDate         ││
         │                      │                         │  │ - browserName       ││
         │                      │                         │  │ - clientOS          ││
         │                      │                         │  │ - userIp            ││
         │                      │                         │  └─────────────────────┘│
         │                      │                         │                         │
         │                      │  7. Response 200 OK     │                         │
         │                      │  {accessToken,          │                         │
         │                      │   refreshToken}         │                         │
         │                      │<────────────────────────│                         │
         │                      │                         │                         │
         │  8. Stocker tokens   │                         │                         │
         │     localStorage     │                         │                         │
         │<─────────────────────│                         │                         │
         │                      │                         │                         │
         │                      │                         │                         │
    ════════════════════════════════════════════════════════════════════════════════════════
         │                      │                         │                         │
         │  9. Requête API      │                         │                         │
         │     avec Token       │                         │                         │
         │─────────────────────>│                         │                         │
         │                      │                         │                         │
         │                      │  10. GET /api/v1.0/xxx  │                         │
         │                      │  Header: Authorization  │                         │
         │                      │  Bearer <accessToken>   │                         │
         │                      │────────────────────────>│                         │
         │                      │                         │                         │
         │                      │                         │  11. AuthTokenFilter    │
         │                      │                         │  ┌─────────────────────┐│
         │                      │                         │  │ - parseJwt()        ││
         │                      │                         │  │ - validateJwtToken()││
         │                      │                         │  └─────────────────────┘│
         │                      │                         │                         │
         │                      │                         │  12. Vérifier Redis     │
         │                      │                         │────────────────────────>│
         │                      │                         │  SISMEMBER username:tok │
         │                      │                         │<────────────────────────│
         │                      │                         │  true/false             │
         │                      │                         │                         │
         │                      │                         │  13. Charger UserDetails│
         │                      │                         │  et définir SecurityCtx │
         │                      │                         │                         │
         │                      │  14. Response Data      │                         │
         │                      │<────────────────────────│                         │
         │                      │                         │                         │
         │  15. Données         │                         │                         │
         │<─────────────────────│                         │                         │
         │                      │                         │                         │
    ════════════════════════════════════════════════════════════════════════════════════════
         │                      │                         │                         │
         │  16. Token Expiré    │                         │                         │
         │      (401 Error)     │                         │                         │
         │<─────────────────────│                         │                         │
         │                      │                         │                         │
         │                      │  17. POST /api/v1.0/    │                         │
         │                      │      refresh            │                         │
         │                      │  {refreshToken,         │                         │
         │                      │   username}             │                         │
         │                      │────────────────────────>│                         │
         │                      │                         │                         │
         │                      │                         │  18. Valider RefreshTok │
         │                      │                         │  ┌─────────────────────┐│
         │                      │                         │  │ - vérifier expirat. ││
         │                      │                         │  │ - vérifier utilisateur│
         │                      │                         │  │   actif             ││
         │                      │                         │  └─────────────────────┘│
         │                      │                         │                         │
         │                      │                         │  19. Générer nouveaux   │
         │                      │                         │      tokens             │
         │                      │                         │────────────────────────>│
         │                      │                         │                         │
         │                      │  20. Nouveaux tokens    │                         │
         │                      │<────────────────────────│                         │
         │                      │                         │                         │
         │  21. Mise à jour     │                         │                         │
         │      localStorage    │                         │                         │
         │<─────────────────────│                         │                         │
         │                      │                         │                         │
    ════════════════════════════════════════════════════════════════════════════════════════
         │                      │                         │                         │
         │  22. Logout          │                         │                         │
         │─────────────────────>│                         │                         │
         │                      │                         │                         │
         │                      │  23. POST /api/v1.0/    │                         │
         │                      │      logout             │                         │
         │                      │────────────────────────>│                         │
         │                      │                         │                         │
         │                      │                         │  24. Invalider Token    │
         │                      │                         │────────────────────────>│
         │                      │                         │  SREM username: token   │
         │                      │                         │                         │
         │                      │                         │  25. Mettre à jour      │
         │                      │                         │      UserLog (logoutDate)│
         │                      │                         │                         │
         │                      │  26. Response 200 OK    │                         │
         │                      │<────────────────────────│                         │
         │                      │                         │                         │
         │  27. Nettoyer        │                         │                         │
         │      localStorage    │                         │                         │
         │<─────────────────────│                         │                         │
         │                      │                         │                         │
         ▼                      ▼                         ▼                         ▼

```

### Détails des Tokens JWT

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                           STRUCTURE DU TOKEN JWT                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘

                            ┌─────────────────────────────────┐
                            │          JWT TOKEN               │
                            │   (JSON Web Token)               │
                            └─────────────────────────────────┘
                                          │
                    ┌─────────────────────┼─────────────────────┐
                    │                     │                     │
                    ▼                     ▼                     ▼
        ┌───────────────────┐ ┌───────────────────┐ ┌───────────────────┐
        │      HEADER       │ │     PAYLOAD       │ │    SIGNATURE      │
        │    (Base64Url)    │ │   (Base64Url)     │ │    (Base64Url)    │
        ├───────────────────┤ ├───────────────────┤ ├───────────────────┤
        │ {                 │ │ {                 │ │ HMACSHA512(       │
        │  "alg": "HS512",  │ │  "sub": "admin",  │ │   base64UrlEncode│
        │  "typ": "JWT"     │ │  "iat": 1706654400│ │   (header) + "."+│
        │ }                 │ │  "exp": 1706740800│ │   base64UrlEncode│
        └───────────────────┘ │ }                 │ │   (payload),      │
                              └───────────────────┘ │   secretKey       │
                                                    │ )                 │
                                                    └───────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                          CONFIGURATION JWT                                               │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│  • Algorithme: HS512 (HMAC SHA-512)                                                     │
│  • Access Token Expiration: Configurable (jwtExpirationMs)                              │
│  • Refresh Token Expiration: Configurable (jwtRefreshExpirationMs)                      │
│  • Secret Key: Chargé depuis global.properties (min 256 bits pour HS512)               │
│  • Stockage: Redis (pour invalidation immédiate)                                        │
│  • Transport: Header Authorization: Bearer <token>                                      │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 4. Diagramme de Séquence - Création et Affichage d'une Carte

### Flux complet de création et visualisation d'une carte

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│               FLUX DE CRÉATION ET AFFICHAGE D'UNE CARTE                                  │
└─────────────────────────────────────────────────────────────────────────────────────────┘

    ┌─────────┐    ┌──────────┐    ┌────────────┐    ┌────────────┐    ┌────────────────┐
    │ Client  │    │ Frontend │    │MapController│   │ MapService │    │   PostgreSQL   │
    │         │    │  Nuxt.js │    │            │    │            │    │   + PostGIS    │
    └────┬────┘    └────┬─────┘    └─────┬──────┘    └─────┬──────┘    └───────┬────────┘
         │              │                │                 │                   │
    ══════════════════════════════ PHASE 1: CRÉATION D'UNE CARTE ═════════════════════════
         │              │                │                 │                   │
         │ 1. Créer nouvelle carte       │                 │                   │
         │──────────────>│                │                 │                   │
         │              │                │                 │                   │
         │              │ 2. POST /api/v1.0/maps           │                   │
         │              │ Header: Authorization            │                   │
         │              │ Body: MapDto {                   │                   │
         │              │   name: "Carte Infrastructure",  │                   │
         │              │   privacy: "PRIVATE",            │                   │
         │              │   layers: [],                    │                   │
         │              │   themes: []                     │                   │
         │              │ }                                │                   │
         │              │───────────────>│                 │                   │
         │              │                │                 │                   │
         │              │                │ 3. Vérifier权限  │                   │
         │              │                │ @PreAuthorize   │                   │
         │              │                │ MAP_CREATE_AUTH │                   │
         │              │                │                 │                   │
         │              │                │ 4. createMap(dto)│                   │
         │              │                │────────────────>│                   │
         │              │                │                 │                   │
         │              │                │                 │ 5. Valider données│
         │              │                │                 │    - name unique  │
         │              │                │                 │    - privacy valid│
         │              │                │                 │                   │
         │              │                │                 │ 6. Créer entité Map│
         │              │                │                 │──────────────────>│
         │              │                │                 │                   │
         │              │                │                 │ 7. INSERT INTO map │
         │              │                │                 │<──────────────────│
         │              │                │                 │                   │
         │              │                │                 │ 8. Générer slug   │
         │              │                │                 │                   │
         │              │                │ 9. MapSimpleDto │                   │
         │              │                │<────────────────│                   │
         │              │                │                 │                   │
         │              │ 10. Response 200 OK              │                   │
         │              │ { id, name, slug, ... }          │                   │
         │              │<───────────────│                 │                   │
         │              │                │                 │                   │
         │ 11. Redirection vers viewer   │                 │                   │
         │<──────────────│                │                 │                   │
         │              │                │                 │                   │
    ══════════════════════════════ PHASE 2: AJOUT DE COUCHES ════════════════════════════
         │              │                │                 │                   │
         │ 12. Ajouter couches à la carte │                │                   │
         │──────────────>│                │                 │                   │
         │              │                │                 │                   │
         │              │ 13. POST /api/v1.0/maps/attach   │                   │
         │              │ Body: [MapLayerDto {             │                   │
         │              │   mapId: "uuid-map",             │                   │
         │              │   layerId: "uuid-layer",         │                   │
         │              │   order: 1,                      │                   │
         │              │   isVisible: true                │                   │
         │              │ }]                               │                   │
         │              │───────────────>│                 │                   │
         │              │                │                 │                   │
         │              │                │ 14. attachLayers│                   │
         │              │                │    ToMap()      │                   │
         │              │                │────────────────>│                   │
         │              │                │                 │                   │
         │              │                │                 │ 15. Créer MapLayer│
         │              │                │                 │     entrées       │
         │              │                │                 │──────────────────>│
         │              │                │                 │                   │
         │              │                │                 │ 16. INSERT INTO   │
         │              │                │                 │     map_layers    │
         │              │                │                 │<──────────────────│
         │              │                │                 │                   │
         │              │                │                 │ 17. Créer Style   │
         │              │                │                 │     par défaut    │
         │              │                │                 │──────────────────>│
         │              │                │                 │                   │
         │              │                │ 18. Layers avec │                   │
         │              │                │     fields      │                   │
         │              │                │<────────────────│                   │
         │              │                │                 │                   │
         │              │ 19. Response 200 OK              │                   │
         │              │<───────────────│                 │                   │
         │              │                │                 │                   │
    ══════════════════════════════ PHASE 3: AFFICHAGE DE LA CARTE ═══════════════════════
         │              │                │                 │                   │
         │ 20. Naviguer vers /dashboard/viewer?id=mapId     │                   │
         │──────────────>│                │                 │                   │
         │              │                │                 │                   │
         │              │ 21. GET /api/v1.0/maps/{mapId}    │                   │
         │              │───────────────>│                 │                   │
         │              │                │                 │                   │
         │              │                │ 22. findById()  │                   │
         │              │                │────────────────>│                   │
         │              │                │                 │                   │
         │              │                │                 │ 23. SELECT FROM   │
         │              │                │                 │     map WHERE id  │
         │              │                │                 │<──────────────────│
         │              │                │                 │                   │
         │              │                │ 24. MapDto      │                   │
         │              │                │<────────────────│                   │
         │              │                │                 │                   │
         │              │ 25. Response   │                 │                   │
         │              │<───────────────│                 │                   │
         │              │                │                 │                   │
         │              │ 26. GET /api/v1.0/maps/          │                   │
         │              │     layersWithFields/{mapId}     │                   │
         │              │───────────────>│                 │                   │
         │              │                │                 │                   │
         │              │                │ 27. getLayers   │                   │
         │              │                │     SimpleWith  │                   │
         │              │                │     Fields()    │                   │
         │              │                │────────────────>│                   │
         │              │                │                 │                   │
         │              │                │                 │ 28. JOIN query    │
         │              │                │                 │     map_layers,   │
         │              │                │                 │     layer, field  │
         │              │                │                 │──────────────────>│
         │              │                │                 │                   │
         │              │                │                 │ 29. Data with     │
         │              │                │                 │     geometry      │
         │              │                │                 │<──────────────────│
         │              │                │                 │                   │
         │              │                │ 30. List<Layer  │                   │
         │              │                │     SimpleWith  │                   │
         │              │                │     FieldsDto>  │                   │
         │              │                │<────────────────│                   │
         │              │                │                 │                   │
         │              │ 31. Response avec couches        │                   │
         │              │<───────────────│                 │                   │
         │              │                │                 │                   │
    ══════════════════════════════ PHASE 4: RENDU LEAFLET ═══════════════════════════════
         │              │                │                 │                   │
         │              │ 32. Initialiser Leaflet Map      │                   │
         │              │ ┌────────────────────────────┐   │                   │
         │              │ │ new L.Map('map')           │   │                   │
         │              │ │ .setView([lat, lng], zoom) │   │                   │
         │              │ └────────────────────────────┘   │                   │
         │              │                │                 │                   │
         │              │ 33. Pour chaque couche:         │                   │
         │              │ ┌────────────────────────────┐   │                   │
         │              │ │ - Créer WMS Layer          │   │                   │
         │              │ │ - Ou GeoJSON Layer         │   │                   │
         │              │ │ - Appliquer Style (SLD)    │   │                   │
         │              │ │ - Ajouter à map.addLayer() │   │                   │
         │              │ └────────────────────────────┘   │                   │
         │              │                │                 │                   │
         │ 34. Carte affichée avec toutes les couches      │                   │
         │<──────────────│                │                 │                   │
         │              │                │                 │                   │
         ▼              ▼                ▼                 ▼                   ▼

```

### Architecture de la carte dans Leaflet

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                      COMPOSANTS LEAFLET DANS LE FRONTEND                                 │
└─────────────────────────────────────────────────────────────────────────────────────────┘

                           ┌─────────────────────────────────┐
                           │        Vue Map Component        │
                           │     (components/map/index.vue)  │
                           └────────────────┬────────────────┘
                                            │
            ┌───────────────────────────────┼───────────────────────────────┐
            │                               │                               │
            ▼                               ▼                               ▼
┌───────────────────────┐   ┌───────────────────────┐   ┌───────────────────────┐
│    Base Layers        │   │    Overlay Layers     │   │    Controls           │
│  (Fond de carte)      │   │  (Couches métier)     │   │  (Contrôles)          │
├───────────────────────┤   ├───────────────────────┤   ├───────────────────────┤
│ • OpenStreetMap       │   │ • WMS Layers          │   │ • Zoom Control        │
│ • Google Maps         │   │   (GeoServer)         │   │ • Layer Control       │
│ • Bing Maps           │   │ • GeoJSON Layers      │   │ • Scale Control       │
│ • Custom Tiles        │   │   (EntityElement)     │   │ • Attribution         │
│                       │   │ • WFS Layers          │   │ • Draw Control        │
│                       │   │ • Marker Clusters     │   │ • Edit Control        │
│                       │   │ • Heatmaps            │   │ • Measure Control     │
└───────────────────────┘   └───────────────────────┘   └───────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                         TYPES DE COUCHES SUPPORTÉES                                      │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐                     │
│  │   WMS Layer     │    │  GeoJSON Layer  │    │   WFS Layer     │                     │
│  ├─────────────────┤    ├─────────────────┤    ├─────────────────┤                     │
│  │ Source:         │    │ Source:         │    │ Source:         │                     │
│  │ GeoServer WMS   │    │ API REST        │    │ GeoServer WFS   │                     │
│  │                 │    │ /entityelements │    │                 │                     │
│  │ Format:         │    │                 │    │ Format:         │                     │
│  │ Image (PNG)     │    │ Format:         │    │ GML/GeoJSON     │                     │
│  │                 │    │ GeoJSON         │    │                 │                     │
│  │ Style:          │    │                 │    │ Style:          │                     │
│  │ SLD (via param) │    │ Style:          │    │ SLD (via param) │                     │
│  │                 │    │ JS Function     │    │                 │                     │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘                     │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 5. Diagramme de Déploiement Docker

### Infrastructure Docker complète

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                    ARCHITECTURE DE DÉPLOIEMENT DOCKER                                    │
│                           docker-compose.yml                                             │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              HÔTE DOCKER (Host Machine)                                  │
│                                                                                         │
│  ┌───────────────────────────────────────────────────────────────────────────────────┐  │
│  │                           RÉSEAU: sig-network                                      │  │
│  │                               (Driver: bridge)                                     │  │
│  │                                                                                    │  │
│  │  ┌─────────────────────────────────────────────────────────────────────────────┐  │  │
│  │  │                                                                             │  │  │
│  │  │  ┌─────────────────────┐    ┌─────────────────────┐    ┌─────────────────┐  │  │  │
│  │  │  │   CONTAINER:        │    │   CONTAINER:        │    │   CONTAINER:    │  │  │  │
│  │  │  │   sig_frontend      │    │   sig_backend       │    │   sig_postgres  │  │  │  │
│  │  │  │                     │    │                     │    │                 │  │  │  │
│  │  │  │  ┌───────────────┐  │    │  ┌───────────────┐  │    │ ┌─────────────┐ │  │  │  │
│  │  │  │  │   Nginx       │  │    │  │  Tomcat 10    │  │    │ │ PostgreSQL  │ │  │  │  │
│  │  │  │  │   (Port 8080) │  │    │  │  Embedded     │  │    │ │ 15          │ │  │  │  │
│  │  │  │  └───────────────┘  │    │  │  (Port 8080)  │  │    │ │             │ │  │  │  │
│  │  │  │         │           │    │  └───────────────┘  │    │ │ ┌─────────┐ │ │  │  │  │
│  │  │  │  ┌───────────────┐  │    │         │           │    │ │ │ PostGIS │ │ │  │  │  │
│  │  │  │  │   Nuxt.js     │  │    │  ┌───────────────┐  │    │ │ │ 3.3     │ │ │  │  │  │
│  │  │  │  │   Static      │  │    │  │ Spring Boot   │  │    │ │ └─────────┘ │ │  │  │  │
│  │  │  │  │   Files       │  │    │  │ 3.2.x         │  │    │ └─────────────┘ │  │  │  │
│  │  │  │  └───────────────┘  │    │  │               │  │    │        │        │  │  │  │
│  │  │  │                     │    │  │ ┌───────────┐ │  │    │        │        │  │  │  │
│  │  │  │  Image:             │    │  │ │ Hibernate │ │  │    │ ┌─────────────┐ │  │  │  │
│  │  │  │  sig_frontend:      │    │  │ │ 6.x       │ │  │    │ │ Schema: sig │ │  │  │  │
│  │  │  │  latest             │    │  │ └───────────┘ │  │    │ │             │ │  │  │  │
│  │  │  │                     │    │  │               │  │    │ │ Tables:     │ │  │  │  │
│  │  │  │  Build:             │    │  │ ┌───────────┐ │  │    │ │ • user      │ │  │  │  │
│  │  │  │  ./sig_frontend/    │    │  │ │ Jakarta EE│ │  │    │ │ • layer     │ │  │  │  │
│  │  │  │  Dockerfile         │    │  │ │ 10        │ │  │    │ │ • map       │ │  │  │  │
│  │  │  │                     │    │  │ └───────────┘ │  │    │ │ • field     │ │  │  │  │
│  │  │  │  Env:               │    │  │               │  │    │ │ • group     │ │  │  │  │
│  │  │  │  NODE_ENV=prod      │    │  │ ┌───────────┐ │  │    │ │ • etc...    │ │  │  │  │
│  │  │  │  API_BASE_URL=      │    │  │ │ JDK 17/21 │ │  │    │ └─────────────┘ │  │  │  │
│  │  │  │  http://backend:8080│    │  │ └───────────┘ │  │    │                 │  │  │  │
│  │  │  │                     │    │  │               │  │    │ Image:          │  │  │  │
│  │  │  │  Ports:             │    │  │ Image:        │  │    │ postgis/        │  │  │  │
│  │  │  │  "80:8080"          │    │  │ sig_backend:  │  │    │ postgis:15-3.3  │  │  │  │
│  │  │  │                     │    │  │ latest        │  │    │                 │  │  │  │
│  │  │  │  Health:            │    │  │               │  │    │ Ports:          │  │  │  │
│  │  │  │  wget localhost:8080│    │  │ Build:        │  │    │ "5432:5432"     │  │  │  │
│  │  │  │                     │    │  │ ./sig_backend/│  │    │ (internal only) │  │  │  │
│  │  │  │  Depends:           │    │  │ Dockerfile    │  │    │                 │  │  │  │
│  │  │  │  backend (healthy)  │    │  │               │  │    │ Env:            │  │  │  │
│  │  │  │                     │    │  │ Env:          │  │    │ POSTGRES_DB     │  │  │  │
│  │  │  └─────────────────────┘    │  │ SPRING_       │  │    │ POSTGRES_USER   │  │  │  │
│  │  │                             │  │ PROFILE=docker│  │    │ POSTGRES_PWD    │  │  │  │
│  │  │                             │  │               │  │    │ PGDATA          │  │  │  │
│  │  │                             │  │ JAVA_OPTS=    │  │    │                 │  │  │  │
│  │  │                             │  │ -Xms512m      │  │    │ Volume:         │  │  │  │
│  │  │                             │  │ -Xmx1024m     │  │    │ postgres_data   │  │  │  │
│  │  │                             │  │               │  │    │                 │  │  │  │
│  │  │                             │  │ Ports:        │  │    │ Health:         │  │  │  │
│  │  │                             │  │ "8080:8080"   │  │    │ pg_isready      │  │  │  │
│  │  │                             │  │               │  │    │                 │  │  │  │
│  │  │                             │  │ Health:       │  │    │ Depends:        │  │  │  │
│  │  │                             │  │ curl actuator │  │    │ (none)          │  │  │  │
│  │  │                             │  │ /health       │  │    │                 │  │  │  │
│  │  │                             │  │               │  │    └─────────────────┘  │  │  │
│  │  │                             │  │ Depends:      │  │           ▲            │  │  │
│  │  │                             │  │ postgres      │──┼───────────┘            │  │  │
│  │  │                             │  │ (healthy)     │  │                        │  │  │
│  │  │                             │  └───────────────┘  │                        │  │  │
│  │  │                             │         │           │                        │  │  │
│  │  │                             │         │           │                        │  │  │
│  │  │                             └─────────┼───────────┘                        │  │  │
│  │  │                                       │                                    │  │  │
│  │  └───────────────────────────────────────┼────────────────────────────────────┘  │  │
│  │                                          │                                        │  │
│  └──────────────────────────────────────────┼────────────────────────────────────────┘  │
│                                             │                                            │
└─────────────────────────────────────────────┼────────────────────────────────────────────┘
                                              │
                    ┌─────────────────────────┴─────────────────────────┐
                    │                                                   │
                    ▼                                                   ▼
        ┌───────────────────────┐                         ┌───────────────────────┐
        │      VOLUMES          │                         │   SERVICES EXTERNES    │
        │  (Persistent Storage) │                         │   (Optionnels)         │
        ├───────────────────────┤                         ├───────────────────────┤
        │ postgres_data:        │                         │ • GeoServer (WMS/WFS) │
        │   /var/lib/postgresql │                         │   Port: 8080          │
        │   /data               │                         │                       │
        │                       │                         │ • Redis (Cache JWT)   │
        │ (Docker Volume Local) │                         │   Port: 6379          │
        └───────────────────────┘                         │                       │
                                                          │ • SMTP Server (Mail)  │
                                                          │   Port: 25/587        │
                                                          └───────────────────────┘

```

### Commandes Docker utiles

```bash
# Démarrer tous les services
docker-compose up -d

# Voir les logs
docker-compose logs -f backend
docker-compose logs -f frontend

# Reconstruire les images
docker-compose build --no-cache

# Arrêter tous les services
docker-compose down

# Supprimer les volumes
docker-compose down -v
```

---

## 6. Diagramme de Flux de Données Géospatiales

### Flux des données géographiques (GeoJSON, Shapefile, WMS, WFS)

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                    FLUX DE DONNÉES GÉOSPATIALES                                          │
└─────────────────────────────────────────────────────────────────────────────────────────┘

                            ┌─────────────────────────────────┐
                            │       SOURCES DE DONNÉES        │
                            └────────────────┬────────────────┘
                                             │
        ┌────────────────────────────────────┼────────────────────────────────────┐
        │                                    │                                    │
        ▼                                    ▼                                    ▼
┌───────────────────┐            ┌───────────────────┐            ┌───────────────────┐
│    FICHIERS       │            │   BASE DE DONNÉES │            │   SERVICES OGC    │
│    LOCAUX         │            │   POSTGIS         │            │   EXTERNES        │
├───────────────────┤            ├───────────────────┤            ├───────────────────┤
│ • GeoJSON (.json) │            │ • Tables avec     │            │ • WMS Services    │
│ • Shapefile (.shp)│            │   géométries      │            │ • WFS Services    │
│ • KML (.kml)      │            │   (Point, Line,   │            │ • TMS Services    │
│ • GML (.gml)      │            │   Polygon)        │            │ • XYZ Tiles       │
│ • CSV (coords)    │            │ • Spatial Index   │            │                   │
└─────────┬─────────┘            │ • Spatial Queries │            └─────────┬─────────┘
          │                      └─────────┬─────────┘                      │
          │                                │                                │
          ▼                                ▼                                ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                 COUCHE INTÉGRATION                                       │
│  ┌────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                            UTILITAIRES DE LECTURE                                   │ │
│  │  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐  │ │
│  │  │ GeoJsonReader   │ │ ShapeFileReader │ │ KMLReader       │ │ GML3_2Reader    │  │ │
│  │  │                 │ │                 │ │                 │ │                 │  │ │
│  │  │ • parse()       │ │ • read()        │ │ • parse()       │ │ • parse()       │  │ │
│  │  │ • geometryToJTS │ │ • toJTS()       │ │ • toJTS()       │ │ • toJTS()       │  │ │
│  │  └─────────────────┘ └─────────────────┘ └─────────────────┘ └─────────────────┘  │ │
│  └────────────────────────────────────────────────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                          SERVICE GeoToolsServiceImpl                               │ │
│  │  ┌─────────────────────────────────────────────────────────────────────────────┐  │ │
│  │  │ • Lecture/Écriture de fichiers géospatiaux                                   │  │ │
│  │  │ • Conversion de formats                                                      │  │ │
│  │  │ • Opérations géométriques (buffer, intersection, union...)                  │  │ │
│  │  │ • Validation de géométries                                                   │  │ │
│  │  └─────────────────────────────────────────────────────────────────────────────┘  │ │
│  └────────────────────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────────────────┘
                                             │
                                             │ EntityElement (Geometry JTS)
                                             ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                   STOCKAGE                                               │
│  ┌────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                          ENTITY_ELEMENT TABLE                                      │ │
│  │  ┌─────────────────────────────────────────────────────────────────────────────┐  │ │
│  │  │  id (UUID)  │ layer_id │ geom (Geometry) │ properties (JSONB)               │  │ │
│  │  ├─────────────┼──────────┼─────────────────┼──────────────────────────────────┤  │ │
│  │  │  uuid-1     │ uuid-l1  │ POINT(3.5 36.7) │ {"name": "MSAN 1", "cap": 100}   │  │ │
│  │  │  uuid-2     │ uuid-l1  │ LINESTRING(...) │ {"name": "Fibre 1", "long": 5km} │  │ │
│  │  │  uuid-3     │ uuid-l2  │ POLYGON(...)    │ {"name": "Zone 1", "area": 2ha}  │  │ │
│  │  └─────────────────────────────────────────────────────────────────────────────┘  │ │
│  └────────────────────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────────────────┘
                                             │
                                             │ Query / Export
                                             ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                  COUCHE EXPORT                                           │
│  ┌────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                            UTILITAIRES D'ÉCRITURE                                   │ │
│  │  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐  │ │
│  │  │ GeoJsonWriter   │ │ ShapeFileWriter │ │ KMLWriter       │ │ GML3Writer      │  │ │
│  │  │                 │ │                 │ │                 │ │                 │  │ │
│  │  │ • write()       │ │ • write()       │ │ • write()       │ │ • write()       │  │ │
│  │  │ • toGeoJSON     │ │ • toShapefile   │ │ • toKML         │ │ • toGML         │  │ │
│  │  └─────────────────┘ └─────────────────┘ └─────────────────┘ └─────────────────┘  │ │
│  │  ┌─────────────────┐ ┌─────────────────┐                                          │ │
│  │  │ ExcelFileWriter │ │ CSVFileWriter   │                                          │ │
│  │  │                 │ │                 │                                          │ │
│  │  │ • exportXLSX()  │ │ • exportCSV()   │                                          │ │
│  │  └─────────────────┘ └─────────────────┘                                          │ │
│  └────────────────────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────────────────┘
                                             │
                                             ▼
                            ┌─────────────────────────────────┐
                            │         CLIENT FINAL            │
                            │   (Téléchargement/Affichage)    │
                            └─────────────────────────────────┘

```

### Services OGC (WMS/WFS) via GeoServer

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                     INTÉGRATION GEOSERVER (WMS/WFS)                                      │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────┐    ┌───────────────────────────────────────┐
│              BACKEND                      │    │            GEOSERVER                   │
│           Spring Boot                     │    │         (Port 8080)                    │
├───────────────────────────────────────────┤    ├───────────────────────────────────────┤
│                                           │    │                                       │
│  GeoServerController                      │    │  Workspace: sig_workspace             │
│  ┌─────────────────────────────────────┐  │    │  ┌─────────────────────────────────┐ │
│  │ • getWmsUrl(layerSlug, params)      │  │    │  │ DataStore:                      │ │
│  │ • createSqlView(layer)              │  │    │  │ • Connection to PostgreSQL      │ │
│  │ • publishLayer(layer)               │  │    │  │ • PostGIS enabled               │ │
│  │ • updateLayerStyle(sld)             │  │    │  │ • Schema: sig                   │ │
│  └─────────────────────────────────────┘  │    │  └─────────────────────────────────┘ │
│                                           │    │                                       │
│  GeoServerRest                            │    │  Layers Published:                    │
│  ┌─────────────────────────────────────┐  │    │  ┌─────────────────────────────────┐ │
│  │ • createWorkspace()                 │  │    │  │ • layer_msan (Point)            │ │
│  │ • createDataStore()                 │  │    │  │ • layer_fibre (LineString)      │ │
│  │ • publishLayer()                    │  │    │  │ • layer_zone (Polygon)          │ │
│  │ • uploadStyle(sld)                  │  │    │  └─────────────────────────────────┘ │
│  └─────────────────────────────────────┘  │    │                                       │
│                                           │    │  Services OGC:                        │
│  SLDGeneratorImpl                         │    │  ┌─────────────────────────────────┐ │
│  ┌─────────────────────────────────────┐  │    │  │ WMS (Web Map Service):          │ │
│  │ • generateSimpleStyle()             │  │    │  │   /wms?SERVICE=WMS&             │ │
│  │ • generateGraduatedStyle()          │  │    │  │   REQUEST=GetMap&               │ │
│  │ • generateClassifiedStyle()         │  │    │  │   LAYERS=layer_msan&            │ │
│  │ • generateHeatMapStyle()            │  │    │  │   SRS=EPSG:4326&                │ │
│  │ • generateClusterStyle()            │  │    │  │   BBOX=...&                     │ │
│  └─────────────────────────────────────┘  │    │  │   WIDTH=800&HEIGHT=600&         │ │
│                                           │    │  │   FORMAT=image/png               │ │
│                                           │    │  │                                  │ │
│                                           │    │  │ WFS (Web Feature Service):      │ │
│                                           │    │  │   /wfs?SERVICE=WFS&             │ │
│                                           │    │  │   REQUEST=GetFeature&           │ │
│                                           │    │  │   TYPENAME=layer_msan&          │ │
│                                           │    │  │   OUTPUTFORMAT=application/json │ │
│                                           │    │  └─────────────────────────────────┘ │
└───────────────────────────────────────────┘    └───────────────────────────────────────┘
                          │                                        │
                          │ REST API                               │ OGC Services
                          └────────────────────────────────────────┘
                                             │
                                             ▼
                              ┌─────────────────────────────┐
                              │        FRONTEND             │
                              │      (Leaflet.js)           │
                              ├─────────────────────────────┤
                              │ L.tileLayer.wms(            │
                              │   geoserverUrl + '/wms',    │
                              │   {                         │
                              │     layers: 'workspace:lyr',│
                              │     format: 'image/png',    │
                              │     transparent: true,      │
                              │     sld: sldUrl             │
                              │   }                         │
                              │ )                           │
                              └─────────────────────────────┘

```

---

## 7. Diagramme ERD - Modèle de Données

### Schéma de la base de données PostgreSQL/PostGIS

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                      MODÈLE DE DONNÉES - SCHEMA "sig"                                    │
│                        PostgreSQL 15 + PostGIS 3.3                                       │
└─────────────────────────────────────────────────────────────────────────────────────────┘

                                    ┌─────────────────────────┐
                                    │        USERS            │
                                    │    (Utilisateurs)       │
                                    ├─────────────────────────┤
                                    │ PK  id              UUID│
                                    │ UK  user_name   VARCHAR │
                                    │ UK  email        VARCHAR │
                                    │     first_name  VARCHAR │
                                    │     last_name   VARCHAR │
                                    │     password    VARCHAR │
                                    │     avatar      VARCHAR │
                                    │     mobile      VARCHAR │
                                    │     enabled     BOOLEAN │
                                    │     activation_date     │
                                    │     desactivation_date  │
                                    └───────────┬─────────────┘
                                                │
                    ┌───────────────────────────┼───────────────────────────┐
                    │                           │                           │
                    │ M:N                       │ M:N                       │ M:N
                    ▼                           ▼                           ▼
     ┌─────────────────────────┐  ┌─────────────────────────┐  ┌─────────────────────────┐
     │       GROUPS            │  │       LAYERS            │  │        MAPS             │
     │    (Groupes)            │  │    (Couches)            │  │     (Cartes)            │
     ├─────────────────────────┤  ├─────────────────────────┤  ├─────────────────────────┤
     │ PK  id              UUID│  │ PK  id              UUID│  │ PK  id              UUID│
     │ UK  name        VARCHAR │  │ UK  name        VARCHAR │  │ UK  name        VARCHAR │
     │     label       VARCHAR │  │     slug        VARCHAR │  │     slug        VARCHAR │
     │     description VARCHAR │  │     topo        VARCHAR │  │     image       VARCHAR │
     └───────────┬─────────────┘  │     identifiant VARCHAR │  │     privacy     ENUM    │
                 │                │     type         ENUM   │  │     (PRIVATE/PUBLIC/   │
                 │                │     type_limit   ENUM   │  │      PUBLIC_WITH_LINK/ │
                 │                └───────────┬─────────────┘  │      ARCHIVED)          │
                 │                            │                └───────────┬─────────────┘
                 │ M:N                        │                            │
                 │                            │                            │
                 ▼                            │                            │
     ┌─────────────────────────┐              │                            │
     │     PERMISSIONS         │              │                            │
     │    (Permissions)        │              │                            │
     ├─────────────────────────┤              │                            │
     │ PK  id              UUID│              │                            │
     │ UK  name        VARCHAR │              │                            │
     │     label       VARCHAR │◄─────────────┘                            │
     └─────────────────────────┘  M:N                                     │
                                                                          │
                                                                          │
     ┌─────────────────────────┐              │              ┌─────────────────────────┐
     │        FIELD            │              │              │      MAP_LAYERS         │
     │    (Champs)             │              │              │   (Table associative)   │
     ├─────────────────────────┤              │              ├─────────────────────────┤
     │ PK  id              UUID│              │              │ PK  mapLayerId     UUID │
     │ FK  layer_id       UUID│◄─────────────┘              │ FK  maps_id        UUID │
     │     name        VARCHAR │              │              │ FK  layers_id      UUID │
     │ UK  slug        VARCHAR │              │              │     layer_order    INT  │
     │     type         ENUM   │              │              │     isVisible   BOOLEAN │
     │     required   BOOLEAN │              │              └───────────┬─────────────┘
     │     visible   BOOLEAN │              │                          │
     │     publique  BOOLEAN │              │                          │ 1:N
     │     parent_id     UUID │              │                          │
     │     field_order   INT  │              │                          ▼
     │ FK  resource_id  UUID │              │              ┌─────────────────────────┐
     └───────────┬─────────────┘              │              │        STYLES           │
                 │                            │              │    (Styles SLD)         │
                 │ 1:1                        │              ├─────────────────────────┤
                 ▼                            │              │ PK  id              UUID │
     ┌─────────────────────────┐              │              │ FK  map_layer_id   UUID │
     │       RESOURCE          │              │              │     name        VARCHAR │
     │    (Ressources)         │              │              │     style        TEXT   │
     ├─────────────────────────┤              │              │     type         ENUM   │
     │ PK  id              UUID│              │              │     (SIMPLE/GRADUATED/ │
     │ UK  name        VARCHAR │              │              │      CLASSIFIED/etc...) │
     │     code        VARCHAR │              │              └─────────────────────────┘
     │ FK  parent_resource_id │              │
     └───────────┬─────────────┘              │                          │
                 │                            │                          │
                 │ 1:N                        │                          │
                 ▼                            │                          │
     ┌─────────────────────────┐              │                          │
     │    RESOURCE_VALUE       │              │                          │
     │   (Valeurs ressource)   │              │                          │
     ├─────────────────────────┤              │                          │
     │ PK  id              UUID│              │                          │
     │ FK  resource_id   UUID │              │                          │
     │     value       VARCHAR │              │                          │
     │ FK  parent_id      UUID │              │                          │
     └─────────────────────────┘              │                          │
                                              │                          │
                                              │                          │
     ┌─────────────────────────┐              │              ┌─────────────────────────┐
     │    ENTITY_ELEMENT       │◄─────────────┘              │        THEMES           │
     │  (Entités géographiques)│              │              │     (Thèmes)            │
     ├─────────────────────────┤              │              ├─────────────────────────┤
     │ PK  id              UUID│              │              │ PK  id              UUID │
     │ FK  layer_entity_element│◄─────────────┘              │ FK  map_id        UUID │
     │     geom     GEOMETRY   │◄───────────────────────────┤     name        VARCHAR │
     │     (PostGIS Type)       │              │              │     order          INT  │
     │     properties   JSONB  │              │              │     color       VARCHAR │
     └───────────┬─────────────┘              │              └─────────────────────────┘
                 │                            │
                 │ M:N                        │
                 ▼                            │
     ┌─────────────────────────┐              │
     │          TAG            │◄─────────────┘
     │    (Étiquettes)         │◄───────────────────────────┐
     ├─────────────────────────┤              │              │
     │ PK  id              UUID│              │              │
     │     name        VARCHAR │              │              │
     └─────────────────────────┘              │              │
                                              │              │
     ┌─────────────────────────┐              │  ┌─────────────────────────┐
     │     USER_LOG            │              │  │    NOTIFICATIONS        │
     │   (Journaux connexion)  │              │  │   (Notifications)       │
     ├─────────────────────────┤              │  ├─────────────────────────┤
     │ PK  id              UUID│              │  │ PK  id              UUID │
     │     username    VARCHAR │              │  │ FK  layer_id       UUID │
     │     login_date   TIMESTAMP│            │  │     message       TEXT   │
     │     logout_date  TIMESTAMP│            │  │     level         ENUM   │
     │     browser_name VARCHAR │              │  │     object        ENUM   │
     │     browser_ver  VARCHAR │              │  │     isSent      BOOLEAN │
     │     user_ip      VARCHAR │              │  └─────────────────────────┘
     │     client_os    VARCHAR │              │
     │     token        VARCHAR │              │
     └─────────────────────────┘              │
                                              │
     ┌─────────────────────────┐              │  ┌─────────────────────────┐
     │   USER_NOTIFICATION     │              │  │    USER_LAYER_FILTER    │
     │  (Notifs utilisateur)   │              │  │  (Filtres utilisateur)  │
     ├─────────────────────────┤              │  ├─────────────────────────┤
     │ PK  id              UUID│              │  │ PK,FK user_id      UUID │
     │ FK  user_id       UUID │◄─────────────┘  │ PK,FK layer_id    UUID │
     │     message       TEXT   │                 │     filter       JSONB │
     │     is_viewed   BOOLEAN │                 └─────────────────────────┘
     │     create_date TIMESTAMP│
     └─────────────────────────┘

```

### Types de géométries PostGIS supportés

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                           TYPES GÉOMÉTRIQUES POSTGIS                                     │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  ┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐ │
│  │     POINT       │   │   LINESTRING    │   │    POLYGON      │   │  MULTIPOINT     │ │
│  │      ●          │   │    ───────      │   │   ┌───────┐     │   │   ●  ●  ●       │ │
│  │                 │   │                 │   │   │       │     │   │                 │ │
│  │  ST_Point()     │   │  ST_LineFromText│   │   │       │     │   │  ST_MultiPoint()│ │
│  │  ST_MakePoint() │   │  ST_LineString  │   │   └───────┘     │   │                 │ │
│  └─────────────────┘   └─────────────────┘   │  ST_Polygon()   │   └─────────────────┘ │
│                                              └─────────────────┘                         │
│  ┌─────────────────┐   ┌─────────────────┐                                              │
│  │ MULTILINESTRING │   │  MULTIPOLYGON   │                                              │
│  │  ─── ─── ───    │   │ ┌───┐ ┌───┐     │                                              │
│  │                 │   │ │   │ │   │     │                                              │
│  │ ST_MultiLine()  │   │ └───┘ └───┘     │                                              │
│  │                 │   │ ST_MultiPolygon │                                              │
│  └─────────────────┘   └─────────────────┘                                              │
│                                                                                         │
│  SRID par défaut: 4326 (WGS 84)                                                        │
│  Fonctions spatiales: ST_Buffer, ST_Intersects, ST_Contains, ST_Distance, etc.         │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 8. Diagramme Architecture Frontend Vue/Nuxt

### Structure des composants Vue.js/Nuxt.js

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                    ARCHITECTURE FRONTEND NUXT.JS                                         │
│                          (sig_frontend/)                                                 │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              APPLICATION NUXT.JS                                         │
│                                  (nuxt.config.js)                                        │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│  Configuration:                                                                         │
│  • SSR: false (SPA mode)                                                               │
│  • Target: static                                                                       │
│  • Port: 3000 (dev) / 8080 (prod)                                                      │
│  • Axios baseURL: http://localhost:8080                                                │
│  • CSS: PrimeVue, Ant Design, Bootstrap, SCSS                                          │
└─────────────────────────────────────────────────────────────────────────────────────────┘
                                             │
        ┌────────────────────────────────────┼────────────────────────────────────┐
        │                                    │                                    │
        ▼                                    ▼                                    ▼
┌───────────────────┐            ┌───────────────────┐            ┌───────────────────┐
│      PAGES        │            │     LAYOUTS       │            │     PLUGINS       │
│   (pages/)        │            │   (layouts/)      │            │   (plugins/)      │
├───────────────────┤            ├───────────────────┤            ├───────────────────┤
│ index.vue         │            │ default.vue       │            │ leaflet.js        │
│ auth/index.vue    │            │ authLayout.vue    │            │ antd.js           │
│ dashboard/        │            │ dashboard.vue     │            │ bootstrap.js      │
│ ├── index.vue     │            │ errorLayout.vue   │            │ persistedState.js │
│ ├── admin/        │            │ publicLayout.vue  │            │ vue-moment.js     │
│ │   ├── users/    │            │ sharedMapLayout   │            │ vue-social-       │
│ │   ├── groups/   │            │ shareIframe...    │            │   sharing.js      │
│ │   ├── roles/    │            │ maintainLayout    │            │ leaflet-editable  │
│ │   ├── sessions/ │            └───────────────────┘            │ vue-bottom-sheet  │
│ │   └── user_logs/│                                             └───────────────────┘
│ ├── layers/       │
│ ├── maps/         │            ┌─────────────────────────────────────────────────────┐
│ ├── viewer/       │            │                    STORE VUEX                       │
│ ├── report/       │            │                    (store/)                         │
│ ├── settings/     │            ├─────────────────────────────────────────────────────┤
│ ├── symbologies/  │            │ index.js          │ users.js       │ layers.js      │
│ ├── tags/         │            │ app.js            │ groups.js      │ maps.js        │
│ ├── auditing/     │            │ profile.js        │ permissions.js │ features.js    │
│ ├── referentiels/ │            │ settings.js       │ resources.js   │ tags.js        │
│ ├── help/         │            │ settingsType.js   │ notifications  │ filter.js      │
│ ├── notifications/│            │ audit.js          │ user_notif.js  │ logs.js        │
│ └── profiles/     │            │ sessions.js       │ regions.js     │ switcher.js    │
│     ├── general/  │            └─────────────────────────────────────────────────────┘
│     ├── password/ │
│     └── notif/    │
└───────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              COMPOSANTS (components/)                                    │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │                              DASHBOARD COMPONENTS                                │   │
│  │  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐│   │
│  │  │ SideMenu.vue    │ │ UserTable.vue   │ │ GroupTable.vue  │ │ RoleTable.vue   ││   │
│  │  │ (Navigation)    │ │ (Gestion users) │ │ (Gestion groups)│ │ (Gestion perms) ││   │
│  │  └─────────────────┘ └─────────────────┘ └─────────────────┘ └─────────────────┘│   │
│  │  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐│   │
│  │  │ NewUser.vue     │ │ NewGroup.vue    │ │ NewRole.vue     │ │ AvatarUpload    ││   │
│  │  │ (Formulaire)    │ │ (Formulaire)    │ │ (Formulaire)    │ │ (Upload avatar) ││   │
│  │  └─────────────────┘ └─────────────────┘ └─────────────────┘ └─────────────────┘│   │
│  │  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐│   │
│  │  │ maps/           │ │ layers/         │ │ stats/          │ │ symbologies/    ││   │
│  │  │ ├── Table.vue   │ │ ├── Table.vue   │ │ ├── Card.vue    │ │ ├── Table.vue   ││   │
│  │  │ ├── NewMap.vue  │ │ ├── NewLayer.vue│ │ ├── LineChart   │ │ ├── NewSymbology││   │
│  │  │ ├── Users.vue   │ │ ├── Share.vue   │ │ ├── PieChart    │ │ ├── types/      ││   │
│  │  │ ├── Permissions │ │ ├── CloneLayer  │ │ ├── BarChart    │ │ │   ├── Simple  ││   │
│  │  │ └── CloneMap    │ │ └── Importation │ │ └── RadarChart  │ │ │   ├── Graduat ││   │
│  │  └─────────────────┘ └─────────────────┘ └─────────────────┘ │ │   ├── Classify││   │
│  │                                                              │ │   ├── HeatMap ││   │
│  │                                                              │ └── Cluster   ││   │
│  │                                                              └─────────────────┘│   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │                               VIEWER COMPONENTS                                  │   │
│  │  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐│   │
│  │  │ Layers.vue      │ │ LayerList.vue   │ │ FeatureFields   │ │ GeoProcessing   ││   │
│  │  │ (Liste couches) │ │ (Contrôles)     │ │ (Affichage)     │ │ (Buffer, Union) ││   │
│  │  └─────────────────┘ └─────────────────┘ └─────────────────┘ └─────────────────┘│   │
│  │  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐│   │
│  │  │ Nearby.vue      │ │ FilterLayers    │ │ GlobalFilter    │ │ GetCurrentPos   ││   │
│  │  │ (Proximité)     │ │ (Filtrage)      │ │ (Recherche)     │ │ (Géolocalisation││   │
│  │  └─────────────────┘ └─────────────────┘ └─────────────────┘ └─────────────────┘│   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │                                MAP COMPONENT                                     │   │
│  │  ┌───────────────────────────────────────────────────────────────────────────┐  │   │
│  │  │                       components/map/index.vue                              │  │   │
│  │  │  ┌─────────────────────────────────────────────────────────────────────┐  │  │   │
│  │  │  │                          LEAFLET MAP                                │  │  │   │
│  │  │  │                                                                      │  │  │   │
│  │  │  │    ┌─────────────────┐    ┌─────────────────┐    ┌───────────────┐  │  │  │   │
│  │  │  │    │   Base Layers   │    │ Overlay Layers  │    │   Controls    │  │  │  │   │
│  │  │  │    │  (Fond de carte)│    │ (Couches métier)│    │  (Outils)     │  │  │  │   │
│  │  │  │    │ • OSM          │    │ • WMS Layers    │    │ • Zoom        │  │  │  │   │
│  │  │  │    │ • Google       │    │ • GeoJSON Layers│    │ • Scale       │  │  │  │   │
│  │  │  │    │ • Bing         │    │ • Cluster       │    │ • Layers      │  │  │  │   │
│  │  │  │    │ • Custom       │    │ • Heatmap       │    │ • Draw/Edit   │  │  │  │   │
│  │  │  │    └─────────────────┘    └─────────────────┘    │ • Measure     │  │  │  │   │
│  │  │  │                                                        │ • Print       │  │  │  │   │
│  │  │  │                                                        └───────────────┘  │  │  │   │
│  │  │  └─────────────────────────────────────────────────────────────────────┘  │  │   │
│  │  └───────────────────────────────────────────────────────────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │                              LAYOUT COMPONENTS                                   │   │
│  │  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐│   │
│  │  │ Navbar.vue      │ │ Footer.vue      │ │ Panel.vue       │ │ Body.vue        ││   │
│  │  │ (Barre nav)     │ │ (Pied page)     │ │ (Panneau latéral│ │ (Contenu princ) ││   │
│  │  └─────────────────┘ └─────────────────┘ └─────────────────┘ └─────────────────┘│   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │                                 UI COMPONENTS                                    │   │
│  │  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐│   │
│  │  │ AutoComplete    │ │ Loader.vue      │ │ TreeDisplay     │ │ FilterCriterias ││   │
│  │  │ (Autocomplete)  │ │ (Chargement)    │ │ (Arbre données) │ │ (Filtres)       ││   │
│  │  └─────────────────┘ └─────────────────┘ └─────────────────┘ └─────────────────┘│   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │                              LANDING COMPONENTS                                  │   │
│  │  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐│   │
│  │  │ HeroSection     │ │ StatsSection    │ │ MapsSection     │ │ FooterSection   ││   │
│  │  │ (Accueil)       │ │ (Statistiques)  │ │ (Cartes publiq) │ │ (Pied landing)  ││   │
│  │  └─────────────────┘ └─────────────────┘ └─────────────────┘ └─────────────────┘│   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │                               AUTH COMPONENTS                                    │   │
│  │  ┌───────────────────────────────────────────────────────────────────────────┐  │   │
│  │  │                          LoginForm.vue                                      │  │   │
│  │  │  • Username input                                                          │  │   │
│  │  │  • Password input                                                          │  │   │
│  │  │  • Remember me checkbox                                                    │  │   │
│  │  │  • Submit button → POST /api/v1.0/login                                    │  │   │
│  │  │  • Forgot password link                                                    │  │   │
│  │  └───────────────────────────────────────────────────────────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘

```

### Flux de données Vuex Store

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                           FLUX DE DONNÉES VUEX                                           │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                                                                         │
│   ┌───────────────┐        ┌───────────────┐        ┌───────────────┐                  │
│   │   COMPONENTS  │───────>│    ACTIONS    │───────>│   SERVICES    │                  │
│   │   (Vue.js)    │        │    (Vuex)     │        │   (API)       │                  │
│   └───────────────┘        └───────────────┘        └───────────────┘                  │
│          │                        │                        │                            │
│          │                        │                        │                            │
│          │                        ▼                        │                            │
│          │                ┌───────────────┐                │                            │
│          │                │   MUTATIONS   │                │                            │
│          │                │   (Vuex)      │                │                            │
│          │                └───────────────┘                │                            │
│          │                        │                        │                            │
│          │                        ▼                        │                            │
│          │                ┌───────────────┐                │                            │
│          │                │     STATE     │<───────────────┘                            │
│          │                │   (Vuex)      │                                             │
│          │                └───────────────┘                                             │
│          │                        │                                                     │
│          │<───────────────────────┘                                                     │
│          │                GETTERS                                                       │
│          │                                                                              │
│          ▼                                                                              │
│   ┌───────────────┐                                                                    │
│   │     VIEW      │                                                                    │
│   │  (Template)   │                                                                    │
│   └───────────────┘                                                                    │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘

Exemple de flux - Chargement des cartes:
──────────────────────────────────────────

    Component                    Store (maps.js)                    API
    ─────────                    ─────────────────                  ─────
         │                              │                            │
         │ this.$store.dispatch         │                            │
         │('maps/findAll', args)        │                            │
         │─────────────────────────────>│                            │
         │                              │                            │
         │                              │ async findAll({commit})    │
         │                              │───────────────────────────>│
         │                              │                            │
         │                              │      GET /api/v1.0/maps    │
         │                              │<───────────────────────────│
         │                              │      Response: {maps}      │
         │                              │                            │
         │                              │ commit('SET_MAPS', maps)   │
         │                              │                            │
         │  this.$store.getters         │                            │
         │  ('maps/allMaps')            │                            │
         │<─────────────────────────────│                            │
         │                              │                            │
         ▼                              ▼                            ▼

```

---

## 9. Diagramme API REST

### Structure des endpoints REST

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                         API REST - STRUCTURE DES ENDPOINTS                               │
│                           Base URL: /api/v1.0                                            │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              AUTHENTIFICATION                                            │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  POST   /login                      Authentification utilisateur                        │
│         Body: {username, password}  → Response: {accessToken, refreshToken}            │
│                                                                                         │
│  POST   /refresh                    Rafraîchir le token                                 │
│         Body: {refreshToken, username} → Response: {accessToken, refreshToken}         │
│                                                                                         │
│  POST   /logout                    Déconnexion                                          │
│         Body: {refreshToken}       → Invalide le token dans Redis                      │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              GESTION DES UTILISATEURS                                    │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  GET    /users                          Liste des utilisateurs (paginée)               │
│         ?page=0&limit=10&sort=name&dir=asc                                             │
│                                                                                         │
│  GET    /users/{uuid}                   Détails d'un utilisateur                       │
│                                                                                         │
│  POST   /users                          Créer un utilisateur                           │
│         Body: UserDto                                                                   │
│                                                                                         │
│  PUT    /users/{uuid}                   Modifier un utilisateur                         │
│         Body: UserDto                                                                   │
│                                                                                         │
│  DELETE /users/{uuid}                   Supprimer un utilisateur                        │
│                                                                                         │
│  GET    /users/currentUser/{username}   Utilisateur courant                             │
│                                                                                         │
│  POST   /users/change-password          Changer le mot de passe                         │
│         Body: {oldPassword, newPassword}                                               │
│                                                                                         │
│  POST   /users/resetPassword/{uuid}     Réinitialiser le mot de passe                  │
│                                                                                         │
│  POST   /users/generatePassword         Générer un mot de passe aléatoire              │
│                                                                                         │
│  POST   /users/sendMail                 Envoyer un email                                │
│                                                                                         │
│  GET    /users/count                    Nombre d'utilisateurs                           │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              GESTION DES GROUPES                                         │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  GET    /groups                         Liste des groupes (paginée)                    │
│         ?page=0&limit=10&sort=name&dir=asc                                             │
│                                                                                         │
│  GET    /groups/{uuid}                  Détails d'un groupe                             │
│                                                                                         │
│  POST   /groups                         Créer un groupe                                 │
│         Body: GroupDto                                                                  │
│                                                                                         │
│  PUT    /groups/{uuid}                  Modifier un groupe                              │
│         Body: GroupDto                                                                  │
│                                                                                         │
│  DELETE /groups/{uuid}                  Supprimer un groupe                             │
│                                                                                         │
│  GET    /groups/count                   Nombre de groupes                               │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              GESTION DES CARTES                                          │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  GET    /maps                           Liste des cartes (paginée)                     │
│         ?page=0&limit=10&sort=createDate&dir=desc                                      │
│                                                                                         │
│  GET    /maps/{uuid}                    Détails d'une carte                             │
│                                                                                         │
│  POST   /maps                           Créer une carte                                 │
│         Body: MapDto                                                                    │
│                                                                                         │
│  PUT    /maps/{uuid}                    Modifier une carte                              │
│         Body: MapDto                                                                    │
│                                                                                         │
│  DELETE /maps/{uuid}                    Supprimer une carte                             │
│                                                                                         │
│  POST   /maps/search                    Rechercher des cartes par critères             │
│         Body: CommonFilter                                                              │
│                                                                                         │
│  POST   /maps/share/{uuid}              Partager une carte                              │
│         Body: ShareMapWithOthers {users, groups}                                       │
│                                                                                         │
│  POST   /maps/archive/{uuid}            Archiver/Désarchiver une carte                 │
│         Body: MapDto {privacy: ARCHIVED}                                               │
│                                                                                         │
│  POST   /maps/{slug}/clone              Cloner une carte                                │
│         Body: CloneMapDto                                                               │
│                                                                                         │
│  POST   /maps/attach                    Attacher des couches à une carte               │
│         Body: [MapLayerDto]                                                             │
│                                                                                         │
│  POST   /maps/detach/{mapId}/{layerId}  Détacher une couche d'une carte                │
│                                                                                         │
│  PUT    /maps/sort                      Trier les couches d'une carte                  │
│         Body: [MapLayerDto]                                                             │
│                                                                                         │
│  GET    /maps/layersWithFields/{uuid}   Couches avec champs d'une carte                │
│                                                                                         │
│  GET    /maps/{map-id}/layers-styles    Styles des couches d'une carte                 │
│                                                                                         │
│  GET    /maps/mapSharedWithOthers/{source}/{uuid}  Utilisateurs/Groupes partagés       │
│                                                                                         │
│  GET    /maps/public                    Cartes publiques                               │
│         ?page=0&limit=10                                                               │
│                                                                                         │
│  GET    /maps/public/{uuid}             Carte publique par UUID                        │
│                                                                                         │
│  GET    /maps/public/{map-id}/layers-styles  Styles couches carte publique             │
│                                                                                         │
│  PUT    /maps/set-visibility            Définir visibilité d'une couche                │
│         ?map-layer-id=uuid&visibility=true                                             │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              GESTION DES COUCHES                                         │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  GET    /layers                         Liste des couches (paginée)                    │
│         ?page=0&limit=10&sort=createDate&dir=desc                                      │
│                                                                                         │
│  GET    /layers/with-slug               Liste des couches avec slug                    │
│                                                                                         │
│  GET    /layers/withFields/{uuid}       Couche avec ses champs                         │
│                                                                                         │
│  GET    /layers/withFieldsAndResource/maps/{mapSlug}/{mode}/{uuid}                     │
│                                         Couche avec champs et ressources               │
│                                                                                         │
│  POST   /layers                         Créer une couche                               │
│         Body: LayerDto                                                                  │
│                                                                                         │
│  PUT    /layers/{uuid}                  Modifier une couche                            │
│         Body: LayerDto                                                                  │
│                                                                                         │
│  DELETE /layers/{uuid}                  Supprimer une couche                           │
│                                                                                         │
│  POST   /layers/search                  Rechercher des couches par critères            │
│         Body: CommonFilter                                                              │
│                                                                                         │
│  POST   /layers/view/{uuid}             Créer une vue SQL pour GeoServer               │
│                                                                                         │
│  POST   /layers/share/{uuid}            Partager une couche                            │
│         Body: ShareLayerWithOthers {users, groups}                                     │
│                                                                                         │
│  POST   /layers/{slug}/clone            Cloner une couche                              │
│         Body: CloneLayerDto                                                             │
│                                                                                         │
│  GET    /layers/check-write-permission/{layerSlug}/{permission}                        │
│                                         Vérifier permission d'écriture                  │
│                                                                                         │
│  GET    /layers/check-read-permission/maps/{mapSlug}/{layerSlug}/{permission}          │
│                                         Vérifier permission de lecture                  │
│                                                                                         │
│  GET    /layers/{uuid}/has-data         Vérifier si la couche a des données            │
│                                                                                         │
│  GET    /layers/findByTypeLimit/Admin   Couches par type (Admin)                       │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              ENTITÉS GÉOGRAPHIQUES                                       │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  GET    /entityelements/{layerSlug}/{identifiant}  Entités par couche                  │
│                                                                                         │
│  GET    /entityelements/public/{layerSlug}/{identifiant}  Entités publiques            │
│                                                                                         │
│  POST   /entityelements/search          Rechercher des entités                         │
│         Body: CommonFilter                                                              │
│                                                                                         │
│  POST   /entityelements                 Créer une entité géographique                  │
│         Body: EntityElementDto {geometry, properties}                                  │
│                                                                                         │
│  PUT    /entityelements/{uuid}          Modifier une entité                            │
│         Body: EntityElementDto                                                          │
│                                                                                         │
│  DELETE /entityelements/{uuid}          Supprimer une entité                           │
│                                                                                         │
│  POST   /entityelements/export/{slug}/{fileType}  Exporter des entités                 │
│         Body: CommonFilter     fileType: geojson | shapefile | kml | gml | csv | excel │
│         Response: Fichier binaire                                                      │
│                                                                                         │
│  POST   /entityelements/authorized      Vérifier si zone autorisée                     │
│         Body: Geometry                                                                  │
│                                                                                         │
│  POST   /entityelements/collection      Créer plusieurs entités                        │
│         Body: [EntityElementDto]                                                       │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              GÉO-TRAITEMENTS                                             │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  POST   /geoprocessing/buffer           Calculer un buffer                             │
│         Body: BufferParamsDto {geometry, distance}                                     │
│         Response: Geometry (buffered)                                                  │
│                                                                                         │
│  POST   /geoprocessing/intersection     Calculer l'intersection                        │
│         Body: {geometry1, geometry2}                                                   │
│                                                                                         │
│  POST   /geoprocessing/union            Calculer l'union                               │
│         Body: {geometry1, geometry2}                                                   │
│                                                                                         │
│  POST   /geoprocessing/difference       Calculer la différence                         │
│         Body: {geometry1, geometry2}                                                   │
│                                                                                         │
│  POST   /geoprocessing/contains         Vérifier si contient                           │
│         Body: {geometry1, geometry2}                                                   │
│                                                                                         │
│  POST   /geoprocessing/intersects       Vérifier si intersecte                         │
│         Body: {geometry1, geometry2}                                                   │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              GEOSERVER (WMS/WFS)                                         │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  GET    /geoserver/wms/{layerSlug}      WMS Layer URL                                  │
│         Params: bbox, width, height, format, sld                                       │
│                                                                                         │
│  GET    /geoserver/public/wms/{layerSlug}  WMS Layer public URL                        │
│         Params: bbox, width, height, format                                            │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              UPLOAD / DOWNLOAD                                           │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  POST   /upload                         Upload un fichier                              │
│         Body: FormData (multipart/form-data)                                           │
│         Response: {filename}                                                            │
│                                                                                         │
│  POST   /upload/{folderName}            Upload dans un dossier spécifique              │
│         Body: FormData                                                                  │
│                                                                                         │
│  DELETE /upload/delete/{fileName}       Supprimer un fichier                           │
│                                                                                         │
│  GET    /download/{fileName}            Télécharger un fichier                         │
│                                                                                         │
│  GET    /download/{folderName}/{fileName}  Télécharger depuis un dossier               │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              AUTRES ENDPOINTS                                            │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  GET    /permissions                    Liste des permissions                          │
│                                                                                         │
│  GET    /tags                           Liste des tags                                 │
│                                                                                         │
│  POST   /tags                           Créer un tag                                   │
│                                                                                         │
│  GET    /themes                         Liste des thèmes                               │
│                                                                                         │
│  GET    /settings                       Liste des paramètres                           │
│                                                                                         │
│  GET    /settings/findByType/{type}     Paramètres par type                            │
│                                                                                         │
│  GET    /settingsType                   Liste des types de paramètres                  │
│                                                                                         │
│  GET    /resources                      Liste des ressources                           │
│                                                                                         │
│  GET    /filters                        Liste des filtres                              │
│                                                                                         │
│  GET    /sessions                       Liste des sessions actives                     │
│                                                                                         │
│  DELETE /sessions                       Fermer une session                             │
│         Body: SessionDto                                                                │
│                                                                                         │
│  GET    /stats                          Statistiques globales                          │
│                                                                                         │
│  GET    /actuator/health                Health check (Spring Actuator)                 │
│                                                                                         │
│  GET    /swagger-ui.html                Documentation API (Swagger)                    │
│                                                                                         │
│  GET    /v3/api-docs                    OpenAPI JSON                                   │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘

```

### Codes de réponse HTTP

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                           CODES DE RÉPONSE HTTP                                          │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  200 OK                     Requête réussie                                            │
│  201 Created                Ressource créée avec succès                                │
│  202 Accepted               Requête acceptée (traitement asynchrone)                   │
│  204 No Content             Succès sans contenu (DELETE)                               │
│                                                                                         │
│  400 Bad Request            Requête mal formée                                         │
│  401 Unauthorized           Authentification requise                                   │
│  403 Forbidden              Accès refusé (permissions insuffisantes)                   │
│  404 Not Found              Ressource non trouvée                                      │
│  406 Not Acceptable         Compte désactivé ou expiré                                 │
│  409 Conflict               Conflit (ex: ressource existe déjà)                        │
│                                                                                         │
│  500 Internal Server Error  Erreur serveur                                             │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 10. Diagramme de Sécurité

### Couches de sécurité et flux

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                        ARCHITECTURE DE SÉCURITÉ                                          │
│                      Spring Security 6 + JWT + Redis                                    │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                                                                         │
│                                 COUCHES DE SÉCURITÉ                                     │
│                                                                                         │
│    ┌─────────────────────────────────────────────────────────────────────────────────┐  │
│    │                           COUCHE 1: AUTHENTIFICATION                             │  │
│    │                                                                                  │  │
│    │    ┌───────────────────┐    ┌───────────────────┐    ┌───────────────────┐     │  │
│    │    │   Login Form      │───>│  AuthManager      │───>│  UserDetailsServ  │     │  │
│    │    │   (Username/Pass) │    │  (Spring Sec)     │    │  (DB Lookup)      │     │  │
│    │    └───────────────────┘    └───────────────────┘    └───────────────────┘     │  │
│    │                                    │                       │                     │  │
│    │                                    │                       ▼                     │  │
│    │                                    │           ┌───────────────────┐             │  │
│    │                                    │           │   BCrypt Verify   │             │  │
│    │                                    │           │   (Password)      │             │  │
│    │                                    │           └───────────────────┘             │  │
│    │                                    │                                             │  │
│    │                                    ▼                                             │  │
│    │                         ┌───────────────────┐                                    │  │
│    │                         │  JWT Generation   │                                    │  │
│    │                         │  (HS512 Signing)  │                                    │  │
│    │                         └───────────────────┘                                    │  │
│    └─────────────────────────────────────────────────────────────────────────────────┘  │
│                                           │                                             │
│    ┌─────────────────────────────────────────────────────────────────────────────────┐  │
│    │                           COUCHE 2: AUTORISATION                                 │  │
│    │                                                                                  │  │
│    │    ┌────────────────────────────────────────────────────────────────────────┐   │  │
│    │    │                     SECURITY FILTER CHAIN                               │   │  │
│    │    │                                                                         │   │  │
│    │    │   Request ──> CORS ──> CSRF ──> Headers ──> Session ──> JWT Filter ──> │   │  │
│    │    │                                              │                       │   │  │
│    │    │                                              ▼                       │   │  │
│    │    │                                    ┌───────────────────┐              │   │  │
│    │    │                                    │  AuthTokenFilter  │              │   │  │
│    │    │                                    │                   │              │   │  │
│    │    │                                    │  1. Extract JWT   │              │   │  │
│    │    │                                    │  2. Validate JWT  │<──── Redis   │   │  │
│    │    │                                    │  3. Load User     │              │   │  │
│    │    │                                    │  4. Set Context   │              │   │  │
│    │    │                                    └───────────────────┘              │   │  │
│    │    └────────────────────────────────────────────────────────────────────────┘   │  │
│    │                                         │                                       │  │
│    │                                         ▼                                       │  │
│    │                              ┌───────────────────┐                              │  │
│    │                              │ @PreAuthorize     │                              │  │
│    │                              │ hasRole()         │                              │  │
│    │                              │ hasAuthority()    │                              │  │
│    │                              └───────────────────┘                              │  │
│    │                                         │                                       │  │
│    │                                         ▼                                       │  │
│    │                              ┌───────────────────┐                              │  │
│    │                              │ CustomPermission  │                              │  │
│    │                              │ Evaluator         │                              │  │
│    │                              │                   │                              │  │
│    │                              │ Vérifie perms     │                              │  │
│    │                              │ sur les couches   │                              │  │
│    │                              │ et cartes         │                              │  │
│    │                              └───────────────────┘                              │  │
│    └─────────────────────────────────────────────────────────────────────────────────┘  │
│                                           │                                             │
│    ┌─────────────────────────────────────────────────────────────────────────────────┐  │
│    │                           COUCHE 3: PROTECTION DES DONNÉES                       │  │
│    │                                                                                  │  │
│    │    ┌───────────────────┐    ┌───────────────────┐    ┌───────────────────┐     │  │
│    │    │   User <--> Layers│    │   User <--> Maps  │    │   Group <--> Layer│     │  │
│    │    │   (M:N Filter)    │    │   (M:N Filter)    │    │   (M:N Filter)    │     │  │
│    │    └───────────────────┘    └───────────────────┘    └───────────────────┘     │  │
│    │                                                                                  │  │
│    │    ┌───────────────────────────────────────────────────────────────────────────┐│  │
│    │    │              FILTRAGE AUTOMATIQUE DES DONNÉES                             ││  │
│    │    │                                                                           ││  │
│    │    │  • User ne voit que ses Layers/Maps + ceux partagés                      ││  │
│    │    │  • Group hérite des permissions Layer/Map                                ││  │
│    │    │  • Admin voit tout                                                       ││  │
│    │    │  • Public maps accessibles sans authentification                         ││  │
│    │    └───────────────────────────────────────────────────────────────────────────┘│  │
│    └─────────────────────────────────────────────────────────────────────────────────┘  │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                           FLUX DE VALIDATION JWT                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘

     Client                      Backend                   Redis
       │                           │                         │
       │  GET /api/v1.0/maps       │                         │
       │  Authorization: Bearer XX │                         │
       │──────────────────────────>│                         │
       │                           │                         │
       │                    ┌──────┴──────┐                  │
       │                    │ AuthToken   │                  │
       │                    │ Filter      │                  │
       │                    └──────┬──────┘                  │
       │                           │                         │
       │                    ┌──────┴──────┐                  │
       │                    │ 1. parseJwt │                  │
       │                    │   Extract   │                  │
       │                    │   token     │                  │
       │                    └──────┬──────┘                  │
       │                           │                         │
       │                    ┌──────┴──────┐                  │
       │                    │ 2. validate │                  │
       │                    │   Signature │                  │
       │                    │   (HS512)   │                  │
       │                    └──────┬──────┘                  │
       │                           │                         │
       │                           │  3. SISMEMBER           │
       │                           │  username: token        │
       │                           │────────────────────────>│
       │                           │                         │
       │                           │  true/false             │
       │                           │<────────────────────────│
       │                           │                         │
       │                    ┌──────┴──────┐                  │
       │                    │ 4. Load     │                  │
       │                    │ UserDetails │                  │
       │                    └──────┬──────┘                  │
       │                           │                         │
       │                    ┌──────┴──────┐                  │
       │                    │ 5. Set      │                  │
       │                    │ SecurityCtx │                  │
       │                    └──────┬──────┘                  │
       │                           │                         │
       │                    ┌──────┴──────┐                  │
       │                    │ 6. Check    │                  │
       │                    │ @PreAuth    │                  │
       │                    └──────┬──────┘                  │
       │                           │                         │
       │  Response Data            │                         │
       │<──────────────────────────│                         │
       │                           │                         │
       ▼                           ▼                         ▼

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                         PERMISSIONS SYSTÈME                                              │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  ┌───────────────────────────────────────────────────────────────────────────────────┐  │
│  │                          PERMISSIONS PAR MODULE                                    │  │
│  ├───────────────────────────────────────────────────────────────────────────────────┤  │
│  │                                                                                   │  │
│  │  USERS              │ LAYERS              │ MAPS                  │ SETTINGS      │  │
│  │  ─────────────────  │ ─────────────────  │ ──────────────────   │ ─────────────  │  │
│  │  USER_CREATE        │ LAYER_CREATE        │ MAP_CREATE            │ SETTING_READ  │  │
│  │  USER_READ          │ LAYER_READ          │ MAP_READ              │ SETTING_WRITE │  │
│  │  USER_UPDATE        │ LAYER_UPDATE        │ MAP_UPDATE            │               │  │
│  │  USER_DELETE        │ LAYER_DELETE        │ MAP_DELETE            │ REPORTING     │  │
│  │                     │ LAYER_SHARE         │ MAP_SHARE             │ ─────────────  │  │
│  │  GROUPS             │ LAYER_CLONE         │ MAP_CLONE             │ REPORTING_READ│  │
│  │  ─────────────────  │                     │ MAP_ARCHIVE           │               │  │
│  │  GROUP_CREATE       │ ENTITIES            │                       │ AUDITING      │  │
│  │  GROUP_READ         │ ─────────────────   │ ATTACH_LAYER_MAP      │ ─────────────  │  │
│  │  GROUP_UPDATE       │ ENTITY_CREATE       │ DETACH_LAYER_MAP      │ AUDITING_READ │  │
│  │  GROUP_DELETE       │ ENTITY_READ         │                       │               │  │
│  │                     │ ENTITY_UPDATE       │                       │ GEOPROCESSING │  │
│  │  ROLES              │ ENTITY_DELETE       │                       │ ─────────────  │  │
│  │  ─────────────────  │                     │                       │ GEOPROCESS_   │  │
│  │  ROLE_CREATE        │                     │                       │   EXECUTE     │  │
│  │  ROLE_READ          │                     │                       │               │  │
│  │  ROLE_UPDATE        │                     │                       │               │  │
│  │  ROLE_DELETE        │                     │                       │               │  │
│  │                                                                                   │  │
│  └───────────────────────────────────────────────────────────────────────────────────┘  │
│                                                                                         │
│  ┌───────────────────────────────────────────────────────────────────────────────────┐  │
│  │                          RÔLES PRÉDÉFINIS                                          │  │
│  ├───────────────────────────────────────────────────────────────────────────────────┤  │
│  │                                                                                   │  │
│  │  ROLE_ADMIN        │ Toutes les permissions                                      │  │
│  │  ROLE_USER         │ USER_READ, LAYER_READ, MAP_READ, ENTITY_READ                │  │
│  │  ROLE_EDITOR       │ + LAYER_CREATE/UPDATE, MAP_CREATE/UPDATE, ENTITY_CREATE     │  │
│  │  ROLE_VIEWER       │ Lecture seule sur tous les modules                          │  │
│  │                                                                                   │  │
│  └───────────────────────────────────────────────────────────────────────────────────┘  │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                         PROTECTION CSP & HEADERS                                         │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  Headers de sécurité configurés (WebSecurityConfig):                                    │
│                                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │  Header                  │  Valeur                         │  Protection        │   │
│  ├─────────────────────────────────────────────────────────────────────────────────┤   │
│  │  X-Frame-Options         │  SAMEORIGIN                    │  Clickjacking      │   │
│  │  X-XSS-Protection        │  1; mode=block                 │  XSS               │   │
│  │  X-Content-Type-Options  │  nosniff                       │  MIME sniffing     │   │
│  │  Strict-Transport-Secur  │  max-age=31536000; includeSubD │  Man-in-the-middle │   │
│  │  Content-Security-Policy │  Configured                    │  XSS/Injection     │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                         │
│  Configuration CORS:                                                                    │
│  • Origines autorisées: Configurable                                                   │
│  • Méthodes: GET, POST, PUT, DELETE, OPTIONS                                           │
│  • Headers: Authorization, Content-Type                                                │
│  • Credentials: true                                                                   │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘

```

---

## Résumé des Technologies

| Catégorie | Technologie | Version | Description |
|-----------|-------------|---------|-------------|
| **Backend** | Spring Boot | 3.2.x | Framework Java |
| **Java** | OpenJDK | 17/21 | Runtime Java |
| **Jakarta EE** | Servlet API | 10 | API Web |
| **ORM** | Hibernate | 6.x | Mapping objet-relationnel |
| **Sécurité** | Spring Security | 6.x | Authentification/Autorisation |
| **Tokens** | JWT (JJWT) | 0.12.x | JSON Web Tokens |
| **Cache** | Redis | Latest | Stockage sessions/tokens |
| **Base de données** | PostgreSQL | 15 | Base relationnelle |
| **Spatial** | PostGIS | 3.3 | Extension géospatiale |
| **Frontend** | Nuxt.js | 2.x | Framework Vue.js |
| **UI** | Ant Design Vue | Latest | Composants UI |
| **Cartographie** | Leaflet.js | Latest | Bibliothèque cartographique |
| **SIG** | GeoServer | 2.x | Serveur WMS/WFS |
| **Conteneurisation** | Docker | Latest | Conteneurs |
| **Orchestration** | Docker Compose | 3.8 | Multi-conteneurs |

---

*Document généré automatiquement pour le projet SIG Maps - Architecture et Diagrammes*
