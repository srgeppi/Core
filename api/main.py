from fastapi import FastAPI, Depends, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session
from passlib.context import CryptContext
from datetime import datetime, timedelta
import secrets
import os

from database import init_db, get_db, User, Player, LinkingToken
from models import (
    UserCreate, UserLogin, UserResponse,
    PlayerResponse, LinkTokenResponse, LinkAccountRequest
)

# Initialize database
init_db()

app = FastAPI(title="Minecraft Server API")

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000", "http://frontend:3000"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Password hashing
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")


def verify_password(plain_password: str, hashed_password: str) -> bool:
    return pwd_context.verify(plain_password, hashed_password)


def get_password_hash(password: str) -> str:
    return pwd_context.hash(password)


@app.on_event("startup")
async def startup():
    init_db()


@app.get("/")
async def root():
    return {"message": "Minecraft Server API", "status": "running"}


@app.get("/health")
async def health():
    return {"status": "healthy"}


# ============= USER ENDPOINTS =============

@app.post("/api/auth/register", response_model=UserResponse)
async def register(user_data: UserCreate, db: Session = Depends(get_db)):
    # Check if username or email already exists
    if db.query(User).filter(User.username == user_data.username).first():
        raise HTTPException(status_code=400, detail="Username already taken")
    if db.query(User).filter(User.email == user_data.email).first():
        raise HTTPException(status_code=400, detail="Email already registered")
    
    # Create new user
    hashed_password = get_password_hash(user_data.password)
    user = User(
        username=user_data.username,
        email=user_data.email,
        password_hash=hashed_password
    )
    db.add(user)
    db.commit()
    db.refresh(user)
    return user


@app.post("/api/auth/login")
async def login(credentials: UserLogin, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.username == credentials.username).first()
    if not user or not verify_password(credentials.password, user.password_hash):
        raise HTTPException(status_code=401, detail="Invalid credentials")
    if not user.is_active:
        raise HTTPException(status_code=403, detail="Account is disabled")
    
    return {"message": "Login successful", "user_id": user.id, "username": user.username}


# ============= PLAYER ENDPOINTS =============

@app.get("/api/players/{uuid}", response_model=PlayerResponse)
async def get_player(uuid: str, db: Session = Depends(get_db)):
    player = db.query(Player).filter(Player.uuid == uuid).first()
    if not player:
        raise HTTPException(status_code=404, detail="Player not found")
    return player


@app.get("/api/users/{user_id}/players", response_model=list[PlayerResponse])
async def get_user_players(user_id: int, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    return user.minecraft_players


# ============= ACCOUNT LINKING =============

@app.post("/api/users/{user_id}/link-token", response_model=LinkTokenResponse)
async def generate_link_token(user_id: int, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    
    # Generate secure token
    token = secrets.token_urlsafe(32)
    expires_at = datetime.utcnow() + timedelta(hours=1)  # Token valid for 1 hour
    
    # Store token in linking_tokens table
    linking_token = LinkingToken(
        token=token,
        user_id=user_id,
        expires_at=expires_at
    )
    db.add(linking_token)
    db.commit()
    db.refresh(linking_token)
    
    return {"token": token, "expires_at": expires_at}


@app.post("/api/players/link")
async def link_account(request: LinkAccountRequest, db: Session = Depends(get_db)):
    # Find player with this token
    player = db.query(Player).filter(Player.linking_token == request.token).first()
    if not player:
        raise HTTPException(status_code=404, detail="Invalid or expired token")
    
    if player.token_expires_at and player.token_expires_at < datetime.utcnow():
        raise HTTPException(status_code=400, detail="Token has expired")
    
    # This endpoint will be called by the plugin after verifying the token
    # The plugin will provide the user_id
    return {"message": "Account linked successfully", "player_uuid": player.uuid}


# ============= PLAYER DATA ENDPOINTS (for plugin to call) =============

@app.post("/api/players/update")
async def update_player_data(
    uuid: str,
    username: str,
    ip_address: str = None,
    is_login: bool = False,
    db: Session = Depends(get_db)
):
    player = db.query(Player).filter(Player.uuid == uuid).first()
    
    if not player:
        # Create new player
        player = Player(
            uuid=uuid,
            username=username,
            ip_address=ip_address,
            first_joined=datetime.utcnow(),
            last_login=datetime.utcnow() if is_login else None
        )
        db.add(player)
    else:
        # Update existing player
        player.username = username
        if ip_address:
            player.ip_address = ip_address
        if is_login:
            player.last_login = datetime.utcnow()
        else:
            player.last_logout = datetime.utcnow()
    
    db.commit()
    db.refresh(player)
    return {"message": "Player data updated", "player": PlayerResponse.from_orm(player)}
