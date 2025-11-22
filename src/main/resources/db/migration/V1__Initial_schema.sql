-- Create user_score table
CREATE TABLE IF NOT EXISTS user_score (
    id BIGSERIAL PRIMARY KEY,
    leaderboard_instance_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    score DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (leaderboard_instance_id, user_id)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_user_score_instance_user ON user_score(leaderboard_instance_id, user_id);
CREATE INDEX IF NOT EXISTS idx_user_score_instance_score ON user_score(leaderboard_instance_id, score);
