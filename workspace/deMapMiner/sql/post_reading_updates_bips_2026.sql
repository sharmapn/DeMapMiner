-- Post-reading updates for the Bitcoin corpus (bips_2026, bitcoin-dev 2011-06 .. 2026-09), September 2026.
-- Mirrors post_reading_updates_2026.sql for Python: sender fields from the raw From: header
-- (the loader stores the sender of the *next* message in the address columns), cluster names,
-- and role labels in authorsrole2020 with the Bitcoin vocabulary:
--   leadmaintainer, bipeditor, coremaintainer, coredeveloper, proposalAuthor, otherCommunityMember
USE bips_2026;
SET SESSION tmp_table_size = 2147483648; SET SESSION max_heap_table_size = 2147483648;

-- 1. sender address from the raw header ("From: local at domain (Name)" pipermail form, or plain address)
UPDATE allmessages
SET senderemail = LOWER(REPLACE(TRIM(SUBSTRING(REGEXP_SUBSTR(email, '(?m)^From: [^ \r\n]+ at [^ \r\n]+'), 7)), ' at ', '@'))
WHERE email REGEXP '(?m)^From: [^ \r\n]+ at [^ \r\n]+';
UPDATE allmessages
SET senderemail = LOWER(TRIM(SUBSTRING(REGEXP_SUBSTR(email, '(?m)^From: [^ \r\n@]+@[^ \r\n]+'), 7)))
WHERE senderemail IS NULL AND email REGEXP '(?m)^From: [^ \r\n@]+@[^ \r\n]+';
-- display name from the header's parenthesised part
UPDATE allmessages
SET senderName = TRIM(BOTH ')' FROM TRIM(BOTH '(' FROM REGEXP_SUBSTR(email, '(?m)^From: [^\r\n]*\\([^)\r\n]*\\)')))
WHERE email REGEXP '(?m)^From: [^\r\n]*\\([^)\r\n]*\\)';
UPDATE allmessages SET senderName = TRIM(SUBSTRING_INDEX(senderName, '(', -1)) WHERE senderName LIKE '%(%';
UPDATE allmessages SET senderName = SUBSTRING_INDEX(senderemail, '@', 1) WHERE senderName IS NULL OR TRIM(senderName) = '';
UPDATE allmessages
SET senderFullName = senderName,
    senderemailProcessed = senderemail,
    senderEmailFirstSegment = SUBSTRING_INDEX(senderemail, '@', 1),
    senderFirstName = SUBSTRING_INDEX(TRIM(senderName), ' ', 1),
    senderLastName  = SUBSTRING_INDEX(TRIM(senderName), ' ', -1);

-- 2. cluster name: the most frequent display name per address, then hand aliases
DROP TEMPORARY TABLE IF EXISTS tmp_email_cluster;
CREATE TEMPORARY TABLE tmp_email_cluster AS
SELECT senderemail, senderName AS cluster FROM (
  SELECT senderemail, senderName, COUNT(*) c, ROW_NUMBER() OVER (PARTITION BY senderemail ORDER BY COUNT(*) DESC, LENGTH(senderName) DESC) rn
  FROM allmessages WHERE senderemail IS NOT NULL AND senderName IS NOT NULL AND senderName <> ''
  GROUP BY senderemail, senderName) x WHERE rn = 1;
