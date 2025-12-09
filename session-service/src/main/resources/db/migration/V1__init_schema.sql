-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Sessions table
CREATE TABLE sessions (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          user_id UUID NOT NULL,
                          name VARCHAR(255) NOT NULL DEFAULT 'New Chat',
                          is_favorite BOOLEAN DEFAULT FALSE,
                          message_count INT DEFAULT 0,
                          last_message_at TIMESTAMP WITH TIME ZONE,
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_sessions_user_id ON sessions(user_id);
CREATE INDEX idx_sessions_is_favorite ON sessions(is_favorite);
CREATE INDEX idx_sessions_updated_at ON sessions(updated_at);