select distinct(proposal) from proposaldetails_titlesforeachpep_debug
select * from proposaldetails_titlesforeachpep_debug where proposal = 308;
select proposal,count(*) as num from proposaldetails_titlesforeachpep_debug
group by proposal;
-- where pep = 308
select * from allmessages where messageid = 90910;