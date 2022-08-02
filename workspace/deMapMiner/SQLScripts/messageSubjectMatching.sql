ALTER TABLE allmessages 
    ADD COLUMN processedSubject MEDIUMTEXT,
    ADD INDEX (processedSubject(40));
-- single ones
update allmessages set processedSubject = replace(subject,'\'','')  where messageID = 376335  ;
update allmessages set processedSubject = replace(subject,'\"' ,'') where messageID = 1300 ;
-- see result 
select pep, messageid, subject, processedsubject from allmessages where messageID = 376335 ;
select pep, messageid, subject, processedsubject from allmessages where subject like '%\"%' limit 2;
-- joined into single query
update allmessages set processedSubject = replace(replace(subject,'\'',''),'\"' ,'');

alter table allmessages drop column processedSubject;
alter table allmessages drop index processedSubject;

select * from allmessagesmatchingsubjects where proposal =226
delete from allmessagesmatchingsubjects where proposal =238;