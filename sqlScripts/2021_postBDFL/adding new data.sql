
-- lets see till when we have data
SELECT folder, MAX(datetimestamp) FROM allmessages
GROUP BY folder
"folder"	"MAX(datetimestamp)"
	
"C:\datasets-additional\python-3000"	"2008-12-15 23:21:36"
"C:\datasets\core-workflow"	"2016-01-01 15:40:11"
"C:\datasets\Datetime-SIG"	"2015-09-22 11:09:00"
"C:\datasets\import-sig"	"2011-03-12 00:04:03"
"C:\datasets\python-announce-list"	"2018-10-31 10:44:08"
"C:\datasets\python-bugs-list"	"2018-11-01 03:19:54"
"C:\datasets\python-checkins"	"2018-11-01 02:16:02"
"C:\datasets\python-committers"	"2018-10-30 14:52:21"
"C:\datasets\python-dev"	"2018-11-01 02:40:50"
"C:\datasets\python-distutils-sig"	"2018-04-28 14:19:23"
"C:\datasets\python-ideas"	"2018-10-31 22:54:28"
"C:\datasets\python-lists"	"2017-03-10 04:06:36"
"C:\datasets\python-patches"	"2010-12-01 22:38:59"

-- may 2021...we start reading in post bdfl data...seo lets find the max id first
SELECT folder, MAX(messageid) FROM allmessages
WHERE messageid > 10000000
GROUP BY folder
-- looking at the data, 10,000,000 would be safe

-- rerun needed multiple times
delete FROM allmessages WHERE messageid >= 10000000;
SELECT COUNT(*) FROM allmessages WHERE messageid >= 10000000;

-- gets new peps
SELECT distinct(pep) FROM allmessages WHERE messageid >= 10000000;
minus
SELECT distinct(pep) FROM allmessages WHERE messageid >= 10000000;

-- get the messages to extract sentences from
SELECT distinct(messageid) FROM allmessages WHERE messageid >= 10000000;

TRUNCATE allparagraphs;
TRUNCATE allsentences;

SELECT * FROM allmessages WHERE messageid >= 10000000;

SELECT folder,lastdir FROM allmessages WHERE messageid >= 10000000 LIMIT 100;
UPDATE allmessages SET folder = REPLACE(folder, 'D:\\datasets\\postBDFL_may2021\\mailingLists\\','') WHERE messageid >= 10000000 
UPDATE allmessages SET lastdir = REPLACE(lastdir, '\\','') WHERE messageid >= 10000000 

-- thus last date seems to be 01 november 2018. so we have to download nov and dec data for all these mailing lists
-- in addition to 2019 and 2020 data.. and jan to march of 2021.
-- we used this url for these 
https://mail.python.org/archives/list/python-dev@python.org/export/python-dev@python.org-2018-11.mbox.gz?start=2018-11-01&end=2021-05-19
https://mail.python.org/archives/list/distutils-sig@python.org/export/distutils-sig@python.org-2018-11.mbox.gz?start=2018-11-01&end=2021-05-19
https://mail.python.org/archives/list/python-committers@python.org/export/python-committers@python.org-2018-11.mbox.gz?start=2018-11-01&end=2021-05-19
-- left to do
https://mail.python.org/archives/list/python-patches@python.org/export/python-patches@python.org-2018-11.mbox.gz?start=2018-11-01&end=2021-05-19
https://mail.python.org/archives/list/python-announce-list@python.org/export/python-announce-list@python.org-2018-11.mbox.gz?start=2018-11-01&end=2021-05-19
-- below list discontinued after March 2018
-- https://mail.python.org/archives/list/import-sig@python.org/export/import-sig-list@python.org-2018-11.mbox.gz?start=2018-11-01&end=2021-05-19
https://mail.python.org/archives/list/datetime-sig@python.org/export/datetime-sig@python.org-2021-05.mbox.gz?start=1970-01-01&end=2021-05-21
https://mail.python.org/archives/list/core-workflow@python.org/export/core-workflow@python.org-2018-11.mbox.gz?start=2018-11-01&end=2021-05-19
-- discontinued after july 2009
https://mail.python.org/archives/list/python-3000@python.org/export/python-3000@python.org-2018-11.mbox.gz?start=2018-11-01&end=2021-05-19

https://mail.python.org/archives/list/python-ideas@python.org/export/python-ideas@python.org-2018-11.mbox.gz?start=2018-11-01&end=2021-05-19
-- done individually
https://mail.python.org/archives/list/python-list@python.org/export/python-list@python.org-2018-11.mbox.gz?start=2018-11-01&end=2021-05-19
https://mail.python.org/archives/list/python-checkins@python.org/export/python-checkins@python.org-2018-11.mbox.gz?start=2018-11-01&end=2021-05-19
https://mail.python.org/archives/list/python-bugs-list@python.org/export/python-bugs-list@python.org-2018-11.mbox.gz?start=2018-11-01&end=2021-05-19

-- re-read peps
TRUNCATE pepdetails2021
SELECT * FROM pepdetails2021
WHERE created > '2018-07-11'

-- add peps, but not those we already have as data has been cleaned
INSERT INTO pepdetails2021b (power_network_id, ap, gp, measuring_and_billing)
SELECT  network_id,
        ap,
        gp,
        measuring_and_billing 
FROM    networks
WHERE type='PowerNetwork'
AND network_id NOT IN (
    SELECT power_network_id 
        FROM network_charges

