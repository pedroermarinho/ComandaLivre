-- Rollback for datasets/v001_insert_tables.sql
DELETE FROM role_types WHERE company_type_id IN (SELECT id FROM company_types WHERE key IN ('restaurant', 'construction_company'));
DELETE FROM company_types WHERE key IN ('restaurant', 'construction_company');
