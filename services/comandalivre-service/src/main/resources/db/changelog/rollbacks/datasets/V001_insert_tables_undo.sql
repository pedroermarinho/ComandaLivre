-- Rollback for datasets/V001_insert_tables.sql
DELETE FROM product_categories WHERE key IN ('appetizers', 'main_courses_meat', 'main_courses_fish_seafood', 'main_courses_pasta', 'vegetarian_vegan', 'salads', 'sandwiches_burgers', 'pizzas', 'side_dishes', 'desserts', 'non_alcoholic_beverages', 'alcoholic_beverages_beer_wine', 'alcoholic_beverages_spirits_cocktails', 'coffees_teas', 'kids_menu');
DELETE FROM order_status WHERE key IN ('pending_confirmation', 'in_preparation', 'ready_for_delivery', 'delivered_served', 'item_canceled', 'returned');
DELETE FROM command_status WHERE key IN ('open', 'closed', 'canceled', 'paying', 'partially_paid');
DELETE FROM table_status WHERE key IN ('available', 'occupied', 'reserved', 'cleaning', 'unavailable', 'awaiting_payment');
