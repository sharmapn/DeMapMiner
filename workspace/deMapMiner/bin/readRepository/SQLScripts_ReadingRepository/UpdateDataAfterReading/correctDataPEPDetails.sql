select count(distinct pep) from pepdetails
SELECT 
    pep
FROM
    pepdetails2
        LEFT JOIN
    pepdetails USING (pep)
WHERE
    pepdetails.pep IS NULL;
    
    select * from pepdetails where pep = 500 
	 order by pep asc
	 
select count(distinct pep) from allmessages;
-- even after corectling using sql, a lot of fields were manually corrected..so make sure to check for errors like...created, created year - had to crross check with python website
-- also cross check with state data table, ,and allmessages table for first instance of that pep
-- some instances where there were multiple authors, the email were not transferred
-- author, email are imprtant for ML so its important to get them right
select author from pepdetails where author like 'Marc-AndrÃ© Lemburg';
select author from pepdetails where author like 'Marc-André Lemburg';

UPDATE pepdetails SET author = REPLACE(author, 'Martin von LÃ¶wis', 'Martin von Lewis') WHERE INSTR(author, 'Martin von LÃ¶wis') > 0;
UPDATE pepdetails SET authorcorrected = REPLACE(authorcorrected, 'Martin von LÃ¶wis', 'Martin von Lewis') WHERE INSTR(authorcorrected, 'Martin von LÃ¶wis') > 0;

