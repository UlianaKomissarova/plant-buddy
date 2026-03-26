-- Add flexible schedule support to plants
ALTER TABLE plants
    ADD COLUMN schedule_type        VARCHAR(20) NOT NULL DEFAULT 'INTERVAL',
    ADD COLUMN watering_days_of_week VARCHAR(100);

ALTER TABLE plants
    ALTER COLUMN watering_interval_days DROP NOT NULL;

ALTER TABLE plants
    DROP CONSTRAINT IF EXISTS chk_plants_interval;

-- Add per-user notification time (HH:mm in user's timezone, default 09:00)
ALTER TABLE users
    ADD COLUMN notification_time VARCHAR(5) NOT NULL DEFAULT '09:00';
