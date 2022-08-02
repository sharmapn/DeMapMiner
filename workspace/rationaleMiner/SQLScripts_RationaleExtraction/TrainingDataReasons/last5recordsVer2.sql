SELECT messageid,datetimestamp, sentence,termsMatched, level, reason, author from manualreasonextraction 
WHERE  messageid IN (
SELECT * FROM 
	(SELECT DISTINCT messageid from manualreasonextraction 
	WHERE (datetimestamp <= '2014-04-19 02:48:05.0' and proposal =  466 and author LIKE 'proposalAuthor' ) -- and level LIKE '" + location_str + "'" + combination_str + ")" 
	OR    (datetimestamp = '2014-04-19 02:48:05.0' AND termsMatched LIKE '%Status:%' )    -- include the state change records ..DRAFT, ACCEPTED, FINAL, etc
	order by datetimestamp desc  LIMIT 5) AS t 
) order by datetimestamp ; 