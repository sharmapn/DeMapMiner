-- 2018 nov -- added new fields for ML
-- sentenceInFirstParagraph,sentenceInLastParagraph
-- messsageTypeIsReasonMessage,sentenceLocationInFirstLastParagraph,SameMsgSubAsStateTriple

-- run this sql as field not assigned
UPDATE  autoextractedreasoncandidatesentences
SET     sentenceLocationInFirstLastParagraph = CASE WHEN sentenceLocationInMessageProbabilityScore = 0.9 THEN 1 ELSE 0 END;-- 
-- select count(*) from autoextractedreasoncandidatesentences where sentenceLocationInMessageProbabilityScore = 0.9;
-- updated nov 2018 --full training dataset
SELECT distinct authorroleweight,datediffweight,
currSentencereasonstermsfoundcount,currSentencereasonsIdentifierTermsCount,currSentencestatesSubstatesCount,currSentenceidentifiersTermCount, 
currSentenceentitiesTermCount,currSentencespecialTermCount,currSentencedecisionTermCount,
messageSubjectStatesSubstatesListCount,messageSubjectEntitiesTermListCount,messageSubjectDecisionTermListCount,messageSubjectProposalIdentifiersTermListCount,messageSubjectSpecialTermListCount,
restOfParagraphStateTermsCount, restOfParagraphIdentifiersTermsCount,restOfParagraphEntityTermsCount,restOfParagraphDecisionTermsCount, restOfParagraphSpecialTermsCount, restOfParagraphReasonsTermsCount, 
messageTypeIsReasonMessage,sentenceLocationInFirstLastParagraph,SameMsgSubAsStateTriple, -- added fields
containsreason 
FROM autoextractedreasoncandidatesentences 
WHERE proposal<5000 and label like '%accepted%' and location = 'sentence'; 

-- for checking class = zeros, ML giving 1 when evaluating
-- change values for 0 and 1 to get different datasets..updated nov 2018
-- distinct
CREATE VIEW ONES AS 	-- not needed when selecting just ones, not when creating csv file
SELECT  -- sentence, 	-- not needed when selecting just ones, not when creating csv file
-- containsreason 		-- ONLY include while creating views, not when creating csv file
authorroleweight,datediffweight,
currSentencereasonstermsfoundcount,currSentencereasonsIdentifierTermsCount,currSentencestatesSubstatesCount,currSentenceidentifiersTermCount, 
currSentenceentitiesTermCount,currSentencespecialTermCount,currSentencedecisionTermCount,
messageSubjectStatesSubstatesListCount,messageSubjectEntitiesTermListCount,messageSubjectDecisionTermListCount,messageSubjectProposalIdentifiersTermListCount,messageSubjectSpecialTermListCount,
restOfParagraphStateTermsCount, restOfParagraphIdentifiersTermsCount,restOfParagraphEntityTermsCount,restOfParagraphDecisionTermsCount, restOfParagraphSpecialTermsCount, restOfParagraphReasonsTermsCount,
messageTypeIsReasonMessage,sentenceLocationInFirstLastParagraph,SameMsgSubAsStateTriple
FROM autoextractedreasoncandidatesentences  -- _novaCCEPTED
WHERE proposal<5000 and label like '%accepted%' and location = 'sentence'
and containsreason =0 -- change 1 or 0
ORDER BY proposal; 
-- SELECT COUNT(*) FROM ONES;

