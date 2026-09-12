"""Export a public-inbox v1 git mirror (one RFC-822 message per blob) into
monthly mbox files named <list>-<yyyy>-<mm>.mbox, the layout that
readRepository.readRepository.HyperKittyMboxToPipermail converts into the
pipermail text files GenericMailingListReader reads.

Usage: python export_public_inbox.py <mirror.git> <outDir> <listname>
"""
import email.utils
import os
import re
import subprocess
import sys
from collections import defaultdict

repo, out_dir, listname = sys.argv[1], sys.argv[2], sys.argv[3]
os.makedirs(out_dir, exist_ok=True)

# blob ids of every message in the current tree
tree = subprocess.run(['git', '-C', repo, 'ls-tree', '-r', 'HEAD'], capture_output=True, text=True, check=True).stdout
blobs = [line.split()[2] for line in tree.splitlines() if line.strip()]
print('messages:', len(blobs))

# fetch all blobs in one batch; the id list goes through a file so that git
# never blocks on a full stdin pipe while we are not yet reading its stdout
ids_path = os.path.join(out_dir, '_blob_ids.txt')
with open(ids_path, 'w') as f:
    f.write('\n'.join(blobs) + '\n')
proc = subprocess.Popen(['git', '-C', repo, 'cat-file', '--batch'], stdin=open(ids_path, 'rb'), stdout=subprocess.PIPE)

months = defaultdict(list)
nodate = 0
date_re = re.compile(rb'^Date:\s*(.+?)\r?$', re.M)
from_re = re.compile(rb'^From:\s*(.+?)\r?$', re.M)
for _ in blobs:
    header = proc.stdout.readline()
    if not header:
        break
    sha, kind, size = header.split()
    raw = proc.stdout.read(int(size))
    proc.stdout.read(1)  # trailing newline
    head = raw.split(b'\n\n', 1)[0]
    m = date_re.search(head)
    dt = None
    if m:
        try:
            dt = email.utils.parsedate_to_datetime(m.group(1).decode('latin-1').strip())
        except Exception:
            dt = None
    if dt is None:
        nodate += 1
        continue
    fm = from_re.search(head)
    addr = email.utils.parseaddr(fm.group(1).decode('latin-1')) [1] if fm else 'unknown'
    sep = 'From %s  %s\n' % (addr or 'unknown', dt.strftime('%a %b %d %H:%M:%S %Y'))
    body = raw.replace(b'\r\n', b'\n')
    # escape body lines that would look like separators
    body = re.sub(rb'(?m)^From ', b'>From ', body)
    months[(dt.year, dt.month)].append(sep.encode('latin-1') + body + (b'' if body.endswith(b'\n') else b'\n') + b'\n')

for (y, mo), msgs in sorted(months.items()):
    p = os.path.join(out_dir, '%s-%04d-%02d.mbox' % (listname, y, mo))
    with open(p, 'wb') as f:
        for msg in msgs:
            f.write(msg)
print('months written:', len(months), 'undated skipped:', nodate)
print('first/last:', min(months), max(months))
