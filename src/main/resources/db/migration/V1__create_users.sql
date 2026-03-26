CREATE TABLE IF NOT EXISTS users
(
    id            UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    email         VARCHAR(1024) UNIQUE NOT NULL,
    password_hash VARCHAR(255)         NOT NULL,
    name          VARCHAR(255),
    is_confirmed  BOOLEAN              NOT NULL DEFAULT FALSE,
    is_blocked    BOOLEAN              NOT NULL DEFAULT FALSE,
    fcm_token     VARCHAR(512),
    timezone      VARCHAR(100)         NOT NULL DEFAULT 'UTC',
    created_at    TIMESTAMP            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at    TIMESTAMP
);

CREATE INDEX idx_users_email ON users (email);
