"""Third assembly stage: literature on how OSS development is influenced, woven
into the Introduction and the Background section of the full paper."""
import io
import os

S = os.path.dirname(os.path.abspath(__file__))
D = os.path.dirname(S)
p = os.path.join(D, 'influence-miner-acm-full.tex')
s = io.open(p, encoding='utf-8').read()


def rep(a, b):
    global s
    assert a in s, a[:70]
    s = s.replace(a, b, 1)


# ---- Introduction, paragraph 1: unequal influence is well documented ----
rep("Open source software (OSS) development is often described as open, distributed and meritocratic. However, the openness of participation does not mean that all participants exert equal influence over decisions.",
    "Open source software (OSS) development is often described as open, distributed and meritocratic. However, the openness of participation does not mean that all participants exert equal influence over decisions. Two decades of empirical work show a small core doing most of the work and taking most of the decisions \\cite{mockus2002, crowston2005}, a status order that is built from public references and then constrains who is heard \\cite{stewart2005}, leadership that is shared but not equal \\cite{fielding1999, dahlander2011}, and governance arrangements---from a benevolent dictator to elected councils and foundation boards---that decide how contested questions are settled \\cite{omahony2007, markus2007, shah2006, weber2004}.")

# ---- Introduction: a paragraph on the influence literature after the prior-work paragraph ----
rep("An important gap remains: we still need a systematic way to identify",
    "Beyond the DeMaP Miner line of work, influence on OSS decisions has been studied from several angles, which Section~\\ref{sec:background} reviews: the social and technical signals that decide whether a contribution is accepted \\cite{tsay2014, tsay2014b, marlow2013, alami2020}; the coordination and governance processes through which authority is exercised and legitimised \\cite{shaikh2017, lindberg2016, howison2014, delaat2007}; the influence of firms on community projects \\cite{dahlander2006, schaarschmidt2015, riehle2014, zhang2021, zhang2022}; conflict, incivility and their consequences \\cite{elliott2003, jensen2004, filippova2016, ferreira2021, miller2022}; and, for Python in particular, socio-cognitive analyses of PEP design discussions \\cite{sack2006, barcellini2008a, barcellini2008b, ducheneaut2005}. These studies establish \\emph{that} influence is unequal and \\emph{who} tends to hold it; what they leave open is a way to identify, at the level of individual sentences, the mechanisms through which it is exercised.\n\nAn important gap remains: we still need a systematic way to identify")

# ---- Background: label and a new subsection reviewing the influence literature ----
rep("\\section{Background and Motivation}\n",
    "\\section{Background and Motivation}\n\\label{sec:background}\n\n\\subsection{The DeMaP Miner Research Agenda}\n")

