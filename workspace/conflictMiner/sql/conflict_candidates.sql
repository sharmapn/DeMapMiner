CREATE TABLE conflict_candidates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    proposal_identifier VARCHAR(20),
    proposal_number INT,
    message_id VARCHAR(100),
    thread_id VARCHAR(100),
    parent_message_id VARCHAR(100),
    author_name VARCHAR(255),
    author_email VARCHAR(255),
    author_role VARCHAR(100),
    message_date DATETIME,
    sentence TEXT,
    conflict_types VARCHAR(255),
    primary_conflict_type VARCHAR(50),
    conflict_stance VARCHAR(50),
    conflict_target VARCHAR(100),
    resolution_status VARCHAR(100),
    civility_status VARCHAR(50),
    intensity_level INT,
    intensity_label VARCHAR(100),
    conflict_score DOUBLE,
    final_decision VARCHAR(50),
    decision_impact VARCHAR(100),
    unresolved_objection BOOLEAN,
    extraction_scheme VARCHAR(30),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_conflict_candidates_proposal
    ON conflict_candidates (proposal_identifier, proposal_number);

CREATE INDEX idx_conflict_candidates_type
    ON conflict_candidates (primary_conflict_type);

CREATE INDEX idx_conflict_candidates_intensity
    ON conflict_candidates (intensity_level);

CREATE INDEX idx_conflict_candidates_author
    ON conflict_candidates (author_email);
