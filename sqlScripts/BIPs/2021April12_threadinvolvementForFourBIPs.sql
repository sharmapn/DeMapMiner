-- April 16th 2021

-- first some updates
UPDATE allmessages SET sendername = TRIM(sendername);
UPDATE allmessages SET inreplytouser = TRIM(inreplytouser);

UPDATE allmessages SET sendername = 'Jorge Timón' WHERE sendername = '=?UTF-8?B?Sm9yZ2UgVGltw7Nu?=';
UPDATE allmessages SET inreplytouser = 'Jorge Timón' WHERE inreplytouser = '=?UTF-8?B?Sm9yZ2UgVGltw7Nu?=';
UPDATE allmessages SET sendername = 'Hampus Sjöberg' WHERE sendername = '=?UTF-8?Q?Hampus_Sj=C3=B6berg?=';
UPDATE allmessages SET inreplytouser = 'Hampus Sjöberg' WHERE inreplytouser = '=?UTF-8?Q?Hampus_Sj=C3=B6berg?=';

-- associate inreplytouser
UPDATE allmessages a1
JOIN allmessages a2 ON a2.emailMessageID = a1.inReplyTo
SET a1.inReplyToUser = a2.senderName
WHERE a1.inReplyTo IS NOT NULL



-- to correct datetimestamp corrections
update allmessages set datetimestampstring = NULL;
-- should run this before
update allmessages set datetimestamp = NULL;
-- ALTER TABLE allmessages ADD COLUMN olddatetimestamp TEXT(50); -- backup
-- update allmessages set olddatetimestamp = datetimestamp;
-- First teh datetimestamp changes..Just verify
SELECT datetimestamp, olddatetimestamp FROM allmessages;
SELECT datetimestamp, olddatetimestamp, email FROM allmessages WHERE datetimestamp <> olddatetimestamp AND messageid = 9239;
SELECT bip, email, datetimestamp, olddatetimestamp, datetimestampstring FROM allmessages WHERE messageid = 9239;

SELECT * FROM allmessages WHERE email LIKE '%like to talk a bit about my view on the relation between the Bitcoin%'

SELECT distinct messageid FROM allmessages

SELECT bip, COUNT(messageid)  AS cnt
FROM allmessages
GROUP BY bip
ORDER BY cnt desc

-- 16 April 2021. aLL selected threads for BIP 9
SELECT bip, messageid, DATE2, datetimestamp, email, author, sendername, subject, emailmessageid, inreplyto, COALESCE(inreplyto,'bitcoin-dev') 
FROM allmessages 
WHERE subject LIKE '%[BIP Proposal] Version bits with timeout and delay%' 
OR subject LIKE '%[bitcoin-dev] Bitcoin Core and hard forks%'
OR subject LIKE '%[bitcoin-dev] BIP 9 style version bits for txns%'
OR subject LIKE '%[bitcoin-dev] Status updates for BIP 9, 68, 112, and 113%'
-- not considered OR subject LIKE '%[Bitcoin-development] User vote in blocksize through fees%'
OR subject LIKE '%Re: Versionbits BIP (009) minor revision proposal%'
GROUP BY messageid -- some messages (with same messageid) are assigned to multiple bips -- see messageid 11.875 
ORDER BY subject ASC, messageid asc, datetimestamp ASC
-- 17 April ..for individual threads comment the others
subject LIKE '%[bitcoin-dev] Bitcoin Core and hard forks%' -- this is the only resonably large thread to consider

-- now main query for SNA for BIP 9 selected threads -- there was only one thread big enough for proper analysis
-- for selected threads, select one thread and comment others  
-- SELECT senderName, "," AS a , COALESCE(inreplytouser,'bitcoin-dev'), "," AS b -- inreplytouser,   ,
SELECT senderName,   COALESCE(inreplytouser,'bitcoin-dev')
FROM allmessages 
WHERE subject LIKE '%[BIP Proposal] Version bits with timeout and delay%'
-- OR subject LIKE '%[bitcoin-dev] Bitcoin Core and hard forks%'
-- OR subject LIKE '%[bitcoin-dev] BIP 9 style version bits for txns%'
-- FOR thread level we diod not consider the next two
-- OR subject LIKE '%[bitcoin-dev] Status updates for BIP 9, 68, 112, and 113%'
-- OR subject LIKE '%Re: Versionbits BIP (009) minor revision proposal%'
-- AND LENGTH(senderName) > 0
GROUP BY messageid -- some messages (with same messageid) are assigned to multiple bips -- see messageid 11.875 
order by datetimestamp 
INTO OUTFILE 'c:\\scripts\\BIPs\\BIPSSNA_2021_AllDevelopers_18-04-2021_BIP9.txt'; -- add a, b, cat the end of filename, before extension

