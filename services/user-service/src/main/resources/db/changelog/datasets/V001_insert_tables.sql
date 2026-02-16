--liquibase formatted sql
--changeset pedroermarinho:V001_insert_tables

--comment: Insere dados iniciais nas tabelas feature_flags, features_catalog e feature_groups do schema public.

INSERT INTO public.feature_flags (public_id, name, description, key_flag, enabled)
VALUES
('01972d4d-f034-72ca-81a6-2accffbffba3', 'IA na descrição dos produtos', 'Utiliza inteligência artificial para sugerir descrições de produtos automaticamente.', 'ai_product_description', FALSE),
('01972d4d-f034-7e3c-b0b6-507d1e9ba434', 'Integração com S3', 'Permite armazenar e recuperar imagens e documentos do S3.', 's3_integration', TRUE),
('01972d4d-f034-753b-8b6b-17a1dce60019', 'Envio de e-mail', 'Possibilita o envio automático de e-mails para clientes e funcionários.', 'email_sending', TRUE),
('01972d4d-f034-7de6-885f-4ce56aed660e', 'Envio de mensagens para o WhatsApp', 'Integração com WhatsApp para envio de mensagens automáticas.', 'whatsapp_messaging', FALSE),
('01972d4d-f034-733e-8eb0-614a1ae64bc5', 'Recomendações Personalizadas', 'Sugere produtos com base no histórico de pedidos do cliente.', 'personalized_recommendations', FALSE),
('01972d4d-f034-7ca9-9883-857f3125ace5', 'Ordenação por proximidade', 'Mostra os restaurantes mais próximos primeiro.', 'proximity_sorting', FALSE),
('01972d4d-f034-757f-9918-0344b9421827', 'Integração com Discord', 'Permite integração com o Discord para notificações e atualizações.', 'discord_webhook', TRUE);



-- Inserindo dados em public.features_catalog (Features Atribuíveis a Grupos de Usuários)
INSERT INTO public.features_catalog (public_id, feature_key, name, description)
VALUES
('01972d4c-527e-7d7b-8ac9-73022df5a607', 'comandalivre_self_order', 'Autoatendimento ComandaLivre', 'Permite que clientes de restaurantes façam seus próprios pedidos através do aplicativo ComandaLivre.'),
('01972d4c-527e-78b8-80d9-95a64bb6934d', 'comandalivre_access', 'Acesso ao Aplicativo ComandaLivre', 'Permite o login e uso das funcionalidades básicas do aplicativo ComandaLivre.'),
('01972d4c-527e-74b3-9450-01eaab5dbbdc', 'prumodigital_access', 'Acesso ao Aplicativo PrumoDigital', 'Permite o login e uso das funcionalidades básicas do aplicativo PrumoDigital.'),
('01972d4c-527e-7afe-94fe-360044a34336', 'admin_dashboard_access', 'Acesso ao Dashboard Administrativo', 'Permite acesso ao painel de administração com visualizações e controles gerais do sistema ou da empresa/restaurante.'),
('01972d4c-527e-7786-a892-727a2ee432bf', 'whatsapp_integration', 'Integração com WhatsApp', 'Habilita funcionalidades de integração com o WhatsApp'),
('01972d4c-527e-7a14-909d-dd66ada407ff', 'command_creation_access', 'Criação e Gerenciamento de Comandas', 'Permite que usuários criem e gerenciem comandas.'),
('01972d4c-527e-792a-9580-a985830b9400', 'product_modifiers_management', 'Gerenciamento de Modificadores de Produto', 'Permite que administradores de restaurante criem e gerenciem grupos e opções de modificadores para produtos.'),
('01972d4c-527e-7d70-86e9-d8ea5fbf51ee', 'table_management_access', 'Gerenciamento de Mesas', 'Permite criar, editar e gerenciar as mesas de um restaurante.'),
('01972d4c-527e-7642-9915-431399933a2b', 'advanced_reporting_access', 'Acesso a Relatórios Avançados', 'Permite visualização e exportação de relatórios detalhados sobre vendas, desempenho, etc.'),
('01972d4c-527e-717c-820f-fd30bbf0ab8b', 'user_role_management', 'Gerenciamento de Cargos de Usuários', 'Permite atribuir ou modificar cargos de usuários dentro de uma empresa/restaurante.');


-- Inserindo dados em public.feature_groups
INSERT INTO public.feature_groups (public_id, group_key, name, description)
VALUES
('01972d4c-527e-7a4e-a28f-1c9905b533d5', 'default_user_cl', 'Usuário Padrão ComandaLivre', 'Acesso básico para todos os usuários do ComandaLivre.'),
('01972d4c-527e-73d4-910c-26d7ac2dc1ea', 'default_user_pd', 'Usuário Padrão PrumoDigital', 'Acesso básico para todos os usuários do PrumoDigital.'),
('01972d4c-527e-78dd-b05b-c6c880043f37', 'admin_system', 'Administradores do Sistema', 'Acesso completo a todas as funcionalidades administrativas globais e de configuração.'),
('01972d4c-527e-7478-a545-2bdbc468233c', 'developers', 'Desenvolvedores', 'Acesso a features em desenvolvimento/preview e ferramentas de depuração.'),
('01972d4c-527e-7c8c-bf06-49fcaa15bece', 'beta_testers', 'Testadores Beta', 'Grupo para testar novas funcionalidades antes do lançamento geral.'),
('01972d4c-527e-781e-b73b-cecbbb5fb01b', 'qa_team', 'Equipe de QA', 'Acesso a funcionalidades para fins de Quality Assurance e testes de regressão.'),
('01972d4c-527e-73fa-8013-b8c2f62e2d6d', 'premium_restaurant_cl', 'Restaurante Premium (ComandaLivre)', 'Restaurantes com acesso a features avançadas no ComandaLivre.'),
('01972d4c-527e-73db-b027-736346fd9c51', 'enterprise_project_pd', 'Projeto Enterprise (PrumoDigital)', 'Projetos com acesso a funcionalidades enterprise no PrumoDigital.');

--rollback sqlFile:path=../rollbacks/datasets/V001_insert_tables_undo.sql
