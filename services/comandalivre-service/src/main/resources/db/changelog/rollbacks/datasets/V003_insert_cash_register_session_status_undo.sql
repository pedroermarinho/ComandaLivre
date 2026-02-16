-- Rollback for datasets/V003_insert_cash_register_session_status.sql
DELETE FROM cash_register_session_status WHERE key IN ('OPEN', 'CLOSED', 'IN_REVIEW');
