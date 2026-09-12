# Influence Miner paper sources

LaTeX sources for *Influence Miner: Mining Strategic, Operational, Functional, and External Influence in Open Source Software Decision-Making* (ACM `acmart` sigconf format).

| file | content |
|------|---------|
| `influence-miner-acm-results.tex` / `.pdf` | 10-page conference version (RQ1-RQ5, original corpus) |
| `influence-miner-acm-full.tex` / `.pdf` | full-length version (23 pages): full taxonomy, tool interfaces, corpus extension, RQ6 controversial mechanisms, landmark timelines, literature review, data-quality section, detector-rule appendix |
| `blocks/` | text blocks and the three assembly scripts that build the full version from the 10-page version |
| `figures/` | screenshots of the desktop and web Influence Miner tabs |
| `acmart.cls` | ACM class file used for the build |

## Rebuilding the full version

```
python blocks/assemble_full.py   # results.tex + taxonomy/tools/corpus/data-quality/appendix blocks
python blocks/assemble_rq6.py    # controversial mechanisms (RQ6), corpus-nominated timeline, bibliography
python blocks/assemble_lit.py    # literature review (intro + background), landmark timeline
pdflatex influence-miner-acm-full.tex   # run three times for cross-references
```

The scripts overwrite `influence-miner-acm-full.tex`; edit the 10-page source or the blocks, not the generated file.

## Data behind the numbers

All statistics come from `workspace/influenceMiner` runs: `output/` (original corpus `peps_new`, 205,042 candidates) and `output_2026/` (extended corpus `peps_2026`, 299,192 candidates), September 2026, cue set v3. The `influence_report.txt` in each folder reproduces the tables; the yearly figures in the timelines were computed with the queries in `workspace/influenceMiner/sql/` and the `controversial` flag on `influence_candidates`.
