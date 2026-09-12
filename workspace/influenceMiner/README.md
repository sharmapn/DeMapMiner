# Influence Miner

Influence Miner is an extension of DeMaP Miner, Rationale Miner, and Preference Miner.

Research question:

> Who influences OSS decisions, and through what strategic, operational, functional, tactical,
> authority-based, compatibility, security, standards, ecosystem, economic, organizational,
> coalition, and user-demand mechanisms do they shape proposal outcomes?

```
DeMap Miner
 ├── Process/state mining
 ├── Rationale Miner      -> why decisions were made
 ├── Preference Miner     -> whose preferences shaped decisions
 └── Influence Miner      -> how actors shaped decisions
```

A stored row is a **candidate influence signal**. The tool reports how signals *align with*
outcomes; it never claims a sentence *caused* a decision.

## Status (heuristic-v2, September 2026)

Runnable end to end against the `peps_new` database: extraction, aggregation, CSV export,
annotation sampling and a text report. Everything is rule-based and transparent; the
detectors are the parts to replace with ML/LLM methods later.

## Layout

```
influenceMiner/
├── src/influenceMiner/
│   ├── InfluenceBatchRunner.java          batch extraction: one PEP, a range, or all
│   ├── InfluencePipeline.java             extraction + aggregation + export + sample + report
│   ├── InfluenceExtractor.java            per-proposal pipeline
│   ├── InfluenceMessageSource.java        schema-aware reader for allmessages (dedups messageIDs)
│   ├── InfluenceAuthorResolver.java       recovers the address on rows with shifted e-mail columns (see Data notes)
│   ├── InfluenceTextPreprocessor.java     quote/signature/code/header removal, sentence splitting
│   ├── InfluenceTypeDetector.java         13-type multi-label cue detector + evidence cues
│   ├── InfluenceDirectionDetector.java    supporting | blocking | revising | neutral
│   ├── InfluenceScopeDetector.java        internal | external | mixed | unknown
│   ├── InfluenceTargetDetector.java       governance | implementation | security | ... | proposal
│   ├── InfluenceHeuristics.java           additive score
│   ├── InfluenceRoleMapper.java           layered role resolution (CSV > row > pepdetails > tables)
│   ├── InfluenceRoleMapLoader.java        data/role_map.csv
│   ├── InfluenceOutcomeResolver.java      accrejpeps > PEP state history > data/proposal_outcomes.csv
│   ├── InfluenceOutcome.java
│   ├── InfluenceTemporalAnalyzer.java     days before decision, decision phase, governance era
│   ├── InfluenceOutcomeAlignmentAnalyzer.java
│   ├── InfluenceCandidate.java / InfluenceDatabaseWriter.java / InfluenceCandidateRepository.java
│   ├── InfluenceSchemaManager.java        creates/upgrades tables and indexes (idempotent)
│   ├── InfluenceResultAggregator.java     actor / proposal summaries + distributions
│   ├── InfluenceCsvExporter.java          research CSVs
│   ├── InfluenceAnnotationSampler.java    gold-standard annotation sheet
│   ├── InfluenceReportGenerator.java      influence_report.txt
│   └── InfluenceConfig.java               output dir, minimum score, governance split date
├── src/GUI/Influence_Miner_GUI.java       Swing entry point (reuses DeMaP Miner GUI)
├── sql/influence_candidates.sql           candidate table DDL
├── sql/influence_summary_tables.sql       summary table DDL + handy queries
├── data/role_map.csv                      optional manual roles (template)
├── data/proposal_outcomes.csv             optional fallback outcomes (template)
└── output/                                generated results (git-ignored)
```

## Configuration

`conf/DEMAPMinerPEPsNew.prop` (read through `connections.PropertiesFile`) supplies `database`
(= `peps_new`) and `proposalIdentifier`. Optional keys, also settable as `-D` system properties:

| key (prop file)                 | system property                 | default                                        |
|---------------------------------|---------------------------------|------------------------------------------------|
| `influenceOutputDir`            | `influence.outputDir`           | `C:/DeMapMiner/workspace/influenceMiner/output` |
| `influenceDataDir`              | `influence.dataDir`             | `C:/DeMapMiner/workspace/influenceMiner/data`   |
| `influenceMinimumScore`         | `influence.minimumScore`        | `1.0`                                          |
| `influenceGovernanceSplitDate`  | `influence.governanceSplitDate` | `2018-07-12` (Guido steps down as BDFL)        |
| `databaseUser` / `databasePassword` | –                           | `root` / `root`; `MysqlConnect` retries with an empty password (XAMPP default) if refused |

## How to run

Compile (Eclipse does this automatically; from a shell):

```
cd C:\DeMapMiner\workspace
javac -encoding UTF-8 -d common/bin common/src/connections/MysqlConnect.java common/src/connections/PropertiesFile.java
javac -encoding UTF-8 -cp "common/bin;C:/DeMapMiner/lib/mysql-connector-java-5.0.8-bin.jar" -d influenceMiner/bin influenceMiner/src/influenceMiner/*.java
```

Set the classpath once:

```
set CP=C:/DeMapMiner/workspace/influenceMiner/bin;C:/DeMapMiner/workspace/common/bin;C:/DeMapMiner/lib/mysql-connector-java-5.0.8-bin.jar
```

1. **Create tables** – automatic (`InfluenceSchemaManager.ensureSchema`) or run
   `sql/influence_candidates.sql` and `sql/influence_summary_tables.sql`.
2. **Extract**
   ```
   java -cp %CP% influenceMiner.InfluenceBatchRunner pep 572
   java -cp %CP% influenceMiner.InfluenceBatchRunner pep 1 800
   java -cp %CP% influenceMiner.InfluenceBatchRunner pep all
   ```
   Options: `--clear` (default: rows of each processed proposal are deleted first),
   `--keep`, `--clear-all`, `--limit N`.
