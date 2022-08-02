-- select * from keywordmatchingremainingpeps;

SELECT pep, clausie, currentSentence,ps,ns from results where clausie like '%accepted%'  order by pep asc, datetimestamp asc;

select * from pepstates_danieldata_datetimestamp
-- where pep =42  
order by pep,datetimestamp ;

select * from allmessages where email like '%final%' and pep = 100;