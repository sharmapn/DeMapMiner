select * from pepstates_danieldata_datetimestamp
where pep = 7

select * from results where pep = 308 and email like '%results of vote%'
select * from pepstates_danieldata_datetimestamp

select * from labels
select * from pepstates_danieldata_datetimestamp where pep = 379

select distinct(pep) from extractedrelations_clausie

select distinct pep from results
select max(pep) from results
select messageid from results where pep = 257 order by messageid 

select distinct pep from allmessages order by pep desc

select * from results where pep = 3333 order by datetimestamp asc;

select pep, count(messageid) as c
from allmessages
where pep > 3333
group by pep

select * from allmessages where pep = 4000;

SELECT pep, author,authorRole,label, subject, relation, object, currentSentence, messageSubject,numberOfPEPsMentionedInMessage, date,
datetimestamp,folder,file, messageID,  clausie,
-- reverb,  ollie, repeatedLabel,role, 
-- allReasons, ps,ns,pp,ep,np, 
-- reasonInSentence,reasonTermsInMatchedTriple, reasonTermsInNearbySentencesParagraphs, reasonTriplesInNearbySentencesParagraphs, 
-- reasonTermsInPreviousSentence,reasonTermsInNextSentence,reasonTriplesInPreviousSentence,reasonTriplesInNextSentence,reasonTriplesInSameSentence, 
labelFoundInMsgSubject,isFirstParagraph, isLastParagraph 
from results where pep = 308
order by pep asc, datetimestamp asc, messageID asc

update results set authorrole = "bdfl" where messageid > 5000000 and messageid < 5010000