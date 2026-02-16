--liquibase formatted sql
--changeset pedroermarinho:V003_add_domain_to_company_settings

-- comment: Adiciona a coluna 'domain' à tabela company_settings para URLs personalizadas

ALTER TABLE company_settings
ADD COLUMN domain VARCHAR(255) UNIQUE;

COMMENT ON COLUMN company_settings.domain IS 'Domínio personalizado para a URL do restaurante (ex: meu-restaurante.comandalivre.com).';

--rollback sqlFile:path=../rollbacks/migrations/V003_add_domain_to_company_settings_undo.sql
