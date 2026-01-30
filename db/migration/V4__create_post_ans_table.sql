-- Migration: Create post_ans table
-- This table stores answers/replies to questions

CREATE TABLE IF NOT EXISTS post_ans (
    paid INT NOT NULL AUTO_INCREMENT,
    pid INT NOT NULL,
    upid INT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (paid),
    INDEX idx_pid (pid),
    INDEX idx_upid (upid),
    INDEX idx_created_at (created_at),
    CONSTRAINT fk_post_ans_pid FOREIGN KEY (pid)
        REFERENCES user_post(pid)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_post_ans_upid FOREIGN KEY (upid)
        REFERENCES user_profile(upid)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
