-- liquibase formatted sql

-- changeset pedroermarinho:v005_add_discount_fields_to_commands

ALTER TABLE comandalivre.commands
ADD COLUMN discount_amount DECIMAL(10, 2) NULL,
ADD COLUMN discount_description TEXT NULL;

COMMENT ON COLUMN comandalivre.commands.discount_amount IS 'Valor total do desconto aplicado à comanda, em reais. Pode ser nulo se não houver desconto.';
COMMENT ON COLUMN comandalivre.commands.discount_description IS 'Descrição textual do motivo ou origem do desconto aplicado à comanda.';

-- rollback ALTER TABLE comandalivre.commands DROP COLUMN discount_amount, DROP COLUMN discount_description;
