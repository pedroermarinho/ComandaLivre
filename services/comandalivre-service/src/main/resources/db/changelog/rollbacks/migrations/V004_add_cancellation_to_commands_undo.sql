-- Rollback for V004_add_cancellation_to_commands.sql
ALTER TABLE commands DROP COLUMN cancellation_reason, DROP COLUMN cancelled_by_user_id;
