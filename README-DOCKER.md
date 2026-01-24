# 🐳 Guide de Déploiement Docker - Projet SIG

Guide complet pour déployer l'application SIG (frontend + backend) avec Docker Compose.

## 📋 Prérequis

- **Docker** version 20.10 ou supérieure
- **Docker Compose** version 2.0 ou supérieure
- Au moins **8 GB de RAM** disponible
- **10 GB d'espace disque** minimum

### Vérification des prérequis

```powershell
# Vérifier Docker
docker --version

# Vérifier Docker Compose
docker-compose --version
```

## 🚀 Démarrage Rapide

### 1. Configuration de l'Environnement

Copiez le fichier d'exemple et ajustez les valeurs si nécessaire :

```powershell
cp .env.example .env
```

Ou sur Windows PowerShell :
```powershell
Copy-Item .env.example .env
```

### 2. Construction des Images

```powershell
docker-compose build
```

⏱️ **Note**: La première construction peut prendre 10-15 minutes selon votre connexion Internet.

### 3. Démarrage des Services

```powershell
docker-compose up -d
```

Vérifier l'état des conteneurs :

```powershell
docker-compose ps
```

### 4. Accès aux Services

Une fois tous les conteneurs démarrés et en bonne santé :

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend** | http://localhost:80 | Interface utilisateur Nuxt.js |
| **Backend API** | http://localhost:8080 | API Spring Boot |
| **API Docs** | http://localhost:8080/swagger-ui.html | Documentation Swagger |
| **Health Check** | http://localhost:8080/actuator/health | État du backend |
| **PostgreSQL** | localhost:5432 | Base de données (accessible uniquement en local) |

## 📁 Architecture des Services

```
┌─────────────────┐
│   Frontend      │
│   (Nuxt.js)     │
│   Port: 80      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   Backend       │
│  (Spring Boot)  │
│   Port: 8080    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   PostgreSQL    │
│   (PostGIS)     │
│   Port: 5432    │
└─────────────────┘
```

## ⚙️ Configuration

### Variables d'Environnement (.env)

| Variable | Description | Valeur par défaut |
|----------|-------------|-------------------|
| `POSTGRES_DB` | Nom de la base de données | `sig_db` |
| `POSTGRES_USER` | Utilisateur PostgreSQL | `sig_user` |
| `POSTGRES_PASSWORD` | Mot de passe PostgreSQL | `Solution.2021!` |
| `JAVA_OPTS` | Options JVM pour le backend | `-Xms2048m -Xmx4096m` |
| `API_BASE_URL` | URL de l'API pour le frontend | `http://localhost:8080` |
| `MAIL_HOST` | Serveur email | `mail.mjeunesse.gov.dz` |
| `MAIL_PORT` | Port du serveur email | `465` |
| `MAIL_USERNAME` | Nom d'utilisateur email | (voir .env) |
| `MAIL_PASSWORD` | Mot de passe email | (voir .env) |

## 🔧 Commandes Utiles

### Gestion des Conteneurs

```powershell
# Démarrer les services
docker-compose up -d

# Arrêter les services
docker-compose down

# Redémarrer un service spécifique
docker-compose restart backend

# Voir les logs
docker-compose logs -f

# Logs d'un service spécifique
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres

# Voir l'état des conteneurs
docker-compose ps
```

### Gestion des Données

```powershell
# Arrêter et supprimer les volumes (⚠️ SUPPRIME LES DONNÉES)
docker-compose down -v

# Sauvegarder la base de données
docker-compose exec postgres pg_dump -U sig_user sig_db > backup.sql

# Restaurer la base de données
docker-compose exec -T postgres psql -U sig_user sig_db < backup.sql
```

### Debugging

```powershell
# Accéder au shell d'un conteneur
docker-compose exec backend sh
docker-compose exec frontend sh
docker-compose exec postgres bash

# Inspecter les logs en temps réel
docker-compose logs -f --tail=100

# Vérifier la santé des conteneurs
docker-compose ps
```

## 🐛 Dépannage

### Le backend ne démarre pas

**Symptômes**: Le conteneur backend redémarre continuellement

