# Preference Miner

Preference Miner is an extension of DeMaP Miner and Rationale Miner.

Purpose:
- Identify who expressed preferences on proposals.
- Detect positive, negative, mixed, or neutral preferences.
- Map preferences to OSS roles such as BDFL, delegate, core developer, or user.
- Compare expressed preferences with final proposal outcomes.

Research Goal:
Preference Miner investigates:

"Whose preferences matter in OSS decision-making?"

Current Architecture:

DeMap Miner
 ├── Process/state mining
 ├── Rationale Miner
 │     └── Why decisions were made
 └── Preference Miner
       └── Whose preferences shaped decisions

Current Components:
- Preference_Miner_GUI.java
- PreferenceExtractor.java
- PreferencePolarity.java
- PreferenceHeuristics.java
- PreferenceAlignmentAnalyzer.java
- PreferenceRoleMapper.java
- PreferenceDatabaseWriter.java

Current Extraction Flow:
1. Read proposal-linked messages.
2. Split messages into sentences.
3. Detect preference polarity.
4. Compute heuristic score.
5. Store high-scoring preference candidates.
6. Compare preferences with final outcomes.

Future Work:
- SBS/MBS ranking.
- BERT-based preference extraction.
- Temporal consensus analysis.
- Argument mining.
- Influence network visualisation.
- Cross-project analysis (Python, Bitcoin, Go, Apache).
