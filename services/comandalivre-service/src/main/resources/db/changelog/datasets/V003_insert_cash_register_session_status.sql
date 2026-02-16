--liquibase formatted sql
--changeset pedroermarinho:V003_insert_cash_register_session_status
-- comment: Insere dados iniciais na tabela de status de sessão de caixa.

INSERT INTO cash_register_session_status (public_id, key, name, description)
VALUES
('0198ff14-f1ec-7df9-ba2d-14b4e795f78b', 'OPEN', 'Aberta', 'Sessão de caixa está aberta e recebendo transações.'),
('0198ff14-f1ec-7488-9bfc-b1fdc5bda0c0', 'CLOSED', 'Fechada', 'Sessão de caixa finalizada e valores conferidos.'),
('0198ff14-f1ec-7929-80f5-44bb7ee37bf7', 'IN_REVIEW', 'Em Conferência', 'Sessão de caixa fechada, aguardando a conferência dos valores pelo gerente.');

--rollback sqlFile:path=../rollbacks/datasets/V003_insert_cash_register_session_status_undo.sql
