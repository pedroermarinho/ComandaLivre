-- Rollback for V008__refine_product_modifiers_tables.sql
ALTER TABLE order_items RENAME COLUMN total_modifiers_price_at_order TO total_modifiers_price;
ALTER TABLE order_items RENAME COLUMN base_price_at_order TO item_price_at_order;
ALTER TABLE order_item_modifiers DROP CONSTRAINT uq_order_item_modifier;
ALTER TABLE order_item_modifiers RENAME COLUMN price_at_order TO price_at_selection;
ALTER TABLE order_item_modifiers RENAME TO order_item_details;
ALTER TABLE product_modifiers_groups DROP CONSTRAINT chk_selection_logic;
