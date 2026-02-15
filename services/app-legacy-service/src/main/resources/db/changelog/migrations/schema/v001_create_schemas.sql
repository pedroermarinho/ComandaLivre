--liquibase formatted sql

-- changeset pedroermarinho:v001
-- comment: Cria os schemas base para a organização da aplicação (public, company, comandalivre, prumodigital).

CREATE SCHEMA IF NOT EXISTS public;

CREATE SCHEMA IF NOT EXISTS company;
COMMENT ON SCHEMA company IS 'Schema para armazenar dados relacionados a empresas/organizações, compartilhado entre diferentes aplicações/módulos.';

CREATE SCHEMA IF NOT EXISTS comandalivre;
COMMENT ON SCHEMA comandalivre IS 'Schema contendo tabelas e lógica de negócio específicas para a aplicação ComandaLivre.';

CREATE SCHEMA IF NOT EXISTS prumodigital;
COMMENT ON SCHEMA prumodigital IS 'Schema contendo tabelas e lógica de negócio específicas para a aplicação PrumoDigital.';

-- rollback DROP SCHEMA IF EXISTS prumodigital;
-- rollback DROP SCHEMA IF EXISTS comandalivre;
-- rollback DROP SCHEMA IF EXISTS company;