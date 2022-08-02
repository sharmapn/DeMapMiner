DELIMITER $$

-- drop FUNCTION LONGESTCOLUMN;
CREATE FUNCTION LONGESTCOLUMN(COLUMN_NAME varchar(100), TABLE_NAME varchar(100)) RETURNS VARCHAR(200)
    DETERMINISTIC
BEGIN
    DECLARE longest varchar(100);
    SET @tname = TABLE_NAME;
    SET @keyField = COLUMN_NAME;

    select @keyField INTO longest 
	 from @tname 
	 where char_length(@keyField) = (select max(char_length(@keyField)) from @tname);
	 
 RETURN (longest);
END

select LONGESTCOLUMN('sendername','distinctsenders');
drop function LONGESTCOLUMN;