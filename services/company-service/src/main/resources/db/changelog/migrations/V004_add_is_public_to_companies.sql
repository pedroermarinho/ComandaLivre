--liquibase formatted sql
--changeset pedroermarinho:V004_add_is_public_to_companies

-- comment: Adiciona o campo is_public na tabela companies para controlar a visibilidade pública

ALTER TABLE companies
ADD COLUMN is_public BOOLEAN NOT NULL DEFAULT FALSE;

COMMENT ON COLUMN companies.is_public IS 'Indica se a empresa/restaurante é publicamente listável (TRUE) ou não (FALSE).';

--rollback sqlFile:path=../rollbacks/migrations/V004_add_is_public_to_companies_undo.sql
