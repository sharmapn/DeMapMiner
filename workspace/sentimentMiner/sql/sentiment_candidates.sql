CREATE TABLE sentiment_candidates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    proposal_identifier VARCHAR(20),
    proposal_number INT,
    message_id VARCHAR(100),
    sentence TEXT,
    author_role VARCHAR(100),
    sentiment_label VARCHAR(50),
    sentiment_score DOUBLE,
    emotion_category VARCHAR(100),
    stress_signal BOOLEAN,
    toxicity_signal BOOLEAN,
    governance_era VARCHAR(50),
    final_decision VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
