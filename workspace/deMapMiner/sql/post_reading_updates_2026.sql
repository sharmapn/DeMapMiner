-- Post-reading updates for rows imported in September 2026 (messageID >= 10066940)
-- into peps_2026.allmessages by GenericMailingListReader_Main.
--
-- Replaces, for the new rows only, the legacy UpdateAllMessages_SenderName /
-- UpdateAllMessages_MessageAuthorsRole scripts. The sender columns are derived
-- directly from the raw "From:" header stored in the email column, keyed by
-- messageID, so the row-shift that affected the 2017-2018 import cannot recur.
--
-- Run:  mysql -uroot peps_2026 < post_reading_updates_2026.sql

USE peps_2026;

SET @first_new = 10066940;
SET SESSION tmp_table_size = 2147483648; SET SESSION max_heap_table_size = 2147483648;

-- 1. sender e-mail / names ----------------------------------------------------
UPDATE allmessages
SET senderemail = LOWER(REPLACE(TRIM(SUBSTRING(REGEXP_SUBSTR(email, '(?m)^From: [^ \r\n]+ at [^ \r\n]+'), 7)), ' at ', '@'))
WHERE messageID >= @first_new AND email REGEXP '(?m)^From: [^ \r\n]+ at [^ \r\n]+';

UPDATE allmessages
SET senderemail = LOWER(TRIM(SUBSTRING(REGEXP_SUBSTR(email, '(?m)^From: [^ \r\n@]+@[^ \r\n]+'), 7)))
WHERE messageID >= @first_new AND senderemail IS NULL AND email REGEXP '(?m)^From: [^ \r\n@]+@[^ \r\n]+';

UPDATE allmessages
SET senderFullName = senderName,
    senderemailProcessed = senderemail,
    senderEmailFirstSegment = SUBSTRING_INDEX(senderemail, '@', 1),
    senderFirstName = SUBSTRING_INDEX(TRIM(senderName), ' ', 1),
    senderLastName  = SUBSTRING_INDEX(TRIM(senderName), ' ', -1)
WHERE messageID >= @first_new;

-- 2. cluster name: reuse the name the old corpus used for the same address,
--    otherwise the display name from the header ---------------------------------
DROP TEMPORARY TABLE IF EXISTS tmp_email_cluster;
CREATE TEMPORARY TABLE tmp_email_cluster AS
SELECT senderemail, clusterBySenderFullName AS cluster
FROM (
  SELECT senderemail, clusterBySenderFullName, COUNT(*) c,
         ROW_NUMBER() OVER (PARTITION BY senderemail ORDER BY COUNT(*) DESC) rn
  FROM allmessages
  WHERE messageID < @first_new AND senderemail IS NOT NULL AND clusterBySenderFullName IS NOT NULL
    AND clusterBySenderFullName <> '' AND senderFullName NOT LIKE 'From %'
  GROUP BY senderemail, clusterBySenderFullName
) x WHERE rn = 1;
ALTER TABLE tmp_email_cluster ADD INDEX (senderemail(120));

UPDATE allmessages a JOIN tmp_email_cluster t ON t.senderemail = a.senderemail
SET a.clusterBySenderFullName = t.cluster
WHERE a.messageID >= @first_new;

UPDATE allmessages
SET clusterBySenderFullName = TRIM(senderName)
WHERE messageID >= @first_new AND (clusterBySenderFullName IS NULL OR clusterBySenderFullName = '');

-- 3. proposal number columns used by the newer scripts ----------------------------
UPDATE allmessages SET pepnum2020 = PEP, pepnum2021 = PEP, peptype2020 = pepType
WHERE messageID >= @first_new AND PEP > 0;

-- 4. author role per message ------------------------------------------------------
--    vocabulary as in authorsrole2020, plus 'steeringcouncil' for the post-BDFL era.
--    Computed on an indexed work table of the new rows only (joins against
--    allmessages with functions on the join column cannot use an index and take hours).
DROP TEMPORARY TABLE IF EXISTS tmp_new;
CREATE TEMPORARY TABLE tmp_new (
  messageID INT, PEP INT NOT NULL, d DATE, email VARCHAR(255), name VARCHAR(255), fl VARCHAR(255), role VARCHAR(40),
  PRIMARY KEY (messageID, PEP), INDEX(name), INDEX(fl), INDEX(email), INDEX(PEP)
) ENGINE=MyISAM;
INSERT IGNORE INTO tmp_new (messageID, PEP, d, email, name, fl)
SELECT messageID, COALESCE(PEP, -1), DATE(dateTimeStamp), senderemail, LOWER(TRIM(clusterBySenderFullName)),
       LOWER(CONCAT(SUBSTRING_INDEX(TRIM(clusterBySenderFullName), ' ', 1), ' ', SUBSTRING_INDEX(TRIM(clusterBySenderFullName), ' ', -1)))
FROM allmessages WHERE messageID >= @first_new;

-- 4a. BDFL: Guido until 12 July 2018
UPDATE tmp_new SET role = 'bdfl'
WHERE role IS NULL AND (email IN ('guido@python.org','gvanrossum@gmail.com','guido@discuss.python.org') OR name = 'guido van rossum') AND d < '2018-07-12';

-- 4b. Steering Council member during the message's term
UPDATE tmp_new t JOIN steeringcouncil s ON t.name = LOWER(s.member) AND t.d BETWEEN s.start_date AND s.end_date
SET t.role = 'steeringcouncil' WHERE t.role IS NULL;

