-- liquibase formatted sql

-- changeset pedroermarinho:v001

-- comment: Criação inicial das tabelas principais no schema comandalivre

CREATE TABLE comandalivre.table_status (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    key VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1
);

COMMENT ON TABLE comandalivre.table_status IS 'Define os possíveis status para as mesas de um restaurante (ex: disponível, ocupada, reservada).';
COMMENT ON COLUMN comandalivre.table_status.key IS 'Chave textual única para referência programática do status.';
COMMENT ON COLUMN comandalivre.table_status.name IS 'Nome legível do status.';
COMMENT ON COLUMN comandalivre.table_status.description IS 'Descrição opcional do status';

--rollback DROP TABLE IF EXISTS comandalivre.table_status;

CREATE TABLE comandalivre.command_status (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    key VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1
);

COMMENT ON TABLE comandalivre.command_status IS 'Define os possíveis status para as comandas (ex: aberta, fechada, cancelada).';
COMMENT ON COLUMN comandalivre.command_status.key IS 'Chave textual única para referência programática do status.';
COMMENT ON COLUMN comandalivre.command_status.name IS 'Nome legível do status.';
COMMENT ON COLUMN comandalivre.command_status.description IS 'Descrição opcional do status';

--rollback DROP TABLE IF EXISTS comandalivre.command_status;

CREATE TABLE comandalivre.order_status (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    key VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1
);

COMMENT ON TABLE comandalivre.order_status IS 'Define os possíveis status para os itens de um pedido dentro de uma comanda (ex: pendente, em preparo, entregue).';
COMMENT ON COLUMN comandalivre.order_status.key IS 'Chave textual única para referência programática do status.';
COMMENT ON COLUMN comandalivre.order_status.name IS 'Nome legível do status.';
COMMENT ON COLUMN comandalivre.order_status.description IS 'Descrição opcional do status';

--rollback DROP TABLE IF EXISTS comandalivre.order_status

CREATE TABLE comandalivre.product_categories (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    key VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1
);

COMMENT ON TABLE comandalivre.product_categories IS 'Categorias para os produtos do cardápio (ex: Entradas, Pratos Principais, Bebidas).';
COMMENT ON COLUMN comandalivre.product_categories.key IS 'Chave textual única para referência programática do status.';
COMMENT ON COLUMN comandalivre.product_categories.name IS 'Nome legível do status.';
COMMENT ON COLUMN comandalivre.product_categories.description IS 'Descrição opcional do status';

--rollback DROP TABLE IF EXISTS comandalivre.product_categories;


CREATE TABLE comandalivre.tables (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    name VARCHAR(50) NOT NULL,
    num_people INT NOT NULL DEFAULT 0,
    status_id INT NOT NULL,
    last_status_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description TEXT,
    company_id INT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    CONSTRAINT fk_rtables_status FOREIGN KEY (status_id) REFERENCES comandalivre.table_status(id) ON DELETE RESTRICT,
    CONSTRAINT fk_rtables_company FOREIGN KEY (company_id) REFERENCES company.companies(id) ON DELETE CASCADE
);

COMMENT ON TABLE comandalivre.tables IS 'Mesas de um restaurante.';
COMMENT ON COLUMN comandalivre.tables.name IS 'Nome ou número identificador da mesa.';
COMMENT ON COLUMN comandalivre.tables.num_people IS 'Capacidade de pessoas da mesa.';
COMMENT ON COLUMN comandalivre.tables.status_id IS 'FK para o status atual da mesa.';
COMMENT ON COLUMN comandalivre.tables.last_status_update IS 'Timestamp da última atualização de status da mesa.';
COMMENT ON COLUMN comandalivre.tables.company_id IS 'FK para a empresa/restaurante ao qual a mesa pertence.';

CREATE INDEX IF NOT EXISTS idx_rtables_company_id ON comandalivre.tables (company_id);
CREATE INDEX IF NOT EXISTS idx_rtables_status_id ON comandalivre.tables (status_id);

--rollback DROP INDEX IF EXISTS comandalivre.idx_rtables_status_id;
--rollback DROP INDEX IF EXISTS comandalivre.idx_rtables_company_id;
--rollback DROP TABLE IF EXISTS comandalivre.tables;


