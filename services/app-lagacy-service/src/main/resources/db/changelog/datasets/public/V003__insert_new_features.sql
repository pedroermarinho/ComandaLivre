-- liquibase formatted sql

-- changeset pedroermarinho:v003

-- comment: Insere novas funcionalidades no catálogo de features: beta_feature, company_creation, invite_staff e qr_code_access.


INSERT INTO public.features_catalog (public_id, feature_key, name, description)
VALUES
('019791a9-705b-7774-a359-debbb770699e', 'beta_feature', 'Funcionalidade em Beta', 'Ativa funcionalidades experimentais ainda não finalizadas.'),
('019791a9-705b-7de6-97b3-85f4ef7698b1', 'company_creation', 'Criação de Empresas', 'Permite criar e registrar novas empresas na plataforma.'),
('019791a9-705b-7dfb-8931-8aed56d46c0e', 'invite_staff', 'Enviar Convite para Funcionários', 'Permite convidar usuários para se tornarem funcionários de uma empresa.'),
('019791a9-705b-788c-9945-c48d78d04021', 'qr_code_access', 'Acesso via QR Code', 'Permite gerar, visualizar e gerenciar QR Codes de mesas.');

--rollback DELETE FROM public.features_catalog WHERE feature_key IN ('beta_feature', 'company_creation', 'invite_staff', 'qr_code_access');
