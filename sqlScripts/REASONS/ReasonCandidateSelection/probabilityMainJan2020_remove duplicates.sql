select * from allmessages where pep = 285 and messageid = 259354;
select * from allmessages where pep = 508 and email like '%only open issue%';
select * from trainingdata where pep = 572  -- order by messageid
where messageid = 204293;
-- If you make chngaes here, MAKE SURE UPDATE NEW FIELDS IN JAVA JTABLE GRID COMPUTATION CODE, and the totalprobability computation in Probability.java 
-- Sentence level
-- select rank, proposal, messageid from (
SELECT @curRank := @curRank + 1 AS rank, messageTypeIsReasonMessage as RM,messageType as MT, proposal,messageid,dateValue,datediff, sentence,termsMatched, 
(sentenceHintProbablity+sentenceLocationHintProbability+restOfParagraphProbabilityScore+messageLocationProbabilityScore+messageSubjectHintProbablityScore
+dateDiffProbability+authorRoleProbability+messageTypeIsReasonMessageProbabilityScore+sentenceLocationInMessageProbabilityScore+sameMsgSubAsStateTripleProbabilityScore
+reasonLabelFoundUsingTripleExtractionProbabilityScore+messageContainsSpecialTermProbabilityScore+prevParagraphSpecialTermProbabilityScore-negationTermPenalty) as TotalProbability, 
label, authorRole, location,
sentenceHintProbablity,sentenceLocationHintProbability,restOfParagraphProbabilityScore,messageTypeIsReasonMessageProbabilityScore,messageLocationProbabilityScore,
messageSubjectHintProbablityScore,negationTermPenalty,dateDiffProbability,authorRoleProbability,sentenceLocationInMessageProbabilityScore,
sameMsgSubAsStateTripleProbabilityScore, reasonLabelFoundUsingTripleExtractionProbabilityScore,messageContainsSpecialTermProbabilityScore
from autoextractedreasoncandidatesentences, (SELECT @curRank := 0) r 
where location = 'sentence'  and -- messageid = 2074922 and
 label = 'accepted'  and  -- sentence like '% seemed generally%' and
proposal = 572 -- and messsageTypeIsReasonMessage =1; 
-- and sentence like '%problems%' 
group by sentence -- messageid,  -- jan 2020 ... this is necesary to remove duplicate sentences
order by label asc, TotalProbability desc, dateValue desc
-- ) as ii
-- group by proposal, messageid
-- and messageid = 204293

select * from allmessages where pep = 325 and -- email like '%little chance for pep%'
messageid = 262325;
select * from autoextractedreasoncandidatesentences ;
update pepdetails set messageid = (6000000 + proposal);

-- jan 2020, we use subquery to get only the unique messages
-- march 2019. message level based on sentence level - mostly same as above 

SELECT * FROM 
(
	SELECT @curRank := @curRank + 1 AS rank, messageTypeIsReasonMessage as RM,messageType as MT, proposal,messageid,dateValue,datediff, sentence,termsMatched, 
	(select analysewords from allmessages w where w.messageid = x.messageid limit 1) as message, 
	(sentenceHintProbablity+sentenceLocationHintProbability+restOfParagraphProbabilityScore+messageLocationProbabilityScore+messageSubjectHintProbablityScore
	+dateDiffProbability+authorRoleProbability+messageTypeIsReasonMessageProbabilityScore+sentenceLocationInMessageProbabilityScore+sameMsgSubAsStateTripleProbabilityScore
	+reasonLabelFoundUsingTripleExtractionProbabilityScore+messageContainsSpecialTermProbabilityScore+prevParagraphSpecialTermProbabilityScore-negationTermPenalty) as TotalProbability, 
	label, authorRole, location,
	sentenceHintProbablity,sentenceLocationHintProbability,restOfParagraphProbabilityScore,messageTypeIsReasonMessageProbabilityScore,messageLocationProbabilityScore,
	messageSubjectHintProbablityScore,negationTermPenalty,dateDiffProbability,authorRoleProbability,sentenceLocationInMessageProbabilityScore,
	sameMsgSubAsStateTripleProbabilityScore, reasonLabelFoundUsingTripleExtractionProbabilityScore,messageContainsSpecialTermProbabilityScore
	from autoextractedreasoncandidatesentences x, (SELECT @curRank := 0) r 
	where location = 'sentence'  and -- messageid = 2074922 and
	 label = 'accepted'  and  -- sentence like '% seemed generally%' and
	proposal = 572 -- and messsageTypeIsReasonMessage =1; 
	GROUP BY sentence	-- jan 2020 ... this is necesary to remove duplicate sentences
	order by label asc, TotalProbability desc, dateValue desc
)  AS messages
GROUP BY messages.messageid
order by label asc, TotalProbability desc, dateValue desc;


