--liquibase formatted sql

-- changeset pedroermarinho:v002

-- comment: Insere os status iniciais para convites de funcionários no schema


INSERT INTO employee_invite_status (public_id, key, name, description, created_by, updated_by, version)
VALUES
(gen_random_uuid(), 'pending', 'Pendente', 'O convite foi enviado e aguarda a resposta do usuário.', 'INITIAL_SETUP', 'INITIAL_SETUP', 1),
(gen_random_uuid(), 'accepted', 'Aceito', 'O usuário aceitou o convite e o vínculo de funcionário foi criado.', 'INITIAL_SETUP', 'INITIAL_SETUP', 1),
(gen_random_uuid(), 'rejected', 'Recusado', 'O usuário recusou explicitamente o convite.', 'INITIAL_SETUP', 'INITIAL_SETUP', 1),
(gen_random_uuid(), 'expired', 'Expirado', 'O convite não foi respondido dentro do prazo de validade e foi invalidado automaticamente pelo sistema.', 'INITIAL_SETUP', 'INITIAL_SETUP', 1);

--rollback DELETE FROM employee_invite_status WHERE key IN ('pending', 'accepted', 'rejected', 'expired');
