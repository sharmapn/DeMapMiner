"""Insert the methodology overview (block_method.tex) into the full Python paper,
replacing the old one-line pipeline figure, and label the implementation subsections."""
import io
import os

S = os.path.dirname(os.path.abspath(__file__))
TEX = os.path.join(os.path.dirname(S), 'influence-miner-acm-full.tex')


def rd(name):
    return io.open(os.path.join(S, name), encoding='utf-8').read()


s = io.open(TEX, encoding='utf-8').read()

a = s.index('\\section{Influence Miner: Implementation}')
b = s.index('\\subsection{Data Source and Message Filtering}')
s = s[:a] + rd('block_method.tex') + '\n' + s[b:]

labels = [
    ('\\subsection{Data Source and Message Filtering}', 'sec:filtering'),
    ('\\subsection{Identity and Role Resolution}', 'sec:roles'),
    ('\\subsection{Text Cleaning and Sentence Splitting}', 'sec:cleaning'),
    ('\\subsection{Classification}', 'sec:classification'),
    ('\\subsection{Outcome, Temporal Features and Scoring}', 'sec:features'),
    ('\\subsection{Aggregation and Outputs}', 'sec:outputs'),
    ('\\subsection{Scoring in Detail}', 'sec:scoring'),
    ('\\subsection{Tool Interfaces}', 'sec:interfaces'),
]
for head, lab in labels:
    assert head in s, head
    if '\\label{' + lab + '}' not in s:
        s = s.replace(head, head + '\n\\label{' + lab + '}', 1)

# taxonomy label on the conceptualisation section
if '\\label{sec:taxonomy}' not in s:
    s = s.replace('\\section{Conceptualising Influence in OSS Decision-Making}',
                  '\\section{Conceptualising Influence in OSS Decision-Making}\n\\label{sec:taxonomy}', 1)

# the ICSE paper: cite the proceedings, not only the preprint
s = s.replace("Extracting Rationale for Open Source Software Development Decisions -- A Study of Python Email Archives. \\emph{arXiv:2102.05232}. \\url{https://arxiv.org/abs/2102.05232}",
              "Extracting Rationale for Open Source Software Development Decisions -- A Study of Python Email Archives. In \\emph{Proceedings of the 43rd IEEE/ACM International Conference on Software Engineering (ICSE 2021)}. IEEE, 1008--1019. \\url{https://arxiv.org/abs/2102.05232}")

# companion Bitcoin study
if '\\bibitem{influenceMinerBitcoin}' not in s:
    s = s.replace('\\bibitem{sharma2021rationale}',
                  '\\bibitem{influenceMinerBitcoin}\nPankaj Sharma. 2026. Influence Miner for Bitcoin: How influence is exercised in Bitcoin Improvement Proposal discussions, 2011--2026. Draft manuscript, September 2026.\n\n\\bibitem{sharma2021rationale}', 1)

# section cross-references that named the old section
s = s.replace('Section~\\ref{sec:results}.3', 'Section~\\ref{sec:results}')

io.open(TEX, 'w', encoding='utf-8', newline='\n').write(s)
print('written', TEX, len(s))
