"""Numbers after the 13 Sept 2026 re-run (direction detector: bare 'split' cue removed)."""
import io, os
S = os.path.dirname(os.path.abspath(__file__))
def rw(name, pairs):
    p = os.path.join(S, name); s = io.open(p, encoding='utf-8').read()
    for a, b in pairs:
        if a not in s: print('MISSING in', name, ':', a[:70]); continue
        s = s.replace(a, b)
    io.open(p, 'w', encoding='utf-8', newline='\n').write(s)
rw('block_results.tex', [
    ('62,791', '62,789'),
    ('Direction is neutral for 88.5\% of candidates, revising for 8.7\%, supporting for 1.5\% and blocking for 1.4\%---almost exactly Python\'s distribution (87.7 / 8.8 / 2.0 / 1.5).',
     'Direction is neutral for 89.4\% of candidates, revising for 7.7\%, supporting for 1.5\% and blocking for 1.4\%---close to Python\'s distribution (87.7 / 8.8 / 2.0 / 1.5); the revising share is lower than Python\'s because the ``split\'\' cue, which reads as a request to divide a proposal in Python, was withdrawn after it proved to fire on \emph{chain split} in Bitcoin.'),
    ('Community member & 36,139 &', 'Community member & 36,137 &'),
    ('Direction--outcome alignment is 74.9\% for supporting sentences, 12.1\% for blocking and 9.7\% for revising', 'Direction--outcome alignment is 74.9\% for supporting sentences, 12.1\% for blocking and 10.8\% for revising'),
])
rw('block_compare.tex', [('62,791', '62,789'), ('Supporting / blocking / revising & 1.5 / 1.4 / 8.7 &', 'Supporting / blocking / revising & 1.5 / 1.4 / 7.7 &'),
    ('Direction distributions are nearly identical (about 88\% neutral, 9\% revising, 1.5\% supporting, 1.4\% blocking)', 'Direction distributions are nearly identical (88--89\% neutral, 8--9\% revising, 1.5--2\% supporting, 1.4--1.5\% blocking)')])
rw('block_rq6.tex', [('62,791', '62,789'), ('Community member & 36,139 &', 'Community member & 36,137 &')])
rw('block_icis.tex', [('Candidates & 3,065 & 2,598 & 3,261 & 551', 'Candidates & 3,065 & 2,597 & 3,261 & 551')])
rw('block_abstract.tex', [('62,791', '62,789')])
rw('block_discussion.tex', [('62,791', '62,789')])
rw('block_intro.tex', [('62,791', '62,789')])
print('done')
