#!/bin/bash

# ============================================
# SIG Maps - Docker Deployment Script
# ============================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}SIG Maps - Docker Deployment${NC}"
echo -e "${GREEN}========================================${NC}"

# Check if .env exists
if [ ! -f .env ]; then
    echo -e "${YELLOW}Warning: .env file not found!${NC}"
    echo -e "${YELLOW}Creating .env from .env.example...${NC}"
    
    if [ -f .env.example ]; then
        cp .env.example .env
        echo -e "${RED}Please edit .env file with your configuration${NC}"
        exit 1
    else
        echo -e "${RED}Error: .env.example not found!${NC}"
        exit 1
    fi
fi

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Error: Docker is not installed!${NC}"
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}Error: Docker Compose is not installed!${NC}"
    exit 1
fi

echo -e "${GREEN}Step 1: Checking environment...${NC}"

# Check required variables
if ! grep -q "POSTGRES_PASSWORD=." .env; then
    echo -e "${RED}Error: POSTGRES_PASSWORD is not set in .env${NC}"
    exit 1
fi

echo -e "${GREEN}Step 2: Building Docker images...${NC}"
docker-compose build --no-cache

echo -e "${GREEN}Step 3: Starting services...${NC}"
docker-compose up -d

echo -e "${GREEN}Step 4: Waiting for services to be healthy...${NC}"
sleep 10

# Check service status
echo -e "${GREEN}Step 5: Checking service status...${NC}"
docker-compose ps

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Deployment Complete!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "Access the application at:"
echo -e "  Frontend: ${GREEN}http://localhost${NC}"
echo -e "  Backend:  ${GREEN}http://localhost:8080${NC}"
echo ""
echo -e "To view logs: ${YELLOW}docker-compose logs -f${NC}"
echo -e "To stop:      ${YELLOW}docker-compose down${NC}"
