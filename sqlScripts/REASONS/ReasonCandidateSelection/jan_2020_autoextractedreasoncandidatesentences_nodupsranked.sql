

SELECT Rank, rankbysystem, proposal,messageid,dateValue,datediff, sentence,
sentenceHintProbablity,sentenceLocationHintProbability,restOfParagraphProbabilityScore,messageLocationProbabilityScore,messageSubjectHintProbablityScore, 
dateDiffProbability,authorRoleProbability,messageTypeIsReasonMessageProbabilityScore,sentenceLocationInMessageProbabilityScore,sameMsgSubAsStateTripleProbabilityScore, 
reasonLabelFoundUsingTripleExtractionProbabilityScore,messageContainsSpecialTermProbabilityScore,prevParagraphSpecialTermProbabilityScore,negationTermPenalty,
messageTypeIsReasonMessage,messageType 
ORIGsentenceHintProbablity,ORIGsentenceLocationHintProbability,ORIGmessageSubjectHintProbablityScore,ORIGdateDiffProbability,ORIGauthorRoleProbability, 
ORIGnegationTermPenalty,ORIGrestOfParagraphProbabilityScore,ORIGmessageLocationProbabilityScore, 
ORIGmessageTypeIsReasonMessageProbabilityScore,ORIGsentenceLocationInMessageProbabilityScore,ORIGsameMsgSubAsStateTripleProbabilityScore,
ORIGreasonLabelFoundUsingTripleExtractionProbabilityScore,ORIGmessageContainsSpecialTermProbabilityScore,ORIGprevParagraphSpecialTermProbabilityScore
FROM autoextractedreasoncandidatesentences_nodupsranked