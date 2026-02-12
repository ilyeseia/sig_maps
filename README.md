# 🗺️ SIG Maps - Geographic Information System

A complete Geographic Information System built with Spring Boot and Nuxt.js, ready for Docker deployment.

## 📋 Project Overview

| Component | Technology | Port |
|-----------|------------|------|
| **Frontend** | Nuxt.js 2 + Vue 2 + Leaflet | 80 |
| **Backend** | Spring Boot 3 + Java 17 | 8080 |
| **Database** | PostgreSQL + PostGIS | 5432 |

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose
- Git

### 1. Clone the repository
```bash
git clone https://github.com/ilyeseia/sig_maps.git
cd sig_maps
```

### 2. Configure environment
```bash
# Copy example environment file
cp .env.example .env

# Edit .env and set your passwords
nano .env
```

### 3. Build and run
```bash
docker-compose up -d --build
```

### 4. Access the application
- **Frontend**: http://localhost
- **Backend API**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html

## 📁 Project Structure

```
sig_maps/
├── docker-compose.yml          # Main Docker Compose configuration
├── .env.example               # Environment template
├── README.md                  # This file
│
├── sig_backend/               # Spring Boot Backend
│   ├── src/
│   │   └── main/
│   │       ├── java/         # Java source code
│   │       └── resources/    # Application properties
│   ├── Dockerfile            # Backend Docker image
│   ├── build.gradle          # Gradle dependencies
│   └── settings.gradle       # Gradle settings
│
└── sig_frontend/             # Nuxt.js Frontend
    ├── pages/               # Vue pages
    ├── components/          # Vue components
    ├── assets/              # Static assets
    ├── plugins/             # Nuxt plugins
    ├── Dockerfile           # Frontend Docker image
    ├── nuxt.config.js       # Nuxt configuration
    └── package.json         # NPM dependencies
```

## ⚙️ Configuration

### Required Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `POSTGRES_PASSWORD` | PostgreSQL password | ✅ Yes |
| `POSTGRES_DB` | Database name | Optional (default: sig_db) |
| `POSTGRES_USER` | Database user | Optional (default: sig_user) |
| `API_BASE_URL` | Backend URL for frontend | Optional (default: http://localhost:8080) |
| `JAVA_OPTS` | JVM options | Optional |

### Email Configuration (Optional)

| Variable | Description |
|----------|-------------|
| `MAIL_HOST` | SMTP server host |
| `MAIL_PORT` | SMTP port |
| `MAIL_USERNAME` | SMTP username |
| `MAIL_PASSWORD` | SMTP password |

## 🔧 Development

### Backend Development
```bash
cd sig_backend

# Build
./gradlew build

# Run locally (requires PostgreSQL)
./gradlew bootRun
```

### Frontend Development
```bash
cd sig_frontend

# Install dependencies
npm install --legacy-peer-deps

# Run development server
npm run dev
```

## 🐳 Docker Commands

```bash
# Build all services
docker-compose build

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Remove all data (including database)
docker-compose down -v
```

## 🔒 Security Notes

1. **Never commit `.env` file** - It contains sensitive credentials
2. **Change default passwords** - Use strong, unique passwords
3. **Production deployment** - Configure proper SSL/TLS certificates
4. **Network security** - Consider using internal networks for backend communication

## 📊 Health Checks

All services include health checks:

```bash
# Check service status
docker-compose ps

# Backend health
curl http://localhost:8080/actuator/health

# Frontend health
curl http://localhost:8080
```

## 🐛 Troubleshooting

### Backend won't start
1. Check database is running: `docker-compose ps postgres`
2. Check logs: `docker-compose logs backend`
3. Verify environment variables in `.env`

### Frontend shows blank page
1. Check backend is healthy: `curl http://localhost:8080/actuator/health`
2. Check browser console for errors
3. Verify `API_BASE_URL` is correct

### Database connection errors
1. Wait for PostgreSQL to be ready (30-60 seconds)
2. Check PostgreSQL logs: `docker-compose logs postgres`
3. Verify credentials in `.env`

## 📝 License

MIT License

## 👥 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request
