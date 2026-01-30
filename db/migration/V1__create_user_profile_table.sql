-- Migration: Create user_profile table
-- This table stores user account information and profile data

CREATE TABLE IF NOT EXISTS user_profile (
    upid INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(500) NOT NULL,
    username VARCHAR(500) NOT NULL UNIQUE,
    password VARCHAR(500) NOT NULL,
    gender VARCHAR(50) NOT NULL,
    email VARCHAR(500) NOT NULL UNIQUE,
    image VARCHAR(500) DEFAULT NULL,
    coverpic VARCHAR(500) DEFAULT NULL,
    birthdate DATE DEFAULT NULL,
    mobile VARCHAR(100) DEFAULT NULL,
    hometown VARCHAR(100) DEFAULT NULL,
    work VARCHAR(100) DEFAULT NULL,
    education VARCHAR(100) DEFAULT NULL,
    aboutme VARCHAR(500) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (upid),
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
