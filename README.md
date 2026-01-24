# 🐳 Configuration Docker - Projet SIG

Ce dossier contient la configuration Docker complète pour le projet SIG.

## 📁 Structure des Fichiers

```
sig/
├── docker-compose.yml          # Configuration principale Docker Compose
├── .env                        # Variables d'environnement (NE PAS COMMITER)
├── .env.example               # Template des variables
├── deploy-docker.ps1          # Script de déploiement Windows
├── deploy-docker.sh           # Script de déploiement Linux/Mac
├── test-docker.ps1            # Script de validation
├── README-DOCKER.md           # Documentation complète
├── QUICK-START-DOCKER.md      # Guide rapide
├── CHECKLIST-FINAL.md         # Liste de vérification
│
├── sig_backend/
│   ├── Dockerfile             # Image backend optimisée
│   ├── .dockerignore          # Exclusions build
│   └── src/main/resources/
│       └── application-docker.properties
│
└── sig_frontend/
    ├── Dockerfile             # Image frontend optimisée
    ├── .dockerignore          # Exclusions build
    ├── package.json           # Dépendances Node.js
    └── nuxt.config.js         # Configuration Nuxt.js
```

## 🚀 Démarrage Rapide

### Windows
```powershell
.\deploy-docker.ps1
```

### Linux/Mac
```bash
chmod +x deploy-docker.sh
./deploy-docker.sh
```

## 📚 Documentation

- **[README-DOCKER.md](README-DOCKER.md)** - Guide complet avec troubleshooting
- **[QUICK-START-DOCKER.md](QUICK-START-DOCKER.md)** - Instructions rapides
- **[CHECKLIST-FINAL.md](CHECKLIST-FINAL.md)** - Liste de vérification

## 🎯 Services

| Service | Port | Description |
|---------|------|-------------|
| Frontend | 80 | Interface Nuxt.js + Nginx |
| Backend | 8080 | API Spring Boot |
| PostgreSQL | 5432 | Base de données PostGIS |

## ⚙️ Configuration

Configurez les variables dans `.env`:

```env
# Base de données
POSTGRES_DB=sig_db
POSTGRES_USER=sig_user
POSTGRES_PASSWORD=VotreMotDePasse

# Backend
JAVA_OPTS=-Xms2048m -Xmx4096m

# Frontend
API_BASE_URL=http://localhost:8080
```

## 🔒 Sécurité
**CRITIQUE:** Les mots de passe par défaut ont été SUPPRIMÉS du code.
Vous DEVEZ configurer un fichier `.env` avec des mots de passe forts avant de démarrer.

**IMPORTANT:** Ne commitez JAMAIS le fichier `.env` !

Ajoutez à `.gitignore`:
```
.env
```

## 📞 Support

En cas de problème, consultez:
1. [README-DOCKER.md](README-DOCKER.md) - Section Dépannage
2. Logs: `docker-compose logs -f`
3. État: `docker-compose ps`

## ✅ Tests Effectués

- ✅ Configuration Docker Compose validée
- ✅ Dockerfiles optimisés (multi-stage builds, mise en cache dépendances)
- ✅ Health checks configurés
- ✅ Dépendances résolues
- ✅ Audit de sécurité effectué (Race conditions fixées, Secrets hardcodés supprimés)
- ✅ Documentation complète

---

**Version:** 1.1.0  
**Date:** 2026-01-23  
**Statut:** ✅ Prêt pour production
