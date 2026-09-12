import io
import os, re

S = os.path.dirname(os.path.abspath(__file__))
D = os.path.dirname(S)


def rd(p):
    return io.open(p, encoding='utf-8').read()


s = rd(os.path.join(D, 'influence-miner-acm-results.tex'))

# 0. header comment, title variant, graphicx is loaded by acmart
s = s.replace('%% Influence Miner paper - results version (ACM acmart format).',
              '%% Influence Miner paper - FULL-LENGTH version (ACM acmart format, no page limit).')

# 1. full taxonomy subsections instead of the condensed one
tax = rd(os.path.join(S, 'taxonomy_full.tex'))
a = s.index('\\subsection{The Thirteen Mechanisms}')
b = s.index('\\subsection{Internal and External Influence}')
s = s[:a] + tax + s[b:]

# 2. tools + scoring detail after "Aggregation and Outputs" subsection (before Study Design)
tools = rd(os.path.join(S, 'block_tools.tex'))
s = s.replace('\\section{Study Design}', tools + '\n\\section{Study Design}', 1)

# 3. corpus extension: replace the short paragraph with the full subsection
a = s.index('\\paragraph{Steering Council era extension.}')
b = s.index('\\subsection{Analysis}')
s = s[:a] + rd(os.path.join(S, 'block_corpus.tex')) + '\n' + s[b:]

# 4. RQ3 extras before RQ4
extra = rd(os.path.join(S, 'block_rq3extra.tex'))
s = s.replace('\\subsection{RQ4: Alignment with Outcomes}', extra + '\n\\subsection{RQ4: Alignment with Outcomes}', 1)
# refer to the new table in the RQ3 text
s = s.replace("The clearer change is \\emph{who} carries the signals. Steering Council members author",
              "The clearer change is \\emph{who} carries the signals (Table~\\ref{tab:rq3roles}). Steering Council members author")

# 5. data-quality section before Threats; shorten the threats/discussion paragraphs that duplicated it
dq = rd(os.path.join(S, 'block_dq.tex'))
s = s.replace('\\section{Threats to Validity}', dq + '\n\\section{Threats to Validity}', 1)
s = re.sub(r'\\paragraph\{Data quality\.\} Two problems were found in \\texttt\{allmessages\}\..*?\n\n',
           '\\\\paragraph{Data quality.} The two corpus problems of Section~\\\\ref{sec:dataquality} are corrected in the reported runs; the correction relies on the raw headers stored with each message, which the Discourse posts do not have (their usernames come from the API and are exact).\n\n', s, flags=re.S)
s = s.replace('\\paragraph{Data quality as a research finding.} Two problems in the shared corpus would have silently distorted every actor-level result: automated senders contributed most of the raw sentences, and the shifted sender columns would have credited PEP~572\'s discussion to the wrong people. Both are now handled in the tool and documented so that Rationale Miner and Preference Miner results built on the same rows can be re-checked.',
              '\\paragraph{Data quality as a research finding.} Two problems in the shared corpus (Section~\\ref{sec:dataquality}) would have silently distorted every actor-level result. Neither is visible in sentence-level statistics; both became obvious the moment an actor table was produced. Tools that report who said what should always produce that table.')

# 6. mention the interfaces in the contributions
s = s.replace('Second, it presents Influence Miner, an implemented extension of the DeMaP Miner tool family that extracts, classifies, scores and aggregates influence signals from proposal-linked mailing-list discussions and exports annotation-ready output.',
              'Second, it presents Influence Miner, an implemented extension of the DeMaP Miner tool family that extracts, classifies, scores and aggregates influence signals from proposal-linked discussions, exports annotation-ready output, and is available as a tab in both the desktop and the web version of the DeMaP Miner viewer. It also describes how the corpus was extended from mailing lists to the Discourse forum and to the PEP repository\'s own history, a procedure that other studies of Python governance can reuse.')

# 7. appendix before the bibliography
s = s.replace('\\begin{thebibliography}{00}', rd(os.path.join(S, 'block_appendix.tex')) + '\n\\begin{thebibliography}{00}', 1)

io.open(os.path.join(D, 'influence-miner-acm-full.tex'), 'w', encoding='utf-8', newline='\n').write(s)
print('written', len(s))
