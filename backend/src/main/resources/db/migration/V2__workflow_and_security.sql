UPDATE app_users SET role = 'STUDENT' WHERE role IS NULL OR role = '' OR role = 'USER';
ALTER TABLE app_users ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE app_users ADD COLUMN must_change_password BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE app_users ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE maintenance_requests ADD COLUMN assignee_id BIGINT NULL;
ALTER TABLE maintenance_requests ADD COLUMN repair_note VARCHAR(2000) NULL;
ALTER TABLE maintenance_requests ADD COLUMN completed_at TIMESTAMP(6) NULL;
ALTER TABLE maintenance_requests ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE maintenance_requests ADD COLUMN attachment_id VARCHAR(64) NULL;
ALTER TABLE maintenance_requests ADD COLUMN attachment_name VARCHAR(255) NULL;
ALTER TABLE maintenance_requests ADD COLUMN attachment_type VARCHAR(50) NULL;
ALTER TABLE maintenance_requests ADD COLUMN attachment_size BIGINT NULL;

CREATE TABLE IF NOT EXISTS request_history (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, request_id BIGINT NOT NULL, actor_id BIGINT NULL,
  action VARCHAR(60) NOT NULL, details VARCHAR(1000) NOT NULL, created_at TIMESTAMP(6) NOT NULL,
  CONSTRAINT fk_history_request FOREIGN KEY (request_id) REFERENCES maintenance_requests(id) ON DELETE CASCADE,
  CONSTRAINT fk_history_actor FOREIGN KEY (actor_id) REFERENCES app_users(id)
);
CREATE TABLE IF NOT EXISTS notifications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, user_id BIGINT NOT NULL, message VARCHAR(500) NOT NULL,
  request_id BIGINT NULL, is_read BOOLEAN NOT NULL DEFAULT FALSE, created_at TIMESTAMP(6) NOT NULL,
  CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS action_tokens (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, user_id BIGINT NOT NULL, token_hash VARCHAR(64) NOT NULL UNIQUE,
  purpose VARCHAR(30) NOT NULL, expires_at TIMESTAMP(6) NOT NULL, used_at TIMESTAMP(6) NULL,
  created_at TIMESTAMP(6) NOT NULL, CONSTRAINT fk_action_user FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE CASCADE
);
