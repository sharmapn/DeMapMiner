# Influence Miner

Influence Miner is an extension of DeMaP Miner, Rationale Miner, and Preference Miner.

Purpose:
- Identify sentences where participants influence OSS proposal decisions.
- Classify influence mechanisms such as strategic, operational, functional, tactical, authority, compatibility, security, standards, ecosystem, economic, organizational, coalition, and user-demand influence.
- Distinguish internal and external influence signals.
- Map influence to OSS roles such as BDFL, delegate, core developer, maintainer, BIP editor, implementer, miner, wallet provider, organization, or user.
- Compare influence direction with final proposal outcomes.

Research Goal:
Influence Miner investigates:

"Who influences OSS decisions, and through what mechanisms do they shape proposal outcomes?"

Current Architecture:

DeMap Miner
 ├── Process/state mining
 ├── Rationale Miner
 │     └── Why decisions were made
 ├── Preference Miner
 │     └── Whose preferences shaped decisions
 └── Influence Miner
       └── How actors shaped decisions

Current Components:
- Influence_Miner_GUI.java
- InfluenceExtractor.java
- InfluenceCandidate.java
- InfluenceTypeDetector.java
- InfluenceDirectionDetector.java
- InfluenceScopeDetector.java
- InfluenceTargetDetector.java
- InfluenceHeuristics.java
- InfluenceOutcomeAlignmentAnalyzer.java
- InfluenceRoleMapper.java
- InfluenceDatabaseWriter.java

Current Extraction Flow:
1. Read proposal-linked messages from the `allmessages` table.
2. Split message bodies into sentences.
3. Detect influence type or types.
4. Detect influence direction: supporting, blocking, revising, or neutral.
5. Detect influence scope: internal, external, mixed, or unknown.
6. Detect likely influence target: proposal, implementation, governance, security, ecosystem, compatibility, or users.
7. Compute a heuristic influence score.
8. Store high-scoring influence candidates in `influence_candidates`.
9. Compare influence direction with final proposal outcomes.

Influence Taxonomy:
- strategic: long-term direction, philosophy, governance, project identity
- operational: implementation, maintenance, release, migration, testing, deployment
- functional: whether the proposal solves the intended technical or user problem
- tactical: reframing, summarising, calling for decision, requesting evidence, compromise
- authority: influence by recognised decision-makers, maintainers, delegates, editors, experts
- compatibility: backward compatibility, interoperability, migration, breaking changes
- security: attack surface, vulnerability, consensus safety, privacy, robustness
- standards: standards, specifications, interoperability norms, conventions
- ecosystem: downstream projects, libraries, wallets, exchanges, miners, users, maintainers
- economic: incentives, cost, fees, funding, business pressure, resources
- organizational: companies, foundations, teams, working groups, release teams
- coalition: visible group alignment, repeated agreement, collective pressure
- user_demand: user requests, beginner confusion, adoption pressure, pain points

First Integration Notes:
- This is a lightweight heuristic prototype, deliberately similar to Preference Miner.
- The first version does not claim causal influence.
- Later versions should incorporate better sentence splitting, role metadata, temporal distance from decision date, reply-network features, proposal revision tracking, and ML/LLM-assisted classification.

Future Work:
- SBS/MBS-style ranking for influential actors.
- Influence network visualisation.
- Temporal influence trajectories.
- Python-versus-Bitcoin comparison.
- BERT/LLM-based multi-label influence extraction.
- Linking influence sentences to proposal revisions and final decision rationale.
