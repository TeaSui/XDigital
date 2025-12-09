CREATE TABLE context_entries (
                                 id UUID PRIMARY KEY,
                                 session_id UUID NOT NULL,
                                 user_id UUID NOT NULL,
                                 message_id UUID,
                                 source_type VARCHAR(50) NOT NULL,
                                 content TEXT NOT NULL,
                                 metadata JSONB,
                                 embedding BYTEA,
                                 created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                 updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_context_entries_session_id ON context_entries (session_id);
CREATE INDEX idx_context_entries_user_id ON context_entries (user_id);
CREATE INDEX idx_context_entries_message_id ON context_entries (message_id);
CREATE INDEX idx_context_entries_created_at ON context_entries (created_at);