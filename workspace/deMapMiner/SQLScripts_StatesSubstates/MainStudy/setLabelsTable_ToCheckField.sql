update labels set tocheck = true;
update labels set tocheck = false 
where idea = 'draft' 
or idea = 'open' or idea = 'active' or idea = 'pending' or idea = 'closed' 
or idea = 'final'   or idea = 'accepted' or idea = 'deferred' or idea = 'replaced' 
or idea = 'rejected' or idea = 'postponed' or idea = 'incomplete' or idea = 'superseded';
update labels set tocheck = false where idea = 'wrote_pep'; 

update labels set tocheck = false where idea = 'pre-pep_changed'; 
update labels set tocheck = false where idea = 'pre-pep' and linenumber= 62; -- should be two rows with same line nunber- i dont know how..maybe when computimng all combinations using '-'
-- we temp remove this label - pre-pep
update labels set tocheck = false where idea = 'pre-pep'
-- tehre were 2 entires for line 826 - consensus non-one disagreed became two lines as while populating table from inpout file all - were made into different copmbinations
-- so i have updated one row as (no-one) and set the tocheck of teh other to 0
-- same for co_bdfl_delegate_accepted_pep...line 668 
-- closed had too many errors..its already part of commit states so maybe we wont need until later when we check for reasons
-- line 598 ..bdfl-pronouncement, guido agreed empty column, retruned too many incorrect ones ..modifioed tocheck to 0 manually in grid
update labels set tocheck = false where linenumber= 598;  -- make sure there are not 2 lines for this
update labels set tocheck = false where idea = 'closed'
update labels set tocheck = false where idea = 'serious_issues'
update labels set tocheck = false where idea = 'issue_raised'
update labels set tocheck = false where idea = 'pep_needs_work'
update labels set tocheck = false where idea like '%hope%' -- hope rough consensus
update labels set idea = 'issue_raised' where lineNumber = 598 -- bdfl_pronouncement - guido agreed, doubke returns several incorrect ones
update labels set idea = 'another_round_discussion' where lineNumber = 221  -- all three another round discussion
update labels set idea = 'another_round_discussion' where lineNumber = 222 
update labels set idea = 'another_round_discussion' where lineNumber = 223 
-- we badly needed this label, (consensus) but since too many false positives we make tocheck=0
update labels set tocheck = false where linenumber= 811; 
select * from labels where tocheck = 1 order by idea
select * from labels where idea like '%discussion%'
-- added additonal
insert into labels (linenumber, idea,subject,verb,object,tocheck) values(2000,'withdrawn_by_author','pep','withdrawn','author',1);
-- check which triples have been matched in results for a linenumber
select * from results_dec2018b where linenumber = 719 or linenumber = 720 order by pep, messageid

update reasonlabels set tocheck = false;
select * from reasonlabels; 
select * from reasonlabels where tocheck = true

-- this we do when we want to check some labels, for example see why 'if' is not captured as negation
update labels set tocheck = true
update labels set tocheck = false
update labels set tocheck = true where idea = 'final'
select * from results_dec2018b where currentsentence like '%there has been some%'
select * from labels where idea like '%bdfl%'