ALTER TABLE tmp_email_cluster ADD INDEX (senderemail(120));
UPDATE allmessages a JOIN tmp_email_cluster t ON t.senderemail = a.senderemail SET a.clusterBySenderFullName = t.cluster;
UPDATE allmessages SET clusterBySenderFullName = TRIM(senderName) WHERE clusterBySenderFullName IS NULL OR clusterBySenderFullName = '';
-- well-known aliases (handles and name variants used on the list)
UPDATE allmessages SET clusterBySenderFullName = 'Wladimir J. van der Laan' WHERE LOWER(clusterBySenderFullName) IN ('wladimir','laanwj','w. j. van der laan','wladimir van der laan','wladimir j. van der laan');
UPDATE allmessages SET clusterBySenderFullName = 'Gregory Maxwell' WHERE LOWER(clusterBySenderFullName) IN ('greg maxwell','gmaxwell','gregory maxwell');
UPDATE allmessages SET clusterBySenderFullName = 'Pieter Wuille' WHERE LOWER(clusterBySenderFullName) IN ('sipa','pieter wuille');
UPDATE allmessages SET clusterBySenderFullName = 'Luke Dashjr' WHERE LOWER(clusterBySenderFullName) IN ('luke-jr','luke dashjr','luke dash jr','luke dashjr (bip editor)');
UPDATE allmessages SET clusterBySenderFullName = 'Marco Falke' WHERE LOWER(clusterBySenderFullName) IN ('marcofalke','macrofake','marco falke');
UPDATE allmessages SET clusterBySenderFullName = 'Ava Chow' WHERE LOWER(clusterBySenderFullName) IN ('andrew chow','ava chow','achow101');
UPDATE allmessages SET clusterBySenderFullName = 'Michael Ford' WHERE LOWER(clusterBySenderFullName) IN ('fanquake','michael ford');
UPDATE allmessages SET clusterBySenderFullName = 'Gloria Zhao' WHERE LOWER(clusterBySenderFullName) IN ('glozow','gloria zhao');
UPDATE allmessages SET clusterBySenderFullName = 'Anthony Towns' WHERE LOWER(clusterBySenderFullName) IN ('aj','ajtowns','anthony towns');
UPDATE allmessages SET clusterBySenderFullName = 'Bryan Bishop' WHERE LOWER(clusterBySenderFullName) IN ('kanzure','bryan bishop');
UPDATE allmessages SET clusterBySenderFullName = 'Mark Erhardt' WHERE LOWER(clusterBySenderFullName) IN ('murch','mark "murch" erhardt','mark erhardt','murchandamus');
UPDATE allmessages SET clusterBySenderFullName = 'Olaoluwa Osuntokun' WHERE LOWER(clusterBySenderFullName) IN ('roasbeef','olaoluwa osuntokun','laolu');
UPDATE allmessages SET clusterBySenderFullName = 'Matt Corallo' WHERE LOWER(clusterBySenderFullName) IN ('bluematt','matt corallo','thebluematt');
UPDATE allmessages SET clusterBySenderFullName = 'Peter Todd' WHERE LOWER(clusterBySenderFullName) IN ('peter todd','petertodd');
UPDATE allmessages SET clusterBySenderFullName = 'Jonas Schnelli' WHERE LOWER(clusterBySenderFullName) IN ('jonas schnelli','jonasschnelli');
UPDATE allmessages SET clusterBySenderFullName = 'Gavin Andresen' WHERE LOWER(clusterBySenderFullName) IN ('gavin andresen','gavin');

-- 3. roles per (message, BIP), keyed like the Python script
DROP TEMPORARY TABLE IF EXISTS tmp_new;
CREATE TEMPORARY TABLE tmp_new (
  messageID INT, BIP INT NOT NULL, d DATE, email VARCHAR(255), name VARCHAR(255), fl VARCHAR(255), role VARCHAR(40),
  PRIMARY KEY (messageID, BIP), INDEX(name), INDEX(fl), INDEX(email), INDEX(BIP)
) ENGINE=MyISAM;
INSERT IGNORE INTO tmp_new (messageID, BIP, d, email, name, fl)
SELECT messageID, COALESCE(bip, -1), DATE(dateTimeStamp), senderemail, LOWER(TRIM(clusterBySenderFullName)),
       LOWER(CONCAT(SUBSTRING_INDEX(TRIM(clusterBySenderFullName), ' ', 1), ' ', SUBSTRING_INDEX(TRIM(clusterBySenderFullName), ' ', -1)))
FROM allmessages;

