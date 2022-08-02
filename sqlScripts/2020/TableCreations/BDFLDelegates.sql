-- march 10 2021...this is alrady done in another file 
-- search for 'CREATE TABLE bdflddpeps AS  '


-- Now why was I only focussing on acc rej peps. 
-- So now I focus on all peps created before bdfl resigned, not only 245 peps
CREATE TABLE allpepstillbdfl_BDFLDelegates AS
	SELECT pep , author, bdfl_delegatecorrected 
	FROM pepdetails 
	WHERE created <= '2018-07-12' and length(bdfl_delegatecorrected) > 0;
	
-- guido cannot be bdfl_delegate, he is the bdfl
-- so we removed him manually	
SELECT * FROM pepdetails WHERE pep = 544	
SELECT * FROM pepdetails2020 WHERE pep = 544

SELECT pep, email, clusterbysenderfullname, authorsrole FROM allmessages WHERE  pep = 544

-- UPDATE allmessages SET clusterbysenderfullname ='Andrew Kuchling' 	WHERE clusterbysenderfullname = 'A M Kuchling';