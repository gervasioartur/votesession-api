CREATE TABLE IF NOT EXISTS t_voting_sessions
(
    id         BIGSERIAL NOT NULL PRIMARY KEY,
    agenda_id  INT       NOT NULL REFERENCES t_agendas (id),
    start_date TIMESTAMP NOT NULL,
    end_date   TIMESTAMP NOT NULL,
    is_active  BOOLEAN   NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);