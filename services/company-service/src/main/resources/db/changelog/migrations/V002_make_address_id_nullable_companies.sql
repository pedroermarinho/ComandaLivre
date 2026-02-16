--liquibase formatted sql
--changeset pedroermarinho:V002_make_address_id_nullable_companies

-- comment: Torna o campo address_id opcional (nullable) na tabela companies

ALTER TABLE companies
    ALTER COLUMN address_id DROP NOT NULL;

--rollback sqlFile:path=../rollbacks/migrations/v002_make_address_id_nullable_companies_undo.sql