UPDATE allmessages SET subject = TRIM(subject)
UPDATE allmessages SET subject = REPLACE(subject, "  ", " ")
UPDATE allmessages SET subject = replace(subject, '\t', ' ')  

-- now main query for SNA for BIP 141 all selected threads
-- for selected threads, select one thread and comment others 
SELECT -- bip, messageid, DATE2, datetimestamp, subject, email, author, 
-- senderName, "," AS a ,  COALESCE(inreplytouser,'bitcoin-dev'), "," AS b -- inreplytouser,
senderName, COALESCE(inreplytouser,'bitcoin-dev') -- inreplytouser,
FROM allmessages 
WHERE subject LIKE '%[bitcoin-dev] Start time for BIP141 (segwit)%'
OR subject LIKE '%[bitcoin-dev] A Small Modification to Segwit%'
OR subject LIKE '%[bitcoin-dev] BIP141 segwit consensus rule update: extension of witness program definition%'
OR subject LIKE '%[bitcoin-dev] On Hardforks in the Context of SegWit%'
OR subject LIKE '%[bitcoin-dev] Capacity increases for the Bitcoin system%'
OR subject LIKE '%[bitcoin-dev] Segregated Witness features wish list%'
OR subject LIKE '%[bitcoin-dev] Segregated Witness in the context of Scaling Bitcoin%'
OR subject LIKE '%[bitcoin-dev] Updating the Scaling Roadmap%'
OR subject LIKE '%[bitcoin-dev] Segregated Witness BIPs%'
OR subject LIKE '%[bitcoin-dev] SegWit testnet is live%'
-- -- AND LENGTH(senderName) > 0
GROUP BY messageid -- some messages (with same messageid) are assigned to multiple bips -- see messageid 11.875 
order by subject 
INTO OUTFILE 'c:\\scripts\\BIPs\\BIPSSNA_2021_AllDevelopers_18-04-2021_BIP141.txt';

-- BIP 141...one by one threads
SELECT -- bip, messageid, DATE2, datetimestamp, subject, email, author, 
senderName, "," AS a ,  COALESCE(inreplytouser,'bitcoin-dev'), "," AS b -- inreplytouser,
FROM allmessages 
WHERE
-- consider all of these one by one 
-- subject LIKE '%[bitcoin-dev] Start time for BIP141 (segwit)%'
-- subject LIKE '%[bitcoin-dev] A Small Modification to Segwit%'
-- subject LIKE '%[bitcoin-dev] BIP141 segwit consensus rule update: extension of witness program definition%'
-- subject LIKE '%[bitcoin-dev] On Hardforks in the Context of SegWit%'
-- subject LIKE '%[bitcoin-dev] Capacity increases for the Bitcoin system%'
-- subject LIKE '%[bitcoin-dev] Segregated Witness features wish list%'
-- subject LIKE '%[bitcoin-dev] Segregated Witness in the context of Scaling Bitcoin%'
-- subject LIKE '%[bitcoin-dev] Updating the Scaling Roadmap%'
-- subject LIKE '%[bitcoin-dev] Segregated Witness BIPs%'
subject LIKE '%[bitcoin-dev] SegWit testnet is live%'
-- AND LENGTH(senderName) > 0
order by subject 
INTO OUTFILE 'c:\\scripts\\BIPs\\BIPSSNA_2021_AllDevelopers_16-04-2021_BIP141j.txt';

