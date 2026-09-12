"""Load BIP metadata, status history and role rosters into bips_2026.

Creates / fills:
  bipdetails        (bip, title, author, authorCorrected, authorEmail, type, bdfl_delegate, bdfl_delegateCorrected, created, layer, discussions)
  bipdetails_github same content (kept separate so bipdetails can be hand-corrected)
  bipstates_github  (id, BIP, dateTimeStamp, statusFrom, statusTo, state, commitHash)
  accrejbips        (BIP, state, date2)  final decision = last status before the BIP 3 bulk relabel of 2025-04-12,
                    or the current status for BIPs assigned after it
  bipeditors        (bipeditor, dateadded, dateremoved)
  leadmaintainers   (name, start_date, end_date)
  coremaintainers   (name, alias, first_merge, last_merge)   merge committers of bitcoin/bitcoin (>= 20 merges)
  coredevelopers    (coredeveloper, github, dateadded)       contributors with >= 10 non-merge commits (dateadded = first commit)

Usage: python load_bip_metadata.py <metaDir>
"""
import csv
import os
import subprocess
import sys

MYSQL = r'C:\xampp\mysql\bin\mysql.exe'
meta = sys.argv[1]


def q(v):
    if v is None:
        return 'NULL'
    return "'" + str(v).replace('\\', '\\\\').replace("'", "\\'") + "'"


sql = ['USE bips_2026;', 'SET NAMES utf8mb4;']

# ---- bipdetails ----
sql.append('DROP TABLE IF EXISTS bipdetails_github;')
sql.append('''CREATE TABLE bipdetails_github (bip INT, title TEXT, author TEXT, authorCorrected TEXT, authorEmail TEXT, type TEXT,
  bdfl_delegate TEXT, bdfl_delegateCorrected TEXT, created DATE, layer TEXT, status TEXT, discussions TEXT, KEY(bip)) ENGINE=InnoDB ROW_FORMAT=DYNAMIC;''')
with open(os.path.join(meta, 'bipdetails_github.csv'), encoding='utf-8') as f:
    for r in csv.DictReader(f):
        created = r['created'].split('|')[0].strip()[:10] if r['created'] else None
        if created and len(created) != 10:
            created = None
        authors = r['authors'].replace(' | ', ', ')
        emails = r['author_emails'].replace(' | ', ', ')
        sql.append('INSERT INTO bipdetails_github VALUES (%s,%s,%s,%s,%s,%s,NULL,NULL,%s,%s,%s,%s);' % (
            r['bip'], q(r['title']), q(authors), q(authors), q(emails), q(r['type']), q(created), q(r['layer']), q(r['status']), q(r['discussions'][:2000])))
sql.append('DROP TABLE IF EXISTS bipdetails; CREATE TABLE bipdetails LIKE bipdetails_github; INSERT INTO bipdetails SELECT * FROM bipdetails_github;')

# ---- state history ----
sql.append('DROP TABLE IF EXISTS bipstates_github;')
sql.append('''CREATE TABLE bipstates_github (id INT AUTO_INCREMENT PRIMARY KEY, BIP INT, dateTimeStamp DATETIME, statusFrom VARCHAR(30),
  statusTo VARCHAR(30), state VARCHAR(30), commitHash CHAR(40), KEY(BIP), KEY(dateTimeStamp)) ENGINE=InnoDB;''')
with open(os.path.join(meta, 'bipstates_github.csv'), encoding='utf-8') as f:
    for r in csv.DictReader(f):
        sql.append('INSERT INTO bipstates_github (BIP, dateTimeStamp, statusFrom, statusTo, state, commitHash) VALUES (%s,%s,%s,%s,%s,%s);' % (
            r['bip'], q(r['datetime_utc']), q(r['status_from'] or None), q(r['status_to']), q(r['status_to']), q(r['commit'])))

# ---- final decision table: last status before the BIP 3 relabel, else current ----
sql.append('DROP TABLE IF EXISTS accrejbips;')
sql.append('''CREATE TABLE accrejbips (BIP INT, state LONGTEXT, date2 DATE, KEY(BIP)) ENGINE=InnoDB;''')
sql.append('''INSERT INTO accrejbips
SELECT s.BIP, s.state, DATE(s.dateTimeStamp) FROM bipstates_github s
JOIN (SELECT BIP, MAX(id) mid FROM bipstates_github WHERE dateTimeStamp < '2025-04-12' GROUP BY BIP) m ON m.mid = s.id
WHERE s.state IN ('Accepted','Final','Active','Rejected','Withdrawn','Replaced','Obsolete','Deferred');''')
sql.append('''INSERT INTO accrejbips
SELECT s.BIP, s.state, DATE(s.dateTimeStamp) FROM bipstates_github s
JOIN (SELECT BIP, MAX(id) mid FROM bipstates_github GROUP BY BIP) m ON m.mid = s.id
WHERE s.BIP NOT IN (SELECT BIP FROM accrejbips) AND s.state IN ('Deployed','Complete','Closed','Final','Active','Rejected','Withdrawn','Replaced','Obsolete');''')

