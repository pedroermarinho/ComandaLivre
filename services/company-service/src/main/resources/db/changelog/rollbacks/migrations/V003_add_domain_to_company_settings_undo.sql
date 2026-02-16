-- Rollback for v003__add_domain_to_company_settings.sql
ALTER TABLE company_settings DROP COLUMN domain;
