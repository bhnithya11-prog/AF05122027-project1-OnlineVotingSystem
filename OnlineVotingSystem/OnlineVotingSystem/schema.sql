-- ============================================================
--  Online Voting System - MySQL Schema
--  Run this script in MySQL before starting the application
-- ============================================================

CREATE DATABASE IF NOT EXISTS online_voting_db;
USE online_voting_db;

-- -----------------------------------------------------------
-- Table: users
-- Stores registered voters
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    user_id   INT          AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(100) NOT NULL,
    password  VARCHAR(100) NOT NULL,
    has_voted TINYINT(1)   NOT NULL DEFAULT 0   -- 0 = not voted, 1 = voted
);

-- -----------------------------------------------------------
-- Table: candidates
-- Stores election candidates
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS candidates (
    candidate_id INT          AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    party        VARCHAR(100) NOT NULL,
    vote_count   INT          NOT NULL DEFAULT 0
);

-- -----------------------------------------------------------
-- Table: votes
-- Records each vote cast (links user -> candidate)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS votes (
    vote_id      INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT NOT NULL,
    candidate_id INT NOT NULL,
    FOREIGN KEY (user_id)      REFERENCES users(user_id),
    FOREIGN KEY (candidate_id) REFERENCES candidates(candidate_id)
);

-- -----------------------------------------------------------
-- Default admin account (username: admin, password: admin123)
-- Stored in a simple config — handled in DBConnection.java
-- -----------------------------------------------------------

-- Optional: seed some sample candidates
-- INSERT INTO candidates (name, party) VALUES ('Alice Johnson', 'Progressive Party');
-- INSERT INTO candidates (name, party) VALUES ('Bob Smith',     'Liberty Party');
-- INSERT INTO candidates (name, party) VALUES ('Carol White',   'Green Alliance');
