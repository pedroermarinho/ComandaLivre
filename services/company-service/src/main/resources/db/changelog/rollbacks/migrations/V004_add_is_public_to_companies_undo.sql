-- Rollback for v004_add_is_public_to_companies.sql
ALTER TABLE companies DROP COLUMN is_public;
