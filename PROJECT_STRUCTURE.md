# Project Structure

## Core Files (Required)

### Docker & Deployment
- `Dockerfile` - Builds Minecraft server container with plugin
- `docker-compose.yml` - Orchestrates all services (Minecraft, PostgreSQL, FastAPI, Next.js)
- `.dockerignore` - Excludes files from Docker builds
- `.env.example` - Template for environment variables

### Build Configuration
- `build.gradle.kts` - Gradle build configuration for Java plugin
- `settings.gradle.kts` - Gradle project settings
- `gradlew` / `gradlew.bat` - Gradle wrapper scripts (Unix/Windows)
- `gradle/wrapper/` - Gradle wrapper files

### Source Code
- `src/main/java/com/srgeppi/main/Main.java` - Main plugin code (ping command)
- `src/main/resources/plugin.yml` - Plugin metadata/configuration

### Backend (FastAPI)
- `api/Dockerfile` - FastAPI container build
- `api/main.py` - FastAPI application
- `api/requirements.txt` - Python dependencies

### Frontend (Next.js)
- `frontend/Dockerfile` - Next.js container build
- `frontend/package.json` - Node.js dependencies
- `frontend/next.config.js` - Next.js configuration
- `frontend/tsconfig.json` - TypeScript configuration
- `frontend/pages/index.tsx` - Main React page
- `frontend/next-env.d.ts` - Next.js TypeScript definitions

### Documentation
- `README.md` - Project documentation
- `.gitignore` - Git ignore rules

## Ignored Files (Not Committed)

These are in `.gitignore` and won't be committed:
- `build/` - Gradle build output
- `node_modules/` - Node.js dependencies
- `__pycache__/` - Python cache
- `.next/` - Next.js build output
- `.gradle/` - Gradle cache
- `.idea/`, `.vscode/` - IDE settings
- `.env` - Environment secrets
- `server-data/` - Minecraft server data

## File Purposes Summary

| File | Purpose |
|------|---------|
| `Dockerfile` | Builds plugin + Minecraft server container |
| `docker-compose.yml` | Defines all services and networking |
| `build.gradle.kts` | Java plugin build configuration |
| `Main.java` | Plugin code (ping command) |
| `plugin.yml` | Plugin metadata for Minecraft |
| `api/main.py` | FastAPI backend server |
| `frontend/pages/index.tsx` | React frontend page |
| `.env.example` | Environment variable template |

