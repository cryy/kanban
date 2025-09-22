CREATE TABLE tasks (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(200) NOT NULL,
                       description TEXT,
                       status VARCHAR(20) NOT NULL DEFAULT 'TO_DO',
                       priority VARCHAR(10) NOT NULL DEFAULT 'MEDIUM',
                       created_at BIGINT NOT NULL,
                       updated_at BIGINT NOT NULL,
                       version BIGINT NOT NULL DEFAULT 0
);

ALTER TABLE tasks ADD CONSTRAINT chk_task_status
    CHECK (status IN ('TO_DO', 'IN_PROGRESS', 'DONE'));

ALTER TABLE tasks ADD CONSTRAINT chk_task_priority
    CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH'));

CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_priority ON tasks(priority);
CREATE INDEX idx_tasks_created_at ON tasks(created_at DESC);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_tasks_updated_at
    BEFORE UPDATE ON tasks
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();