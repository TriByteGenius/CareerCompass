import pandas as pd
import psycopg2
import os
from dotenv import load_dotenv

load_dotenv()

conn = psycopg2.connect(os.getenv("SQLALCHEMY_DATABASE_URL"))

# Define SQL query
query = "SELECT * FROM jobs;"

# Read data into a Pandas DataFrame
df = pd.read_sql_query(query, conn)

# Close the connection
conn.close()

print(df.url[22])