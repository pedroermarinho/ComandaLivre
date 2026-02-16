--liquibase formatted sql
--changeset pedroermarinho:V002_create_application_versions_table

--comment: Cria a tabela para armazenar as versões da aplicação.

CREATE TABLE public.application_versions (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    application_version VARCHAR(255) NOT NULL,
    platform VARCHAR(50) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1
);

COMMENT ON TABLE public.application_versions IS 'Tabela para armazenar o histórico de versões de cada plataforma da aplicação (PWA, ANDROID, IOS).';
COMMENT ON COLUMN public.application_versions.id IS 'Identificador único da versão (UUID).';
COMMENT ON COLUMN public.application_versions.application_version IS 'A versão da aplicação (ex: "1.2.0").';
COMMENT ON COLUMN public.application_versions.platform IS 'A plataforma a que a versão se refere (ex: "PWA", "ANDROID", "IOS").';

CREATE INDEX IF NOT EXISTS idx_application_versions_platform_created_at ON public.application_versions(platform, created_at DESC);

--rollback sqlFile:path=../rollbacks/migrations/V002_create_application_versions_table_undo.sql
