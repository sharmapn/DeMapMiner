SELECT * 
FROM pepstates_danieldata_datetimestamp_firstoneused
WHERE pep = 3127
-- pepstates_danieldata_datetimestamp

SELECT * 
FROM allmessages 
WHERE messageid = 302993

SELECT * 
FROM pepstates_danieldata_datetimestamp
WHERE pep = 297


FOR the message id selected
GET the pep NUMBER AND state
QUERY the autoextractedsentences TABLE TO GET ALL reason sentences 
highlight these sentences ONE BY ONE

SELECT * 
FROM autoextractedreasoncandidatesentences 
WHERE proposal = 308 AND label = 'accepted'
AND messageid = 

SELECT * FROM autoextractedreasoncandidatesentences 
WHERE proposal =308 AND messageid = 95153 and label = 'accepted' 