**Solutions**:
1. Vérifier les logs: `docker-compose logs backend`
2. Vérifier que PostgreSQL est prêt: `docker-compose logs postgres`
3. Vérifier la connectivité: `docker-compose exec backend nc -zv postgres 5432`
4. Augmenter la mémoire Docker si nécessaire

### Le frontend affiche une erreur 502

**Symptômes**: Erreur nginx "Bad Gateway"

**Solutions**:
1. Vérifier que le backend est démarré: `docker-compose ps backend`
2. Vérifier les logs du backend: `docker-compose logs backend`
3. Tester l'API directement: `curl http://localhost:8080/actuator/health`

### Erreur de connexion à la base de données

**Symptômes**: Backend affiche "Connection refused" ou "Unable to connect to database"

**Solutions**:
1. Vérifier que PostgreSQL est démarré: `docker-compose ps postgres`
2. Vérifier les credentials dans `.env`
3. Redémarrer les services: `docker-compose restart`

### Le build Gradle échoue

**Symptômes**: Erreur pendant `docker-compose build`

**Solutions**:
1. Vérifier que `gradlew` a les permissions d'exécution
2. Nettoyer le cache Docker: `docker-compose build --no-cache backend`
3. Vérifier l'accès aux dépôts Maven

### Manque de mémoire

**Symptômes**: Conteneurs qui s'arrêtent, erreurs OutOfMemory

**Solutions**:
1. Augmenter la RAM allouée à Docker Desktop (Settings > Resources)
2. Ajuster `JAVA_OPTS` dans `.env`: `-Xms1024m -Xmx2048m`
3. Fermer d'autres applications

### Port déjà utilisé

**Symptômes**: "Port is already allocated"

**Solutions**:
```powershell
# Trouver le processus utilisant le port
netstat -ano | findstr :8080
netstat -ano | findstr :80

# Arrêter le processus ou changer le port dans docker-compose.yml
# Exemple: "8081:8080" au lieu de "8080:8080"
```

## 🔒 Sécurité

### Pour la Production

1. **Changer tous les mots de passe** dans `.env`
2. **Ne pas committer** le fichier `.env` (déjà dans .gitignore)
3. **Utiliser des secrets** Docker pour les credentials sensibles
4. **Activer HTTPS** avec un reverse proxy (nginx, traefik)
5. **Limiter l'accès** à PostgreSQL (ne pas exposer le port 5432)

### Configuration HTTPS (Optionnel)

Pour utiliser HTTPS en production, ajouter un reverse proxy comme Traefik ou nginx avec Let's Encrypt.

## 📊 Monitoring

### Vérifier l'état de santé

```powershell
# Backend
curl http://localhost:8080/actuator/health

# Frontend
curl http://localhost:80

# PostgreSQL
docker-compose exec postgres pg_isready -U sig_user
```

### Statistiques des conteneurs

```powershell
# Utilisation CPU/Mémoire
docker stats

# Spécifique au projet SIG
docker stats sig_backend sig_frontend sig_postgres
```

## 🔄 Mise à Jour

```powershell
# 1. Arrêter les services
docker-compose down

# 2. Récupérer les dernières modifications
git pull

# 3. Reconstruire les images
docker-compose build

# 4. Redémarrer
docker-compose up -d
```

## 📝 Développement Local

Pour le développement, vous pouvez monter les volumes pour le hot-reload:

```yaml
# Ajouter dans docker-compose.yml sous 'backend'
volumes:
  - ./sig_backend/src:/app/src

# Ajouter dans docker-compose.yml sous 'frontend'
volumes:
  - ./sig_frontend:/usr/src/app
```

## 🆘 Support

Si vous rencontrez des problèmes :

1. Vérifier les logs: `docker-compose logs`
2. Consulter la section Dépannage ci-dessus
3. Vérifier l'état: `docker-compose ps`
4. Redémarrer proprement: `docker-compose down && docker-compose up -d`

## 📚 Ressources

- [Documentation Docker](https://docs.docker.com/)
- [Documentation Docker Compose](https://docs.docker.com/compose/)
- [Spring Boot avec Docker](https://spring.io/guides/gs/spring-boot-docker/)
- [Nuxt.js Deployment](https://nuxtjs.org/docs/deployment/docker/)
