# Minecraft Server + Web Dashboard - Docker Setup

Complete containerized setup with:
- 🎮 Minecraft Server (using your existing `~/mc-dev/server/` folder)
- 🗄️ PostgreSQL Database
- 🐍 FastAPI Backend
- ⚛️ Next.js Frontend

## Quick Start

1. **Copy environment file:**
   ```bash
   cp .env.example .env
   ```

2. **Edit `.env` and set your PostgreSQL password:**
   ```bash
   POSTGRES_PASSWORD=your_secure_password_here
   ```

3. **Start everything:**
   ```bash
   docker-compose up --build
   ```

## Services

- **Minecraft Server:** `localhost:25565`
- **FastAPI Backend:** `http://localhost:8000`
- **Next.js Frontend:** `http://localhost:3000`
- **PostgreSQL:** `localhost:5432`

## Architecture

```
┌─────────────────┐
│  Next.js (3000) │
└────────┬────────┘
         │
┌────────▼────────┐
│  FastAPI (8000) │
└────────┬────────┘
         │
┌────────▼────────┐
│  PostgreSQL     │
└─────────────────┘

┌─────────────────┐
│ Minecraft (25565)│
└─────────────────┘
```

All services share the same PostgreSQL database and can communicate via Docker network.

## For VPS Deployment

1. **Set strong passwords in `.env`**
2. **Update CORS origins in `api/main.py`** to your domain
3. **Build for production:**
   ```bash
   docker-compose -f docker-compose.prod.yml up --build
   ```

## Commands

**Start all services:**
```bash
docker-compose up
```

**Start in background:**
```bash
docker-compose up -d
```

**View logs:**
```bash
docker-compose logs -f
```

**Stop everything:**
```bash
docker-compose down
```

**Rebuild after code changes:**
```bash
docker-compose up --build
```

## Data Persistence

- **PostgreSQL data:** Stored in Docker volume `postgres_data`
- **Minecraft server:** Uses your existing `~/mc-dev/server/` folder
- **All data persists** between container restarts
