delimiter //
CREATE procedure peps_new.insertEmptyReasosnRows()
wholeblock:BEGIN
  DECLARE x INT;
  SET x = 0;

  loop_label: LOOP
    IF x > 466 THEN
      LEAVE loop_label;
    END IF;
    SELECT x;
 --   SET str = CONCAT(str,x,',');
    INSERT INTO manualreasonextraction (proposal, messageid, author,dateTimeStamp)
	 	select distinct a.PEP,  a.messageid, a.authorsrole, a.dateTimeStamp 
	 	from allmessages as a 
	 	where a.pep = x
	 	AND a.messageid NOT IN (select distinct b.messageid 
                 from manualreasonextraction as b 
                 where b.proposal = x);
    SET x = x + 1;
    ITERATE loop_label;
  END LOOP;
  SELECT str;
END//