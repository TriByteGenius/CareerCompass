-- ini.sql

-- Create the "jobs" table
CREATE TABLE jobs (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    company TEXT NOT NULL,
    type TEXT NOT NULL,
    location TEXT NOT NULL,
    time TIMESTAMP NOT NULL,
    status TEXT NOT NULL,
    url TEXT NOT NULL,
    website TEXT NOT NULL
);