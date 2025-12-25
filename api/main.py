from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import os

app = FastAPI(title="Minecraft Server API")

# CORS middleware for Next.js frontend
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000", "http://frontend:3000"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
async def root():
    return {"message": "Minecraft Server API", "status": "running"}

@app.get("/health")
async def health():
    return {"status": "healthy"}

# TODO: Add your API endpoints here
# Example: Get player data, server stats, etc.