-- Comandas
CREATE TABLE comandalivre.commands (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    command_name VARCHAR(255) NOT NULL,
    number_of_people INT NOT NULL DEFAULT 1,
    total_amount NUMERIC(10, 2) NULL,
    status_id INT NOT NULL,
    table_id INT NOT NULL,
    employee_id INT NULL,
    user_id INT NULL,
    company_id INT NOT NULL,


    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    CONSTRAINT fk_commands_employee FOREIGN KEY (employee_id) REFERENCES company.employees(id) ON DELETE RESTRICT,
    CONSTRAINT fk_commands_status FOREIGN KEY (status_id) REFERENCES comandalivre.command_status(id) ON DELETE RESTRICT,
    CONSTRAINT fk_commands_table FOREIGN KEY (table_id) REFERENCES comandalivre.tables(id) ON DELETE RESTRICT,
    CONSTRAINT fk_commands_user FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_commands_company FOREIGN KEY (company_id) REFERENCES company.companies(id) ON DELETE CASCADE
);

COMMENT ON TABLE comandalivre.commands IS 'Comandas abertas em mesas de restaurantes.';
COMMENT ON COLUMN comandalivre.commands.company_id IS 'FK para a empresa/restaurante da comanda.';
COMMENT ON COLUMN comandalivre.commands.command_name IS 'Nome ou identificador da comanda.';
COMMENT ON COLUMN comandalivre.commands.number_of_people IS 'Número de pessoas associadas à comanda.';
COMMENT ON COLUMN comandalivre.commands.total_amount IS 'Valor total da comanda.';
COMMENT ON COLUMN comandalivre.commands.status_id IS 'FK para o status atual da comanda.';
COMMENT ON COLUMN comandalivre.commands.table_id IS 'FK para a mesa associada à comanda.';
COMMENT ON COLUMN comandalivre.commands.employee_id IS 'FK para o funcionário responsável pela comanda.';
COMMENT ON COLUMN comandalivre.commands.user_id IS 'FK para o usuário associado à comanda.';

CREATE INDEX IF NOT EXISTS idx_commands_table_id ON comandalivre.commands (table_id);
CREATE INDEX IF NOT EXISTS idx_commands_employee_id_company_id ON comandalivre.commands (employee_id,company_id);
CREATE INDEX IF NOT EXISTS idx_commands_status_id_company_id ON comandalivre.commands (status_id, company_id);
CREATE INDEX IF NOT EXISTS idx_commands_company_id ON comandalivre.commands (company_id);


CREATE TABLE comandalivre.products (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    name VARCHAR(255) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    serves_persons INT NOT NULL DEFAULT 1,
    image_asset_id INT NULL,
    description TEXT NULL,
    ingredients TEXT[] NULL,
    availability BOOLEAN DEFAULT TRUE, -- Disponibilidade
    category_id INT NOT NULL,
    company_id INT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES comandalivre.product_categories(id) ON DELETE RESTRICT,
    CONSTRAINT fk_products_company FOREIGN KEY (company_id) REFERENCES company.companies(id) ON DELETE CASCADE,
    CONSTRAINT fk_products_image_asset FOREIGN KEY (image_asset_id) REFERENCES public.assets(id) ON DELETE SET NULL ON UPDATE CASCADE
);

COMMENT ON TABLE comandalivre.products IS 'Produtos (itens de cardápio) oferecidos pelos restaurantes.';
COMMENT ON COLUMN comandalivre.products.company_id IS 'FK para a empresa/restaurante que oferece este produto.';
COMMENT ON COLUMN comandalivre.products.image_asset_id IS 'FK para o asset da imagem principal do produto.';
COMMENT ON COLUMN comandalivre.products.serves_persons IS 'Número estimado de pessoas que este prato serve. Nulo se não informado.';
COMMENT ON COLUMN comandalivre.products.ingredients IS 'Lista de ingredientes principais do produto.';
COMMENT ON COLUMN comandalivre.products.description IS 'Descrição detalhada do produto.';
COMMENT ON COLUMN comandalivre.products.availability IS 'Indica se o produto está disponível para venda.';
COMMENT ON COLUMN comandalivre.products.category_id IS 'FK para a categoria do produto.';
COMMENT ON COLUMN comandalivre.products.name IS 'Nome do produto.';
COMMENT ON COLUMN comandalivre.products.price IS 'Preço do produto.';


CREATE INDEX idx_products_name_category ON comandalivre.products(name, category_id);
CREATE INDEX idx_products_company_id ON comandalivre.products(company_id);

