-- Influence Miner: candidate sentence table (heuristic-v2)
-- InfluenceSchemaManager.ensureSchema() creates/upgrades this automatically at
-- the start of every batch run; this file is for running it by hand.

CREATE TABLE IF NOT EXISTS influence_candidates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    proposal_identifier VARCHAR(20),
    proposal_number INT,
    message_id VARCHAR(100),
    author_name VARCHAR(255),
    author_email VARCHAR(255),
    author_role VARCHAR(100),
    message_date DATETIME,
    sentence TEXT,
    influence_types VARCHAR(255),          -- comma-separated multi-label, taxonomy order
    primary_influence_type VARCHAR(50),
    influence_scope VARCHAR(50),           -- internal | external | mixed | unknown
    influence_direction VARCHAR(50),       -- supporting | blocking | revising | neutral
    influence_target VARCHAR(100),         -- governance | implementation | security | compatibility | ecosystem | users | proposal | unknown
    influence_score DOUBLE,
    final_decision VARCHAR(50),            -- accepted | rejected | withdrawn | deferred | final | active | superseded | draft | provisional | unknown
    aligns_with_outcome BOOLEAN,
    extraction_scheme VARCHAR(30),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- added in heuristic-v2
    evidence_cues TEXT,                    -- "compatibility: backward compatibility|breaking change; security: attack surface"
    decision_date DATETIME,
    days_before_decision INT,              -- NULL when unknown; negative = message after the decision
    decision_phase VARCHAR(30),            -- pre_draft | early_discussion | active_debate | revision | decision_window | post_decision | unknown
    governance_era VARCHAR(30)             -- bdfl_era | post_bdfl_era | unknown (split date is configurable)
);

-- Upgrading a table created by the first prototype (MySQL < 8 has no ADD COLUMN IF NOT EXISTS;
-- run these only if the columns are missing, or let InfluenceSchemaManager do it):
-- ALTER TABLE influence_candidates ADD COLUMN evidence_cues TEXT;
-- ALTER TABLE influence_candidates ADD COLUMN decision_date DATETIME;
-- ALTER TABLE influence_candidates ADD COLUMN days_before_decision INT;
-- ALTER TABLE influence_candidates ADD COLUMN decision_phase VARCHAR(30);
-- ALTER TABLE influence_candidates ADD COLUMN governance_era VARCHAR(30);

CREATE INDEX idx_influence_proposal ON influence_candidates(proposal_identifier, proposal_number);
CREATE INDEX idx_influence_author ON influence_candidates(author_email);
CREATE INDEX idx_influence_type ON influence_candidates(primary_influence_type);
CREATE INDEX idx_influence_direction ON influence_candidates(influence_direction);
CREATE INDEX idx_influence_score ON influence_candidates(influence_score);
