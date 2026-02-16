-- liquibase formatted sql

-- changeset pedroermarinho:v004

-- comment: Adiciona o campo is_public na tabela companies para controlar a visibilidade pública

ALTER TABLE companies
ADD COLUMN is_public BOOLEAN NOT NULL DEFAULT FALSE;

COMMENT ON COLUMN companies.is_public IS 'Indica se a empresa/restaurante é publicamente listável (TRUE) ou não (FALSE).';

--rollback ALTER TABLE companies DROP COLUMN is_public;