--rollback DROP INDEX IF EXISTS comandalivre.idx_products_company_id;
--rollback DROP INDEX IF EXISTS comandalivre.idx_products_name_category;
--rollback DROP TABLE IF EXISTS comandalivre.products;

CREATE TABLE comandalivre.product_modifiers_groups (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    name VARCHAR(255) NOT NULL,
    product_id INT NOT NULL,
    min_selection INT NOT NULL DEFAULT 0,
    max_selection INT NOT NULL DEFAULT 1,
    display_order INT NOT NULL DEFAULT 0,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    CONSTRAINT fk_pmg_product FOREIGN KEY (product_id) REFERENCES comandalivre.products(id) ON DELETE CASCADE
);

COMMENT ON TABLE comandalivre.product_modifiers_groups IS 'Grupos de opções de customização para um produto (ex: Adicionais, Ponto da Carne).';
COMMENT ON COLUMN comandalivre.product_modifiers_groups.product_id IS 'Referência ao produto (FK para comandalivre.products.id) ao qual este grupo de modificadores pertence.';
COMMENT ON COLUMN comandalivre.product_modifiers_groups.name IS 'Nome legível para humanos do grupo de modificadores (ex: "Adicionais para X-Burger", "Escolha o Ponto").';
COMMENT ON COLUMN comandalivre.product_modifiers_groups.min_selection IS 'Número mínimo de opções que devem ser selecionadas dentro deste grupo.';
COMMENT ON COLUMN comandalivre.product_modifiers_groups.max_selection IS 'Número máximo de opções que podem ser selecionadas dentro deste grupo (ex: 1 para "Ponto da Carne", N para "Adicionais").';
COMMENT ON COLUMN comandalivre.product_modifiers_groups.display_order IS 'Ordem de exibição deste grupo de modificadores na interface do usuário.';

CREATE INDEX IF NOT EXISTS idx_pmg_product_id ON comandalivre.product_modifiers_groups(product_id);

--rollback DROP INDEX IF EXISTS comandalivre.idx_pmg_product_id;
--rollback DROP TABLE IF EXISTS comandalivre.product_modifiers_groups;

CREATE TABLE comandalivre.product_modifiers_options (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    name VARCHAR(255) NOT NULL,
    modifier_group_id INT NOT NULL,
    price_change NUMERIC(10, 2) NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INT NOT NULL DEFAULT 0,
    image_asset_id INT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    CONSTRAINT fk_pmo_modifier_group FOREIGN KEY (modifier_group_id) REFERENCES comandalivre.product_modifiers_groups(id) ON DELETE CASCADE,
    CONSTRAINT fk_pmo_image_asset FOREIGN KEY (image_asset_id) REFERENCES public.assets(id) ON DELETE SET NULL ON UPDATE CASCADE
);


COMMENT ON TABLE comandalivre.product_modifiers_options IS 'Opções individuais dentro de um grupo de modificadores (ex: Queijo Extra, Sem Cebola, Ao Ponto).';
COMMENT ON COLUMN comandalivre.product_modifiers_options.modifier_group_id IS 'Referência ao grupo de modificadores (FK para comandalivre.product_modifiers_groups.id) ao qual esta opção pertence.';
COMMENT ON COLUMN comandalivre.product_modifiers_options.name IS 'Nome legível para humanos da opção de modificador (ex: "Queijo Cheddar Extra", "Sem Picles").';
COMMENT ON COLUMN comandalivre.product_modifiers_options.price_change IS 'Variação de preço (positiva ou negativa) que esta opção aplica ao produto base.';
COMMENT ON COLUMN comandalivre.product_modifiers_options.is_default IS 'Indica se esta opção é selecionada por padrão quando o grupo é apresentado.';
COMMENT ON COLUMN comandalivre.product_modifiers_options.display_order IS 'Ordem de exibição desta opção dentro do seu grupo na interface do usuário.';
COMMENT ON COLUMN comandalivre.product_modifiers_options.image_asset_id IS 'FK opcional para public.assets(id), para uma imagem ilustrativa da opção do modificador.';

CREATE INDEX IF NOT EXISTS idx_pmo_modifier_group_id ON comandalivre.product_modifiers_options(modifier_group_id);

--rollback DROP INDEX IF EXISTS comandalivre.idx_pmo_modifier_group_id;
--rollback DROP TABLE IF EXISTS comandalivre.product_modifiers_options;



