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
--    vocabulary as in authorsrole2020, plus 'steeringcouncil' for the post-BDFL era
UPDATE allmessages SET authorsrole2020 = NULL, authorsrole = NULL WHERE messageID >= @first_new;

-- 4a. BDFL: Guido until 12 July 2018
UPDATE allmessages
SET authorsrole2020 = 'bdfl'
WHERE messageID >= @first_new AND authorsrole2020 IS NULL
  AND (senderemail IN ('guido@python.org','gvanrossum@gmail.com','guido@discuss.python.org') OR LOWER(clusterBySenderFullName) = 'guido van rossum')
  AND dateTimeStamp < '2018-07-12';

-- 4b. Steering Council member during the message's term
UPDATE allmessages a JOIN steeringcouncil s
   ON LOWER(TRIM(a.clusterBySenderFullName)) = LOWER(s.member)
  AND DATE(a.dateTimeStamp) BETWEEN s.start_date AND s.end_date
SET a.authorsrole2020 = 'steeringcouncil'
WHERE a.messageID >= @first_new AND a.authorsrole2020 IS NULL;

-- 4c. proposal author of the PEP the row is linked to
UPDATE allmessages a JOIN pepdetails p ON p.pep = a.PEP
SET a.authorsrole2020 = 'proposalAuthor'
WHERE a.messageID >= @first_new AND a.authorsrole2020 IS NULL AND a.PEP > 0
  AND ( (a.senderemail IS NOT NULL AND p.authorEmail IS NOT NULL AND FIND_IN_SET(a.senderemail, REPLACE(LOWER(p.authorEmail), ' ', '')) > 0)
     OR (p.authorCorrected IS NOT NULL AND a.clusterBySenderFullName <> '' AND FIND_IN_SET(LOWER(TRIM(a.clusterBySenderFullName)), LOWER(REPLACE(p.authorCorrected, ', ', ','))) > 0) );

-- 4d. PEP delegate (BDFL-Delegate / PEP-Delegate header) of that PEP
UPDATE allmessages a JOIN pepdetails p ON p.pep = a.PEP
SET a.authorsrole2020 = 'bdfl_delegate'
WHERE a.messageID >= @first_new AND a.authorsrole2020 IS NULL AND a.PEP > 0
  AND p.bdfl_delegateCorrected IS NOT NULL AND p.bdfl_delegateCorrected <> ''
  AND FIND_IN_SET(LOWER(TRIM(a.clusterBySenderFullName)), LOWER(REPLACE(p.bdfl_delegateCorrected, ', ', ','))) > 0;

-- 4e. PEP editor
UPDATE allmessages a JOIN pepeditors e ON LOWER(TRIM(a.clusterBySenderFullName)) = LOWER(TRIM(e.pepeditor))
SET a.authorsrole2020 = 'pepeditors'
WHERE a.messageID >= @first_new AND a.authorsrole2020 IS NULL
  AND (e.dateadded IS NULL OR DATE(a.dateTimeStamp) >= e.dateadded);

-- 4f. core developer (current roster with join/leave dates, then the legacy table)
UPDATE allmessages a JOIN coredevelopers_2026 c ON LOWER(TRIM(a.clusterBySenderFullName)) = LOWER(c.name)
SET a.authorsrole2020 = 'coredeveloper'
WHERE a.messageID >= @first_new AND a.authorsrole2020 IS NULL
  AND (c.joined IS NULL OR DATE(a.dateTimeStamp) >= c.joined)
  AND (c.left_date IS NULL OR DATE(a.dateTimeStamp) <= c.left_date);

UPDATE allmessages a JOIN coredevelopers c ON LOWER(TRIM(a.clusterBySenderFullName)) = LOWER(TRIM(REPLACE(c.coredeveloper, '\t', '')))
SET a.authorsrole2020 = 'coredeveloper'
WHERE a.messageID >= @first_new AND a.authorsrole2020 IS NULL
  AND (c.dateadded IS NULL OR DATE(a.dateTimeStamp) >= c.dateadded);

-- 4g. everyone else
UPDATE allmessages SET authorsrole2020 = 'otherCommunityMember'
WHERE messageID >= @first_new AND authorsrole2020 IS NULL;

UPDATE allmessages SET authorsrole = authorsrole2020 WHERE messageID >= @first_new;

-- 5. summary --------------------------------------------------------------------
SELECT lastdir, COUNT(*) rows_, SUM(PEP > 0) pep_linked, MIN(date2) first_, MAX(date2) last_
FROM allmessages WHERE messageID >= @first_new GROUP BY lastdir ORDER BY rows_ DESC;

SELECT authorsrole2020, COUNT(*) FROM allmessages WHERE messageID >= @first_new AND PEP > 0 GROUP BY 1 ORDER BY 2 DESC;

SELECT SUM(senderemail IS NULL) no_email, SUM(clusterBySenderFullName IS NULL OR clusterBySenderFullName = '') no_name
FROM allmessages WHERE messageID >= @first_new;
