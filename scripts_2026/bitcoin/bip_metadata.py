"""Parse the bitcoin/bips repository into two CSV files:

  bipdetails_github.csv   one row per BIP from the current preamble
                          (bip, title, authors, author_emails, status, type, layer, created, discussions)
  bipstates_github.csv    one row per Status: change found in the git history
                          (bip, status_from, status_to, datetime_utc, commit)

Handles both the BIP 2 preamble (Author:, Created:) and the BIP 3 preamble
introduced in 2025 (Authors:, Assigned:, Status: Deployed/Complete ...).

Usage: python bip_metadata.py <repo> <outDir>
"""
import csv
import os
import re
import subprocess
import sys
from datetime import datetime, timezone

repo, out_dir = sys.argv[1], sys.argv[2]
os.makedirs(out_dir, exist_ok=True)

HEADER_KEYS = ('BIP', 'Title', 'Author', 'Authors', 'Status', 'Type', 'Layer', 'Created', 'Assigned',
               'Discussions-To', 'Discussion', 'Comments-URI', 'Replaces', 'Superseded-By', 'Requires', 'License')


def parse_preamble(text):
    """Return dict of header -> value (continuation lines joined with ' | ')."""
    m = re.search(r'<pre>(.*?)</pre>', text, re.S)
    block = m.group(1) if m else text[:4000]
    fields, key = {}, None
    for line in block.splitlines():
        if not line.strip():
            continue
        mm = re.match(r'^\s*([A-Za-z-]+):\s*(.*)$', line)
        if mm and mm.group(1) in HEADER_KEYS:
            key = mm.group(1)
            fields[key] = mm.group(2).strip()
        elif key and line.startswith(' '):
            fields[key] = (fields[key] + ' | ' + line.strip()).strip(' |')
    return fields


def bip_number(fname):
    m = re.match(r'bip-(\d+)\.(mediawiki|md)$', fname)
    return int(m.group(1)) if m else None


# ---- current preambles ----
rows = []
for fname in sorted(os.listdir(repo)):
    n = bip_number(fname)
    if n is None:
        continue
    text = open(os.path.join(repo, fname), encoding='utf-8', errors='replace').read()
    f = parse_preamble(text)
    authors = f.get('Authors') or f.get('Author') or ''
    emails = ' | '.join(re.findall(r'<([^>]+)>', authors))
    names = ' | '.join(re.sub(r'\s*<[^>]+>', '', a).strip() for a in authors.split('|'))
    rows.append({
        'bip': n, 'title': f.get('Title', ''), 'authors': names, 'author_emails': emails,
        'status': f.get('Status', ''), 'type': f.get('Type', ''), 'layer': f.get('Layer', ''),
        'created': f.get('Created') or f.get('Assigned') or '',
        'discussions': f.get('Discussions-To') or f.get('Discussion') or '',
        'file': fname,
    })
with open(os.path.join(out_dir, 'bipdetails_github.csv'), 'w', newline='', encoding='utf-8') as fh:
    w = csv.DictWriter(fh, fieldnames=list(rows[0].keys()))
    w.writeheader()
    w.writerows(rows)
print('bips:', len(rows))

# ---- status history: every commit that touches a Status: line ----
log = subprocess.run(['git', '-C', repo, 'log', '--reverse', '-p', '--date=iso-strict', '--format=@@@%H %ad',
                      '-G', r'^\s*Status:', '--', 'bip-*.mediawiki', 'bip-*.md'],
                     capture_output=True, text=True, encoding='utf-8', errors='replace').stdout
states = []
commit = when = None
current_file = None
old_status = new_status = None


def flush():
    global old_status, new_status
    if current_file is not None and new_status is not None and old_status != new_status:
        n = bip_number(current_file)
        if n is not None:
            states.append({'bip': n, 'status_from': old_status or '', 'status_to': new_status,
                           'datetime_utc': when, 'commit': commit})
    old_status = new_status = None


for line in log.splitlines():
    if line.startswith('@@@'):
        flush()
        commit, ad = line[3:].split(' ', 1)
        when = datetime.fromisoformat(ad).astimezone(timezone.utc).strftime('%Y-%m-%d %H:%M:%S')
        current_file = None
    elif line.startswith('diff --git'):
        flush()
        m = re.search(r' b/(bip-\d+\.(?:mediawiki|md))$', line)
        current_file = m.group(1) if m else None
    elif current_file and line.startswith('-') and not line.startswith('---'):
        m = re.match(r'^-\s*Status:\s*(.+?)\s*$', line)
        if m:
            old_status = m.group(1)
    elif current_file and line.startswith('+') and not line.startswith('+++'):
        m = re.match(r'^\+\s*Status:\s*(.+?)\s*$', line)
        if m:
            new_status = m.group(1)
flush()

with open(os.path.join(out_dir, 'bipstates_github.csv'), 'w', newline='', encoding='utf-8') as fh:
    w = csv.DictWriter(fh, fieldnames=['bip', 'status_from', 'status_to', 'datetime_utc', 'commit'])
    w.writeheader()
    w.writerows(states)
print('status changes:', len(states), 'bips with history:', len({s['bip'] for s in states}))
