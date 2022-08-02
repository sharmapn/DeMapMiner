select * from results where pep = 308
DELETE from results where pep > 308
select * from results_postprocessed_dec2018 where pep = 308
select * from results_postprocessed where pep = 308
order by timestamp asc
select * from labels where idea like '%vote%'
select * from debug_coref
select * from allmessages where messageid = 1215272
select * from allmessages where pep = 348 and email like '%tweaked%'
select * from labels
select distinct * from allsentences 
where pep = 308;
 messageid = 1215272;
 
 select distinct pep from results
select max(pep) from results
select messageid from results where pep = 257 order by messageid 
select * from results_bckdec where pep = 308 and length(clausie) > 0;
select pep, count(messageid) as msgs from allmessages where pep > 3154 
group by pep
order by pep desc

select * from results where pep = 3333 order by datetimestamp asc;
select * from results where isfirstparagraph =1; -- messageid = 138933 
select pep, count(messageid) as c
from allmessages where pep > 3131 group by pep
select distinct pep from allmessages where pep > 471;
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