SELECT messageid,datetimestamp, sentence,termsMatched, level, reason, author from manualreasonextraction 
WHERE  messageid IN (
							SELECT * FROM
								(SELECT DISTINCT messageid from manualreasonextraction 
								where  proposal =  466  and termsMatched IS NULL 
								-- datetimestamp <= '2000-08-03 15:41:50.0' and
						--		and author Like 'proposalAuthor' --		+ " author LIKE '"+ messageAuthor_str + "' and level LIKE '" + location_str + "' and termsMatched LIKE '" + combination_str + "'" 
								
								order by datetimestamp desc  LIMIT 5) AS t 	
						  ) 
order by datetimestamp;




