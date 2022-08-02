select  id, pep, label, messageid, causesentence, effectsentence,notes from trainingdata 
where label = 'None' 
order by id
select sentenceLocationInFirstLastParagraph from autoextractedreasoncandidatesentences where containsreason = 1
select * from autoextractedreasoncandidatesentences where containsreason = 1
select * from autoextractedreasoncandidatesentences
where proposal = 343	and messageid = 133556


DELETE t1 FROM autoextractedreasoncandidatesentences t1
        INNER JOIN
    autoextractedreasoncandidatesentences t2 
WHERE
    t1.id < t2.id AND t1.sentence = t2.sentence AND t1.messageID = t2.messageID and t1.messagesUBJECT = t2.messageSubject ;