SELECT max(date2) m, (CURDATE()- m) from allmessages 
WHERE senderName = 'Guido vaSTATUAn Rossum'