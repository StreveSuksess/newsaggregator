SELECT 'CREATE DATABASE newsdb'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'newsdb')\gexec

DO
$do$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_catalog.pg_roles
      WHERE  rolname = 'newsuser') THEN
      CREATE USER newsuser WITH PASSWORD 'newspass';
   END IF;
END
$do$;

GRANT ALL PRIVILEGES ON DATABASE newsdb TO newsuser; 