import io, os
S = os.path.dirname(os.path.abspath(__file__))


def rw(name, pairs):
    p = os.path.join(S, name)
    s = io.open(p, encoding='utf-8').read()
    for a, b in pairs:
        if a not in s:
            print('MISSING in', name, ':', a[:70])
            continue
        s = s.replace(a, b)
    io.open(p, 'w', encoding='utf-8', newline='\n').write(s)


rw('block_results.tex', [
    ('62,892', '62,791'),
    (r"\emph{Ecosystem} arguments lead by a wide margin (33.6\%", r"\emph{Ecosystem} arguments lead by a wide margin (33.7\%"),
    (r"\emph{Compatibility} follows (12.0\%", r"\emph{Compatibility} follows (12.1\%"),
    ('Ecosystem & 21,151 & 33.6 & 18.4', 'Ecosystem & 21,151 & 33.7 & 18.4'),
    ('Compatibility & 7,568 & 12.0 & 13.6', 'Compatibility & 7,568 & 12.1 & 13.6'),
    ('Organizational & 2,796 & 4.4 & 3.2', 'Organizational & 2,796 & 4.5 & 3.2'),
    (r"17.5\% of Bitcoin's candidates are \emph{external}", r"17.6\% of Bitcoin's candidates are \emph{external}"),
    (r"56.7\% are ``active debate'' and 36.6\% post-decision", r"56.7\% are ``active debate'' and 36.5\% post-decision"),
    ('Community member & 36,223 & 57.6 & 597 & 2.3 & 17.8 & 13.7', 'Community member & 36,139 & 57.6 & 597 & 2.3 & 17.9 & 13.7'),
    ('Core developer & 17,050 & 27.1 & 48 & 2.8 & 18.1 & 17.4', 'Core developer & 17,045 & 27.1 & 48 & 2.8 & 18.1 & 17.4'),
    ('Proposal author & 6,188 & 9.8', 'Proposal author & 6,179 & 9.8'),
    ('BIP editor & 1,531 & 2.4', 'BIP editor & 1,530 & 2.4'),
    ('Maintainer & 1,246 & 2.0', 'Maintainer & 1,243 & 2.0'),
    (r"community members and core developers carry the highest security shares (17.8\% and 18.1\%)", r"community members and core developers carry the highest security shares (17.9\% and 18.1\%)"),
    ("Antoine Riard (3,717 candidates on 64 BIPs), Anthony Towns (1,866 on 58), Eric Voskuil (1,829 on 49), the pseudonymous \\emph{conduition} (1,711 on 19), Peter Todd (1,360 on 51), Johnson Lau (2,970 across his roles as core developer, author and community member), Jorge Tim\\'on (1,059 on 35), Matt Corallo (957 on 59) and Mike Hearn (819 on 17)",
     "Antoine Riard (about 3,700 candidates on 64 BIPs), Anthony Towns (1,900 on 58), Eric Voskuil (1,800 on 49), the pseudonymous \\emph{conduition} (1,700 on 19), Peter Todd (1,400 on 51), Johnson Lau (3,000 across his roles as core developer, author and community member), Jorge Tim\\'on (1,100 on 35), Matt Corallo (950 on 59) and Mike Hearn (800 on 17)"),
    (r"Direction--outcome alignment is 74.8\% for supporting sentences", r"Direction--outcome alignment is 74.9\% for supporting sentences"),
    ('Candidates & 3,484 & 21,729 & 14,920 & 22,759', 'Candidates & 3,472 & 21,713 & 14,900 & 22,706'),
    ('Any controversial & 6.3 & 3.6 & 3.4 & 5.0', 'Any controversial & 5.8 & 3.4 & 3.2 & 4.6'),
    (r"from the drop in controversial mechanisms from 6.3\% (Andresen years) to 3.6\% (war) and 3.4\% (post-war), and their rise again to 5.0\% in the post-lead era",
     r"from the drop in controversial mechanisms from 5.8\% (Andresen years) to 3.4\% (war) and 3.2\% (post-war), and their rise again to 4.6\% in the post-lead era"),
])
rw('block_compare.tex', [
    ('62,892', '62,791'), ('Ecosystem & 33.6 & 18.4', 'Ecosystem & 33.7 & 18.4'), ('Compatibility & 12.0 & 13.6', 'Compatibility & 12.1 & 13.6'),
    ('Organizational & 4.4 & 3.2', 'Organizational & 4.5 & 3.2'), ('External or mixed scope & 27.5 & 7.7', 'External or mixed scope & 27.6 & 7.7'),
    ('Supporting aligned with outcome & 74.8 & 70.0', 'Supporting aligned with outcome & 74.9 & 70.0'),
    (r"Scope confirms the same picture from another angle: 27.5\%", r"Scope confirms the same picture from another angle: 27.6\%"),
])
rw('block_discussion.tex', [('62,892', '62,791')])
rw('block_abstract.tex', [('62,892', '62,791')])
io.open(os.path.join(S, 'data_contro_years.txt'), 'w').write('2011/4.4, 2012/4.3, 2013/6.4, 2014/4.7, 2015/4.4, 2016/3.0, 2017/2.6, 2018/4.0, 2019/4.4, 2020/2.3, 2021/2.9, 2022/4.9, 2023/5.9, 2024/3.4, 2025/4.0, 2026/4.1')
io.open(os.path.join(S, 'data_contro_pct.txt'), 'w').write('3.9')
print('ok')
