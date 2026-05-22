CREATE TABLE preference_candidates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    proposal_identifier VARCHAR(20),
    proposal_number INT,
    message_id VARCHAR(100),
    author_name VARCHAR(255),
    author_email VARCHAR(255),
    author_role VARCHAR(100),
    message_date DATETIME,
    sentence TEXT,
    preference_polarity VARCHAR(20),
    preference_score DOUBLE,
    final_decision VARCHAR(50),
    aligns_with_decision BOOLEAN,
    extraction_scheme VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
