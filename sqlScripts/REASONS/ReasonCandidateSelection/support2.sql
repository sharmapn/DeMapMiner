-- date check

SELECT pep,originalpepnumber,messageID, subject, date2,datetimestamp, analysewords,authorsRole, DATEDIFF('2000-08-03',date2) as diff
from allmessages where pep = 201
and authorsRole IN('proposalAuthor','bdfl','coredeveloper','bdfl') 
order by datetimestamp desc; 

SELECT DATEDIFF("2017-06-25", "2017-06-15") > 1000;

select distinct(authorsrole) from allmessages where pep > 300 and pep < 400;
-- proposalAuthor, otherCommunityMember, coredeveloper, pepeditors,bdfl,bdfl_delegate