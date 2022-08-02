-- why a large number of sentences are not matched in the sentencebased approach

-- we checking sentences for pep 391 'i believe i addressed all the comments made on the discussion threads mentioned in the pep and so i m not sure what more i need to do to get a pronouncement'
-- why the above sentence is not matched
SELECT * FROM autoextractedreasoncandidatesentences WHERE proposal = 391 AND sentence LIKE '%i addressed all the%'
-- sentence exists in table but NOT MATCHED

-- is it in the duplicates removed table
SELECT * FROM autoextractedreasoncandidatesentences_nodupsranked_sent WHERE proposal = 391 AND sentence LIKE '%i addressed all the%'
-- sentence exists in table but NOT MATCHED
-- MID = 174963 

-- Now we try to resolve
-- first check, is it in the results
SELECT * FROM autoextractedreasoncandidatesentences WHERE proposal = 391 AND sentence LIKE '%i addressed all the%'
-- yes it is

-- therefore we get the message if containing the sentence using the abive query
-- there are several messages 174963, 175318, 175716, 176114, 176511 

-- see if sentence is matched or unmatched
SELECT * FROM trackrankingforsentences WHERE proposal = 391 AND trainingsentence LIKE '%i addressed all the%' ORDER BY rankedsentence

-- any sentences from that message id was checked
SELECT * FROM trackrankingforsentences WHERE proposal = 391 AND messageid = 174963
-- yes , there is one other 

-- SO that record (containing sentence) is not tested for mathing at some place in the code

-- MAIN APPROACH we insert code for debug and track that row along the way (proposal = 391 and MID = 174963)
SELECT * FROM trackrankingforsentences WHERE proposal = 391 AND matched = -2 AND MESSAGEID = 174963
AND trainingsentence LIKE '%i addressed all the%' ORDER BY rankedsentence

-- AND rankedsentence LIKE '%i addressed all the%' ORDER BY proposal


 --