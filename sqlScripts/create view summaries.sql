create view summaries as select pep, messageid, date2, email, subject, identifiercount from allmessages where identifiercount > 5 and (subject like '%summary%' or subject like '%summaries%')
		

select distinct messageid,email, date2, subject,identifiercount from summaries