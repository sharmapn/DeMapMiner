select * from trainingdata;
select * from trainingdata where pep = 201 order by pep;
select pep, label, causesentence, effectsentence, causecategory, communityreview, proposalauthorreview,bdfldelegatepronouncement, notes   from trainingdata where pep > 460 order by pep;
-- delete from trainingdata where pep >= 389 order by pep;
rollback;
ALTER TABLE trainingdata ADD id INT PRIMARY KEY AUTO_INCREMENT;
insert into trainingdata (pep, label,folder,file,msgNumberInFile,messageID, causeParagraphNum, causeSentenceNum, effectParagraphNum, effectSentenceNum, causeSentence, effectSentence) 
VALUES (99,'accepted','python-dev','2012-May.txt',322,197784,2,1,3,1, 
'','');
UPDATE trainingdata SET causeCategory = 'testman123'  WHERE id = 281;

SELECT pep,messageid,dateTimeStamp, email,subject from allmessages where pep = 42 and messageid = 343649 

select distinct(folder)  from allmessages;
select * from allmessages where  pep = 217 and folder = 'C:\\datasets\\python-bugs-list' and email like '%pep%'  limit 100;		-- email like '%pep 217%'  and
select * from pepstates_danieldata_datetimestamp where pep = 311

INSERT INTO trainingdata
	SELECT *
	FROM trainingdata_bck13April s
	WHERE s.pep > 3000;
SELECT * FROM  trainingdata ORDER BY label ASC
select id, pep, label, causesentence, causecategory from trainingdata where label IS NOT NULL order by label asc;
SELECT * FROM  trainingdata_copy30april where label = 'accepted' order by pep

select pep from pepdetails order by pep; 
select pep from trainingdata order by pep;

SELECT pep FROM pepdetails
    LEFT JOIN trainingdata ON pep WHERE trainingdata.pep IS NULL; 
    
SELECT pep from pepdetails LEFT JOIN trainingdata USING(pep) WHERE trainingdata.pep IS NULL;