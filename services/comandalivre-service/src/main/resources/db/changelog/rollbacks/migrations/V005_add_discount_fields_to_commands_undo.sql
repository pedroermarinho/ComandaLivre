-- Rollback for V005_add_discount_fields_to_commands.sql
ALTER TABLE commands DROP COLUMN discount_amount, DROP COLUMN discount_description;
