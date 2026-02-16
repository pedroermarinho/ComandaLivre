--liquibase formatted sql

-- changeset pedroermarinho:v002

-- comment: Torna o campo address_id opcional (nullable) na tabela companies

ALTER TABLE companies
    ALTER COLUMN address_id DROP NOT NULL;

--rollback ALTER TABLE companies ALTER COLUMN address_id SET NOT NULL;
