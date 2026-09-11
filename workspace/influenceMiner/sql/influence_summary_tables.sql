-- Influence Miner: aggregated result tables, populated by InfluenceResultAggregator
-- (it deletes and rewrites them on every run). Created automatically by
-- InfluenceSchemaManager.ensureSchema(); kept here for manual use.

CREATE TABLE IF NOT EXISTS influence_actor_summary (
    id INT AUTO_INCREMENT PRIMARY KEY,
    proposal_identifier VARCHAR(20),
    proposal_number INT,
    author_name VARCHAR(255),
    author_email VARCHAR(255),
    author_role VARCHAR(100),
    total_influence_sentences INT,
    strategic_count INT DEFAULT 0,
    operational_count INT DEFAULT 0,
    functional_count INT DEFAULT 0,
    tactical_count INT DEFAULT 0,
    authority_count INT DEFAULT 0,
    compatibility_count INT DEFAULT 0,
    security_count INT DEFAULT 0,
    standards_count INT DEFAULT 0,
    ecosystem_count INT DEFAULT 0,
    economic_count INT DEFAULT 0,
    organizational_count INT DEFAULT 0,
    coalition_count INT DEFAULT 0,
    user_demand_count INT DEFAULT 0,
    supporting_count INT DEFAULT 0,
    blocking_count INT DEFAULT 0,
    revising_count INT DEFAULT 0,
    neutral_count INT DEFAULT 0,
    average_score DOUBLE,
    max_score DOUBLE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS influence_proposal_summary (
    id INT AUTO_INCREMENT PRIMARY KEY,
    proposal_identifier VARCHAR(20),
    proposal_number INT,
    final_decision VARCHAR(50),
    total_influence_sentences INT,
    total_actors INT,
    strategic_count INT DEFAULT 0,
    operational_count INT DEFAULT 0,
    functional_count INT DEFAULT 0,
    tactical_count INT DEFAULT 0,
    authority_count INT DEFAULT 0,
    compatibility_count INT DEFAULT 0,
    security_count INT DEFAULT 0,
    standards_count INT DEFAULT 0,
    ecosystem_count INT DEFAULT 0,
    economic_count INT DEFAULT 0,
    organizational_count INT DEFAULT 0,
    coalition_count INT DEFAULT 0,
    user_demand_count INT DEFAULT 0,
    supporting_count INT DEFAULT 0,
    blocking_count INT DEFAULT 0,
    revising_count INT DEFAULT 0,
    neutral_count INT DEFAULT 0,
    internal_count INT DEFAULT 0,
    external_count INT DEFAULT 0,
    mixed_count INT DEFAULT 0,
    unknown_scope_count INT DEFAULT 0,
    average_score DOUBLE,
    max_score DOUBLE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_actor_summary_proposal ON influence_actor_summary(proposal_identifier, proposal_number);
CREATE INDEX idx_proposal_summary_proposal ON influence_proposal_summary(proposal_identifier, proposal_number);

-- Handy queries -------------------------------------------------------------

-- RQ1 primary type distribution
-- SELECT primary_influence_type, COUNT(*) FROM influence_candidates GROUP BY 1 ORDER BY 2 DESC;

-- RQ2 top actors
-- SELECT author_email, author_name, author_role, COUNT(*) n, AVG(influence_score) avg_score
-- FROM influence_candidates GROUP BY 1,2,3 ORDER BY n DESC, avg_score DESC LIMIT 30;

-- RQ3 era comparison (multi-label types need the CSV export; primary type works in SQL)
-- SELECT governance_era, primary_influence_type, COUNT(*) FROM influence_candidates GROUP BY 1,2 ORDER BY 1,3 DESC;

-- RQ4 direction vs outcome
-- SELECT final_decision, influence_direction, COUNT(*), SUM(aligns_with_outcome)
-- FROM influence_candidates GROUP BY 1,2 ORDER BY 1,2;
