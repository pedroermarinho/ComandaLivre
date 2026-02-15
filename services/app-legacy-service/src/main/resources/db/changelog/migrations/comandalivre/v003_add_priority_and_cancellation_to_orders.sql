-- liquibase formatted sql

-- changeset pedroermarinho:v003_add_priority_and_cancellation_to_orders

ALTER TABLE comandalivre.order_items
    ADD COLUMN priority_level INT NOT NULL DEFAULT 0,
    ADD COLUMN cancellation_reason TEXT NULL,
    ADD COLUMN cancelled_by_user_id INT NULL,
    ADD CONSTRAINT fk_cancelled_by_user
        FOREIGN KEY (cancelled_by_user_id)
        REFERENCES public.users(id);

COMMENT ON COLUMN comandalivre.order_items.priority_level IS 'Nível de prioridade do pedido (0 = normal, 1 = alta, 2 = crítica, etc.).';
COMMENT ON COLUMN comandalivre.order_items.cancellation_reason IS 'Motivo textual informado para o cancelamento do pedido.';
COMMENT ON COLUMN comandalivre.order_items.cancelled_by_user_id IS 'ID do usuário que realizou o cancelamento do pedido, se aplicável.';

-- rollback ALTER TABLE comandalivre.order_items DROP COLUMN priority_level, DROP COLUMN cancellation_reason, DROP COLUMN cancelled_by_user_id;
