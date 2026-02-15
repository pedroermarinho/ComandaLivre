-- liquibase formatted sql

-- changeset pedroermarinho:v007__create_cash_register_closings_table.sql

CREATE TABLE IF NOT EXISTS comandalivre.cash_register_closings
(
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    session_id INT NOT NULL,
    employee_id INT NOT NULL,

    counted_cash DECIMAL(10, 2) NOT NULL DEFAULT 0,
    counted_card DECIMAL(10, 2) NOT NULL DEFAULT 0,
    counted_pix DECIMAL(10, 2) NOT NULL DEFAULT 0,
    counted_others DECIMAL(10, 2) NOT NULL DEFAULT 0,

    final_balance DECIMAL(10, 2) NOT NULL,
    final_balance_expected DECIMAL(10, 2) NOT NULL,
    final_balance_difference DECIMAL(10, 2) NOT NULL,

    observations TEXT,

    audit_data JSONB NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    CONSTRAINT fk_cash_register_closings_session FOREIGN KEY (session_id) REFERENCES comandalivre.cash_register_sessions (id),
    CONSTRAINT fk_cash_register_closings_employee FOREIGN KEY (employee_id) REFERENCES company.employees (id)
);

COMMENT ON TABLE comandalivre.cash_register_closings IS 'Registros de fechamento de sessões de caixa, detalhando valores esperados, contados e discrepâncias.';
COMMENT ON COLUMN comandalivre.cash_register_closings.session_id IS 'FK para a sessão de caixa que está sendo fechada.';
COMMENT ON COLUMN comandalivre.cash_register_closings.employee_id IS 'FK para o funcionário responsável pelo fechamento do caixa.';
COMMENT ON COLUMN comandalivre.cash_register_closings.counted_cash IS 'Valor em dinheiro contado no fechamento.';
COMMENT ON COLUMN comandalivre.cash_register_closings.counted_card IS 'Valor em cartão contado no fechamento.';
COMMENT ON COLUMN comandalivre.cash_register_closings.counted_pix IS 'Valor em PIX contado no fechamento.';
COMMENT ON COLUMN comandalivre.cash_register_closings.counted_others IS 'Outros valores contados no fechamento.';
COMMENT ON COLUMN comandalivre.cash_register_closings.final_balance IS 'Saldo final do caixa após fechamento.';
COMMENT ON COLUMN comandalivre.cash_register_closings.final_balance_expected IS 'Saldo final esperado do caixa com base nas transações registradas.';
COMMENT ON COLUMN comandalivre.cash_register_closings.final_balance_difference IS 'Diferença entre o saldo final contado e o saldo final esperado.';
COMMENT ON COLUMN comandalivre.cash_register_closings.observations IS 'Observações gerais sobre o fechamento do caixa.';


-- rollback DROP TABLE comandalivre.cash_register_closings;