review = r"""
\subsection{How Open Source Development Is Influenced}
\label{sec:litreview}

\paragraph{Unequal participation and status.} The earliest empirical studies of OSS already showed that a handful of developers write most of the code and decide most questions: in Apache and Mozilla the top fifteen contributors produced over 80\% of the changes \cite{mockus2002}, and the onion model of a core surrounded by co-developers and active users has since been reproduced across projects \cite{crowston2005, vonkrogh2003}. Position in this structure is earned and defended socially: Stewart's analysis of a large developer community found that reputation is assessed from public references to a person's work and that these references then constrain status mobility \cite{stewart2005}; Dahlander and O'Mahony showed that developers gain \emph{lateral} authority over project tasks by first contributing code and then taking on coordination work \cite{dahlander2011}; and Fleming and Waguespack found that leadership in open innovation communities goes to brokers and boundary spanners rather than to the most prolific contributors \cite{fleming2007}. Newcomers meet social barriers before technical ones \cite{steinmacher2015}. Influence, in this literature, is a property of position in a network and a status order, measured from activity traces rather than from what people say.

\paragraph{Governance and legitimate authority.} A second strand asks how contested questions are settled. Weber \cite{weber2004} and Raymond \cite{raymond1999} describe the benevolent-dictator model; O'Mahony and Ferraro trace how the Debian community moved from a founder's authority to an elected leader constrained by a constitution \cite{omahony2007}; Markus argues that governance is configurational rather than a single mode \cite{markus2007}; Shah shows that governance shapes who participates and why \cite{shah2006}; de Laat surveys the mechanisms---modularisation, division of roles, delegation, formal decision procedures---by which projects govern themselves \cite{delaat2007}. Shaikh and Henfridsson, studying eight years of Linux kernel version-control coordination, identify four coordination processes (autocratic clearing, oligarchic recursion, federated self-governance, meritocratic idea-testing), each resting on a different authority structure and its own form of legitimation \cite{shaikh2017}; Lindberg et al.\ show how interdependencies are coordinated in an open source project through routines rather than hierarchy \cite{lindberg2016}; Howison and Crowston theorise open superposition, in which work proceeds by layering small independent tasks and decisions are deferred rather than taken \cite{howison2014}. Python's own transition from the BDFL to a Steering Council \cite{pep13} is a recent instance of the movement these studies describe, and one of the reasons this paper compares the two eras.

\paragraph{How contributions are evaluated.} On GitHub, whether a pull request is accepted depends on social as well as technical factors: prior interaction with the project, the submitter's status and social connections, and the amount of discussion the contribution attracts \cite{tsay2014}; the discussion itself is where objections are raised, reframed and resolved \cite{tsay2014b}; and evaluators form impressions of contributors from activity traces and profiles before reading their code \cite{marlow2013}. Alami et al.\ found three styles of pull-request governance in FOSS communities---protective, equitable and lenient---that determine whose objections count \cite{alami2020}. These studies are the closest in spirit to Influence Miner: they show that the arguments and signals exchanged around a change affect its outcome, but they measure them through counts and metadata rather than by classifying what is argued.

\paragraph{Firms.} About half of OSS development has been paid work for many years \cite{riehle2014}. Firms influence community projects by placing employees inside them \cite{dahlander2006}, by leadership or by deploying resources \cite{schaarschmidt2015}, and through the work practices they bring \cite{butler2018}; in OpenStack a few companies came to dominate, with measurable effects on the ecosystem's health \cite{zhang2021, zhang2022}. Relationships between companies and communities range from symbiotic to parasitic \cite{dahlander2005}, and the economic motivations of the different stakeholders differ \cite{riehle2007}. This is the literature behind the \emph{corporate interest} mechanism of Section~\ref{sec:controversial}: a company's stake in a proposal is a form of influence that the community may or may not regard as legitimate.

\paragraph{Conflict and incivility.} Free software developers resolve conflicts through shared occupational norms and beliefs \cite{elliott2003}; leadership and control are recurrent sources of conflict in large communities \cite{jensen2004}; task conflict can be productive while relational conflict drives people away \cite{filippova2016}; and incivility and toxicity in code review and issue discussions---entitlement, arrogance, insults, tone policing---are now documented at scale \cite{ferreira2021, miller2022, squire2015} and linked to stress and burnout \cite{raman2020}. Exit, whether by leaving or by forking, is the other side of voice \cite{hirschman1970, robles2012, gamalielsson2014}. These studies motivate the \emph{incivility} and \emph{exit threat} mechanisms.

\paragraph{Python.} Python's PEP discussions have been studied before, from the socialisation of newcomers into the python-dev community \cite{ducheneaut2005}, through socio-cognitive analyses of PEP design threads that reconstruct argumentation from quoting practices and show how roles emerge during a discussion \cite{sack2006, barcellini2008b} and how a few cross-participants span the user and developer lists \cite{barcellini2008a}, to the process-mining and role studies of the DeMaP Miner line \cite{keertipati2016, sharma2017, sharma2022demap, sharma2021roles}. Barcellini et al.\ in particular showed, on a handful of PEP threads, that argumentation and position in the thread shape which design proposals survive; Influence Miner takes the same object of study and scales the identification of the arguments to the whole corpus. Figure~\ref{fig:timeline} places the landmark influences on the project---changes of governance, contested language decisions, employers and funders, and venues---on one timeline, above the corpus-derived measure of contestation introduced in Section~\ref{sec:rq6}.

"""
rep("Influence Miner complements these tools. While Preference Miner asks",
    review + io.open(os.path.join(S, 'block_timeline.tex'), encoding='utf-8').read() + "\\subsection{From Preferences to Influence}\n\nInfluence Miner complements these tools. While Preference Miner asks")

# ---- bibliography ----
rep("\\bibitem{ossGovernance}", io.open(os.path.join(S, 'block_bib2.tex'), encoding='utf-8').read().lstrip('\n') + "\n\\bibitem{ossGovernance}")

io.open(p, 'w', encoding='utf-8', newline='\n').write(s)
print('lit assembled', len(s))
