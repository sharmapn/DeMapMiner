select * from results  _bckdec WHERE PEP =0 order by pep, datetimestamp;
where pep=8 order by messageid;
select COUNT(*) from allmessages where pep =0 order by messageid;	-- and messageid > 5000258 

select pep, messageid, datetimestamp,label,currentsentence,clausie from results where pep =308 and clausie <> '' order by datetimestamp;

select distinct(label) from results;

select * from pepstates_danieldata_datetimestamp order by pep;

select pep from allmessages where pep =8 and identifiercount >1;

select * from results_bckdec where pep =308;

SELECT date2,datetimestamp, clusterBySenderFullName as author, analysewords, statusFrom, statusTo, statusChanged,  messageID, subject,folder,file, required, date2 as date3,  
		inReplyTo,IdentifierCount from allmessages WHERE pep = 0
		order by messageid asc