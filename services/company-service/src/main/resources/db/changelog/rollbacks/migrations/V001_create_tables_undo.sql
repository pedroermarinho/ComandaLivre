-- Rollback for v001_create_tables.sql

DROP INDEX IF EXISTS idx_employee_invites_company_id;
DROP INDEX IF EXISTS idx_employee_invites_user_id;
DROP INDEX IF EXISTS idx_employee_invites_email;
DROP INDEX IF EXISTS idx_employee_invites_token;
DROP TABLE IF EXISTS employee_invites;

DROP TABLE IF EXISTS employee_invite_status;

DROP INDEX IF EXISTS idx_employees_user_id;
DROP INDEX IF EXISTS idx_employees_company_id_user_id;
DROP TABLE IF EXISTS employees;

DROP TABLE IF EXISTS company_settings;

DROP INDEX IF EXISTS idx_companies_cnpj;
DROP INDEX IF EXISTS idx_companies_name_type;
DROP TABLE IF EXISTS companies;

DROP TABLE IF EXISTS role_types;

DROP TABLE IF EXISTS company_types;