UPDATE pepdetails SET author = REPLACE(author, 'l?ukasz langa', 'Lukasz Langa') WHERE INSTR(author, 'Ml?ukasz langa)';
UPDATE pepdetails SET authorcorrected = REPLACE(authorcorrected, 'l?ukasz langa', 'Lukasz Langa') WHERE INSTR(authorcorrected, 'Martin von LÃ¶wis') > 0;

UPDATE pepdetails SET author = REPLACE(author, 'Marc-AndrÃ© Lemburg', 'Marc-Andre Lemburg') WHERE INSTR(author, 'Marc-AndrÃ© Lemburg') > 0;
UPDATE pepdetails SET authorcorrected = REPLACE(authorcorrected, 'Marc-AndrÃ© Lemburg', 'Marc-Andre Lemburg') WHERE INSTR(authorcorrected, 'Marc-AndrÃ© Lemburg') > 0;
UPDATE pepdetails SET author = REPLACE(author, 'Fred L Drake,Jr', 'Fred Drake') WHERE INSTR(author, 'Fred L Drake,Jr') > 0;
UPDATE pepdetails SET authorcorrected = REPLACE(authorcorrected, 'Fred L Drake,Jr', 'Fred Drake') WHERE INSTR(authorcorrected, 'Fred L Drake,Jr') > 0;
UPDATE pepdetails SET author = REPLACE(author, 'Martin von Lewis', 'Martin v Lewis') WHERE INSTR(author, 'Martin von Lewis') > 0;
UPDATE pepdetails SET authorcorrected = REPLACE(authorcorrected, 'Martin von Lewis', 'Martin v Lewis') WHERE INSTR(authorcorrected, 'Martin von Lewis') > 0;
-- FrÃ©dÃ©ric B Giacometti Frederic Giacometti
UPDATE pepdetails SET author = REPLACE(author, 'FrÃ©dÃ©ric B Giacometti', 'Frederic Giacometti') WHERE INSTR(author, 'FrÃ©dÃ©ric B Giacometti') > 0;
UPDATE pepdetails SET authorcorrected = REPLACE(authorcorrected, 'FrÃ©dÃ©ric B Giacometti', 'Frederic Giacometti') WHERE INSTR(authorcorrected, 'FrÃ©dÃ©ric B Giacometti') > 0;
-- Walter DÃ¶rwald Walter Dörwald 
UPDATE pepdetails SET author = REPLACE(author, 'Walter DÃ¶rwald', 'Walter Dörwald') WHERE INSTR(author, 'Walter DÃ¶rwald') > 0;
UPDATE pepdetails SET authorcorrected = REPLACE(authorcorrected, 'Walter DÃ¶rwald', 'Walter Dörwald') WHERE INSTR(authorcorrected, 'Walter DÃ¶rwald') > 0;
-- BjÃ¶rn Lindqvist  BJörn Lindqvist
UPDATE pepdetails SET author = REPLACE(author, 'BjÃ¶rn Lindqvist', 'BJörn Lindqvist') WHERE INSTR(author, 'BjÃ¶rn Lindqvist') > 0;
UPDATE pepdetails SET authorcorrected = REPLACE(authorcorrected, 'BjÃ¶rn Lindqvist', 'BJörn Lindqvist') WHERE INSTR(authorcorrected, 'BjÃ¶rn Lindqvist') > 0;
-- Tarek ZiadÃ©, Tarek Ziad
UPDATE pepdetails SET author = REPLACE(author, 'Tarek ZiadÃ©', 'Tarek Ziad') WHERE INSTR(author, 'Tarek ZiadÃ©') > 0;
UPDATE pepdetails SET authorcorrected = REPLACE(authorcorrected, 'Tarek ZiadÃ©', 'Tarek Ziad') WHERE INSTR(authorcorrected, 'Tarek ZiadÃ©') > 0;
-- Martin v LÃ¶wis Martin v Lewis
UPDATE pepdetails SET author = REPLACE(author, 'Martin v LÃ¶wis', 'Martin v Lewis') WHERE INSTR(author, 'Martin v LÃ¶wis') > 0;
UPDATE pepdetails SET authorcorrected = REPLACE(authorcorrected, 'Martin v LÃ¶wis', 'Martin v Lewis') WHERE INSTR(authorcorrected, 'Martin v LÃ¶wis') > 0;
UPDATE pepdetails SET author = REPLACE(author, 'Charles-FranÃ§ois Natali', 'Charles-François Natali') WHERE INSTR(author, 'Charles-FranÃ§ois Natali') > 0;
UPDATE pepdetails SET authorcorrected = REPLACE(authorcorrected, 'Charles-FranÃ§ois Natali', 'Charles-François Natali') WHERE INSTR(authorcorrected, 'Charles-FranÃ§ois Natali') > 0;

-- RUN THE ABOVE SAME THINGS ON BDFL DELEGATE COLUMN
UPDATE pepdetails SET bdfl_delegate = REPLACE(bdfl_delegate, 'Martin von LÃ¶wis', 'Martin von Lewis') WHERE INSTR(bdfl_delegate, 'Martin von LÃ¶wis') > 0;
UPDATE pepdetails SET bdfl_delegatecorrected = REPLACE(bdfl_delegatecorrected, 'Martin von LÃ¶wis', 'Martin von Lewis') WHERE INSTR(bdfl_delegatecorrected, 'Martin von LÃ¶wis') > 0;
UPDATE pepdetails SET bdfl_delegate = REPLACE(bdfl_delegate, 'Marc-AndrÃ© Lemburg', 'Marc-Andre Lemburg') WHERE INSTR(bdfl_delegate, 'Marc-AndrÃ© Lemburg') > 0;
UPDATE pepdetails SET bdfl_delegatecorrected = REPLACE(bdfl_delegatecorrected, 'Marc-AndrÃ© Lemburg', 'Marc-Andre Lemburg') WHERE INSTR(bdfl_delegatecorrected, 'Marc-AndrÃ© Lemburg') > 0;
UPDATE pepdetails SET bdfl_delegate = REPLACE(bdfl_delegate, 'Fred L Drake,Jr', 'Fred Drake') WHERE INSTR(bdfl_delegate, 'Fred L Drake,Jr') > 0;
UPDATE pepdetails SET bdfl_delegatecorrected = REPLACE(bdfl_delegatecorrected, 'Fred L Drake,Jr', 'Fred Drake') WHERE INSTR(bdfl_delegatecorrected, 'Fred L Drake,Jr') > 0;
UPDATE pepdetails SET bdfl_delegate = REPLACE(bdfl_delegate, 'Martin von Lewis', 'Martin v Lewis') WHERE INSTR(bdfl_delegate, 'Martin von Lewis') > 0;
UPDATE pepdetails SET bdfl_delegatecorrected = REPLACE(bdfl_delegatecorrected, 'Martin von Lewis', 'Martin v Lewis') WHERE INSTR(bdfl_delegatecorrected, 'Martin von Lewis') > 0;
-- FrÃ©dÃ©ric B Giacometti Frederic Giacometti
UPDATE pepdetails SET bdfl_delegate = REPLACE(bdfl_delegate, 'FrÃ©dÃ©ric B Giacometti', 'Frederic Giacometti') WHERE INSTR(bdfl_delegate, 'FrÃ©dÃ©ric B Giacometti') > 0;
UPDATE pepdetails SET bdfl_delegatecorrected = REPLACE(bdfl_delegatecorrected, 'FrÃ©dÃ©ric B Giacometti', 'Frederic Giacometti') WHERE INSTR(bdfl_delegatecorrected, 'FrÃ©dÃ©ric B Giacometti') > 0;
-- Walter DÃ¶rwald Walter Dörwald 
UPDATE pepdetails SET bdfl_delegate = REPLACE(bdfl_delegate, 'Walter DÃ¶rwald', 'Walter Dörwald') WHERE INSTR(bdfl_delegate, 'Walter DÃ¶rwald') > 0;
UPDATE pepdetails SET bdfl_delegatecorrected = REPLACE(bdfl_delegatecorrected, 'Walter DÃ¶rwald', 'Walter Dörwald') WHERE INSTR(bdfl_delegatecorrected, 'Walter DÃ¶rwald') > 0;
-- BjÃ¶rn Lindqvist  BJörn Lindqvist
UPDATE pepdetails SET bdfl_delegate = REPLACE(bdfl_delegate, 'BjÃ¶rn Lindqvist', 'BJörn Lindqvist') WHERE INSTR(bdfl_delegate, 'BjÃ¶rn Lindqvist') > 0;
UPDATE pepdetails SET bdfl_delegatecorrected = REPLACE(bdfl_delegatecorrected, 'BjÃ¶rn Lindqvist', 'BJörn Lindqvist') WHERE INSTR(bdfl_delegatecorrected, 'BjÃ¶rn Lindqvist') > 0;
-- Tarek ZiadÃ©, Tarek Ziad
UPDATE pepdetails SET bdfl_delegate = REPLACE(bdfl_delegate, 'Tarek ZiadÃ©', 'Tarek Ziad') WHERE INSTR(bdfl_delegate, 'Tarek ZiadÃ©') > 0;
UPDATE pepdetails SET bdfl_delegatecorrected = REPLACE(bdfl_delegatecorrected, 'Tarek ZiadÃ©', 'Tarek Ziad') WHERE INSTR(bdfl_delegatecorrected, 'Tarek ZiadÃ©') > 0;
-- Martin v LÃ¶wis Martin v Lewis
UPDATE pepdetails SET bdfl_delegate = REPLACE(bdfl_delegate, 'Martin v LÃ¶wis', 'Martin v Lewis') WHERE INSTR(bdfl_delegate, 'Martin v LÃ¶wis') > 0;
UPDATE pepdetails SET bdfl_delegatecorrected = REPLACE(bdfl_delegatecorrected, 'Martin v LÃ¶wis', 'Martin v Lewis') WHERE INSTR(bdfl_delegatecorrected, 'Martin v LÃ¶wis') > 0;
UPDATE pepdetails SET bdfl_delegate = REPLACE(bdfl_delegate, 'Charles-FranÃ§ois Natali', 'Charles-François Natali') WHERE INSTR(bdfl_delegate, 'Charles-FranÃ§ois Natali') > 0;
UPDATE pepdetails SET bdfl_delegatecorrected = REPLACE(bdfl_delegatecorrected, 'Charles-FranÃ§ois Natali', 'Charles-François Natali') WHERE INSTR(bdfl_delegatecorrected, 'Charles-FranÃ§ois Natali') > 0;

-- firstname and lastname
-- a m kuchling -	andrew kuchling	
UPDATE pepdetails SET bdfl_delegate = REPLACE(bdfl_delegate, 'a m kuchling', 'andrew kuchling	') WHERE INSTR(bdfl_delegate, 'a m kuchling') > 0;
UPDATE pepdetails SET bdfl_delegatecorrected = REPLACE(bdfl_delegatecorrected, 'a m kuchling', 'andrew kuchling	') WHERE INSTR(bdfl_delegatecorrected, 'a m kuchling') > 0;

-- correct all email addresses for matching
UPDATE pepdetails SET authoremail = REPLACE(authoremail, ' at ', '@') WHERE INSTR(authoremail, ' at ') > 0;
UPDATE pepdetails SET bdfl_delegate = REPLACE(bdfl_delegate, ' at ', '@') WHERE INSTR(bdfl_delegate, ' at ') > 0;
UPDATE pepdetails SET bdfl_delegatecorrected = REPLACE(bdfl_delegatecorrected, ' at ', '@') WHERE INSTR(bdfl_delegatecorrected, ' at ') > 0;

UPDATE pepdetails SET bdfl_delegate = REPLACE(bdfl_delegate, '&lt', '') WHERE INSTR(bdfl_delegate, '&lt') > 0;
UPDATE pepdetails SET bdfl_delegate = REPLACE(bdfl_delegate, '&gt', '') WHERE INSTR(bdfl_delegate, '&gt') > 0;
UPDATE pepdetails SET bdfl_delegatecorrected = REPLACE(bdfl_delegatecorrected, '&lt', '') WHERE INSTR(bdfl_delegatecorrected, '&lt') > 0;
UPDATE pepdetails SET bdfl_delegatecorrected = REPLACE(bdfl_delegatecorrected, '&gt', '') WHERE INSTR(bdfl_delegatecorrected, '&gt') > 0;
UPDATE pepdetails SET authoremail = REPLACE(authoremail, '&lt', '') WHERE INSTR(authoremail, '&lt') > 0;
UPDATE pepdetails SET authoremail = REPLACE(authoremail, '&gt', '') WHERE INSTR(authoremail, '&gt') > 0;
UPDATE pepdetails SET authoremail = REPLACE(authoremail, '</td>', ',') WHERE INSTR(authoremail, '</td>') > 0;
-- author email had this instances
UPDATE pepdetails SET authoremail = REPLACE(authoremail, ',fdrake@acm.org','fdrake@acm.org') WHERE INSTR(authoremail, ',fdrake@acm.org') > 0;
UPDATE pepdetails SET authoremail = SUBSTRING_INDEX(authoremail,'</td>', 1); -- remove everything after '</td>'
-- where pep = 8;
select * from pepdetails where pep = 3145;
select * from pythonmembers_coredevelopers order by name
select * from pepstates_danieldata_datetimestamp where pep = 200;
select * from allmessages where pep = 230 order by date2 limit 10;  -- 2000-07-13,2000-07-13, 2000-07-15
select * from pepstates_danieldata_datetimestamp where pep = 3145;
select * from pepstates_danieldata_datetimestamp order by pep, date2;

SELECT pepdetails.pep as pep, pepdetails.title as 'pepdetailsTitle', pepstates_danieldata_datetimestamp.date2 as date2,pepstates_danieldata_datetimestamp.peptitle as 'pep state title'
FROM pepdetails, pepstates_danieldata_datetimestamp 
WHERE pepdetails.pep = pepstates_danieldata_datetimestamp.pep
order by pep, date2;
 -- pep 477, 500, 499 title and numerous emails were added after seraching same table - manually and programmatically (below)
 -- Charles-François Natali .. Charles-FranÃ§ois Natali
 select * from pepdetails where -- pep = 3145 
 authorcorrected like '%james%';