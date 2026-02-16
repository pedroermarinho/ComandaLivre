-- liquibase formatted sql

-- changeset pedroermarinho:v002_add_table_reservations


CREATE TABLE table_reservation_status (
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

COMMENT ON TABLE table_reservation_status IS 'Define os possíveis status para reservas de mesa.';
COMMENT ON COLUMN table_reservation_status.key IS 'Chave textual única para referência programática do status.';
COMMENT ON COLUMN table_reservation_status.name IS 'Nome legível do status.';
COMMENT ON COLUMN table_reservation_status.description IS 'Descrição opcional do status';

--rollback DROP TABLE IF EXISTS table_reservation_status;


CREATE TABLE table_reservations (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,
    table_id INT NOT NULL,
    reserved_for VARCHAR(255) NULL,
    reserved_for_user_id INT NULL,
    reservation_start TIMESTAMP NOT NULL,
    reservation_end TIMESTAMP NULL,
    status_id INT NOT NULL,
    notes TEXT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    CONSTRAINT fk_table_reservations_table FOREIGN KEY (table_id) REFERENCES tables(id) ON DELETE CASCADE,
    CONSTRAINT fk_table_reservations_status FOREIGN KEY (status_id) REFERENCES table_reservation_status(id) ON DELETE RESTRICT
    );

COMMENT ON TABLE table_reservations IS 'Registra reservas de mesas, incluindo cliente, usuário, horários e status.';
COMMENT ON COLUMN table_reservations.table_id IS 'FK para a mesa reservada.';
COMMENT ON COLUMN table_reservations.reserved_for IS 'Nome do cliente que reservou a mesa (caso não seja usuário do sistema).';
COMMENT ON COLUMN table_reservations.reserved_for_user_id IS 'ID do usuário que reservou a mesa (caso seja usuário do sistema).';
COMMENT ON COLUMN table_reservations.reservation_start IS 'Data/hora de início da reserva.';
COMMENT ON COLUMN table_reservations.reservation_end IS 'Data/hora de término da reserva.';
COMMENT ON COLUMN table_reservations.status_id IS 'FK para o status atual da reserva de mesa.';

--rollback DROP TABLE IF EXISTS table_reservations;
