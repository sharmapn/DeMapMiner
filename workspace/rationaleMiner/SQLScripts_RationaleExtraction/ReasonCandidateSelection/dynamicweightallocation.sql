-- march dynamic allocation of weights
-- sentence level variables
		
-- datedifferenceweight
-- if datedifference weight = 0.4, we inc
-- DROP table dynamicweightallocation ;
create table 	dynamicweightallocation (label TEXT, 
					sentenceHintProbablity DOUBLE, sentenceLocationHintProbability DOUBLE, restOfParagraphProbabilityScore DOUBLE, messageLocationProbabilityScore DOUBLE, messageSubjectHintProbablityScore DOUBLE,
					dateDiffProbability DOUBLE, authorRoleProbability DOUBLE, messageTypeIsReasonMessageProbabilityScore DOUBLE, sentenceLocationInMessageProbabilityScore DOUBLE, sameMsgSubAsStateTripleProbabilityScore DOUBLE,
					reasonLabelFoundUsingTripleExtractionProbabilityScore DOUBLE, messageContainsSpecialTermProbabilityScore DOUBLE, prevParagraphSpecialTermProbabilityScore DOUBLE, negationTermPenalty DOUBLE,
					top5 INT, top10 INT, top15 INT, top30 INT, top50 INT, top100 INT, outsidetop100 INT, notfound INT, totalProbability DOUBLE);

ALTER TABLE dynamicweightallocation ADD run INT PRIMARY KEY AUTO_INCREMENT;

-- just have baseline results
DELETE FROM dynamicweightallocation  WHERE run > 6

SELECT  run, RIGHT(evalLevel,8), label, 
top5, top10, top15, top30, top50, top100, outsidetop100, notfound, totalprobability,
sentenceHintProbablity as shp, sentenceLocationInMessageProbabilityScore as slimps, dateDiffProbability as ddp,
-- sentenceLocationHintProbability as slhp, not needed - not useful as remains constant
restOfParagraphProbabilityScore as ropps, messageLocationProbabilityScore as mlps, 
messageSubjectHintProbablityScore as mshps, authorRoleProbability as arp, messageTypeIsReasonMessageProbabilityScore as mtrmps,  
sameMsgSubAsStateTripleProbabilityScore as smsstps,reasonLabelFoundUsingTripleExtractionProbabilityScore as rlfteps, messageContainsSpecialTermProbabilityScore as mcstps, 
prevParagraphSpecialTermProbabilityScore as ppstps, negationTermPenalty as nps
FROM dynamicweightallocation  
-- where label = 'accepted'  -- rejected -- final  -- dynamicweightallocation_copy2 
-- and evalLevel like '%sent%'
order by top5 DESC, top10 desc, top15 desc, top30 DESC;


-- TO see progress during the 4 day processing
SELECT DISTINCT sentenceHintProbablity FROM dynamicweightallocation 
-- SELECT DISTINCT sentenceLocationInMessageProbabilityScore  FROM dynamicweightallocation 
SELECT DISTINCT dateDiffProbability FROM dynamicweightallocation 
-- sentenceLocationHintProbability as slhp, not needed - not useful as remains constant
--SELECT DISTINCT restOfParagraphProbabilityScore FROM dynamicweightallocation 
SELECT DISTINCT messageLocationProbabilityScore FROM dynamicweightallocation 
SELECT DISTINCT messageSubjectHintProbablityScore FROM dynamicweightallocation 
-- SELECT DISTINCT authorRoleProbability FROM dynamicweightallocation
SELECT DISTINCT  messageTypeIsReasonMessageProbabilityScore FROM dynamicweightallocation 
-- SELECT DISTINCT sameMsgSubAsStateTripleProbabilityScore FROM dynamicweightallocation
SELECT DISTINCT reasonLabelFoundUsingTripleExtractionProbabilityScore FROM dynamicweightallocation 
-- SELECT DISTINCT messageContainsSpecialTermProbabilityScore FROM dynamicweightallocation 
-- SELECT DISTINCT prevParagraphSpecialTermProbabilityScore FROM dynamicweightallocation 
-- SELECT DISTINCT negationTermPenalty FROM dynamicweightallocation  

SELECT MAX(run) FROM dynamicweightallocation 
DELETE FROM dynamicweightallocation  WHERE run > 579
-- 579

INSERT INTO dynamicweightallocation  
SELECT * FROM dynamicweightallocation_jan2020_removefeatures
WHERE RUN <7