-- 332054
-- and messageid = 204293
-- Message Level
-- what about reasonLabelFoundUsingTripleExtractionProbabilityScore?
drop table msg;
create table msg as
SELECT  id, @curRank := @curRank + 1 AS rank,proposal,label, messageTypeIsReasonMessage as RM,messageType as MT, messageid,dateValue, 
datediff, (select analysewords from allmessages w where w.messageid = x.messageid limit 1) as message, 
(select count(*) from autoextractedreasoncandidatesentences s where s.proposal = x.proposal and s.messageID= x.messageID) as sentNum,
(select count(*) from autoextractedreasoncandidatesentences s where s.proposal = x.proposal and s.messageID= x.messageID and s.totalprobability > 1.0) as numSentThresholdNum,
(select SUM(s.totalprobability) from autoextractedreasoncandidatesentences s where s.proposal = x.proposal and s.messageID= x.messageID) as sumSentProb,
(messageLocationProbabilityScore+messageSubjectHintProbablityScore
+dateDiffProbability+authorRoleProbability+messageTypeIsReasonMessageProbabilityScore+sameMsgSubAsStateTripleProbabilityScore
+messageContainsSpecialTermProbabilityScore) as TotalProbability,authorRole, messagesubject,   location, termsMatched, 
TotalProbability as tp,  -- already computed and stored probability 
messageTypeIsReasonMessageProbabilityScore,messageLocationProbabilityScore,
messageSubjectHintProbablityScore,dateDiffProbability,authorRoleProbability,
sameMsgSubAsStateTripleProbabilityScore, messageContainsSpecialTermProbabilityScore 
-- it is likely a number of messages will have same totalprobability beuacse we consider at message level and few fields
-- pep 201 is an example where about all top 40 records have the same total probability of 1.4
-- to get even better results, we can add the following criteria
-- ranking of highest ranked sentence from this message
-- number of sentnces from this message in top candidates
from autoextractedreasoncandidatemessages x, (SELECT @curRank := 0) r where -- sentence like '%unprecedented%' and
-- (label = 'accepted' OR label like 'Status : accepted')  and 	
proposal = 572
-- and message like '%pep%'
-- and messageid = 318242
-- and messsageTypeIsReasonMessage =1; 
order by TotalProbability desc, dateValue desc; -- TotalProbability / tp , label asc,
-- 318252
select count(*) from autoextractedreasoncandidatesentences where proposal = 518 and messageid = 253275;
select * from msg where message like '%I just pushed%'
select * from allmessages where pep = 508 and email like '%the only open issue%'
select * from allmessages where pep = 382 and analysewords like '%here at PyCon.%'
select * from allmessages where messageid = 1070979;
-- for message not assigned to pep
select * from allmessages -- limit 5
select distinct messageType from autoextractedreasoncandidatesentences
where 
 pep =0 and
-- and subject like '%distutils-sig%' -- and subject like '%build system requirements PEP%'
-- and 
date2 = '2013-05-10' and 
 author like '%nick%' 
 order by date2 asc
 and
-- email like '%hints which are orthogonal%'

select count(messageid) from allmessages where pep = 255
 TRUNCATE autoextractedreasoncandidatemessages;
 TRUNCATE autoextractedreasoncandidateparagraphs;
 TRUNCATE autoextractedreasoncandidatesentences;
 DELETE FROM autoextractedreasoncandidatemessages WHERE proposal = 352;
 DELETE FROM autoextractedreasoncandidateparagraphs WHERE proposal = 352;
 DELETE FROM autoextractedreasoncandidatesentences WHERE proposal = 352;
 
select * from autoextractedreasoncandidatemessages;
select * from autoextractedreasoncandidatesentences;
select distinct(proposal) from autoextractedreasoncandidatesentences;

select max(messageid) from autoextractedreasoncandidatemessages
where messageid < 6000000

select * from autoextractedreasoncandidatemessages
select distinct proposal from autoextractedreasoncandidatemessages
select distinct messageid from autoextractedreasoncandidatemessages

select * from allmessages where pep = 214
 and messageid > 91682
 
 select * from allmessages where pep = 201 and messageid = 62392

select * from pepstates_danieldata_datetimestamp where email like '%<%'

select * from pepstates_danieldata_datetimestamp where pep = 258

select distinct pep from trainingdata
select * from trainingdata where pep = 201
select  distinct proposal,  messageid, datevalue, datediff   -- @curRank := @curRank + 1 AS rank,
from autoextractedreasoncandidatesentences -- x, (SELECT @curRank := 0) r
where proposal = 201 and datediff > -1
order by datediff asc

update autoextractedreasoncandidatesentences set containsreason =0 where proposal = 201

-- authorrole, authorroleweight,
drop table columnvalues  
create table columnvalues as
select id, proposal as pep, datediff as dd, datediffweight as ddw, sentence, label, sentenceinfirstparagraph as sifp, sentenceinlastparagraph as silp, sentenceHintProbablity as shp,
sentencelocationhintprobability as slps, messagelocationprobabilityscore as mlps, messageSubjectHintProbablityScore as mshps, negationtermpenalty as ntp, 
datediffprobability as ddp, authorroleprobability as arp, restofparagraphprobabilityscore as ropps, messagetypeisreasonmessageprobabilityscore as mtrmps, 
prevparagraphspecialtermprobabilityscore as ppstps, samemsgsubasstatetripleprobabilityscore as smstps, sentencelocationinmessageprobabilityscore as slimps, 
messagecontainsspecialtermprobabilityscore as mcstps, messagetypeisreasonmessage as mtirm, samemsgsubasstatetriple as smsst, containsreason as cr, totalprobability as tp,
reasonLabelFoundUsingTripleExtractionProbabilityScore as rlfuteps
from autoextractedreasoncandidatesentences -- x, (SELECT @curRank := 0) r
where proposal = 285 and 
 label = 'accepted' -- and containsreason=1   -- or label = 'rejected'
order by proposal; -- , rankbysql, rankbysystem;
-- see distribution
SELECT slps, COUNT(*) AS freq FROM columnvalues GROUP BY slps
SELECT mlps, COUNT(*) AS freq FROM columnvalues GROUP BY mlps