CREATE TABLE comandalivre.order_items (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    command_id INT NOT NULL,
    product_id INT NOT NULL,
    status_id INT NOT NULL,
    notes TEXT NULL,
    item_price_at_order NUMERIC(10, 2) NULL,
    total_modifiers_price NUMERIC(10, 2) NULL,


    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    CONSTRAINT fk_orderitems_command FOREIGN KEY (command_id) REFERENCES comandalivre.commands(id) ON DELETE CASCADE,
    CONSTRAINT fk_orderitems_product FOREIGN KEY (product_id) REFERENCES comandalivre.products(id) ON DELETE RESTRICT,
    CONSTRAINT fk_orderitems_status FOREIGN KEY (status_id) REFERENCES comandalivre.order_status(id) ON DELETE RESTRICT

);

COMMENT ON TABLE comandalivre.order_items IS 'Itens de pedido individuais dentro de uma comanda, incluindo preço base e soma de modificadores.';
COMMENT ON COLUMN comandalivre.order_items.command_id IS 'Referência à comanda (FK para comandalivre.commands.id) à qual este item pertence.';
COMMENT ON COLUMN comandalivre.order_items.product_id IS 'Referência ao produto base (FK para comandalivre.products.id) deste item.';
COMMENT ON COLUMN comandalivre.order_items.status_id IS 'Referência ao status atual (FK para comandalivre.order_status.id) deste item de pedido.';
COMMENT ON COLUMN comandalivre.order_items.notes IS 'Observações específicas do cliente para este item do pedido (ex: "sem pimenta", "ponto da carne").';
COMMENT ON COLUMN comandalivre.order_items.item_price_at_order IS 'Preço unitário base do produto no momento em que o pedido foi feito (para histórico).';
COMMENT ON COLUMN comandalivre.order_items.total_modifiers_price IS 'Soma dos preços de todos os modificadores selecionados para este item do pedido.';

CREATE INDEX IF NOT EXISTS idx_orderitems_command_id ON comandalivre.order_items (command_id);
CREATE INDEX IF NOT EXISTS idx_orderitems_product_id ON comandalivre.order_items (product_id);
CREATE INDEX IF NOT EXISTS idx_orderitems_status_id ON comandalivre.order_items (status_id);

--rollback DROP INDEX IF EXISTS comandalivre.idx_orderitems_status_id;
--rollback DROP INDEX IF EXISTS comandalivre.idx_orderitems_product_id;
--rollback DROP INDEX IF EXISTS comandalivre.idx_orderitems_command_id;
--rollback DROP TABLE IF EXISTS comandalivre.order_items;


CREATE TABLE comandalivre.order_item_details (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    order_item_id INT NOT NULL,
    modifier_option_id INT NOT NULL,
    price_at_selection NUMERIC(10, 2) NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    CONSTRAINT fk_oid_order_item FOREIGN KEY (order_item_id) REFERENCES comandalivre.order_items(id) ON DELETE CASCADE,
    CONSTRAINT fk_oid_modifier_option FOREIGN KEY (modifier_option_id) REFERENCES comandalivre.product_modifiers_options(id) ON DELETE RESTRICT
);

COMMENT ON TABLE comandalivre.order_item_details IS 'Registra as opções de modificadores selecionadas para um item específico de um pedido.';
COMMENT ON COLUMN comandalivre.order_item_details.order_item_id IS 'Referência ao item do pedido (FK para comandalivre.order_items.id) ao qual esta customização pertence.';
COMMENT ON COLUMN comandalivre.order_item_details.modifier_option_id IS 'Referência à opção de modificador selecionada (FK para comandalivre.product_modifiers_options.id).';
COMMENT ON COLUMN comandalivre.order_item_details.price_at_selection IS 'Preço do modificador no momento em que o pedido foi feito, para garantir precisão histórica.';

CREATE INDEX IF NOT EXISTS idx_oid_order_item_id ON comandalivre.order_item_details(order_item_id);
CREATE INDEX IF NOT EXISTS idx_oid_modifier_option_id ON comandalivre.order_item_details(modifier_option_id);

--rollback DROP INDEX IF EXISTS comandalivre.idx_oid_modifier_option_id;
--rollback DROP INDEX IF EXISTS comandalivre.idx_oid_order_item_id;
--rollback DROP TABLE IF EXISTS comandalivre.order_item_details;
