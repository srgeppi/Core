from pydantic import BaseModel, EmailStr
from datetime import datetime
from typing import Optional


# User models
class UserCreate(BaseModel):
    username: str
    email: EmailStr
    password: str


class UserLogin(BaseModel):
    username: str
    password: str


class UserResponse(BaseModel):
    id: int
    username: str
    email: str
    created_at: datetime
    is_active: bool
    
    class Config:
        from_attributes = True


# Player models
class PlayerResponse(BaseModel):
    id: int
    uuid: str
    username: str
    ip_address: Optional[str]
    first_joined: Optional[datetime]
    last_login: Optional[datetime]
    last_logout: Optional[datetime]
    user_id: Optional[int]
    
    class Config:
        from_attributes = True


# Linking models
class LinkTokenResponse(BaseModel):
    token: str
    expires_at: datetime


class LinkAccountRequest(BaseModel):
    token: str

