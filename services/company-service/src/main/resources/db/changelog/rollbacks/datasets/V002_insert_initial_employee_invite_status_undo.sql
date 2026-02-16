-- Rollback for datasets/v002_insert_initial_employee_invite_status.sql
DELETE FROM employee_invite_status WHERE key IN ('pending', 'accepted', 'rejected', 'expired');
