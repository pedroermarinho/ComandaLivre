-- liquibase formatted sql

-- changeset pedroermarinho:v003

-- comment: Adiciona a coluna 'domain' à tabela company.company_settings para URLs personalizadas

ALTER TABLE company.company_settings
ADD COLUMN domain VARCHAR(255) UNIQUE;

COMMENT ON COLUMN company.company_settings.domain IS 'Domínio personalizado para a URL do restaurante (ex: meu-restaurante.comandalivre.com).';

--rollback ALTER TABLE company.company_settings DROP COLUMN domain;