# Corpus extension, September 2026 (post-BDFL era)

Steps used to extend the DeMaP Miner Python corpus from November 2018 to September 2026
into the `peps_2026` database (a copy of `peps_new`; see `copy_db2.sql`).

1. **Mailing-list archives** into `C:\datasets\postBDFL_2026\<list>\<yyyy>-<Month>.txt`
   - `fetch_pipermail.sh <list> <from> <to>` — Mailman 2 monthly text archives
     (python-dev/ideas/committers to 2019-06, python-checkins to 2023-08, python-bugs-list to 2022-04, python-list to 2025-05)
   - `fetch_hyperkitty.sh <list> <from> <to>` — Mailman 3 monthly mbox exports (2019-07 onward), then
     `java readRepository.readRepository.HyperKittyMboxToPipermail hk_<list> <list>` converts them to the same text format
2. **discuss.python.org** (PEPs, Core Development, Committers, Ideas categories) into `discourse-<slug>\<yyyy>-<Month>.txt`
   - `java readRepository.readRepository.DiscourseToPipermail C:/datasets/postBDFL_2026/discourse peps:19 core-dev:23 committers:5 ideas:6`
3. **PEP metadata and state history** from a clone of github.com/python/peps
   - `git log -p -G'^Status:' ...` parsed into `pepstates_github` (PEP, statusFrom, statusTo, dateTimeStamp UTC, commit)
   - PEP headers parsed into `pepdetails_github`; missing PEPs inserted into `pepdetails`
4. **Roles**: `steering_council.csv` (terms from PEPs 8100-8107) -> `steeringcouncil`;
   devguide `core-team/core-team.csv` -> `coredevelopers_2026` and merged into `coredevelopers`
5. **Reading**: `java -Ddemap.prop=C:\DeMapMiner\conf\DEMAPMinerPEPs2026.prop -Xmx8g readRepository.readRepository.GenericMailingListReader_Main`
   (`mailing_list` in the prop file lists the folders; message IDs continue after the current maximum)
6. **Post-reading updates**: `mysql -uroot < workspace/deMapMiner/sql/post_reading_updates_2026.sql`
   (sender fields from the raw From: header, cluster names, roles incl. `steeringcouncil`)
7. Influence Miner: `proposalStateTableName=pepstates_github` in the 2026 prop file selects the new state table.
