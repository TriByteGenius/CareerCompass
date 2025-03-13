-- ini.sql

-- Create the "jobs" table
CREATE TABLE jobs (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    company VARCHAR NOT NULL,
    type VARCHAR NOT NULL,
    location VARCHAR NOT NULL,
    time TIMESTAMP NOT NULL,
    status VARCHAR NOT NULL,
    url VARCHAR NOT NULL
);