-- Creates additional databases on the same PostgreSQL instance.
-- The default database (auction_bidding) is created automatically by POSTGRES_DB.
-- This script runs only on first initialization (empty data volume).

SELECT 'CREATE DATABASE auction_auth'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'auction_auth')\gexec
