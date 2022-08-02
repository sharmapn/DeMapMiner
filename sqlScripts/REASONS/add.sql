SELECT proposal,messageid,dateValue, sentence,termsMatched, probability, label, authorRole, location, 
(sentenceHintProbablity+sentenceLocationHintProbability+restOfParagraphProbabilityScore+messageLocationProbabilityScore+messageSubjectHintProbablityScore+
dateDiffProbability+authorRoleProbability+messsageTypeIsReasonMessageProbabilityScore-negationTermPenalty) as TotalProbability 
from autoextractedreasoncandidatesentences where 
location LIKE '%sentence%' and 
proposal = 443 order by label asc, TotalProbability desc, dateValue desc 

select * from pepdetails where pep = 628;

select * from  autoextractedreasoncandidatesentences where sentenceinlastparagraph =true;


select * from pepdetailsold
update pepdetails set messageid = (6000000 + pep);