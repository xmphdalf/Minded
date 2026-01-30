-- Migration: Create user_post table
-- This table stores questions/posts created by users

CREATE TABLE IF NOT EXISTS user_post (
    pid INT NOT NULL AUTO_INCREMENT,
    upid INT NOT NULL,
    content TEXT NOT NULL,
    topic VARCHAR(1000) NOT NULL,
    upvote INT NOT NULL DEFAULT 0,
    downvote INT NOT NULL DEFAULT 0,
    image VARCHAR(1000) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (pid),
    INDEX idx_upid (upid),
    INDEX idx_topic (topic(255)),
    INDEX idx_created_at (created_at),
    CONSTRAINT fk_user_post_upid FOREIGN KEY (upid)
        REFERENCES user_profile(upid)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