3. **Aggregate** – `java -cp %CP% influenceMiner.InfluenceResultAggregator pep`
4. **Export CSVs** – `java -cp %CP% influenceMiner.InfluenceCsvExporter pep`
5. **Annotation sample** – `java -cp %CP% influenceMiner.InfluenceAnnotationSampler pep 100 100 20 42`
6. **Report** – `java -cp %CP% influenceMiner.InfluenceReportGenerator pep`

Or all steps at once:

```
java -cp %CP% influenceMiner.InfluencePipeline pep all --clear-all
java -cp %CP% influenceMiner.InfluencePipeline pep --no-extract      (reporting steps only)
```

## Outputs (`output/`)

| file | content |
|------|---------|
| `influence_candidates.csv` | every candidate sentence with labels, evidence cues, temporal features |
| `influence_actor_summary.csv` | per (proposal, actor) counts |
| `influence_actor_overall.csv` | per actor across proposals |
| `influence_proposal_summary.csv` | per proposal counts, outcome group, alignment ratio |
| `influence_type_distribution.csv` | RQ1 – multi-label type counts |
| `influence_direction_distribution.csv`, `influence_scope_distribution.csv`, `influence_role_distribution.csv` | |
| `influence_actor_type_matrix.csv` | RQ2 – top-50 actors × 13 types |
| `influence_era_comparison.csv` | RQ3 – type/direction/scope by governance era |
| `influence_outcome_alignment.csv`, `influence_type_by_outcome.csv` | RQ4 – direction/type vs outcome |
| `influence_preference_overlap.csv` | RQ5 – sentence overlap with `preference_candidates` |
| `influence_annotation_sample.csv` | stratified sample with empty `human_*` columns |
| `influence_report.txt` | plain-text summary for the paper |
| `influence_batch_log.txt` | per-proposal extraction statistics (appended per run) |

## Database assumptions (verified on `peps_new`, 2026-09-12)

* `allmessages` – proposal number in `PEP`; body in `analyseWords` (DeMaP Miner's cleaned text,
  falls back to `email`); author e-mail `senderemail`; name `clusterBySenderFullName`;
  role `authorsrole2020` (`bdfl`, `bdfl_delegate`, `coredeveloper`, `pepeditors`,
  `proposalAuthor`, `otherCommunityMember`); date `dateTimeStamp`.
  Column names are discovered at run time (`InfluenceMessageSource`), so other layouts
  with similar names work.
* Outcomes – `accrejpeps(PEP, state, date2)` for the 248 accepted/rejected PEPs, then
  `pepstates_danieldata_datetimestamp(PEP, state, dateTimeStamp)` for the full state history
  (final, active, deferred, withdrawn, superseded, draft ...). Creation dates from `pepdetails.created`.
* Roles – `pepdetails` (authors, BDFL delegates), `coredevelopers`, `pepeditors`, `authorandrole`.

### Data notes / known issues in `allmessages`

* Rows imported by the newer loader (`senderFullName LIKE 'From %'`, mostly 2017–2018, i.e. all of  PEP 572) have `senderemail` and `senderFullName` **shifted by one row** – they belong to the  next message. `clusterBySenderFullName`, `senderName` and `authorsrole2020` agree with the raw  `From:` header stored in the `email` column (9,532 of 10,831 rows) and are used as they are;  `InfluenceAuthorResolver` takes the address from that header. The batch log reports  `shiftedAddressRows=… (address recovered from header …)`.
* `messageID` is duplicated for messages that mention several PEPs (109,083 PEP-linked rows,
  91,562 distinct ids); duplicates within one PEP are collapsed.

## Taxonomy

| type | meaning |
|------|---------|
| strategic | long-term direction, philosophy, governance, project identity |
| operational | implementation, maintenance, release, migration, testing, deployment |
| functional | whether the proposal solves the intended technical/user problem |
| tactical | reframing, summarising, calling for decision, requesting evidence, compromise |
| authority | recognised decision-makers, maintainers, delegates, editors, experts |
| compatibility | backward compatibility, interoperability, migration, breaking changes |
| security | attack surface, vulnerability, consensus safety, privacy, robustness |
| standards | standards, specifications, interoperability norms, conventions |
| ecosystem | downstream projects, libraries, wallets, exchanges, miners, nodes |
| economic | incentives, cost, fees, funding, business pressure, resources |
| organizational | companies, foundations, teams, working groups, release teams |
| coalition | visible group alignment, repeated agreement, collective pressure |
| user_demand | user requests, beginner confusion, adoption pressure, pain points |

Direction: supporting / blocking / revising / neutral. Scope: internal / external / mixed / unknown.
Target: governance / implementation / security / compatibility / ecosystem / users / proposal / unknown.

## Limitations

* Cue-based labels without a validated gold standard – build one from
  `influence_annotation_sample.csv` before reporting precision/recall.
* Direction is deliberately conservative; most candidates are `neutral`.
* Decision dates exist only for proposals with a terminal state; otherwise temporal
  features are NULL and the score gets no proximity bonus.
* The governance-era split is a single configurable date.
* Multi-label type counts sum to more than the number of candidates.

## Next steps

* Annotate the sample (two coders, Cohen's κ), then tune cues or train a classifier.
* Reply-network and revision-linkage features (who was answered, what changed in the PEP).
* Bitcoin BIP run: point `proposalIdentifier`/`database` at the BIP database; the outcome
  resolver and role mapper already accept BIP vocabulary but the tables need checking.
