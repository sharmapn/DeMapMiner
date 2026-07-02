# Conflict Miner

Conflict Miner is an extension of DeMaP Miner, Rationale Miner, Preference Miner, Sentiment Miner, and Influence Miner.

Purpose:
- Identify sentences and messages where OSS proposal decisions are contested.
- Classify conflict types such as technical, process, governance/authority, preference, rationale, value/ideology, priority/resource, relational, and ecosystem conflict.
- Estimate conflict intensity from mild concern to decision-blocking objection.
- Detect conflict stance, target, civility, and possible resolution status.
- Map conflicts to OSS roles such as BDFL, delegate, steering council, core developer, maintainer, BIP editor, implementer, miner, wallet provider, organization, or user.
- Compare conflict direction with final proposal outcomes.

Research Goal:
Conflict Miner investigates:

"Where, how, and between whom does conflict occur during OSS decision-making, and how does that conflict relate to proposal outcomes?"

Current Architecture:

DeMap Miner
 ├── Process/state mining
 ├── Rationale Miner
 │     └── Why decisions were made
 ├── Preference Miner
 │     └── Whose preferences shaped decisions
 ├── Sentiment Miner
 │     └── What emotional tone surrounded decisions
 ├── Influence Miner
 │     └── How actors shaped decisions
 └── Conflict Miner
       └── How contested decisions unfolded

Current Components:
- Conflict_Miner_GUI.java
- ConflictExtractor.java
- ConflictCandidate.java
- ConflictTypeDetector.java
- ConflictStanceDetector.java
- ConflictIntensityDetector.java
- ConflictTargetDetector.java
- ConflictResolutionDetector.java
- ConflictCivilityDetector.java
- ConflictHeuristics.java
- ConflictOutcomeImpactAnalyzer.java
- ConflictRoleMapper.java
- ConflictDatabaseWriter.java
- ConflictExtractorTestRunner.java

Current Extraction Flow:
1. Read proposal-linked messages from the `allmessages` table.
2. Split message bodies into sentences.
3. Detect conflict type or types.
4. Detect stance: oppose, concern, rebuttal, compromise, procedural, alternative, support, or neutral.
5. Detect likely conflict target: proposal, implementation, process, governance, security, compatibility, ecosystem/users, actor/relationship, or resources.
6. Detect possible resolution status: unresolved objection, compromise/revision, clarification, authority closure, deferral, actor rejection, actor acceptance, open question, or none.
7. Estimate civility: civil, tense, uncivil, or unknown.
8. Estimate intensity on a 0-5 scale.
9. Compute a transparent heuristic conflict score.
10. Store high-scoring conflict candidates in `conflict_candidates`.
11. Compare conflict stance with final proposal outcome.

Conflict Taxonomy:
- technical: design, syntax, architecture, performance, implementation, security, correctness, maintainability
- process: proposal process, discussion sufficiency, consensus procedure, status movement, decision timing
- governance_authority: who decides, legitimacy, steering council, BDFL, delegates, maintainers, BIP editors
- preference: explicit actor preferences such as prefer, want, rather, +1, -1, ACK, NACK
- rationale: competing reasons, trade-offs, evidence, examples, because/therefore reasoning
- value_ideology: project philosophy, decentralisation, minimalism, backwards compatibility, stability versus innovation
- priority_resource: maintenance burden, review effort, roadmap priority, time, cost, release resources
- relational: personal tension, bad faith, frustration, insults, hostile wording
- ecosystem: downstream projects, users, libraries, wallets, miners, exchanges, vendors, organisations

Intensity Scale:
- 0: no conflict
- 1: mild concern or question
- 2: explicit disagreement or objection
- 3: sustained or strong dispute signal
- 4: escalated conflict or unresolved objection
- 5: decision-blocking conflict

First Integration Notes:
- This is a lightweight heuristic prototype, deliberately similar to Preference Miner and Influence Miner.
- The first version does not claim causal conflict impact.
- Later versions should incorporate thread-level episode building, reply-network features, temporal distance from decision date, real proposal outcome lookup, proposal revision tracking, and ML/LLM-assisted conflict classification.

Future Work:
- Conflict episode reconstruction across full threads.
- Conflict network visualisation.
- Temporal conflict trajectories around proposal acceptance/rejection.
- Python-versus-Bitcoin comparison.
- BERT/LLM-based multi-label conflict extraction.
- Linking unresolved objections to final decision rationale and proposal revisions.
