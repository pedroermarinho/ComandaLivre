--liquibase formatted sql

-- changeset pedroermarinho:v002

-- comment: Torna o campo address_id opcional (nullable) na tabela company.companies

ALTER TABLE company.companies
    ALTER COLUMN address_id DROP NOT NULL;

--rollback ALTER TABLE company.companies ALTER COLUMN address_id SET NOT NULL;
