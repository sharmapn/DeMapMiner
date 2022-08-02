select * from allmessages
where pep= 308 
and analysewords like '%This COMPLEMENTARY vote%'

select max(pep) from results 
where clausie = 'draft_pep'
where label like '%unofficial_vote%' order by date

select * from pepdetails where pep < 221 order by pep 
where title IS  NULL
order by pep asc

delete from results where pep =343