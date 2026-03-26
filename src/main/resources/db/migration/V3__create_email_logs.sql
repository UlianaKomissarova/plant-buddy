CREATE TABLE IF NOT EXISTS email_logs
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email      TEXT        NOT NULL,
    type       VARCHAR(50) NOT NULL,
    success    BOOLEAN,
    error      TEXT,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);
