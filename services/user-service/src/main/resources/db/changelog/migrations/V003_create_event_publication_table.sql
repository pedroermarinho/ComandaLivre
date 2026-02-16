--liquibase formatted sql
--changeset pedroermarinho:V003_create_event_publication_table

CREATE TABLE IF NOT EXISTS event_publication (
    id UUID PRIMARY KEY,
    event_type VARCHAR(512) NOT NULL,
    listener_id VARCHAR(512) NOT NULL,
    publication_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    serialized_event TEXT NOT NULL,
    completion_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT uc_event_publication UNIQUE (listener_id, event_type, publication_date)
);

CREATE INDEX IF NOT EXISTS ix_event_publication_completion_date
ON event_publication(completion_date);

--rollback sqlFile:path=../rollbacks/migrations/V003_create_event_publication_table_undo.sql
