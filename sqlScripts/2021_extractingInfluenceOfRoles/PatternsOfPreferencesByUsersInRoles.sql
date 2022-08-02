-- 19 may 2021 ..main script used to get patterns

-- Patterns
-- Term patterns
	-- In message subject..... MessageID= 122616, 'I support PEP 326'   = Entity + PI + P (preference) 'support' 
	
	-- In message  body
	
	
-- Other heuristics/patterns	
	-- role: if posted from python list, then he is a user
	-- last messgages by user: we look at last 5 messages of each user
	-- Decision date: closer to the date of accepted or rejected
	-- Sentence location - first and last paragrapghs 



SELECT pep, datetimestamp, messageid, clusterBySenderFullName, authorsrole2021, email, subject, lastdir 
fROM allmessages WHERE pep = 326
ORDER BY author;
-- author, sendername, 

ALTER TABLE allmessages ADD COLUMN authorsrole2021 TEXT(20);
UPDATE allmessages SET authorsrole2021 = authorsrole2020;
UPDATE allmessages SET authorsrole2021 = 'users' WHERE pep = 326 AND lastdir = 'python-lists';

TRUNCATE autoextractedinfluencecandidatemessages;
TRUNCATE autoextractedinfluencecandidatesentences;

ALTER TABLE autoextractedinfluencecandidatemessages ADD COLUMN clusterBySenderFullName TEXT(50);
ALTER TABLE autoextractedinfluencecandidatesentences ADD COLUMN clusterBySenderFullName TEXT(50);

-- add the author
ALTER table allsentences ADD clusterbysenderfullname TEXT; 
-- UPDATE allsentences SET clusterbysenderfullname ()
UPDATE allsentences t1 
        INNER JOIN allmessages t2 
             ON t1.messageid = t2.messageid
SET t1.clusterbysenderfullname = t2.clusterbysenderfullname; 
-- WHERE t2.messageid = 'Joe'

SELECT * FROM allsentences WHERE messageid = 122582
-- who wrote this
SELECT * FROM allmessages WHERE messageid = 122582


-- we assign a pep NUMBER TO those messages which are replies to messages witha  pep number

-- main sample message..pep 326

-- > Unfortunately, I have a feeling that the PEP may be killed because there 
-- > doesn't seem to be a location/name that is agreeable to those with 
-- > voting power in python-dev.

-- we look at the last messages by an author

Feelings on inclusion into Python or the standard library somewhere (name of objects and location preferences vary, if I mistook your opinion, my apologies, etc.)
+1:
Terry J. Reedy
Andrew P. Lentvorski, Jr.
David Eppstein
Bob Ippolito
Evan Simpson
Andrew Koenig
Gary Robinson		(from c.l.py)
John Roth		(from c.l.py)
A. Lloyd Flanagan	(from c.l.py)
Jeremy Fincher		(from c.l.py)
Andrew Koenig		(from c.l.py)
Erik Max Francis	(from c.l.py)

+0
John Hazen
Robert Brewer
Michael Chermside

0
Phillip J. Eby

-0
Gerrit Holl

-1
Guido van Rossum
Gustavo Niemeyer
Jeremy Hylton
Raymond Hettinger


-- 

SELECT lastdir, clusterbysenderfullname, COUNT(messageid)  AS ct
FROM allmessages 
WHERE PEP <> -1 
-- AND lastdir LIKE '%users%'
GROUP BY lastdir, clusterbysenderfullname
ORDER BY lastdir ASC, ct DESC, clusterbysenderfullname ASC
-- SELECT * FROM allmessages  LIMIT 5

-- data in excel file 

-- triples
-- i like/dislike pep/proposal/idea

-- run NER on the sentence column which have pep proposal/idea
-- run topic modelling on sentences which have pep/proposal/idea 
-- then run triple extraction on the sentence column for  

-- also nte while the sentence may not refer to the pp/propsoal or idea, the remainder of the paragraph would

SELECT * FROM allsentences 
-- WHERE proposal = -1 LIMIT 6
where sentence LIKE '%pep%' OR sentence LIKE '%proposal%' OR sentence LIKE '%idea%' limit 5
DATE2 > '12-05-2018' LIMIT 5

-- triples extracted already
SELECT pep, messageid, arg1, relation,arg2, sentence, allverbsinsub, allverbsinrel, allverbsinobj, allnounsinsub, allnounsinrel, allnounsinobj
FROM extractedrelations_clausie_mainfirst -- relations_clausie 
WHERE (arg1 like '%idea%' or arg2 like '%idea%' OR arg1 like '%pep%' or arg2 like '%pep%' OR arg1 like '%proposal%' or arg2 like '%proposal%')
AND (relation LIKE '%like%' OR relation LIKE '%support%');
LIMIT 10

-- maybe we can look into gthis table which we alfeady created couple years back
SELECT * FROM topicmodellingsentences;