select max(pep) from extractedrelations_clausie;  -- get last processed pep 
select max(messageid) from extractedrelations_clausie where pep = 3107; -- get last sucessful processed mesage id = 863043
select messageid from allmessages where messageid > 863043 and pep = 3107 order by messageid limit 1; -- get error messageid to skip..insert this in prop file