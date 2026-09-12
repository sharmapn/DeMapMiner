# Influence Miner for Bitcoin — paper sources

`influence-miner-bitcoin.tex` / `.pdf`: *Influence Miner for Bitcoin: How Influence Is Exercised in Bitcoin Improvement Proposal Discussions, 2011–2026* (ACM acmart, 20 pages).

The paper is generated: `build/assemble_bitcoin.py` takes the project-independent sections (conceptualisation, taxonomy, implementation, detector-rule appendix, bibliography) from `../influence-miner/influence-miner-acm-full.tex` and adds the Bitcoin-specific blocks in `build/` (abstract, introduction, background, research questions, tool adaptation, corpus construction, external and corpus-nominated timelines, results RQ1–RQ6, Python comparison, discussion, data quality, threats, conclusion, Bitcoin bibliography).

```
python build/assemble_bitcoin.py
pdflatex influence-miner-bitcoin.tex   # three times; acmart.cls from ../influence-miner
```

Data: `bips_2026` database built by `scripts_2026/bitcoin/` (public-inbox mirror of bitcoin-dev, `bitcoin/bips` and `bitcoin/bitcoin` histories); Influence Miner run in `workspace/influenceMiner/output_bips/`; analysis queries in `workspace/influenceMiner/sql/bitcoin_analysis.sql` and `bitcoin_examples.sql`.
