DROP TABLE comparebaseline;
create table comparebaseline
(
	field TEXT, acc_sent INT, rej_sent INT,acc_msg INT,rej_msg INT
);

INSERT INTO comparebaseline (FIELD) VALUES ('sentenceHintProbablity');
-- INSERT INTO comparebaseline (FIELD) VALUES ('sentenceLocationHintProbability');
INSERT INTO comparebaseline (FIELD) VALUES ('restOfParagraphProbabilityScore');
INSERT INTO comparebaseline (FIELD) VALUES ('messageLocationProbabilityScore');
INSERT INTO comparebaseline (FIELD) VALUES ('messageSubjectHintProbablityScore');
INSERT INTO comparebaseline (FIELD) VALUES ('dateDiffProbability');
INSERT INTO comparebaseline (FIELD) VALUES ('authorRoleProbability');
INSERT INTO comparebaseline (FIELD) VALUES ('messageTypeIsReasonMessageProbabilityScore');
INSERT INTO comparebaseline (FIELD) VALUES ('sentenceLocationInMessageProbabilityScore');
INSERT INTO comparebaseline (FIELD) VALUES ('sameMsgSubAsStateTripleProbabilityScore');
INSERT INTO comparebaseline (FIELD) VALUES ('reasonLabelFoundUsingTripleExtractionProbabilityScore');
INSERT INTO comparebaseline (FIELD) VALUES ('messageContainsSpecialTermProbabilityScore');
INSERT INTO comparebaseline (FIELD) VALUES ('prevParagraphSpecialTermProbabilityScore');
INSERT INTO comparebaseline (FIELD) VALUES ('negationTermPenalty');

SELECT * FROM comparebaseline
ORDER BY acc_sent ASC, rej_sent ASC; 
ORDER BY acc_msg ASC, rej_msg ASC; 

			Sentence Based		Message Based	
		Accepted	Rejected	Accepted	Rejected
shp					
slimps					
ddp					
ropps					
mlps					
mshps					
arp					
mtrmps					
smsstps					
rlfteps					
mcstps					
ppstps					
nps					

