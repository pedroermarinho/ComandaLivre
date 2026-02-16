-- Rollback for datasets/V002_insert_reservation_status_values.sql
DELETE FROM table_reservation_status WHERE key IN ('ATIVA', 'CANCELADA', 'FINALIZADA', 'EXPIRADA');
