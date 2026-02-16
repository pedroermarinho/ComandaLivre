--liquibase formatted sql
--changeset pedroermarinho:V005_add_discount_fields_to_commands

ALTER TABLE commands
ADD COLUMN discount_amount DECIMAL(10, 2) NULL,
ADD COLUMN discount_description TEXT NULL;

COMMENT ON COLUMN commands.discount_amount IS 'Valor total do desconto aplicado à comanda, em reais. Pode ser nulo se não houver desconto.';
COMMENT ON COLUMN commands.discount_description IS 'Descrição textual do motivo ou origem do desconto aplicado à comanda.';

--rollback sqlFile:path=../rollbacks/migrations/V005_add_discount_fields_to_commands_undo.sql
