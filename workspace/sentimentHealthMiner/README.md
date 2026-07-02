# Sentiment/Health Miner

Sentiment/Health Miner is an extension of DeMaP Miner, Rationale Miner, Preference Miner, Influence Miner, and Conflict Miner.

Purpose:
- Mine emotional tone in OSS proposal discussions at sentence level.
- Detect sentiment categories such as agreement, appreciation, constructive concern, objection, frustration, exhaustion, toxicity, authority challenge, repair, uncertainty, and confidence.
- Extend simple sentiment mining into broader OSS community-health analysis.
- Combine emotional signals with community-health dimensions such as emotional climate, conflict quality, participation structure, governance stress, sustainability risk, and post-decision recovery.
- Map emotional and health signals to OSS roles such as BDFL, delegate, steering council, core developer, maintainer, BIP editor, implementer, miner, wallet provider, organization, user, or contributor.
- Compare emotional direction and health risk with final proposal outcomes.

Research Goal:

Sentiment/Health Miner investigates:

"What emotional and structural health signals appear during OSS decision-making, and how do those signals relate to proposal controversy, governance stress, and project sustainability?"

Current Architecture:

DeMap Miner
 ├── Process/state mining
 ├── Rationale Miner
 │     └── Why decisions were made
 ├── Preference Miner
 │     └── Whose preferences shaped decisions
 ├── Influence Miner
 │     └── How actors shaped decisions
 ├── Conflict Miner
 │     └── How contested decisions unfolded
 └── Sentiment/Health Miner
       └── How emotionally and socially healthy decision discussions were

Current Components:
- Sentiment_Health_Miner_GUI.java
- SentimentHealthExtractor.java
- SentimentHealthCandidate.java
- SentimentCategoryDetector.java
- SentimentPolarityDetector.java
- SentimentTargetDetector.java
- HealthSignalDetector.java
- HealthDimensionDetector.java
- SentimentHealthIntensityDetector.java
- SentimentHealthHeuristics.java
- SentimentHealthOutcomeAnalyzer.java
- SentimentHealthRoleMapper.java
- SentimentHealthDatabaseWriter.java
- SentimentHealthExtractorTestRunner.java

Current Extraction Flow:
1. Read proposal-linked messages from the `allmessages` table.
2. Split message bodies into sentences.
3. Detect sentiment category or categories.
4. Detect polarity: positive, negative, mixed, neutral, or unknown.
5. Detect likely sentiment target: proposal, implementation, process, governance, actor/relationship, community, ecosystem/users, or self.
6. Detect community-health signals such as support, gratitude, constructive disagreement, unresolved concern, fatigue, toxicity, authority pressure, exclusion, repair, and recovery.
7. Map health signals to broader health dimensions: emotional climate, conflict quality, participation structure, governance stress, sustainability risk, and post-decision recovery.
8. Estimate sentiment/health intensity on a 0-5 scale.
9. Compute a transparent heuristic health-risk and health-support score.
10. Store high-scoring sentiment/health candidates in `sentiment_health_candidates`.
11. Compare emotional direction and health-risk category with the final proposal outcome.

Sentiment Taxonomy:
- positive_agreement: agreement, support, +1, ACK, endorsement
- appreciation_gratitude: thanks, appreciation, acknowledgement of effort
- constructive_concern: careful technical or process concern
- strong_objection: strong -1, NACK, reject, unacceptable
- frustration: repeated irritation, tension, impatience, exasperation
- exhaustion_burnout: tiredness, emotional depletion, inability to continue
- toxicity_disrespect: hostile, insulting, belittling, personal attack signals
- authority_challenge: objections to who decides, legitimacy, governance, BDFL, council, delegates, maintainers, BIP editors
- reconciliation_repair: clarification, apology, de-escalation, compromise, stepping back
- uncertainty_doubt: maybe, perhaps, unsure, concern about missing something
- confidence_decisiveness: confidence, certainty, finality, clear direction
- exclusion_isolation: ignored, dismissed, not heard, concern not addressed

Health Dimensions:
- emotional_climate: general positive/negative tone around decisions
- conflict_quality: whether disagreement is constructive, tense, uncivil, repaired, or unresolved
- participation_structure: inclusion, exclusion, reciprocity, concentration of voices
- governance_stress: authority pressure, legitimacy challenge, process fatigue, decision burden
- sustainability_risk: exhaustion, burnout, maintainer burden, repeated strain, contributor withdrawal cues
- post_decision_recovery: acceptance, repair, closure, lingering resentment, continued objection

Intensity Scale:
- 0: no relevant sentiment/health signal
- 1: weak emotional or health signal
- 2: clear positive/negative affect or concern
- 3: strong affect, strong objection, strong support, or health-relevant tension
- 4: escalated health risk, unresolved concern, toxicity, fatigue, or governance stress
- 5: severe sustainability or governance-risk signal

First Integration Notes:
- This is a lightweight heuristic prototype, deliberately similar to Preference Miner, Influence Miner, and Conflict Miner.
- The first version does not claim to diagnose individual developer mental health.
- The tool treats sentiment as a signal of community health, not as community health itself.
- Later versions should incorporate thread-level health episodes, reply-network features, temporal distance from decision date, role-aware burden measures, proposal revision tracking, and ML/LLM-assisted classification.

Future Work:
- Thread-level emotional trajectory reconstruction.
- Actor-level health burden analysis.
- Sentiment and health network visualisation.
- Early-warning indicators for governance stress.
- Python PEP 572 case study and post-BDFL governance comparison.
- Bitcoin mailing-list comparison.
- BERT/LLM-based multi-label sentiment and health extraction.
- Integration with Preference Miner, Influence Miner, and Conflict Miner outputs.
