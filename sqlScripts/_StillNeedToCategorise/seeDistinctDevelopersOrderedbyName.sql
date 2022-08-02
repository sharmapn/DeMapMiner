select count(distinct(messageid)) as c, sendername  as s
from allmessages_dev
group by sendername
order by c desc
limit 200
INTO OUTFILE 'c:\\scripts\\Top200DiffDevelopers_02-05-2017.txt';
