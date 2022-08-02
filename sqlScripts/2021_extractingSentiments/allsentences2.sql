
SELECT proposal, COUNT(*) FROM allsentences
WHERE proposal = 572
GROUP BY proposal

create table dela as SELECT distinct pep FROM allmessages;
MINUS 
create table delb as SELECT distinct proposal FROM allsentences;

SELECT pep, ',' FROM dela
LEFT JOIN delb USING (pep)
WHERE delb.pep IS NULL; 

SELECT * FROM results 
WHERE pep = 572

SELECT bip, COUNT(*) FROM allmessages 
GROUP BY bip

DELETE FROM allsentences3 WHERE proposal = 657

SELECT lastdir, MAX(proposal), MAX(messageid) FROM allsentences
GROUP BY lastdir

SELECT MAX(messageid) FROM allmessages; -- 10066880

SELECT * FROM allsentences
WHERE proposal > 10000000

-- DROP TABLE allsentences2;

CREATE TABLE allsentences3 (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`proposal` INT(11) NULL DEFAULT NULL,
	`messageid` INT(11) NULL DEFAULT NULL,
	`sentence` LONGTEXT NOT NULL,
	`lastdir` MEDIUMTEXT NOT NULL,
	`isCorefParagraph` TINYINT(4) NOT NULL,
	`previousSentence` LONGTEXT NULL,
	`nextSentence` TEXT NULL,
	`msgSubject` TEXT NULL,
	`entireParagraph` TEXT NULL,
	`previousParagraph` TEXT NULL,
	`nextParagraph` TEXT NULL,
	`locationMatched` TINYTEXT NULL,
	`label` TEXT NULL,
	`msgAuthorRole` TEXT NULL,
	`isEnglishOrCode` TINYINT(4) NULL DEFAULT NULL,
	`isFirstParagraph` TINYINT(4) NULL DEFAULT NULL,
	`isLastParagraph` TINYINT(4) NULL DEFAULT NULL,
	`paragraphCounter` INT(11) NULL DEFAULT NULL,
	`sentenceCounterinParagraph` INT(11) NULL DEFAULT NULL,
	`sentenceCounterinMessage` INT(11) NULL DEFAULT NULL,
	`reasonTerms` TEXT NULL,
	`reasonIdentifierTerms` INT(11) NULL DEFAULT NULL,
	`stateSubStateTerms` INT(11) NULL DEFAULT NULL,
	`entityTerms` INT(11) NULL DEFAULT NULL,
	`decisionTerms` INT(11) NULL DEFAULT NULL,
	`specialTerms` INT(11) NULL DEFAULT NULL,
	`authorRole` INT(11) NULL DEFAULT NULL,
	`dateDiff` INT(11) NULL DEFAULT NULL,
	`messageSubjectDecisionTerms` INT(11) NULL DEFAULT NULL,
	`messageSubjectStateSubStateTerms` INT(11) NULL DEFAULT NULL,
	`messageSubjectProposalIdentifier` INT(11) NULL DEFAULT NULL,
	`reasonsTermsFoundCount` INT(11) NULL DEFAULT NULL,
	`reasonsIdentifierTermsCount` INT(11) NULL DEFAULT NULL,
	`statesSubstatesCount` INT(11) NULL DEFAULT NULL,
	`identifiersTermCount` INT(11) NULL DEFAULT NULL,
	`entitiesTermCount` INT(11) NULL DEFAULT NULL,
	`specialTermCount` INT(11) NULL DEFAULT NULL,
	`decisionTermCount` INT(11) NULL DEFAULT NULL,
	`negationTermCount` INT(11) NULL DEFAULT NULL,
	`positiveWordCount` INT(11) NULL DEFAULT NULL,
	`negativeWordCount` INT(11) NULL DEFAULT NULL,
	`messageSubjectStatesSubstatesListCount` INT(11) NULL DEFAULT NULL,
	`messageSubjectEntitiesTermListCount` INT(11) NULL DEFAULT NULL,
	`messageSubjectDecisionTermListCount` INT(11) NULL DEFAULT NULL,
	`messageSubjectVerbListCount` INT(11) NULL DEFAULT NULL,
	`messageSubjectProposalIdentifiersTermListCount` INT(11) NULL DEFAULT NULL,
	`messageSubjectSpecialTermListCount` INT(11) NULL DEFAULT NULL,
	`containsReason` INT(11) NULL DEFAULT NULL,
	`author` TINYTEXT NULL,
	`authorsrole` TINYTEXT NULL,
	PRIMARY KEY (`id`),
	INDEX `proposal` (`proposal`),
	INDEX `messageid` (`messageid`),
	INDEX `paragraphCounter` (`paragraphCounter`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=421212
;
