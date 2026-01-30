-- Migration: Create favtopic table
-- This table stores user topic subscriptions/follows

CREATE TABLE IF NOT EXISTS favtopic (
    ftid INT NOT NULL AUTO_INCREMENT,
    tpid INT NOT NULL,
    upid INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (ftid),
    UNIQUE KEY unique_user_topic (upid, tpid),
    INDEX idx_tpid (tpid),
    INDEX idx_upid (upid),
    CONSTRAINT fk_favtopic_tpid FOREIGN KEY (tpid)
        REFERENCES topics(tpid)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_favtopic_upid FOREIGN KEY (upid)
        REFERENCES user_profile(upid)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
