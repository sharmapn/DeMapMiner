

-- distinct rows
SELECT  DISTINCT evalLevel, label, 
top5, top10, top15, top30, top50, top100, outsidetop100, notfound, totalprobability,
sentenceHintProbablity as shp, sentenceLocationInMessageProbabilityScore as slimps, dateDiffProbability as ddp,
-- sentenceLocationHintProbability as slhp, not needed - not useful as remains constant
restOfParagraphProbabilityScore as ropps, messageLocationProbabilityScore as mlps, 
messageSubjectHintProbablityScore as mshps, authorRoleProbability as arp, messageTypeIsReasonMessageProbabilityScore as mtrmps,  
sameMsgSubAsStateTripleProbabilityScore as smsstps,reasonLabelFoundUsingTripleExtractionProbabilityScore as rlfteps, messageContainsSpecialTermProbabilityScore as mcstps, 
prevParagraphSpecialTermProbabilityScore as ppstps, negationTermPenalty as nps
FROM dynamicweightallocation  
WHERE label NOT LIKE '%final%'
AND evalLevel like '%MESSAGES%'
AND label LIKE '%rejected%'
-- where label = 'accepted'  -- rejected -- final  -- dynamicweightallocation_copy2 
-- WHERE evalLevel not like '%MESSAGES%' AND label = 'accepted'
order by top5 DESC, top10 desc, top15 desc, top30 DESC;
-- order by run ASC; 

--only unique copmbination of top-k
SELECT  DISTINCT evalLevel, label,  top5, top10, top15, top30, top50, top100, outsidetop100, notfound 
	
FROM dynamicweightallocation  
WHERE label NOT LIKE '%final%'
AND evalLevel like '%MESSAGES%'
AND label LIKE '%accepted%'
-- where label = 'accepted'  -- rejected -- final  -- dynamicweightallocation_copy2 
-- WHERE evalLevel not like '%MESSAGES%' AND label = 'accepted'
order by top5 DESC, top10 desc, top15 desc, top30 DESC;

	--, totalprobability,
      -- sentenceHintProbablity as shp, sentenceLocationInMessageProbabilityScore as slimps, dateDiffProbability as ddp,
-- sentenceLocationHintProbability as slhp, not needed - not useful as remains constant
		--	restOfParagraphProbabilityScore as ropps, messageLocationProbabilityScore as mlps, 
		--	messageSubjectHintProbablityScore as mshps, authorRoleProbability as arp, messageTypeIsReasonMessageProbabilityScore as mtrmps,  
		--	sameMsgSubAsStateTripleProbabilityScore as smsstps,reasonLabelFoundUsingTripleExtractionProbabilityScore as rlfteps, messageContainsSpecialTermProbabilityScore as mcstps, 
		--	prevParagraphSpecialTermProbabilityScore as ppstps, negationTermPenalty as nps