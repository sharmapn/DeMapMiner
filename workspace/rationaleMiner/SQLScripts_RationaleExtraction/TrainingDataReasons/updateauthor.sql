select causeSentence, effectSentence from trainingData;

SELECT proposal,messageid,datetimestamp, sentence,termsMatched, level, reason, author from manualreasonextraction
where proposal = 3148
select * from allmessages limit 50

update manualreasonextraction set author = select author from 

UPDATE manualreasonextraction t1 
        INNER JOIN allmessages t2 
             ON t1.messageid = t2.messageid
SET t1.author = t2.authorsrole 