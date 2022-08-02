SELECT * FROM allsentences WHERE sentence LIKE '%objections%'
TRUNCATE allsentences;
truncate allparagraphs;

SELECT distinct bip FROM allmessages order BY bip ASC
SELECT DISTINCT bip FROM bipstates_danieldata_datetimestamp
SELECT count(*) FROM allmessages WHERE bip = -1

SELECT * FROM allmessages WHERE bip =9;

select distinct(DATE2) as dt, count(*) as cnt 
from allmessages
WHERE bip = 9 
GROUP BY dt order by cnt DESC;

-- temporary
SELECT * FROM autoextractedinfluencecandidatesentences_copy
UPDATE autoextractedinfluencecandidatesentences_copy SET proposal = 9
UPDATE autoextractedinfluencecandidatesentences_copy SET label = 'FINAL'

-- add column author and authorsole
ALTER TABLE allsentences ADD COLUMN author TEXT(20);
ALTER TABLE allsentences ADD COLUMN authorsrole TEXT(20);
SELECT * FROM allsentences WHERE proposal = -1 ORDER BY messageid LIMIT 100
DELETE FROM allsentences WHERE proposal = -1 

select distinct(subject) as msgsubjects, COUNT(*) AS cnt from allmessages WHERE BIP = 141 GROUP BY subject ORDER BY cnt desc

select distinct(description) as role FROM people
DESC people
SELECT * FROM people
UPDATE people SET role = description;
UPDATE people SET role = REPLACE(role,'Bitcoin','');
UPDATE people SET role = REPLACE(role,'BTCPAY',''); 
UPDATE people SET role = REPLACE(role,'and BTCPAY','');
UPDATE people SET role = REPLACE(role,'and','');
UPDATE people SET role = REPLACE(role,'(now working on Rust Lightning)','');
UPDATE people SET role = TRIM(role);
UPDATE people SET role = REPLACE(role,'developer','Developer');


SELECT * FROM 
	( 
	SELECT  id, @curRank := @curRank + 1 AS rank, messageTypeIsReasonMessage as RM,messageType as MT, proposal, messageid, dateValue, 
   datediff, sentence, termsMatched, (select analysewords from allmessages w where w.messageid = x.messageid limit 1) as message, 
	ROUND( (sentenceHintProbablity+sentenceLocationHintProbability+restOfParagraphProbabilityScore+messageLocationProbabilityScore+messageSubjectHintProbablityScore 
			+ dateDiffProbability+authorRoleProbability+messageTypeIsReasonMessageProbabilityScore+sentenceLocationInMessageProbabilityScore+sameMsgSubAsStateTripleProbabilityScore 
				+ reasonLabelFoundUsingTripleExtractionProbabilityScore+messageContainsSpecialTermProbabilityScore+prevParagraphSpecialTermProbabilityScore-negationTermPenalty), 1) 
			 as TotalProbability, label, authorRole, location, 
			  sentenceHintProbablity,sentenceLocationHintProbability,restOfParagraphProbabilityScore,messageTypeIsReasonMessageProbabilityScore,messageLocationProbabilityScore, 
			  messageSubjectHintProbablityScore,negationTermPenalty,dateDiffProbability,authorRoleProbability,sentenceLocationInMessageProbabilityScore, 
			  sameMsgSubAsStateTripleProbabilityScore, reasonLabelFoundUsingTripleExtractionProbabilityScore,messageContainsSpecialTermProbabilityScore 
			  from autoextractedinfluencecandidatesentences x, (SELECT @curRank := 0) r  
			  WHERE proposal = 326 
			  AND clusterBySenderFullName = 'Guido van Rossum'
			  GROUP BY sentence 
			  order by label asc, TotalProbability desc, dateValue desc 
	)  AS messages 
	GROUP BY messages.messageid 
   order by label asc, TotalProbability desc, dateValue desc;


SELECT @curRank := @curRank + 1 AS rank, messageTypeIsReasonMessage as RM,messageType as MT, proposal,messageid,dateValue,datediff, sentence,termsMatched,  
ROUND( (sentenceHintProbablity+sentenceLocationHintProbability+restOfParagraphProbabilityScore+messageLocationProbabilityScore+messageSubjectHintProbablityScore 
+dateDiffProbability+authorRoleProbability+messageTypeIsReasonMessageProbabilityScore+sentenceLocationInMessageProbabilityScore+sameMsgSubAsStateTripleProbabilityScore  
+reasonLabelFoundUsingTripleExtractionProbabilityScore+messageContainsSpecialTermProbabilityScore+prevParagraphSpecialTermProbabilityScore-negationTermPenalty), 1) as TotalProbability,
label, authorRole, location,  
sentenceHintProbablity,sentenceLocationHintProbability,restOfParagraphProbabilityScore,messageTypeIsReasonMessageProbabilityScore,messageLocationProbabilityScore,  
messageSubjectHintProbablityScore,negationTermPenalty,dateDiffProbability,authorRoleProbability,sentenceLocationInMessageProbabilityScore, 
sameMsgSubAsStateTripleProbabilityScore, reasonLabelFoundUsingTripleExtractionProbabilityScore,messageContainsSpecialTermProbabilityScore   
from autoextractedreasoncandidatesentences, (SELECT @curRank := 0) r  
WHERE proposal = 326
AND clusterBySenderFullName = 'Guido van Rossum'
GROUP by sentence 
ORDER by label asc, TotalProbability desc, dateValue desc;

-- decisions ..we look closer to date when propsoal was made final (not accepted) and rejected
INSERT INTO reason (bip,role,rationale,rationalesentence) VALUES (9,'BIP Author','Lazy consensus','Last call: Does anyone mind if I update BIP 9 to Final status?');

-- bip 141
More than 80\% of the miners and many users are willing to go in the Segwit2x direction. With the support and great talent of the Bitcoin Core
developers, Segwit2x activation will not cause any major disruptions. Without Core, there will be a temporary split. Both sides will have TO hard-fork.
-- 148
I am fully supportive of Segregated Witness and its activation by users through BIP148. However, I also believe that a soft fork would be less risky if it was initially activated by miners, before the date set in BIP148.
So far, the lack of miner consensus about Segwit has been frustrating both users and developers. This had led some users to propose a soft fork activation regardless of the expressed level of miner support (UASF, BIP148).