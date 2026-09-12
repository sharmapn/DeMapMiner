"""Insert the ranking-schemes subsection (block_ranking.tex) before Tool Interfaces in the
full Python paper and update the interface description and figure captions."""
import io
import os

S = os.path.dirname(os.path.abspath(__file__))
TEX = os.path.join(os.path.dirname(S), 'influence-miner-acm-full.tex')


def rd(name):
    return io.open(os.path.join(S, name), encoding='utf-8').read()


s = io.open(TEX, encoding='utf-8').read()


def rep(a, b):
    global s
    assert a in s, a[:70]
    s = s.replace(a, b, 1)


if '\\subsection{Ranking Schemes and Stored Output}' not in s:
    rep('\\subsection{Tool Interfaces}', rd('block_ranking.tex').strip() + '\n\n\\subsection{Tool Interfaces}')

rep("Its controls filter by mechanism (including a \\emph{Controversial (any of the 7)} option for the mechanisms of Section~\\ref{sec:controversial}), direction, author role, governance era, minimum score and sort order; \\emph{Load Influence Candidates} fills the results table of the viewer with the proposal's candidates (date, message id, author with role, mechanisms, direction, scope and target, sentence with its evidence cues, score) and a summary line gives the counts by mechanism and direction; \\emph{Run Influence Miner for PEP} re-extracts the proposal in the background. Selecting a row loads the source message into the central viewer, exactly as for reason candidates, so that an analyst can read the sentence in its thread.",
    "Its controls filter by mechanism (including a \\emph{Controversial (any of the 7)} option for the mechanisms of Section~\\ref{sec:controversial}), direction, author role, governance era and minimum score, choose the sort order and the ranking scheme of Section~\\ref{sec:ranking}---one row per candidate sentence (SBS) or one row per message with its candidate count, mechanism union, strongest sentence and summed score (MBS); \\emph{Load Influence Candidates} fills the results table of the viewer accordingly and a summary line gives the counts by mechanism and direction; \\emph{Run Influence Miner for PEP} re-extracts the proposal in the background. Selecting a row opens the source message in the central viewer and underlines every candidate sentence in it, in blue, with the selected sentence in red and scrolled into view, as Rationale Miner does for reason sentences; the underline is placed by a tolerant match that ignores line breaks, quote markers and punctuation between the words of the stored sentence, since the sentence was cut from the cleaned text. An analyst therefore reads each signal in its thread, and sees at once how much of the message the tool has labelled.")
rep("the Influence Miner tab adds bar summaries of mechanisms and directions (controversial mechanisms highlighted in red), role and phase tags, a top-participants table and the candidate table with message links, and a separate overview page reports the corpus-wide distributions and the governance-era comparison of Section~\\ref{sec:results}.",
    "the Influence Miner tab adds bar summaries of mechanisms and directions (controversial mechanisms highlighted in red), role and phase tags, a top-participants table, the same sentence/message ranking switch, and the candidate table, every row of which opens its message in the central panel with the candidate sentences underlined in the same way; a separate overview page reports the corpus-wide distributions and the governance-era comparison of Section~\\ref{sec:results}.")
rep("\\caption{Influence Miner tab in the DeMaP Miner desktop tool (PEP~572, extended corpus). Right: filters, summary and the candidate table; centre: the message selected from the table; left: navigation, parameters and the PEP's state history.}",
    "\\caption{Influence Miner tab in the DeMaP Miner desktop tool (PEP~572, extended corpus). Right: filters, ranking switch, summary and the candidate table; centre: the message opened by selecting the first row, with its candidate sentences underlined (blue) and the selected one in red; left: navigation, parameters and the PEP's state history.}")
rep("\\Description{Screenshot of a Java Swing application with a maximised window: a left navigation panel, a central message viewer showing a python-dev e-mail, and a right panel whose \"Influence Miner\" tab shows filter drop-downs, a summary of 3,534 candidates and a table of candidate sentences with scores.}",
    "\\Description{Screenshot of a Java Swing application with a maximised window: a left navigation panel, a central message viewer showing a python-dev e-mail with two sentences underlined, and a right panel whose \"Influence Miner\" tab shows filter drop-downs, a summary of 3,717 candidates and a table of candidate sentences with scores.}")
rep("Left: the PEP's status history from the \\texttt{python/peps} repository; centre: the selected Discourse post; right: filters, mechanism and direction bars, roles, phases and the candidate table.}",
    "Left: the PEP's status history from the \\texttt{python/peps} repository; centre: the Steering Council's acceptance post, opened from the first row of the candidate table, with its 33 candidate sentences underlined and the selected one in red; right: filters, ranking switch, mechanism and direction bars, roles, phases and the candidate table.}")

io.open(TEX, 'w', encoding='utf-8', newline='\n').write(s)
print('written', TEX, len(s))
