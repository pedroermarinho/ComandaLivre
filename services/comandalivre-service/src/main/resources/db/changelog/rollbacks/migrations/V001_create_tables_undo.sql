-- Rollback for V001_create_tables.sql

DROP INDEX IF EXISTS idx_oid_modifier_option_id;
DROP INDEX IF EXISTS idx_oid_order_item_id;
DROP TABLE IF EXISTS order_item_details;

DROP INDEX IF EXISTS idx_orderitems_status_id;
DROP INDEX IF EXISTS idx_orderitems_product_id;
DROP INDEX IF EXISTS idx_orderitems_command_id;
DROP TABLE IF EXISTS order_items;

DROP INDEX IF EXISTS idx_pmo_modifier_group_id;
DROP TABLE IF EXISTS product_modifiers_options;

DROP INDEX IF EXISTS idx_pmg_product_id;
DROP TABLE IF EXISTS product_modifiers_groups;

DROP INDEX IF EXISTS idx_products_company_id;
DROP INDEX IF EXISTS idx_products_name_category;
DROP TABLE IF EXISTS products;

DROP TABLE IF EXISTS commands;

DROP INDEX IF EXISTS idx_rtables_status_id;
DROP INDEX IF EXISTS idx_rtables_company_id;
DROP TABLE IF EXISTS tables;

DROP TABLE IF EXISTS product_categories;

DROP TABLE IF EXISTS order_status;

DROP TABLE IF EXISTS command_status;

DROP TABLE IF EXISTS table_status;
