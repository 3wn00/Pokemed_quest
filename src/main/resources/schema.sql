-- Table for Users
CREATE TABLE users (
    id              INTEGER PRIMARY KEY AUTOINCREMENT, -- Unique ID for the user
    username        TEXT UNIQUE NOT NULL,             -- Username, must be unique
    password_hash   TEXT NOT NULL,                    -- Hashed password (store hash, not plain text!)
    role            TEXT NOT NULL                     -- Role (e.g., 'child', 'admin')
);

-- Table for Avatars (assuming one avatar per user)
CREATE TABLE avatars (
    avatar_id       INTEGER PRIMARY KEY AUTOINCREMENT, -- Unique ID for the avatar
    user_id         INTEGER NOT NULL UNIQUE,           -- Links to the user, ensures one avatar per user
    avatar_name     TEXT,                              -- Name of the avatar
    color           TEXT,                              -- Customization: color
    accessory       TEXT,                              -- Customization: accessory
    level           INTEGER DEFAULT 1,                 -- Gamification: level
    total_experience INTEGER DEFAULT 0,                -- Gamification: cumulative score
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE -- If user is deleted, delete avatar too
);

-- Table for Test Progress Records
CREATE TABLE test_progress (
    progress_id    INTEGER PRIMARY KEY AUTOINCREMENT, -- Unique ID for the record
    user_id        INTEGER NOT NULL,                  -- Links to the user
    test_timestamp DATETIME NOT NULL,                  -- When the test was taken
    cmas_score     INTEGER NOT NULL,                  -- The score achieved
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE -- If user is deleted, delete their progress too
);