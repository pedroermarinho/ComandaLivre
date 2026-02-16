-- Rollback for V003_add_priority_and_cancellation_to_orders.sql
ALTER TABLE order_items DROP COLUMN priority_level, DROP COLUMN cancellation_reason, DROP COLUMN cancelled_by_user_id;