-- EASIER FOR Tracking where is the process - -see distinct values done
SELECT  RIGHT(evalLevel,8), label, 
sentenceHintProbablity as shp, sentenceLocationInMessageProbabilityScore as slimps, dateDiffProbability as ddp,
-- sentenceLocationHintProbability as slhp, not needed - not useful as remains constant
restOfParagraphProbabilityScore as ropps, messageLocationProbabilityScore as mlps, 
messageSubjectHintProbablityScore as mshps, authorRoleProbability as arp, messageTypeIsReasonMessageProbabilityScore as mtrmps,  
sameMsgSubAsStateTripleProbabilityScore as smsstps,reasonLabelFoundUsingTripleExtractionProbabilityScore as rlfteps, messageContainsSpecialTermProbabilityScore as mcstps, 
prevParagraphSpecialTermProbabilityScore as ppstps, negationTermPenalty as nps
FROM dynamicweightallocation  where label = 'ACCEPTED'  -- rejected -- final  -- dynamicweightallocation_copy2 
and evalLevel like '%message%'
group by shp, slimps, ddp, ropps, mlps, mshps,arp,mtrmps,smsstps,rlfteps,mcstps,ppstps,nps
order by shp desc, slimps desc, ddp desc, ropps desc, mlps desc, mshps desc,arp desc,mtrmps desc,smsstps desc,rlfteps desc,mcstps desc,ppstps desc ,nps desc

select 8, 
	(select distinct sentenceHintProbablity FROM dynamicweightallocation  where label = 'ACCEPTED' and evalLevel like '%message%'),
	(select distinct sentenceHintProbablity FROM dynamicweightallocation  where label = 'ACCEPTED' and evalLevel like '%message%')
FROM dynamicweightallocation 

-- dec 2019 ..i guess only needed if we were reading and writing too much in our previous approach
CREATE TABLE autoextractedreasoncandidatesentences2 ENGINE=MEMORY;
	SELECT * FROM autoextractedreasoncandidatesentences;
LOAD DATA INFILE 'table_filename' INTO TABLE autoextractedreasoncandidatesentences2;

select distinct(sentenceLocationHintProbability) from autoextractedreasoncandidatesentences

SELECT count(*) from autoextractedreasoncandidatesentences
select proposal, messageid, sentenceHintProbablity, ORIGsentenceHintProbablity from autoextractedreasoncandidatesentences
where proposal = 42 and messageid = 6000042 and label = 'FINAL';

SELECT DISTINCT sentenceHintProbablity from autoextractedreasoncandidatesentences

sentenceHintProbablity+sentenceLocationHintProbability+restOfParagraphProbabilityScore+messageLocationProbabilityScore+messageSubjectHintProbablityScore
+dateDiffProbability+authorRoleProbability+messageTypeIsReasonMessageProbabilityScore+sentenceLocationInMessageProbabilityScore+sameMsgSubAsStateTripleProbabilityScore
+reasonLabelFoundUsingTripleExtractionProbabilityScore+messageContainsSpecialTermProbabilityScore+prevParagraphSpecialTermProbabilityScore-negationTermPenalty) as TotalProbability, 

from autoextractedreasoncandidatesentences x, (SELECT @curRank := 0) r 
where location = 'sentence'  and -- messageid = 2074922 and
 label = 'accepted'  and  -- sentence like '% seemed generally%' and
proposal = 289 -- and messsageTypeIsReasonMessage =1; 
order by label asc, TotalProbability desc, dateValue desc

-- FIRST ONE OFF - create backup original values column - DONE ONCE ONLY
ALTER TABLE autoextractedreasoncandidatesentences add column origsentenceHintProbablity DOUBLE;
-- ALTER TABLE autoextractedreasoncandidatesentences DROP origsentenceHintProbablity;
UPDATE autoextractedreasoncandidatesentences SET origsentenceHintProbablity = sentenceHintProbablity;
-- analyse
SELECT DISTINCT sentenceHintProbablity from autoextractedreasoncandidatesentences;
SELECT sentenceHintProbablity, COUNT(*) from autoextractedreasoncandidatesentences group by sentenceHintProbablity;
SELECT sentenceLocationHintProbability, COUNT(*) from autoextractedreasoncandidatesentences group by sentenceLocationHintProbability;
SELECT origsentenceHintProbablity, COUNT(*) from autoextractedreasoncandidatesentences group by origsentenceHintProbablity;
SELECT dateDiffProbability, COUNT(*) from autoextractedreasoncandidatesentences group by dateDiffProbability;
SELECT authorRoleProbability, COUNT(*) from autoextractedreasoncandidatesentences group by authorRoleProbability;
-- Cross checking - should be same as above after initial assignment
SELECT origsentenceHintProbablity, COUNT(*) from autoextractedreasoncandidatesentences group by origsentenceHintProbablity;

-- MAIN CHANGES HERE


