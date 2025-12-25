# GitHub Setup Guide

## Step 1: Create GitHub Repository

1. Go to https://github.com/new
2. Repository name: `HelloPlugin` (or whatever you want)
3. Description: "Minecraft plugin with FastAPI backend and Next.js frontend"
4. Choose **Private** or **Public**
5. **DO NOT** initialize with README, .gitignore, or license (we already have these)
6. Click **"Create repository"**

## Step 2: Connect Local Repo to GitHub

After creating the repo, GitHub will show you commands. Use these:

```bash
# Add all files
git add .

# Make first commit
git commit -m "Initial commit: Minecraft plugin with Docker setup"

# Connect to GitHub (replace YOUR_USERNAME with your GitHub username)
git remote add origin https://github.com/YOUR_USERNAME/HelloPlugin.git

# Push to GitHub
git branch -M main
git push -u origin main
```

## Step 3: Future Updates

When you make changes:

```bash
# See what changed
git status

# Add changes
git add .

# Commit with message
git commit -m "Description of your changes"

# Push to GitHub
git push
```

## Step 4: On Your VPS

```bash
# SSH into VPS
ssh user@your-vps-ip

# Navigate to project
cd /path/to/HelloPlugin

# Pull latest changes
git pull

# Rebuild and restart
docker-compose down
docker-compose up --build -d
```

## Important Notes

- **Never commit `.env` file** - it contains secrets
- **Never commit `server-data/`** - contains world files
- The `.gitignore` is already set up to exclude these

