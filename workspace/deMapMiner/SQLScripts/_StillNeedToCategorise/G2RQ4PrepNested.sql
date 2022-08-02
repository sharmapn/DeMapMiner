SELECT date2, subject,folder, inReplyTo, emailMessageID
FROM allmessages
WHERE inReplyTo IN (select * from (
	SELECT emailMessageID 
	from allmessages
	WHERE YEAR(date2) = 2017 AND MONTH(date2) = 1 
	AND folder like '%python-dev%' 
	AND inReplyTo IS NULL
	ORDER BY date2
	LIMIT 50
	) temp_tab)
	
	-- -- date2 as 'date', subject as 'sub',folder as 'folder', inReplyTo as 'irt'