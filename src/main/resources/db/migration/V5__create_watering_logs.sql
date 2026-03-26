CREATE TABLE IF NOT EXISTS watering_logs
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plant_id       UUID      NOT NULL REFERENCES plants (id) ON DELETE CASCADE,
    user_id        UUID      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    watered_at     TIMESTAMP NOT NULL,
    scheduled_date DATE      NOT NULL,
    note           TEXT,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_watering_logs_plant_id ON watering_logs (plant_id);
CREATE INDEX idx_watering_logs_user_id ON watering_logs (user_id);
