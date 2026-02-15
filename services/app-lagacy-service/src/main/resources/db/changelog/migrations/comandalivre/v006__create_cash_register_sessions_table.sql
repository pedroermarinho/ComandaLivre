-- liquibase formatted sql

-- changeset pedroermarinho:v006__create_cash_register_sessions_table.sql

-- Tabela de status para sessões de caixa (ex: aberta, fechada, em conferência)
CREATE TABLE IF NOT EXISTS comandalivre.cash_register_session_status (
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

COMMENT ON TABLE comandalivre.cash_register_session_status IS 'Define os possíveis status para sessões de caixa (ex: aberta, fechada, em conferência).';
COMMENT ON COLUMN comandalivre.cash_register_session_status.key IS 'Chave textual única para referência programática do status.';
COMMENT ON COLUMN comandalivre.cash_register_session_status.name IS 'Nome legível do status.';
COMMENT ON COLUMN comandalivre.cash_register_session_status.description IS 'Descrição opcional do status';

--rollback DROP TABLE IF EXISTS comandalivre.cash_register_session_status;

-- Tabela principal de sessões de caixa
CREATE TABLE IF NOT EXISTS comandalivre.cash_register_sessions
(
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    company_id INT NOT NULL,
    employee_id INT  NOT NULL,
    opened_by_user_id INT NULL,
    closed_by_user_id INT NULL,

    initial_value DECIMAL(10, 2) NOT NULL,

    status_id INT NOT NULL,
    started_at TIMESTAMP WITHOUT TIME ZONE,
    ended_at TIMESTAMP WITHOUT TIME ZONE,

    notes TEXT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    CONSTRAINT fk_cash_register_sessions_company FOREIGN KEY (company_id) REFERENCES company.companies (id),
    CONSTRAINT fk_cash_register_sessions_employee FOREIGN KEY (employee_id) REFERENCES company.employees (id),
    CONSTRAINT fk_cash_register_sessions_status FOREIGN KEY (status_id) REFERENCES comandalivre.cash_register_session_status(id),
    CONSTRAINT fk_cash_register_sessions_opened_by_user FOREIGN KEY (opened_by_user_id) REFERENCES public.users(id),
    CONSTRAINT fk_cash_register_sessions_closed_by_user FOREIGN KEY (closed_by_user_id) REFERENCES public.users(id)
);

COMMENT ON TABLE comandalivre.cash_register_sessions IS 'Sessões de caixa registradas para controle financeiro do restaurante.';
COMMENT ON COLUMN comandalivre.cash_register_sessions.company_id IS 'FK para a empresa/restaurante do caixa.';
COMMENT ON COLUMN comandalivre.cash_register_sessions.employee_id IS 'FK para o funcionário responsável pela abertura do caixa.';
COMMENT ON COLUMN comandalivre.cash_register_sessions.opened_by_user_id IS 'FK para o usuário que abriu a sessão do caixa.';
COMMENT ON COLUMN comandalivre.cash_register_sessions.closed_by_user_id IS 'FK para o usuário que fechou a sessão do caixa.';
COMMENT ON COLUMN comandalivre.cash_register_sessions.initial_value IS 'Valor inicial do caixa ao abrir a sessão.';
COMMENT ON COLUMN comandalivre.cash_register_sessions.status_id IS 'FK para o status atual da sessão do caixa.';
COMMENT ON COLUMN comandalivre.cash_register_sessions.started_at IS 'Data/hora de abertura da sessão do caixa.';
COMMENT ON COLUMN comandalivre.cash_register_sessions.ended_at IS 'Data/hora de fechamento da sessão do caixa.';
COMMENT ON COLUMN comandalivre.cash_register_sessions.notes IS 'Observações gerais sobre a sessão de caixa.';

--rollback DROP TABLE comandalivre.cash_register_sessions;
