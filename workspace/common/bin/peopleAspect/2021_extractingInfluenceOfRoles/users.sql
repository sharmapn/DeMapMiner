SELECT pep, datetimestamp, folder, email, clusterbysenderfullname, authorsrole FROM allmessages 
WHERE pep = 326
AND clusterbysenderfullname IN
('Terry Reedy', -- ,
-- 'Andrew P. Lentvorski, Jr.',
'David Eppstein','Bob Ippolito','Evan Simpson','Andrew Koenig',
'Gary Robinson',		-- (from c.l.py)
'John Roth',		-- (from c.l.py)
-- 'A. Lloyd Flanagan',	-- (from c.l.py)
'Jeremy Fincher',	--	(from c.l.py)
'Andrew Koenig',	--	(from c.l.py)
'Erik Francis',	-- (from c.l.py)
-- 'John Hazen') , 'Robert Brewer', 'Michael Chermside', 'Phillip Eby', 'Gerrit Holl', -- +0  -- CORRECT THIS LINE
'Guido van Rossum' , 'Gustavo Niemeyer','Jeremy Hylton','Raymond Hettinger')  -- -1
ORDER BY datetimestamp ASC

SELECT pep, messageid, datetimestamp, folder, email, clusterbysenderfullname, authorsrole FROM allmessages 
WHERE pep = 326
AND clusterbysenderfullname IN
('Terry Reedy',  'David Eppstein','Bob Ippolito','Evan Simpson','Gary Robinson',
'John Roth','Jeremy Fincher',	'Andrew Koenig', 'Erik Francis',
'Guido van Rossum', 'Gustavo Niemeyer','Jeremy Hylton','Raymond Hettinger')  
ORDER BY authorsrole, clusterbysenderfullname, datetimestamp ASC
-- 'John Hazen') , 'Robert Brewer', 'Michael Chermside', 'Phillip Eby', 'Gerrit Holl', -- +0  -- CORRECT THIS LINE

SELECT * FROM allsentences WHERE messageid = 122582
-- who wrote this
SELECT * FROM allmessages WHERE messageid = 122582

CREATE TABLE influenceResults (pep INT, messageid INT, sentence TEXT, clusterbysenderfullname TEXT, score TEXT);

SELECT pep, clusterbysenderfullname,messageid, sentence, score FROM influenceResults
ORDER BY clusterbysenderfullname,messageid

-- may 2021