SET @newsentenceLocationHintProbability = 10;
SET @newrestOfParagraphProbabilityScore = 10;
SET @newmessageLocationProbabilityScore = 10;
SET @newmessageSubjectHintProbablityScore = 10;
SET @newdateDiffProbability = 10;
SET @newauthorRoleProbability = 10;
SET @newmessageTypeIsReasonMessageProbabilityScore = 10;
SET @newsentenceLocationInMessageProbabilityScore = 10;
SET @newsameMsgSubAsStateTripleProbabilityScore = 10;
SET @newreasonLabelFoundUsingTripleExtractionProbabilityScore = 10;
SET @newmessageContainsSpecialTermProbabilityScore = 10;
SET @newprevParagraphSpecialTermProbabilityScore = 10;
SET @newnegationTermPenalty = 10;

-- EXECUTE CHANGE ..This is done manually here for selected fields...but in java code is done automatically
-- RUN 1
	SET @newsentenceHintProbablityA = 1.4; 
   UPDATE autoextractedreasoncandidatesentences SET sentenceHintProbablity 										= @newsentenceHintProbablityA WHERE sentenceHintProbablity = 0.7;
	SET @newsentenceHintProbablityB = 0.6; 
	UPDATE autoextractedreasoncandidatesentences SET sentenceHintProbablity 										= @newsentenceHintProbablityB WHERE sentenceHintProbablity = 0.3;
-- RUN 2
UPDATE autoextractedreasoncandidatesentences SET sentenceLocationHintProbability 								= @sentenceLocationHintProbability WHERE sentenceHintProbablity = 0.7;
-- RUN 3
UPDATE autoextractedreasoncandidatesentences SET restOfParagraphProbabilityScore 								= @restOfParagraphProbabilityScore WHERE sentenceHintProbablity = 0.7;
-- RUN 4
UPDATE autoextractedreasoncandidatesentences SET messageLocationProbabilityScore 								= @messageLocationProbabilityScore WHERE sentenceHintProbablity = 0.7;
-- RUN 5
UPDATE autoextractedreasoncandidatesentences SET messageSubjectHintProbablityScore  							= @messageSubjectHintProbablityScore WHERE sentenceHintProbablity = 0.7;

-- RUN 6
	SET @newdateDiffProbabilityA = 1.8; 
	UPDATE autoextractedreasoncandidatesentences SET dateDiffProbability 												= @newdateDiffProbabilityA WHERE dateDiffProbability = 0.9;
	SET @newdateDiffProbabilityB = 1.2; 
	UPDATE autoextractedreasoncandidatesentences SET dateDiffProbability 												= @newdateDiffProbabilityB WHERE dateDiffProbability = 0.6;
	SET @newdateDiffProbabilityC = 0.4; 
	UPDATE autoextractedreasoncandidatesentences SET dateDiffProbability 												= @newdateDiffProbabilityC WHERE dateDiffProbability = 0.2;

-- RUN 7
	SET @newauthorRoleProbabilityA = 1.8; 
	UPDATE autoextractedreasoncandidatesentences SET authorRoleProbability 												= @newauthorRoleProbabilityA WHERE authorRoleProbability = 0.9;
	SET @newauthorRoleProbabilityB = 1.2; 
	UPDATE autoextractedreasoncandidatesentences SET authorRoleProbability 												= @newauthorRoleProbabilityB WHERE authorRoleProbability = 0.6;
	SET @newauthorRoleProbabilityC = 1.0; 
	UPDATE autoextractedreasoncandidatesentences SET authorRoleProbability 												= @newauthorRoleProbabilityC WHERE authorRoleProbability = 0.5;
	SET @newauthorRoleProbabilityD = 0.8; 
	UPDATE autoextractedreasoncandidatesentences SET authorRoleProbability 												= @newauthorRoleProbabilityD WHERE authorRoleProbability = 0.4;
-- RUN 8
UPDATE autoextractedreasoncandidatesentences SET messageTypeIsReasonMessageProbabilityScore 					= @messageTypeIsReasonMessageProbabilityScore WHERE sentenceHintProbablity = 0.7;
-- RUN 9
UPDATE autoextractedreasoncandidatesentences SET sentenceLocationInMessageProbabilityScore 					= @sentenceLocationInMessageProbabilityScore WHERE sentenceHintProbablity = 0.7;
-- RUN 10
UPDATE autoextractedreasoncandidatesentences SET sameMsgSubAsStateTripleProbabilityScore 						= @sameMsgSubAsStateTripleProbabilityScore WHERE sentenceHintProbablity = 0.7;
-- RUN 11
UPDATE autoextractedreasoncandidatesentences SET reasonLabelFoundUsingTripleExtractionProbabilityScore 	= @reasonLabelFoundUsingTripleExtractionProbabilityScore WHERE sentenceHintProbablity = 0.7;
-- RUN 12
UPDATE autoextractedreasoncandidatesentences SET messageContainsSpecialTermProbabilityScore 					= @messageContainsSpecialTermProbabilityScore WHERE sentenceHintProbablity = 0.7;
-- RUN 13
UPDATE autoextractedreasoncandidatesentences SET prevParagraphSpecialTermProbabilityScore 					= @prevParagraphSpecialTermProbabilityScore WHERE sentenceHintProbablity = 0.7;
-- RUN 14
UPDATE autoextractedreasoncandidatesentences SET negationTermPenalty 												= @negationTermPenalty WHERE sentenceHintProbablity = 0.7;

