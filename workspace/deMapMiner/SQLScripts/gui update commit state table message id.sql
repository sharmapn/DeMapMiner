select pep, date2, email from pepstates_danieldata_datetimestamp where pep = 308;

select pep, date2, email, messageid from allmessages where pep = 308 and email like '%status: accepted%' and date2 = '2005-09-30';

update pepstates_danieldata_datetimestamp set messageID_allmessages = 318242  
where pep = 308 and email like '%status: accepted%' and date2 = '2005-09-30';


-- and email like '%status: accepted%' and date2 = '2005-09-30';


UPDATE
    pepstates_danieldata_datetimestamp INNER JOIN allmessages USING (pep, date2)
SET
    pepstates_danieldata_datetimestamp.messageID_allmessages = allmessages.messageid;