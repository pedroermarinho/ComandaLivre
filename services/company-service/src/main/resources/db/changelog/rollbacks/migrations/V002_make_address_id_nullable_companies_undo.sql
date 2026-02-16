-- Rollback for v002_make_address_id_nullable_companies.sql
ALTER TABLE companies ALTER COLUMN address_id SET NOT NULL;