-- LAST Reset to original
UPDATE autoextractedreasoncandidatesentences SET sentenceHintProbablity 										= origsentenceHintProbablity;
UPDATE autoextractedreasoncandidatesentences SET sentenceLocationHintProbability 							= origsentenceLocationHintProbability;
UPDATE autoextractedreasoncandidatesentences SET restOfParagraphProbabilityScore 							= origrestOfParagraphProbabilityScore;
UPDATE autoextractedreasoncandidatesentences SET messageLocationProbabilityScore 							= origmessageLocationProbabilityScore;
UPDATE autoextractedreasoncandidatesentences SET messageSubjectHintProbablityScore 							= origmessageSubjectHintProbablityScore;
UPDATE autoextractedreasoncandidatesentences SET dateDiffProbability 											= origdateDiffProbability;
UPDATE autoextractedreasoncandidatesentences SET authorRoleProbability 											= origauthorRoleProbability;
UPDATE autoextractedreasoncandidatesentences SET messageTypeIsReasonMessageProbabilityScore 				= origmessageTypeIsReasonMessageProbabilityScore;
UPDATE autoextractedreasoncandidatesentences SET sentenceLocationInMessageProbabilityScore 				= origsentenceLocationInMessageProbabilityScore;
UPDATE autoextractedreasoncandidatesentences SET sameMsgSubAsStateTripleProbabilityScore 					= origsameMsgSubAsStateTripleProbabilityScore;
UPDATE autoextractedreasoncandidatesentences SET reasonLabelFoundUsingTripleExtractionProbabilityScore = origreasonLabelFoundUsingTripleExtractionProbabilityScore;
UPDATE autoextractedreasoncandidatesentences SET messageContainsSpecialTermProbabilityScore				 = origmessageContainsSpecialTermProbabilityScore;
UPDATE autoextractedreasoncandidatesentences SET prevParagraphSpecialTermProbabilityScore 				= origprevParagraphSpecialTermProbabilityScore;
UPDATE autoextractedreasoncandidatesentences SET negationTermPenalty 											= orignegationTermPenalty;
-- UPDATE autoextractedreasoncandidatesentences SET origsentenceHintProbablity = 0.7 WHERE sentenceHintProbablity = 0.7;
-- UPDATE autoextractedreasoncandidatesentences SET sentenceHintProbablity = 0.3 WHERE sentenceHintProbablity = @newsentenceHintProbablityB;
		
-- try create index for faster 		
CREATE  INDEX shp ON autoextractedreasoncandidatesentences(sentenceHintProbablity);
CREATE  INDEX slhp ON autoextractedreasoncandidatesentences(sentenceLocationHintProbability);
CREATE  INDEX ropps ON autoextractedreasoncandidatesentences(restOfParagraphProbabilityScore);
CREATE  INDEX mlps ON autoextractedreasoncandidatesentences(messageLocationProbabilityScore);
CREATE  INDEX mshps ON autoextractedreasoncandidatesentences(messageSubjectHintProbablityScore);
CREATE  INDEX ddps ON autoextractedreasoncandidatesentences(dateDiffProbability);
CREATE  INDEX arp ON autoextractedreasoncandidatesentences(authorRoleProbability);
CREATE  INDEX mtirmps ON autoextractedreasoncandidatesentences(messageTypeIsReasonMessageProbabilityScore);
CREATE  INDEX slimps ON autoextractedreasoncandidatesentences(sentenceLocationInMessageProbabilityScore);
CREATE  INDEX smsastps ON autoextractedreasoncandidatesentences(sameMsgSubAsStateTripleProbabilityScore);
CREATE  INDEX rlfuteps ON autoextractedreasoncandidatesentences(reasonLabelFoundUsingTripleExtractionProbabilityScore);
CREATE  INDEX mcstps ON autoextractedreasoncandidatesentences(messageContainsSpecialTermProbabilityScore);
CREATE  INDEX ppstps ON autoextractedreasoncandidatesentences(prevParagraphSpecialTermProbabilityScore);
CREATE  INDEX ntp ON autoextractedreasoncandidatesentences(negationTermPenalty);