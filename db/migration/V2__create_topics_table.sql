-- Migration: Create topics table
-- This table stores discussion topics (jQuery, PHP, JavaScript, etc.)

CREATE TABLE IF NOT EXISTS topics (
    tpid INT NOT NULL AUTO_INCREMENT,
    tpname VARCHAR(500) NOT NULL UNIQUE,
    description VARCHAR(1000) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (tpid),
    INDEX idx_tpname (tpname)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