-- lead maintainer (Gavin Andresen, then Wladimir van der Laan) during their terms
UPDATE tmp_new t JOIN leadmaintainers l ON t.name = LOWER(l.name) AND t.d BETWEEN l.start_date AND l.end_date
SET t.role = 'leadmaintainer' WHERE t.role IS NULL;
-- BIP editors during their terms
UPDATE tmp_new t JOIN bipeditors e ON t.name = LOWER(TRIM(e.bipeditor)) AND t.d >= e.dateadded AND (e.dateremoved IS NULL OR t.d <= e.dateremoved)
SET t.role = 'bipeditor' WHERE t.role IS NULL;
-- proposal authors on their own BIP (emails or names from the preamble)
DROP TEMPORARY TABLE IF EXISTS tmp_bip_people;
CREATE TEMPORARY TABLE tmp_bip_people (bip INT, kind VARCHAR(10), who VARCHAR(255), INDEX(bip), INDEX(who));
INSERT INTO tmp_bip_people
SELECT p.bip, 'email', LOWER(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(p.authorEmail, ',', n.n), ',', -1)))
FROM bipdetails p JOIN (SELECT 1 n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8) n
  ON n.n <= 1 + LENGTH(p.authorEmail) - LENGTH(REPLACE(p.authorEmail, ',', ''))
WHERE p.authorEmail IS NOT NULL AND p.authorEmail <> '';
INSERT INTO tmp_bip_people
SELECT p.bip, 'name', LOWER(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(p.authorCorrected, ',', n.n), ',', -1)))
FROM bipdetails p JOIN (SELECT 1 n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8) n
  ON n.n <= 1 + LENGTH(p.authorCorrected) - LENGTH(REPLACE(p.authorCorrected, ',', ''))
WHERE p.authorCorrected IS NOT NULL AND p.authorCorrected <> '';
UPDATE tmp_new t JOIN tmp_bip_people a ON a.bip = t.BIP AND ((a.kind = 'email' AND a.who = t.email) OR (a.kind = 'name' AND a.who = t.name))
SET t.role = 'proposalAuthor' WHERE t.role IS NULL AND t.BIP > 0;
-- Bitcoin Core maintainers (merge access) during the years they merged
UPDATE tmp_new t JOIN coremaintainers m ON (t.name = LOWER(m.name) OR t.name = LOWER(m.alias)) AND t.d BETWEEN m.first_merge AND m.last_merge
SET t.role = 'coremaintainer' WHERE t.role IS NULL;
-- regular Bitcoin Core contributors from their first commit
DROP TEMPORARY TABLE IF EXISTS tmp_core;
CREATE TEMPORARY TABLE tmp_core (name VARCHAR(255), fl VARCHAR(255), joined DATE, INDEX(name), INDEX(fl));
INSERT INTO tmp_core SELECT LOWER(TRIM(coredeveloper)), LOWER(CONCAT(SUBSTRING_INDEX(TRIM(coredeveloper), ' ', 1), ' ', SUBSTRING_INDEX(TRIM(coredeveloper), ' ', -1))), dateadded FROM coredevelopers;
UPDATE tmp_new t JOIN tmp_core c ON c.name = t.name SET t.role = 'coredeveloper' WHERE t.role IS NULL AND (c.joined IS NULL OR t.d >= c.joined);
UPDATE tmp_new t JOIN tmp_core c ON c.fl = t.fl AND c.fl LIKE '% %' SET t.role = 'coredeveloper' WHERE t.role IS NULL AND (c.joined IS NULL OR t.d >= c.joined);
UPDATE tmp_new SET role = 'otherCommunityMember' WHERE role IS NULL;

UPDATE allmessages a JOIN tmp_new t ON t.messageID = a.messageID AND t.BIP = COALESCE(a.bip, -1)
SET a.authorsrole2020 = t.role, a.authorsrole = t.role;

-- summary
SELECT COUNT(*) rows_, COUNT(DISTINCT messageID) messages, SUM(bip > 0) bip_linked, MIN(date2) first_, MAX(date2) last_ FROM allmessages;
SELECT authorsrole2020, COUNT(*) FROM allmessages WHERE bip > 0 GROUP BY 1 ORDER BY 2 DESC;
SELECT SUM(senderemail IS NULL) no_email, SUM(clusterBySenderFullName IS NULL OR clusterBySenderFullName = '') no_name FROM allmessages;
SELECT clusterBySenderFullName, COUNT(*) c FROM allmessages GROUP BY 1 ORDER BY c DESC LIMIT 15;