# ---- rosters ----
sql.append('DROP TABLE IF EXISTS bipeditors; CREATE TABLE bipeditors (bipeditor TINYTEXT, dateadded DATE, dateremoved DATE) ENGINE=InnoDB;')
for name, a, r in [('Amir Taaki', '2011-08-19', '2013-02-01'), ('Gregory Maxwell', '2013-02-01', '2016-01-06'),
                   ('Luke Dashjr', '2016-01-06', '2026-08-09'), ('Kalle Alm', '2021-05-06', '2024-04-22'),
                   ('Bryan Bishop', '2024-04-22', None), ('Jon Atack', '2024-04-22', None), ('Mark Erhardt', '2024-04-22', None),
                   ('Murch', '2024-04-22', None), ('Olaoluwa Osuntokun', '2024-04-22', None), ('Ruben Somsen', '2024-04-22', None)]:
    sql.append('INSERT INTO bipeditors VALUES (%s,%s,%s);' % (q(name), q(a), q(r)))

sql.append('DROP TABLE IF EXISTS leadmaintainers; CREATE TABLE leadmaintainers (name TINYTEXT, start_date DATE, end_date DATE) ENGINE=InnoDB;')
sql.append("INSERT INTO leadmaintainers VALUES ('Gavin Andresen','2010-12-19','2014-04-07'), ('Wladimir J. van der Laan','2014-04-08','2022-01-31');")

sql.append('DROP TABLE IF EXISTS coremaintainers; CREATE TABLE coremaintainers (name TINYTEXT, alias TINYTEXT, first_merge DATE, last_merge DATE) ENGINE=InnoDB;')
alias = {'W. J. van der Laan': 'Wladimir J. van der Laan', 'laanwj': 'Wladimir J. van der Laan', 'MacroFake': 'MarcoFalke',
         'Ava Chow': 'Andrew Chow', 'fanquake': 'Michael Ford', 'glozow': 'Gloria Zhao', 'MeshCollider': 'Samuel Dobson'}
with open(os.path.join(meta, 'bitcoin_maintainers.csv'), encoding='utf-8') as f:
    for r in csv.DictReader(f):
        if r['name'] == 'merge-script':
            continue
        canon = alias.get(r['name'], r['name'])
        sql.append('INSERT INTO coremaintainers VALUES (%s,%s,%s,%s);' % (q(canon), q(r['name']), q(r['first_merge']), q(r['last_merge'])))
sql.append("INSERT INTO coremaintainers VALUES ('Marco Falke','MarcoFalke','2015-10-09','2022-11-28'), ('Ava Chow','Andrew Chow','2021-12-20','2026-09-09');")

sql.append('DROP TABLE IF EXISTS coredevelopers; CREATE TABLE coredevelopers (coredeveloper TINYTEXT, github TINYTEXT, dateadded DATE) ENGINE=InnoDB;')
with open(os.path.join(meta, 'bitcoin_contributors.csv'), encoding='utf-8') as f:
    for r in csv.DictReader(f):
        sql.append('INSERT INTO coredevelopers VALUES (%s,NULL,%s);' % (q(r['name']), q(r['first_commit'])))

# empty tables the tools expect
sql.append('CREATE TABLE IF NOT EXISTS authorandrole (author MEDIUMTEXT, authorsrole MEDIUMTEXT) ENGINE=InnoDB;')

path = os.path.join(meta, 'load_bip_metadata.sql')
open(path, 'w', encoding='utf-8').write('\n'.join(sql))
r = subprocess.run([MYSQL, '-uroot', '--default-character-set=utf8mb4'], stdin=open(path, 'rb'), capture_output=True, text=True)
print(r.stdout, r.stderr)
print(subprocess.run([MYSQL, '-uroot', '-t', 'bips_2026', '-e',
                      "SELECT COUNT(*) bips FROM bipdetails; SELECT COUNT(*) states FROM bipstates_github; SELECT state, COUNT(*) FROM accrejbips GROUP BY state; SELECT COUNT(*) maint FROM coremaintainers; SELECT COUNT(*) devs FROM coredevelopers;"],
                     capture_output=True, text=True).stdout)
