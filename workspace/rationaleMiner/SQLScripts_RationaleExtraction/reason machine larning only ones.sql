SELECT authorroleweight,datediffweight,
               reasonstermsfoundcount,reasonsIdentifierTermsCount,statesSubstatesCount,identifiersTermCount, 
               entitiesTermCount,specialTermCount,decisionTermCount,messageSubjectStatesSubstatesListCount, 
               messageSubjectEntitiesTermListCount,messageSubjectDecisionTermListCount,
               messageSubjectProposalIdentifiersTermListCount,messageSubjectSpecialTermListCount, 
               containsreason 
               FROM autoextractedreasoncandidatesentences WHERE proposal<5000 and containsreason = 1
               
select *  FROM autoextractedreasoncandidatesentences 
WHERE proposal<5000 and containsreason = 1 and datediffweight=0 and label like 'accepted'

update autoextractedreasoncandidatesentences set termsmatched = SUBSTR(termsmatched, 2);
select SUBSTR(termsmatched, 2) from autoextractedreasoncandidatesentences;

SELECT pep,messageid,author,date2 as date,datetimestamp as timestamp, email as clausie
from pepstates_danieldata_datetimestamp order by pep
where pep > 259 and email like '%accepted%' 
order by pep asc, clausie asc, timestamp asc

select pep, bdfl_delegate from pepdetails where length(bdfl_delegate) >0

-- update pepstates_danieldata_datetimestamp set state = replace(email,'Status : ','');
-- SELECT replace(email,'Status :','') from pepstates_danieldata_datetimestamp;

update autoextractedreasoncandidatesentences 
set datestatecommit = (
	select date2 from pepstates_danieldata_datetimestamp 
	where pep = autoextractedreasoncandidatesentences.proposal and state = autoextractedreasoncandidatesentences.label limit 1)
	
select proposal, label, datevalue, datestatecommit from autoextractedreasoncandidatesentences order by proposal;
select * from pepstates_danieldata_datetimestamp where pep = 246

SELECT * from results_postprocessed where pep = 246; 