-- now join
SELECT main.sentence, 	-- not needed when selecting just ones, not when creating csv file
containsreason 		-- ONLY include while creating views, not when creating csv file
authorroleweight,datediffweight,
currSentencereasonstermsfoundcount,currSentencereasonsIdentifierTermsCount,currSentencestatesSubstatesCount,currSentenceidentifiersTermCount, 
currSentenceentitiesTermCount,currSentencespecialTermCount,currSentencedecisionTermCount,
messageSubjectStatesSubstatesListCount,messageSubjectEntitiesTermListCount,messageSubjectDecisionTermListCount,messageSubjectProposalIdentifiersTermListCount,messageSubjectSpecialTermListCount,
restOfParagraphStateTermsCount, restOfParagraphIdentifiersTermsCount,restOfParagraphEntityTermsCount,restOfParagraphDecisionTermsCount, restOfParagraphSpecialTermsCount, restOfParagraphReasonsTermsCount,
messsageTypeIsReasonMessage,sentenceLocationInFirstLastParagraph,SameMsgSubAsStateTriple
FROM autoextractedreasoncandidatesentences_novaCCEPTED as main
INNER JOIN ones ON main.authorroleweight=ones.authorroleweight;

-- select only rows with clas 1, for checking model
-- to se which is is not predicted properly, we use order by  -- select distinct location from autoextractedreasoncandidatesentences;
SELECT authorroleweight,datediffweight,
currSentencereasonstermsfoundcount,currSentencereasonsIdentifierTermsCount,currSentencestatesSubstatesCount,currSentenceidentifiersTermCount, 
currSentenceentitiesTermCount,currSentencespecialTermCount,currSentencedecisionTermCount,
messageSubjectStatesSubstatesListCount,messageSubjectEntitiesTermListCount,messageSubjectDecisionTermListCount,messageSubjectProposalIdentifiersTermListCount,messageSubjectSpecialTermListCount,
restOfParagraphStateTermsCount, restOfParagraphIdentifiersTermsCount,restOfParagraphEntityTermsCount,restOfParagraphDecisionTermsCount, restOfParagraphSpecialTermsCount, restOfParagraphReasonsTermsCount 
FROM autoextractedreasoncandidatesentences 
WHERE proposal<5000 and label like '%accepted%' and containsreason =1 and location = 'sentence'
order by proposal, messageid;

-- to cross check which sentences are not catched
SELECT proposal, authorRole, messageid,datediff, sentence, restOfParagraph, messageSubject, -- location
authorroleweight,datediffweight,
currSentencereasonstermsfoundcount,currSentencereasonsIdentifierTermsCount,currSentencestatesSubstatesCount,currSentenceidentifiersTermCount, 
currSentenceentitiesTermCount,currSentencespecialTermCount,currSentencedecisionTermCount,
messageSubjectStatesSubstatesListCount,messageSubjectEntitiesTermListCount,messageSubjectDecisionTermListCount,messageSubjectProposalIdentifiersTermListCount,messageSubjectSpecialTermListCount,
restOfParagraphStateTermsCount, restOfParagraphIdentifiersTermsCount,restOfParagraphEntityTermsCount,restOfParagraphDecisionTermsCount, restOfParagraphSpecialTermsCount, restOfParagraphReasonsTermsCount, 
sentenceHintProbablity,sentenceLocationHintProbability,restOfParagraphProbabilityScore,messageLocationProbabilityScore,messageSubjectHintProbablityScore,
+dateDiffProbability,authorRoleProbability,messsageTypeIsReasonMessageProbabilityScore,sentenceLocationInMessageProbabilityScore,sameMsgSubAsStateTripleProbabilityScore,
(sentenceHintProbablity+sentenceLocationHintProbability+restOfParagraphProbabilityScore+messageLocationProbabilityScore+messageSubjectHintProbablityScore
+dateDiffProbability+authorRoleProbability+messsageTypeIsReasonMessageProbabilityScore+sentenceLocationInMessageProbabilityScore+sameMsgSubAsStateTripleProbabilityScore
-negationTermPenalty) as TotalProbability
FROM autoextractedreasoncandidatesentences 
WHERE proposal<5000 and label like '%accepted%' and containsreason =1 and location = 'sentence' 
order by proposal, messageid;

select * from allmessages where messageid = 81178; -- 62397;
select pep, causesentence,effectsentence,causecategory from trainingdata where pep < 5000 and label like '%accepted%' and length(causesentence) >1 order by pep ;

select pep, messageid, causesentence,effectsentence from trainingdata where label like '%accepted%' order by pep, messageid;

