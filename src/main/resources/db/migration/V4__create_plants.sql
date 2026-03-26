CREATE TABLE IF NOT EXISTS plants
(
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id               UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    name                  VARCHAR(255) NOT NULL,
    icon_key              VARCHAR(100) NOT NULL,
    quantity              INTEGER      NOT NULL DEFAULT 1,
    watering_interval_days INTEGER     NOT NULL,
    next_watering_date    DATE         NOT NULL,
    created_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at            TIMESTAMP,

    CONSTRAINT chk_plants_quantity CHECK (quantity >= 1),
    CONSTRAINT chk_plants_interval CHECK (watering_interval_days >= 1)
);

CREATE INDEX idx_plants_user_id ON plants (user_id) WHERE deleted_at IS NULL;