-- BIP 341
SELECT -- bip, messageid, DATE2, datetimestamp, subject, email, author, 
-- senderName, "," AS a ,  COALESCE(inreplytouser,'bitcoin-dev'), "," AS b -- inreplytouser,
senderName, COALESCE(inreplytouser,'bitcoin-dev') -- inreplytouser,
FROM allmessages 
WHERE subject LIKE '%[bitcoin-dev] BIP-341: Committing to all scriptPubKeys in the signature message%'
OR subject LIKE '%[bitcoin-dev] Progress on bech32 for future Segwit Versions (BIP-173)%'
OR subject LIKE '%[bitcoin-dev] Taproot (and graftroot) complexity (reflowed)%'
OR subject LIKE '%[bitcoin-dev] Taproot (and graftroot) complexity%'
OR subject LIKE '%[bitcoin-dev] Hash function requirements for Taproot%'
OR subject LIKE '%[bitcoin-dev] BIP-341: Committing to all scriptPubKeys in the signature message%'
OR subject LIKE '%[bitcoin-dev] Bech32 weakness and impact on bip-taproot addresses%'
-- 21 April 2021 suggested by Rewat after initial round
OR subject LIKE '%[bitcoin-dev] An alternative deployment path for taproot technology (Re: Taproot (and graftroot) complexity)%'
OR subject LIKE '%[bitcoin-dev] Taproot: Privacy preserving switchable scripting%'
OR subject LIKE '%[bitcoin-dev] Taproot proposal%'
OR subject LIKE '%[bitcoin-dev] 32-byte public keys in Schnorr and Taproot%'
GROUP BY messageid -- some messages (with same messageid) are assigned to multiple bips -- see messageid 11.875 
order by subject 
INTO OUTFILE 'c:\\scripts\\BIPs\\BIPSSNA_2021_AllDevelopers_21-04-2021_BIP341.txt';

-- soem records have sendername as null
SELECT  messageid, bip, sendername, NAME, 
POSITION('(' IN email) AS st, 
POSITION(')' IN email) AS en, 
SUBSTRING(email, POSITION('(' IN email), POSITION(')' IN email) - POSITION('(' IN email))  AS sub, 
-- , POSITION('(' IN email), POSITION(')' IN email)),
email 
FROM allmessages WHERE sendername IS NULL 

-- UPDATE allmessages SET sendername = 'veleslav.bips' WHERE sendername = '=?UTF-8?B?0JLQtdC70LXRgdC70LDQsg==?='
SELECT REPLACE(NAME,' . ','')  FROM allmessages WHERE sendername IS NULL
UPDATE allmessages SET sendername = REPLACE(sendername,' . ','') -- Raystonn .
-- check messageid = 15649
-- UPDATE allmessages SET sendername = NAME WHERE sendername IS NULL

-- UPDATE allmessages set NAME = SUBSTRING(email, POSITION('(' IN email), POSITION(')' IN email) - POSITION('(' IN email))  WHERE sendername IS NULL;
-- 361 rows updated
-- UPDATE allmessages set NAME = REPLACE(NAME,'(','') WHERE sendername IS NULL   

-- BIP 91 requested later by Rewat
SELECT -- bip, messageid, DATE2, datetimestamp, subject, email, author, 
-- COMMENT TEMP JUST TO GET THE ROWS WITHOUT COMMA senderName, "," AS a ,  COALESCE(inreplytouser,'bitcoin-dev'), "," AS b -- inreplytouser,
senderName, COALESCE(inreplytouser,'bitcoin-dev') -- inreplytouser,
FROM allmessages 
WHERE subject LIKE '%[bitcoin-dev] User Activated Soft Fork Split Protection%'
OR subject LIKE '%[bitcoin-dev] Miners forced to run non-core code in order to get segwit activated%'
OR subject LIKE '%[bitcoin-dev] Reduced signalling threshold activation of existing segwit deployment%'
GROUP BY messageid -- some messages (with same messageid) are assigned to multiple bips -- see messageid 11.875 
order by subject 
INTO OUTFILE 'c:\\scripts\\BIPs\\BIPSSNA_2021_AllDevelopers_21-04-2021_BIP91.txt';

-- BIP 148 requested later by Rewat
SELECT -- bip, messageid, DATE2, datetimestamp, subject, email, author, 
-- senderName, "," AS a ,  COALESCE(inreplytouser,'bitcoin-dev'), "," AS b -- inreplytouser,
senderName,  COALESCE(inreplytouser,'bitcoin-dev') -- inreplytouser,
FROM allmessages 
WHERE subject LIKE '%[bitcoin-dev] The BIP148 chain split may be inevitable%'
OR subject LIKE '%[bitcoin-dev] Replay attacks make BIP148 and BIP149 untennable%'
OR subject LIKE '%[bitcoin-dev] Moving towards user activated soft fork activation%'
OR subject LIKE '%[bitcoin-dev] I do not support the BIP 148 UASF%'
GROUP BY messageid -- some messages (with same messageid) are assigned to multiple bips -- see messageid 11.875 
order by subject 
INTO OUTFILE 'c:\\scripts\\BIPs\\BIPSSNA_2021_AllDevelopers_21-04-2021_BIP148.txt';


UPDATE allmessages SET subject = TRIM(subject)