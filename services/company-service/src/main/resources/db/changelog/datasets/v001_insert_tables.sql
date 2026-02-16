-- liquibase formatted sql

-- changeset pedroermarinho:v001

-- comment: Insere dados iniciais focados nos tipos de empresa Restaurante e Empresa de Construção, e seus respectivos cargos.


INSERT INTO company_types (public_id, key, name, description)
VALUES
('01972dd2-3744-7f7a-9f42-c3d807be60bc', 'restaurant', 'Restaurante', 'Estabelecimento focado na preparação e serviço de alimentos e bebidas para consumo no local, entrega ou retirada.'),
('01972dd2-3744-7691-b03e-0f7b98359361', 'construction_company', 'Empresa de Construção', 'Organização especializada na execução de obras e projetos de construção civil.');

--rollback DELETE FROM company_types WHERE key IN ('restaurant', 'construction_company');


INSERT INTO role_types (public_id, key, name, description, company_type_id)
VALUES
('01972dd2-3744-7292-9588-b8fe13423ad1', 'restaurant_owner', 'Proprietário(a) de Restaurante', 'Responsável legal, estratégico e pela gestão geral do restaurante.', (SELECT id FROM company_types WHERE key = 'restaurant')),
('01972dd2-3744-7443-8837-652ea7309044', 'restaurant_manager', 'Gerente de Restaurante', 'Supervisiona as operações diárias, equipe, estoque e atendimento ao cliente do restaurante.', (SELECT id FROM company_types WHERE key = 'restaurant')),
('01972dd2-3744-7a63-b9dc-bac441e664d9', 'waiter', 'Garçom / Garçonete', 'Atende os clientes nas mesas, anota pedidos, serve alimentos e bebidas, e processa pagamentos.', (SELECT id FROM company_types WHERE key = 'restaurant')),
('01972dd2-3744-7058-b8b5-cdd1f1e1f86b', 'kitchen_chef', 'Chefe de Cozinha', 'Lidera a equipe da cozinha, responsável pelo cardápio, preparo e qualidade dos pratos.', (SELECT id FROM company_types WHERE key = 'restaurant')),
('01972dd2-3744-7a4b-9c59-fd53a7e19498', 'kitchen_staff', 'Auxiliar de Cozinha', 'Auxilia no preparo dos alimentos, limpeza e organização da cozinha.', (SELECT id FROM company_types WHERE key = 'restaurant')),
('01972dd2-3744-7ad4-8106-5ea1f596772b', 'bartender', 'Barman / Bartender', 'Prepara e serve bebidas alcoólicas e não alcoólicas. Comum em restaurantes com bar.', (SELECT id FROM company_types WHERE key = 'restaurant')),
('01972dd2-3744-7391-8183-f8bb559276b7', 'cashier_restaurant', 'Operador(a) de Caixa (Restaurante)', 'Responsável pelo caixa, processamento de pagamentos e fechamento financeiro do turno no restaurante.', (SELECT id FROM company_types WHERE key = 'restaurant')),
('01972dd2-3744-7757-93f7-b6a584cc7543', 'host_hostess', 'Host / Hostess', 'Recepciona os clientes, gerencia a lista de espera e acompanha os clientes até suas mesas.', (SELECT id FROM company_types WHERE key = 'restaurant'));

INSERT INTO role_types (public_id, key, name, description, company_type_id)
VALUES
('01972dd2-3744-7a77-ae7e-4e5bc3742140', 'construction_owner', 'Proprietário(a) de Construtora', 'Responsável legal e estratégico pela empresa de construção.', (SELECT id FROM company_types WHERE key = 'construction_company')),
('01972dd2-3744-7e46-8445-7333d0b880a9', 'construction_manager', 'Gerente de Obras / Construção', 'Supervisiona e gerencia todas as fases de um ou mais projetos de construção.', (SELECT id FROM company_types WHERE key = 'construction_company')),
('01972dd2-3744-7909-8116-8cb370a0b626', 'civil_engineer', 'Engenheiro(a) Civil', 'Responsável pelo planejamento, projeto, execução e supervisão de obras de construção civil.', (SELECT id FROM company_types WHERE key = 'construction_company')),
('01972dd2-3744-7fa0-862d-e3e03a048227', 'master_builder', 'Mestre de Obras', 'Lidera e coordena as equipes de trabalhadores no canteiro de obras, garantindo a execução conforme o projeto.', (SELECT id FROM company_types WHERE key = 'construction_company')),
('01972dd2-3744-7283-89e8-d24d49d9d3d0', 'bricklayer', 'Pedreiro(a)', 'Executa trabalhos de alvenaria, revestimento e outras tarefas construtivas.', (SELECT id FROM company_types WHERE key = 'construction_company')),
('01972dd2-3744-7e5b-9802-09b5d6fc8ac8', 'painter_construction', 'Pintor(a) de Obras', 'Responsável pela pintura de edificações e estruturas.', (SELECT id FROM company_types WHERE key = 'construction_company')),
('01972dd2-3744-7950-918f-cc2357fa04d5', 'welder_construction', 'Soldador(a) de Estruturas', 'Realiza trabalhos de soldagem em estruturas metálicas e componentes da construção.', (SELECT id FROM company_types WHERE key = 'construction_company')),
('01972dd2-3744-7df3-a1de-7f1272078551', 'electrician_construction', 'Eletricista Predial/Industrial', 'Responsável pelas instalações elétricas em projetos de construção.', (SELECT id FROM company_types WHERE key = 'construction_company')),
('01972dd2-3744-7577-9a5b-ff744877df60', 'plumber_construction', 'Encanador(a) / Bombeiro Hidráulico', 'Responsável pelas instalações hidráulicas e de saneamento.', (SELECT id FROM company_types WHERE key = 'construction_company')),
('01972dd2-3744-7889-9dce-fe9101752d81', 'admin_staff_construction', 'Administrativo de Obras', 'Realiza suporte administrativo para projetos de construção, como controle de documentos e suprimentos.', (SELECT id FROM company_types WHERE key = 'construction_company'));

--rollback DELETE FROM role_types WHERE company_type_id IN (SELECT id FROM company_types WHERE key IN ('restaurant', 'construction_company'));
