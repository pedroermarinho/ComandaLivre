--liquibase formatted sql
--changeset pedroermarinho:V004_add_cancellation_to_commands

ALTER TABLE commands
    ADD COLUMN cancellation_reason TEXT NULL,
    ADD COLUMN cancelled_by_user_id INT NULL;

COMMENT ON COLUMN commands.cancellation_reason IS 'Motivo detalhado informado para o cancelamento da comanda.';
COMMENT ON COLUMN commands.cancelled_by_user_id IS 'ID do usuário responsável pelo cancelamento da comanda.';

--rollback sqlFile:path=../rollbacks/migrations/V004_add_cancellation_to_commands_undo.sql
