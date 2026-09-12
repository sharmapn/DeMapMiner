# Bitcoin corpus (bips_2026), September 2026

1. Mirror the list: `git clone --mirror https://gnusha.org/pi/bitcoindev C:\datasets\bitcoin_2026\pi-bitcoindev.git` (public-inbox v1; one RFC-822 message per blob; June 2011 – present, all three list homes).
2. `python export_public_inbox.py <mirror.git> <mboxDir> bitcoin-dev` — monthly mbox files.
3. `java readRepository.readRepository.HyperKittyMboxToPipermail <mboxDir> C:\datasets\bitcoin_2026\bitcoin-dev` — pipermail text (recovers the real sender from `X-Original-From` for the Google Groups era).
4. Create the database from `sqlScripts/REPOSITORY/NewOSSDRepBIPs/createGenericDatabase_asBIPsNewMar2021.sql` (as `bips_2026`, utf8mb4, ROW_FORMAT=DYNAMIC) and read with `java -Ddemap.prop=C:\DeMapMiner\conf\DEMAPMinerBIPs2026.prop readRepository.readRepository.GenericMailingListReader_Main`.
5. Clone `bitcoin/bips` and `bitcoin/bitcoin` (`--filter=blob:none --no-checkout`); `python bip_metadata.py <bips-repo> <metaDir>`; `git log --format='%an|%ae|%ad|%P|%s'` on bitcoin/bitcoin for maintainers and contributors; `python load_bip_metadata.py <metaDir>` fills bipdetails, bipstates_github, accrejbips, bipeditors, leadmaintainers, coremaintainers, coredevelopers.
6. `mysql -uroot < workspace/deMapMiner/sql/post_reading_updates_bips_2026.sql` — sender fields, clusters, roles (leadmaintainer, bipeditor, coremaintainer, coredeveloper, proposalAuthor, otherCommunityMember).
7. Influence Miner: `java -Ddemap.prop=...DEMAPMinerBIPs2026.prop -Dinfluence.outputDir=...\output_bips -Dinfluence.stateTable=bipstates_github influenceMiner.InfluencePipeline bip all --clear-all`.
