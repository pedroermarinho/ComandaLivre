-- changeset pedroermarinho:v002_add_table_reservation_status_values

INSERT INTO comandalivre.table_reservation_status (public_id, key, name, description)
VALUES
    ('0198d99f-487d-77a8-b4eb-684b3fdbfa4d', 'ATIVA', 'Ativa', 'Reserva ativa e válida'),
    ('0198d99f-487d-7436-a885-742f1262d5b7', 'CANCELADA', 'Cancelada', 'Reserva cancelada pelo usuário ou sistema'),
    ('0198d99f-487d-70db-b82c-33c0fa166dfc', 'FINALIZADA', 'Finalizada', 'Reserva concluída e mesa liberada'),
    ('0198d99f-487d-71df-8251-bdc259aaa7c8', 'EXPIRADA', 'Expirada', 'Reserva expirada por tempo limite');

--rollback DELETE FROM comandalivre.table_reservation_status WHERE key IN ('ATIVA', 'CANCELADA', 'FINALIZADA', 'EXPIRADA');
