UNION -- firstname similarity
SELECT DISTINCT (senderfirstname) AS MATCHING,id,sendername, emailaddress, senderFirstName, senderLastName, 
NULL AS 'firstnamematch', NULL AS 'lastnamematch', NULL AS 'emailVs1stLetterFirstNameRestLName', NULL AS 'emailVsFirstNameFirstLetterLname',
	levenshtein_distance('guido',senderfirstname) AS levenshtein_distance,'FIRSTNAME MATCH' AS 'matche'
FROM distinctsenders
HAVING levenshtein_distance <2 -- order by t asc limit 15)
UNION -- lastname similarity
SELECT DISTINCT (senderlastname) AS MATCHING,id,sendername, emailaddress, senderFirstName, senderLastName, 
NULL AS 'firstnamematch', NULL AS 'lastnamematch', NULL AS 'emailVs1stLetterFirstNameRestLName', NULL AS 'emailVsFirstNameFirstLetterLname',
	levenshtein_distance('rossum',senderlastname) AS 'levenshtein_distance','LASTNAME MATCH' AS 'matche'
FROM distinctsenders
HAVING levenshtein_distance <2 -- order by t asc limit 15