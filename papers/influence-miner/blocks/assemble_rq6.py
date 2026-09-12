"""Second assembly stage: adds the controversial-mechanism material (RQ6) to the
full-length paper produced by assemble_full.py."""
import io
import os

S = os.path.dirname(os.path.abspath(__file__))
D = os.path.dirname(S)


def rd(p):
    return io.open(p, encoding='utf-8').read()


p = os.path.join(D, 'influence-miner-acm-full.tex')
s = rd(p)


def rep(a, b):
    global s
    assert a in s, a[:70]
    s = s.replace(a, b, 1)


# --- abstract ---
rep("Only 1.6\\% of influence sentences coincide with Preference Miner's preference sentences, confirming that the two tools capture different phenomena.",
    "Only 1.6\\% of influence sentences coincide with Preference Miner's preference sentences, confirming that the two tools capture different phenomena. "
    "Seven further \\emph{controversial} mechanisms drawn from the OSS-governance literature---unilateral decision, corporate interest, gatekeeping, exit threat, incivility, backchannel and procedural control---mark 4.7\\% of candidates; they are concentrated in the debates the community remembers as bitter (PEP~572, 285, 238, 308) and in the two weeks before a decision, and their composition changes with governance: after the transition, unilateral decisions, gatekeeping and incivility fall by a third while procedural control doubles and corporate-interest references rise, with Steering Council members showing the lowest unilateral and gatekeeping shares of any role and the highest procedural and backchannel shares. A year-by-year timeline of the highest-scoring decision-closing sentences, nominated by the tool, traces the change in the language of decision from personal pronouncement (2001--2009) through delegation (2010--2017) to the council's collective and procedural formulas (2019--2026).")
rep("The contribution is a taxonomy, an open pipeline",
    "The contribution is a taxonomy of twenty mechanisms, an open pipeline")

# --- contributions ---
rep("This paper makes four contributions. First, it proposes a taxonomy of thirteen influence mechanisms in OSS decision-making.",
    "This paper makes four contributions. First, it proposes a taxonomy of thirteen influence mechanisms in OSS decision-making, extended by seven \\emph{controversial} mechanisms---decision by fiat, corporate interest, gatekeeping, exit threat, incivility, backchannel and procedural control---that operationalise the contested forms of influence described in the open source governance literature.")
rep("and answers five research questions with descriptive results.",
    "and answers six research questions with descriptive results, including a year-by-year timeline of landmark influence acts that the tool nominates from the corpus itself.")

# --- research questions ---
rep("\\textbf{RQ5: How does Influence Miner complement existing decision-mining tools?}\\  How much do influence signals overlap with the preference signals extracted by Preference Miner from the same corpus?",
    "\\textbf{RQ5: How does Influence Miner complement existing decision-mining tools?}\\  How much do influence signals overlap with the preference signals extracted by Preference Miner from the same corpus?\n\n"
    "\\textbf{RQ6: How much controversial influence is there, who exercises it, and did it change with governance?} How often do decisions by fiat, corporate interests, gatekeeping, exit threats, incivility, off-list agreements and procedural control appear, in whose messages, on which proposals, and how do their shares differ between the BDFL and Steering Council eras?")

# --- taxonomy: controversial subsection before "Internal and External Influence" ---
rep("\\subsection{Internal and External Influence}", rd(os.path.join(S, 'block_ctax.tex')) + "\\subsection{Internal and External Influence}")
rep("The \\emph{type} detector matches cue phrases for the thirteen mechanisms on word boundaries,",
    "The \\emph{type} detector matches cue phrases for the twenty mechanisms (thirteen base, seven controversial) on word boundaries,")

# --- analysis paragraph ---
rep("RQ5 compares influence sentences with the 12,690 sentences stored by Preference Miner for the same PEPs after normalising whitespace and punctuation.",
    "RQ5 compares influence sentences with the 12,690 sentences stored by Preference Miner for the same PEPs after normalising whitespace and punctuation. RQ6 uses the seven controversial mechanisms: their frequency, their share within roles, eras, venues, outcomes and decision phases, the proposals in which they concentrate, and example sentences.")

# --- results: RQ6 after RQ5 ---
rep("\\section{Discussion}", rd(os.path.join(S, 'block_rq6.tex')) + rd(os.path.join(S, 'block_timeline2.tex')) + "\\section{Discussion}")

# --- discussion paragraph ---
rep("\\paragraph{Volume is not influence.}",
    "\\paragraph{Controversial influence is rare, concentrated, and changes form.} One candidate sentence in twenty exercises influence in a way the governance literature treats as contested, and those sentences are not spread evenly: they pile up in the debates the community remembers as painful and in the days before a decision. That the transition to the Steering Council left the total unchanged but shifted it from persons to process---fewer decisions by fiat, less gatekeeping and less incivility; twice as much procedural control and more disclosure of corporate stakes---is the clearest evidence in this study that governance design changes \\emph{how} influence is exercised even when it does not change what people argue about. It is also a warning about what a procedural regime looks like from the inside: the council's members are the least likely of any role to decide by fiat and the most likely to point at PEP~13, to report internal discussion, and to say who they work for.\n\n\\paragraph{Volume is not influence.}")

# --- threats ---
rep("Multi-label counts overstate the number of distinct arguments.",
    "Multi-label counts overstate the number of distinct arguments. The controversial cues are more precision-oriented than the base cues but are still surface patterns: ``code of conduct'' and ``bikeshedding'' mark talk \\emph{about} incivility as well as incivility, ``as BDFL-Delegate I hereby accept'' is a sanctioned pronouncement rather than a usurpation, and ``conflict of interest'' appears in election reports because none was found; three rounds of cue tightening on the full corpus removed the most frequent false positives (``I'll leave the details'', ``I designed'', ``won't happen until 3.5''), but the remaining labels index contested passages rather than establish abuse.")
rep("RQ1, RQ2, RQ4 and RQ5 are reported on the 1999--2018 mailing-list corpus; RQ3 adds the Steering Council era,",
    "RQ1, RQ2, RQ4 and RQ5 are reported on the 1999--2018 mailing-list corpus; RQ3 and RQ6 add the Steering Council era,")

# --- conclusion ---
rep("stance volume does not track outcomes; and influence and preference signals occupy largely different sentences.",
    "stance volume does not track outcomes; influence and preference signals occupy largely different sentences; and the seven controversial mechanisms, present in one candidate in twenty, concentrate in the community's bitterest debates and shift from decisions by persons to control by process after the governance transition.")

# --- bibliography ---
rep("\\bibitem{ossGovernance}", rd(os.path.join(S, 'block_bib.tex')).lstrip('\n') + "\n\\bibitem{ossGovernance}")

io.open(p, 'w', encoding='utf-8', newline='\n').write(s)
print('rq6 assembled', len(s))
