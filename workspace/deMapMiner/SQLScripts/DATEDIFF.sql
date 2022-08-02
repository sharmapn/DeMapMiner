SELECT DATEDIFF("2017-01-01", "2016-12-24");

SELECT pep,messageid,  date,timestamp, clausie,ps,currentSentence,ns, pp,ep,np,subject,relation,object 
from results_postprocessed where pep = 268

SELECT pep,messageID, subject, date2,datetimestamp, email,authorsRole from allmessages 
where pep = 268 and authorsRole NOT LIKE '%other%'
and DATEDIFF('2007-05-18',date2) < 14  
order by datetimestamp desc