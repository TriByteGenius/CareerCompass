# schemas.py
# pylint: disable=R0903

from typing import List
from pydantic import BaseModel

class JobUpdateRequestSchema(BaseModel):

    websites : str
    type: List[str]
    location: str
    time: int

    class Config:
        orm_mode = True
