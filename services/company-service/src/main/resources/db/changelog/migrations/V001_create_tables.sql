--liquibase formatted sql
--changeset pedroermarinho:V001_create_tables

-- comment: Criação inicial das tabelas principais no schema company

CREATE TABLE company_types (
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

COMMENT ON TABLE company_types IS 'Tipos de empresas/estabelecimentos cadastrados no sistema (ex: RESTAURANTE, ESCRITORIO_SERVICOS).';
COMMENT ON COLUMN company_types.key IS 'Chave textual única para identificar programaticamente o tipo de empresa.';
COMMENT ON COLUMN company_types.name IS 'Nome legível para humanos do tipo de empresa.';
COMMENT ON COLUMN company_types.description IS 'Descrição detalhada do tipo de empresa.';

CREATE TABLE role_types (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    key VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    company_type_id INT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    FOREIGN KEY (company_type_id) REFERENCES company_types(id) ON DELETE CASCADE
);

COMMENT ON TABLE role_types IS 'Tipos de cargos ou papéis que usuários podem ter dentro de uma empresa/organização.';
COMMENT ON COLUMN role_types.key IS 'Chave textual única para identificar programaticamente o tipo de cargo.';
COMMENT ON COLUMN role_types.name IS 'Nome legível para humanos do tipo de cargo (ex: Gerente, Garçom, Desenvolvedor).';
COMMENT ON COLUMN role_types.description IS 'Descrição detalhada das responsabilidades do tipo de cargo.';
COMMENT ON COLUMN role_types.company_type_id IS 'FK opcional para company_types.id, se o cargo for específico para um tipo de empresa.';

CREATE TABLE companies (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    name VARCHAR(255) NOT NULL,
    legal_name VARCHAR(255) NULL,
    cnpj VARCHAR(20) UNIQUE,
    email VARCHAR(255) UNIQUE NULL,
    phone VARCHAR(20) NULL,
    description TEXT,
    company_type_id INT NOT NULL,
    address_id INT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    CONSTRAINT fk_companies_type FOREIGN KEY (company_type_id) REFERENCES company_types(id) ON DELETE RESTRICT ON UPDATE CASCADE
);

COMMENT ON TABLE companies IS 'Tabela central para empresas ou restaurantes cadastrados na plataforma.';
COMMENT ON COLUMN companies.name IS 'Nome fantasia ou principal da empresa/restaurante.';
COMMENT ON COLUMN companies.legal_name IS 'Razão Social da empresa, se aplicável.';
COMMENT ON COLUMN companies.cnpj IS 'CNPJ da empresa, único se informado.';
COMMENT ON COLUMN companies.email IS 'Email principal de contato da empresa, único se informado.';
COMMENT ON COLUMN companies.phone IS 'Telefone principal de contato da empresa.';
COMMENT ON COLUMN companies.description IS 'Descrição sobre a empresa/restaurante.';
COMMENT ON COLUMN companies.company_type_id IS 'FK para company_types.id, especificando o tipo de empresa.';
COMMENT ON COLUMN companies.address_id IS 'FK para addresses(id), especificando o endereço principal da empresa.';

CREATE INDEX IF NOT EXISTS idx_companies_name_type ON companies (name, company_type_id);
CREATE INDEX IF NOT EXISTS idx_companies_cnpj ON companies (cnpj) WHERE cnpj IS NOT NULL;

CREATE TABLE company_settings (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    company_id INT UNIQUE NOT NULL,
    logo_asset_id INT NULL,
    banner_asset_id INT NULL,
    primary_theme_color VARCHAR(10) NULL,
    secondary_theme_color VARCHAR(10) NULL,
    welcome_message TEXT NULL,
    timezone VARCHAR(50) NULL DEFAULT 'America/Manaus' ,
    open_time TIME NULL,
    close_time TIME NULL,
    is_closed BOOLEAN NULL,
    notification_emails TEXT[] NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    CONSTRAINT fk_companysettings_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
);


COMMENT ON TABLE company_settings IS 'Configurações específicas para cada empresa/restaurante.';
COMMENT ON COLUMN company_settings.company_id IS 'FK para companies(id), identificando a empresa destas configurações (UNIQUE).';
COMMENT ON COLUMN company_settings.logo_asset_id IS 'FK para public.assets(id), referenciando o logo principal.';
COMMENT ON COLUMN company_settings.banner_asset_id IS 'FK para public.assets(id), referenciando a imagem de capa/banner.';
COMMENT ON COLUMN company_settings.primary_theme_color IS 'Cor primária para customização da interface.';
COMMENT ON COLUMN company_settings.secondary_theme_color IS 'Cor secundária para customização da interface.';
COMMENT ON COLUMN company_settings.welcome_message IS 'Mensagem de boas-vindas exibida para a empresa.';
COMMENT ON COLUMN company_settings.timezone IS 'Fuso horário de operação da empresa (ex: America/Manaus).';
COMMENT ON COLUMN company_settings.notification_emails IS 'Lista de e-mails para notificação.';
COMMENT ON COLUMN company_settings.open_time IS 'Horário de abertura da empresa.';
COMMENT ON COLUMN company_settings.close_time IS 'Horário de fechamento da empresa.';
COMMENT ON COLUMN company_settings.is_closed IS 'Indica se a empresa está temporariamente fechada (TRUE) ou aberta (FALSE).';

CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    role_id INT NOT NULL,
    company_id INT NOT NULL,
    user_id INT NOT NULL,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    hiring_date Date NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    CONSTRAINT fk_employees_role FOREIGN KEY (role_id) REFERENCES role_types(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_employees_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    CONSTRAINT uq_employee_company_user UNIQUE (company_id, user_id) -- Um usuário só pode ser funcionário uma vez para uma mesma empresa.
);

COMMENT ON TABLE employees IS 'Registra os funcionários associados a empresas e seus respectivos cargos.';
COMMENT ON COLUMN employees.role_id IS 'FK para role_types(id), definindo o cargo do funcionário.';
COMMENT ON COLUMN employees.company_id IS 'FK para companies(id), indicando a empresa à qual o funcionário pertence.';
COMMENT ON COLUMN employees.user_id IS 'FK para public.users(id), vinculando o funcionário a um registro de usuário. Deve ser único por empresa.';
COMMENT ON COLUMN employees.status IS 'Status do funcionário (TRUE para ativo, FALSE para inativo).';
COMMENT ON COLUMN employees.hiring_date IS 'Data de contratação do funcionário.';

CREATE INDEX IF NOT EXISTS idx_employees_company_id_user_id ON employees (company_id, user_id);
CREATE INDEX IF NOT EXISTS idx_employees_user_id ON employees (user_id);

CREATE TABLE employee_invite_status (
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

COMMENT ON TABLE employee_invite_status IS 'Status possíveis para um convite de funcionário (ex: pendente, aceito, expirado).';
COMMENT ON COLUMN employee_invite_status.key IS 'Chave textual única para identificar programaticamente o status.';
COMMENT ON COLUMN employee_invite_status.name IS 'Nome legível para humanos do status do convite.';
COMMENT ON COLUMN employee_invite_status.description IS 'Descrição detalhada do status.';

CREATE TABLE employee_invites (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    status_id INT NOT NULL,
    token UUID UNIQUE NOT NULL,
    expiration_date DATE NOT NULL,
    email VARCHAR(255) NOT NULL,
    user_id INT,
    company_id INT NOT NULL,
    role_id INT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    CONSTRAINT fk_employeeinvites_status FOREIGN KEY (status_id) REFERENCES employee_invite_status(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_employeeinvites_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    CONSTRAINT fk_employeeinvites_role FOREIGN KEY (role_id) REFERENCES role_types(id) ON DELETE RESTRICT ON UPDATE CASCADE
);

COMMENT ON TABLE employee_invites IS 'Registra convites enviados para usuários se tornarem funcionários de uma empresa.';
COMMENT ON COLUMN employee_invites.status_id IS 'FK para employee_invite_status(id), indicando o estado atual do convite.';
COMMENT ON COLUMN employee_invites.token IS 'Token único (UUID) para o aceite do convite.';
COMMENT ON COLUMN employee_invites.email IS 'Email para o qual o convite foi enviado.';
COMMENT ON COLUMN employee_invites.expiration_date IS 'Data e hora em que o convite expira.';
COMMENT ON COLUMN employee_invites.user_id IS 'FK opcional para public.users(id), preenchido se o email do convite corresponder a um usuário existente ou após o aceite.';
COMMENT ON COLUMN employee_invites.company_id IS 'FK para companies(id), a empresa que está convidando.';
COMMENT ON COLUMN employee_invites.role_id IS 'FK para role_types(id), o cargo oferecido no convite.';

CREATE INDEX IF NOT EXISTS idx_employee_invites_token ON employee_invites(token);
CREATE INDEX IF NOT EXISTS idx_employee_invites_email ON employee_invites(email);
CREATE INDEX IF NOT EXISTS idx_employee_invites_user_id ON employee_invites(user_id) WHERE user_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_employee_invites_company_id ON employee_invites(company_id);

--rollback sqlFile:path=../rollbacks/migrations/V001_create_tables_undo.sql
