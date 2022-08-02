 SELECT * FROM 
 ( 
	 SELECT  id, @curRank := @curRank + 1 AS rank, messageTypeIsReasonMessage as RM,messageType as MT, proposal, messageid, dateValue, 
	 datediff, sentence, termsMatched, (select analysewords from allmessages w where w.messageid = x.messageid limit 1) as message, 
	
	 (sentenceHintProbablity+sentenceLocationHintProbability+restOfParagraphProbabilityScore+messageLocationProbabilityScore+messageSubjectHintProbablityScore 
		+ dateDiffProbability+authorRoleProbability+messageTypeIsReasonMessageProbabilityScore+sentenceLocationInMessageProbabilityScore+sameMsgSubAsStateTripleProbabilityScore 
		+ reasonLabelFoundUsingTripleExtractionProbabilityScore+messageContainsSpecialTermProbabilityScore+prevParagraphSpecialTermProbabilityScore-negationTermPenalty)  
	   as TotalProbability, 
	
	 label, authorRole, location, 
		sentenceHintProbablity,sentenceLocationHintProbability,restOfParagraphProbabilityScore,messageTypeIsReasonMessageProbabilityScore,messageLocationProbabilityScore, 
		messageSubjectHintProbablityScore,negationTermPenalty,dateDiffProbability,authorRoleProbability,sentenceLocationInMessageProbabilityScore,  
		sameMsgSubAsStateTripleProbabilityScore, reasonLabelFoundUsingTripleExtractionProbabilityScore,messageContainsSpecialTermProbabilityScore 
	
	 from autoextractedreasoncandidatemessages x, (SELECT @curRank := 0) r  
	 where location = 'sentence' 
	 AND label = 'Accepted' 
	 AND proposal = 391
	-- and message like '%pep%' // and messageid = 318242 // and messsageTypeIsReasonMessage =1; 
	 GROUP BY sentence -- "	// jan 2020 ... this is necessary to remove duplicate sentences
	 order by label asc, TotalProbability desc, dateValue desc 
 )  AS messages 
 GROUP BY messages.messageid 
 order by label asc, TotalProbability desc, dateValue desc; 