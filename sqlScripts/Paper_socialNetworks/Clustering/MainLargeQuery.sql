-- The ones from this result you think to be same, put in the same cluster
-- 1. A fullname similarity (MAIN QUERY)
SELECT id,sendername, emailaddress, senderFirstName, senderLastName, -- not sure why i put in front DISTINCT (sendername) AS MATCHING,
	NULL AS 'DUMMY',
	Levenshtein('Guido van Rossum',sendername) AS Levenshtein,'FULL SENDERNAME MATCH' AS 'match'
FROM distinctsenders
HAVING Levenshtein <2 -- order by t asc limit 15
-- B. names similarity (combined firstname and lastname)
UNION 
SELECT id,sendername, emailaddress, senderFirstName, senderLastName,
	Levenshtein('Guido',senderfirstname) AS 'firstnamematch', Levenshtein('Rossum',senderlastname) AS 'lastnamematch', 'COMBINED FIRST&LASTNAME MATCH' AS 'match'
FROM distinctsenders
HAVING lastnamematch <2 AND firstnamematch <2 -- order by lastnamematch asc, firstnamematch asc
UNION 
-- 3. names-email similarity (email vs combined firstname and lastname) -- first part --  'guido' = email
SELECT id,sendername, emailaddress, senderFirstName, senderLastName, 
	NULL AS 'DUMMY',
	Levenshtein('Guido Rossum', CONCAT(senderfirstname,' ',senderLastName)) AS 'COMBINEDFIRSTLASTNAMEMATCH', 'EMAIL VS COMB F&L NAME MATCH' AS 'match'
FROM distinctsenders
HAVING COMBINEDFIRSTLASTNAMEMATCH <2 -- order by COMBINEDFIRSTLASTNAMEMATCH asc
UNION -- second part - EMAIL VS FIRST LETTER F&L NAME MATCH, 'guido' = email
SELECT id,sendername, emailaddress, senderFirstName, senderLastName,
	Levenshtein('guido', CONCAT(SUBSTRING(senderfirstname,0,1),senderLastName)) AS 'emailVs1stLetterFirstNameRestLName',
	Levenshtein('guido', CONCAT(senderfirstname, SUBSTRING(senderLastname,0,1))) AS 'emailVsFirstNameFirstLetterLname', 
	'EMAIL VS FIRST LETTER F&L NAME MATCH' AS 'match'
FROM distinctsenders
HAVING emailVs1stLetterFirstNameRestLName <2 OR emailVsFirstNameFirstLetterLname <2 -- order by COMBINEDFIRSTLASTNAMEMATCH asc
-- 4. email similarity (firstsegment only)
UNION
SELECT id,sendername, emailaddress, senderFirstName, senderLastName, 
	NULL AS 'DUMMY',
	Levenshtein('guido',emailaddress) AS Levenshtein,'EMAIL MATCH' AS 'match'
FROM distinctsenders
HAVING Levenshtein <2 -- order by t asc limit 15
-- order by t asc limit 10 -- xx ) AS SUB WHERE SUB.t<2 -- order by t asc limit 50;