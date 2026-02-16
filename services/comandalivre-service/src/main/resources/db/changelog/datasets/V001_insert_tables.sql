--liquibase formatted sql
--changeset pedroermarinho:V001_insert_tables
-- comment: Insere dados iniciais nas tabelas de status e categorias do schema 'comandalivre'.

INSERT INTO table_status (public_id, key, name, description)
VALUES
('01972df2-1abc-7070-a467-9c55ecc3a8ac', 'available', 'Disponível', 'Mesa livre e pronta para ocupação.'),
('01972df2-1abc-7d8f-80bd-5d3f3c86c896', 'occupied', 'Ocupada', 'Mesa atualmente com clientes.'),
('01972df2-1abc-749d-a6f5-9c5180d9843b', 'reserved', 'Reservada', 'Mesa aguardando a chegada de clientes com reserva.'),
('01972df2-1abc-72c8-9d71-6fb44e65568e', 'cleaning', 'Em Limpeza', 'Mesa sendo preparada para o próximo cliente.'),
('01972df2-1abc-7068-bada-3813d33982fe', 'unavailable', 'Indisponível', 'Mesa temporariamente indisponível para uso (ex: quebrada, área fechada).'),
('01972df2-1abc-723b-bd62-576f5fdfc59c', 'awaiting_payment', 'Aguardando Pagamento', 'Mesa com clientes que solicitaram a conta ou estão em processo de pagamento.');


INSERT INTO command_status (public_id, key, name, description)
VALUES
('01972df2-1abc-757b-8332-fe5cc9e5cbb3', 'open', 'Aberta', 'Comanda ativa, recebendo pedidos.'),
('01972df2-1abc-7e90-af3b-703f17f45b72', 'closed', 'Fechada', 'Comanda finalizada e paga.'),
('01972df2-1abc-78ef-9cc7-f023b0001025', 'canceled', 'Cancelada', 'Comanda cancelada antes da finalização.'),
('01972df2-1abc-7d79-8dea-95b0ecf68426', 'paying', 'Em Pagamento', 'Comanda com a conta solicitada, aguardando o pagamento ser efetuado.'),
('01972df2-1abc-7d74-a368-a640bfcbd2fb', 'partially_paid', 'Parcialmente Paga', 'Comanda com pagamento parcial realizado, comum em divisão de contas.');


INSERT INTO order_status (public_id, key, name, description)
VALUES
('01972df2-1abc-7e6a-92ad-d366cfb1bd12', 'pending_confirmation', 'Pendente Confirmação', 'Pedido recebido pelo sistema, aguardando confirmação da cozinha/bar.'),
('01972df2-1abc-785b-9c1d-a90009d2a2ba', 'in_preparation', 'Em Preparo', 'Pedido confirmado e sendo preparado.'),
('01972df2-1abc-7d2d-ac7d-e1e9c04f7b70', 'ready_for_delivery', 'Pronto para Entrega/Servir', 'Item do pedido finalizado e pronto para ser levado à mesa ou para entrega.'),
('01972df2-1abc-7a51-bc27-d240579664a4', 'delivered_served', 'Entregue/Servido', 'Item do pedido entregue ao cliente na mesa.'),
('01972df2-1abc-79e7-9574-917ef3ac33b0', 'item_canceled', 'Item Cancelado', 'Item específico do pedido foi cancelado (ex: por falta de ingrediente, solicitação do cliente).'),
('01972df2-1abc-75e2-b0e9-147b1c735ac4', 'returned', 'Devolvido', 'Item do pedido foi devolvido pelo cliente por algum motivo.');


INSERT INTO product_categories (public_id, key, name, description)
VALUES
('01972df2-1abc-7b10-a3ae-aa35b759984a', 'appetizers', 'Entradas e Petiscos', 'Seleção de itens para iniciar a refeição ou para compartilhar.'),
('01972df2-1abc-7011-a710-b04544efbc76', 'main_courses_meat', 'Pratos Principais (Carnes)', 'Pratos principais com foco em carnes vermelhas e aves.'),
('01972df2-1abc-79e0-8338-84d594625e53', 'main_courses_fish_seafood', 'Pratos Principais (Peixes e Frutos do Mar)', 'Pratos principais com foco em peixes e frutos do mar.'),
('01972df2-1abc-7e40-8337-42b7ef6ebd98', 'main_courses_pasta', 'Pratos Principais (Massas)', 'Pratos principais com foco em massas e molhos.'),
('01972df2-1abc-7e6c-b1b1-69130707f1c2', 'vegetarian_vegan', 'Pratos Vegetarianos/Veganos', 'Opções sem ingredientes de origem animal.'),
('01972df2-1abc-7a43-a9a1-5684b1b096d6', 'salads', 'Saladas', 'Opções de saladas frescas e acompanhamentos.'),
('01972df2-1abc-7bc7-af16-46c3600fcd94', 'sandwiches_burgers', 'Lanches e Hambúrgueres', 'Sanduíches, hambúrgueres e opções de lanches rápidos.'),
('01972df2-1abc-7781-b328-369466ef222f', 'pizzas', 'Pizzas', 'Pizzas com diversos sabores e tamanhos.'),
('01972df2-1abc-779b-ac68-6a1684d4bedd', 'side_dishes', 'Acompanhamentos', 'Porções extras para complementar os pratos principais.'),
('01972df2-1abc-7edc-be21-1587b916a505', 'desserts', 'Sobremesas', 'Doces, bolos, sorvetes e outras opções para finalizar a refeição.'),
('01972df2-1abc-7e5f-a67d-357d48292978', 'non_alcoholic_beverages', 'Bebidas Não Alcoólicas', 'Refrigerantes, sucos, águas e outras bebidas sem álcool.'),
('01972df2-1abc-78c1-9989-dc4baf52ffbb', 'alcoholic_beverages_beer_wine', 'Bebidas Alcoólicas (Cervejas e Vinhos)', 'Seleção de cervejas e vinhos.'),
('01972df2-1abc-7585-a851-cbb31ec986c6', 'alcoholic_beverages_spirits_cocktails', 'Bebidas Alcoólicas (Destilados e Cocktails)', 'Destilados, drinks e cocktails.'),
('01972df2-1abc-7634-a6b5-e5c42f614cdc', 'coffees_teas', 'Cafés e Chás', 'Variedade de cafés, chás e infusões.'),
('01972df2-1abc-71ea-a386-296ddd89880f', 'kids_menu', 'Menu Infantil', 'Pratos e opções especialmente desenvolvidos para crianças.');

--rollback sqlFile:path=../rollbacks/datasets/V001_insert_tables_undo.sql
