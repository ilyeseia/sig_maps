#!/bin/bash
# sig_maps Quick Deployment Script
# Usage: bash deploy.sh

set -e

echo "=============================================="
echo "   sig_maps Deployment Script"
echo "=============================================="
echo ""

# Check if .env exists
if [ ! -f .env ]; then
    echo "Creating .env file from .env.example..."
    cp .env.example .env
    echo "Please edit .env file with your configuration"
    echo "Then run this script again."
    exit 1
fi

# Load environment variables
source .env

# Check required variables
if [ -z "$POSTGRES_PASSWORD" ]; then
    echo "Error: POSTGRES_PASSWORD is required in .env file"
    exit 1
fi

echo "Configuration:"
echo "  Database: $POSTGRES_DB"
echo "  User: $POSTGRES_USER"
echo ""

# Stop existing containers
echo "Stopping existing containers..."
docker-compose down -v 2>/dev/null || true

# Build and start containers
echo ""
echo "Building and starting containers..."
echo "This may take several minutes..."
echo ""

docker-compose up -d --build

# Wait for services to be ready
echo ""
echo "Waiting for services to be ready..."
sleep 30

# Check status
echo ""
echo "Container Status:"
docker-compose ps

echo ""
echo "=============================================="
echo "   Deployment Complete!"
echo "=============================================="
echo ""
echo "Access the application:"
echo "  Frontend: http://localhost"
echo "  Backend API: http://localhost:8080"
echo ""
echo "Useful commands:"
echo "  View logs: docker-compose logs -f"
echo "  Stop: docker-compose down"
echo "  Restart: docker-compose restart"
