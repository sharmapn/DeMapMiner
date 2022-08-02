-- Main query
-- use pepstates_danieldata table for quering daniel data
create table pepstates_danieldata_wide
as 
select pep, title as finaltitle, 
	(SELECT peptitle FROM pepstates_danieldata WHERE pep = a.pep order by date2 asc LIMIT 1) as titleinFirstState,
	(SELECT peptitle FROM pepstates_danieldata WHERE pep = a.pep and (email = 'Status : ACTIVE') LIMIT 1) as titleInActiveState,
	(SELECT peptitle FROM pepstates_danieldata WHERE pep = a.pep and (email = 'Status : DRAFT' ) LIMIT 1) as titleInDraftState,
	(SELECT peptitle FROM pepstates_danieldata WHERE pep = a.pep and (email = 'Status : ACCEPTED' ) LIMIT 1) as titleInAcceptedState,
	(SELECT peptitle FROM pepstates_danieldata WHERE pep = a.pep and (email = 'Status : REJECTED' ) LIMIT 1) as titleInRejectedState,
	(SELECT peptitle FROM pepstates_danieldata WHERE pep = a.pep and (email = 'Status : FINAL' ) LIMIT 1) as titleInFinalState
from pepdetails a
order by pep asc;

-- use pepstates table for quering your data
create table pepstates_wide
as 
select pep, title as finaltitle, 
	(SELECT peptitle FROM pepstates WHERE pep = a.pep order by date2 asc LIMIT 1) as titleinFirstState,
	(SELECT peptitle FROM pepstates WHERE pep = a.pep and (email = 'Status : ACTIVE') LIMIT 1) as titleInActiveState,
	(SELECT peptitle FROM pepstates WHERE pep = a.pep and (email = 'Status : DRAFT' ) LIMIT 1) as titleInDraftState,
	(SELECT peptitle FROM pepstates WHERE pep = a.pep and (email = 'Status : ACCEPTED' ) LIMIT 1) as titleInAcceptedState,
	(SELECT peptitle FROM pepstates WHERE pep = a.pep and (email = 'Status : REJECTED' ) LIMIT 1) as titleInRejectedState,
	(SELECT peptitle FROM pepstates WHERE pep = a.pep and (email = 'Status : FINAL' ) LIMIT 1) as titleInFinalState
from pepdetails a
order by pep asc;

-- some query I may use
select * from pepstates_danieldata where pep = 308;
SELECT pep, peptitle, email, entiremessage
	FROM pepstates_danieldata
	WHERE email = 'Status : DRAFT' OR email = 'Status : ACTIVE'
	order by pep asc
select * from
(select pep, title as title,'pepdetails',null from pepdetails
UNION
SELECT pep, peptitle as title,'states',email FROM pepstates_danieldata)
as t order by pep asc;
 -- WHERE pep = a.pep and (email = 'Status : DRAFT' OR email = 'Status : ACTIVE') 
 
 -- add additional column previosu pep number
alter table allmessages add COLUMN originalPEPNumber INTEGER after pep;
alter table allmessages_announce_list add COLUMN originalPEPNumber INTEGER after pep;
alter table allmessages_authors add COLUMN originalPEPNumber INTEGER after pep;
alter table allmessages_bugs_list add COLUMN originalPEPNumber INTEGER after pep;
alter table allmessages_checkins add COLUMN originalPEPNumber INTEGER after pep;
alter table allmessages_committers add COLUMN originalPEPNumber INTEGER after pep;
alter table allmessages_dev add COLUMN originalPEPNumber INTEGER after pep;
alter table allmessages_distutils_sig add COLUMN originalPEPNumber INTEGER after pep;
alter table allmessages_ideas add COLUMN originalPEPNumber INTEGER after pep;
alter table allmessages_lists add COLUMN originalPEPNumber INTEGER after pep;
alter table allmessages_patches add COLUMN originalPEPNumber INTEGER after pep;
alter table allmessages_peptitleonly_copy add COLUMN originalPEPNumber INTEGER after pep;
alter table allmessages_withpepnumbers add COLUMN originalPEPNumber INTEGER after pep;
alter table allmessagesbck add COLUMN originalPEPNumber INTEGER after pep;
-- update
update allmessages set originalPEPNumber = pep;
update allmessages_announce_list set originalPEPNumber = pep;
update allmessages_authors set originalPEPNumber = pep;
update allmessages_bugs_list set originalPEPNumber = pep;
update allmessages_checkins set originalPEPNumber = pep;
update allmessages_committers set originalPEPNumber = pep;
update allmessages_dev set originalPEPNumber = pep;
update allmessages_distutils_sig set originalPEPNumber = pep;
update allmessages_ideas set originalPEPNumber = pep;
update allmessages_lists set originalPEPNumber = pep;
update allmessages_patches set originalPEPNumber = pep;
update allmessages_peptitleonly_copy set originalPEPNumber = pep;
update allmessages_withpepnumbers set originalPEPNumber = pep;
update allmessagesbck set originalPEPNumber = pep;

select * from allmessages_ideas 
where subject in (select titleinfirststate from pepstates_danieldata_wide )
and pep = -1

update allmessages_ideas
set pep = p
where subject in (select titleinfirststate from pepstates_danieldata_wide )
and pep = -1;

