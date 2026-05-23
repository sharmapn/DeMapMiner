CREATE TABLE influence_candidates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    proposal_identifier VARCHAR(20),
    proposal_number INT,
    message_id VARCHAR(100),
    sentence TEXT,
    author_role VARCHAR(100),
    influence_category VARCHAR(100),
    influence_subcategory VARCHAR(100),
    influence_direction VARCHAR(50),
    influence_score DOUBLE,
    final_decision VARCHAR(50),
    aligns_with_decision BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
