-- liquibase formatted sql

-- changeset pedroermarinho:v004_add_cancellation_to_commands

ALTER TABLE comandalivre.commands
    ADD COLUMN cancellation_reason TEXT NULL,
    ADD COLUMN cancelled_by_user_id INT NULL,
    ADD CONSTRAINT fk_commands_cancelled_by_user
        FOREIGN KEY (cancelled_by_user_id)
        REFERENCES public.users(id);

COMMENT ON COLUMN comandalivre.commands.cancellation_reason IS 'Motivo detalhado informado para o cancelamento da comanda.';
COMMENT ON COLUMN comandalivre.commands.cancelled_by_user_id IS 'ID do usuário responsável pelo cancelamento da comanda.';

-- rollback ALTER TABLE comandalivre.commands DROP COLUMN cancellation_reason, DROP COLUMN cancelled_by_user_id;
