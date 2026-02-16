--liquibase formatted sql
--changeset pedroermarinho:V008_refine_product_modifiers_tables
-- comment: Refina as tabelas de modificadores de produto, adicionando constraints e renomeando colunas para maior clareza.

-- Adiciona a checagem de lógica de seleção para grupos de modificadores
ALTER TABLE product_modifiers_groups
ADD CONSTRAINT chk_selection_logic CHECK (min_selection <= max_selection);

-- Renomeia a tabela de detalhes do item do pedido para maior clareza
ALTER TABLE order_item_details RENAME TO order_item_modifiers;

-- Renomeia a coluna de preço para consistência
ALTER TABLE order_item_modifiers RENAME COLUMN price_at_selection TO price_at_order;

-- Adiciona constraint de unicidade para evitar duplicatas
ALTER TABLE order_item_modifiers
ADD CONSTRAINT uq_order_item_modifier UNIQUE (order_item_id, modifier_option_id);

-- Renomeia colunas na tabela de itens de pedido para maior clareza
ALTER TABLE order_items RENAME COLUMN item_price_at_order TO base_price_at_order;
ALTER TABLE order_items RENAME COLUMN total_modifiers_price TO total_modifiers_price_at_order;

--rollback sqlFile:path=../rollbacks/migrations/V008__refine_product_modifiers_tables_undo.sql