-- 4c. proposal author of the PEP the row is linked to
DROP TEMPORARY TABLE IF EXISTS tmp_pep_people;
CREATE TEMPORARY TABLE tmp_pep_people (pep INT, kind VARCHAR(10), who VARCHAR(255), INDEX(pep), INDEX(who));
INSERT INTO tmp_pep_people
SELECT p.pep, 'email', LOWER(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(p.authorEmail, ',', n.n), ',', -1)))
FROM pepdetails p JOIN (SELECT 1 n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6) n
  ON n.n <= 1 + LENGTH(p.authorEmail) - LENGTH(REPLACE(p.authorEmail, ',', ''))
WHERE p.authorEmail IS NOT NULL AND p.authorEmail <> '';
INSERT INTO tmp_pep_people
SELECT p.pep, 'name', LOWER(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(p.authorCorrected, ',', n.n), ',', -1)))
FROM pepdetails p JOIN (SELECT 1 n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6) n
  ON n.n <= 1 + LENGTH(p.authorCorrected) - LENGTH(REPLACE(p.authorCorrected, ',', ''))
WHERE p.authorCorrected IS NOT NULL AND p.authorCorrected <> '';
INSERT INTO tmp_pep_people
SELECT p.pep, 'delegate', LOWER(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(p.bdfl_delegateCorrected, ',', n.n), ',', -1)))
FROM pepdetails p JOIN (SELECT 1 n UNION SELECT 2 UNION SELECT 3) n
  ON n.n <= 1 + LENGTH(p.bdfl_delegateCorrected) - LENGTH(REPLACE(p.bdfl_delegateCorrected, ',', ''))
WHERE p.bdfl_delegateCorrected IS NOT NULL AND p.bdfl_delegateCorrected <> '';

UPDATE tmp_new t JOIN tmp_pep_people a ON a.pep = t.PEP AND ((a.kind = 'email' AND a.who = t.email) OR (a.kind = 'name' AND a.who = t.name))
SET t.role = 'proposalAuthor' WHERE t.role IS NULL AND t.PEP > 0;

-- 4d. PEP delegate (BDFL-Delegate / PEP-Delegate header) of that PEP
UPDATE tmp_new t JOIN tmp_pep_people a ON a.pep = t.PEP AND a.kind = 'delegate' AND a.who = t.name
SET t.role = 'bdfl_delegate' WHERE t.role IS NULL AND t.PEP > 0;

-- 4e. PEP editor
UPDATE tmp_new t JOIN pepeditors e ON t.name = LOWER(TRIM(e.pepeditor))
SET t.role = 'pepeditors' WHERE t.role IS NULL AND (e.dateadded IS NULL OR t.d >= e.dateadded);

-- 4f. core developer: current roster (join/leave dates), by full name then by first+last name, then the legacy table
DROP TEMPORARY TABLE IF EXISTS tmp_core;
CREATE TEMPORARY TABLE tmp_core (name VARCHAR(255), fl VARCHAR(255), joined DATE, left_date DATE, INDEX(name), INDEX(fl));
INSERT INTO tmp_core SELECT LOWER(name), LOWER(CONCAT(SUBSTRING_INDEX(name, ' ', 1), ' ', SUBSTRING_INDEX(name, ' ', -1))), joined, left_date FROM coredevelopers_2026;
INSERT INTO tmp_core SELECT LOWER(TRIM(REPLACE(coredeveloper, '\t', ''))), LOWER(CONCAT(SUBSTRING_INDEX(TRIM(REPLACE(coredeveloper, '\t', '')), ' ', 1), ' ', SUBSTRING_INDEX(TRIM(REPLACE(coredeveloper, '\t', '')), ' ', -1))), dateadded, NULL FROM coredevelopers;

UPDATE tmp_new t JOIN tmp_core c ON c.name = t.name
SET t.role = 'coredeveloper' WHERE t.role IS NULL AND (c.joined IS NULL OR t.d >= c.joined) AND (c.left_date IS NULL OR t.d <= c.left_date);
UPDATE tmp_new t JOIN tmp_core c ON c.fl = t.fl
SET t.role = 'coredeveloper' WHERE t.role IS NULL AND (c.joined IS NULL OR t.d >= c.joined) AND (c.left_date IS NULL OR t.d <= c.left_date);

-- 4g. everyone else
UPDATE tmp_new SET role = 'otherCommunityMember' WHERE role IS NULL;

-- 4h. write back
UPDATE allmessages a JOIN tmp_new t ON t.messageID = a.messageID AND t.PEP = COALESCE(a.PEP, -1)
SET a.authorsrole2020 = t.role, a.authorsrole = t.role;

-- 5. summary --------------------------------------------------------------------
SELECT lastdir, COUNT(*) rows_, SUM(PEP > 0) pep_linked, MIN(date2) first_, MAX(date2) last_
FROM allmessages WHERE messageID >= @first_new GROUP BY lastdir ORDER BY rows_ DESC;

SELECT authorsrole2020, COUNT(*) FROM allmessages WHERE messageID >= @first_new AND PEP > 0 GROUP BY 1 ORDER BY 2 DESC;

SELECT SUM(senderemail IS NULL) no_email, SUM(clusterBySenderFullName IS NULL OR clusterBySenderFullName = '') no_name
FROM allmessages WHERE messageID >= @first_new;
