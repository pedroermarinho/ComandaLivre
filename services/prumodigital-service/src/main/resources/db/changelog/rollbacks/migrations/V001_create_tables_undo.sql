-- Rollback for V001_create_tables.sql

DROP INDEX IF EXISTS idx_aa_daily_activity_id;
DROP TABLE IF EXISTS activity_attachments;

DROP INDEX IF EXISTS idx_dact_status_id;
DROP INDEX IF EXISTS idx_dact_daily_report_id;
DROP TABLE IF EXISTS daily_activities;

DROP INDEX IF EXISTS idx_da_employee_assignment_id;
DROP INDEX IF EXISTS idx_da_daily_report_id;
DROP TABLE IF EXISTS daily_attendances;

DROP INDEX IF EXISTS idx_dr_project_id;
DROP INDEX IF EXISTS idx_dr_project_id_date;
DROP TABLE IF EXISTS daily_reports;

DROP TABLE IF EXISTS employee_project_assignments;

DROP INDEX IF EXISTS idx_projects_project_status_id;
DROP INDEX IF EXISTS idx_projects_company_id;
DROP TABLE IF EXISTS projects;

DROP TABLE IF EXISTS daily_activity_status;

DROP TABLE IF EXISTS weather_status;

DROP TABLE IF EXISTS project_status;
