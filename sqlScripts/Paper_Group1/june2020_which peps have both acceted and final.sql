
-- july 2020 ..chapter 4... whcih poeos have both accepted and final and whih dont

-- designing the query
SELECT * 
FROM pepstates_danieldata_datetimestamp 
WHERE state LIKE '%final%'

CREATE or replace VIEW email_a as
SELECT distinct pep, dateTIMESTAMP, state, 
(SELECT type From pepdetails p  WHERE pepstates_danieldata_datetimestamp.pep = p.pep LIMIT 1) as ptype
FROM pepstates_danieldata_datetimestamp
WHERE state LIKE '%accepted%'  AND pep <> -1
-- OR clausie LIKE '%poll%' OR clausie LIKE '%vot%'
ORDER BY pep, dateTIMESTAMP DESC
-- 152 peps are accepted


CREATE or replace VIEW email_f as
SELECT distinct pep, dateTIMESTAMP, state, 
(SELECT type From pepdetails p  WHERE pepstates_danieldata_datetimestamp.pep = p.pep LIMIT 1) as ptype
FROM pepstates_danieldata_datetimestamp
WHERE state LIKE '%final%'  AND pep <> -1
-- OR clausie LIKE '%poll%' OR clausie LIKE '%vot%'
ORDER BY pep, dateTIMESTAMP DESC
-- 216 are final

-- which peps have both
-- in final and also in accepted
SELECT email_f.pep
FROM email_f
WHERE email_f.pep <> -1
AND email_f.pep IN
   (SELECT email_a.pep
		FROM email_a
		WHERE email_a.pep <> -1
		AND email_a.pep );
-- 131 peps have final and also in accepted

SELECT email_a.pep
FROM email_a
WHERE email_a.pep <> -1
AND email_a.pep IN
   (SELECT email_f.pep
		FROM email_f
		WHERE email_f.pep <> -1
		AND email_f.pep );
-- 131 peps have accepted and also in final

-- Minus, which peps have one of each only, exchange the columns here to mius from the two sets
CREATE or replace VIEW email_af as 
SELECT pep
FROM email_f  
-- WHERE clausie LIKE '%bdfl pronouncement%' OR clausie LIKE '%bdfl delegate%'
 LEFT JOIN
   	email_a USING (PEP)
   	WHERE
    	email_a.PEP IS NULL;
-- 85 PEPs have final but not accepted
-- 21 peps have accepted but not final

-- last check to see if these peps actually have both
SELECT pep, TIMESTAMP, clausie
FROM pepstates_danieldata_datetimestamp
WHERE pep IN
	(SELECT pep
		FROM rp_both)	
ORDER BY pep, TIMESTAMP asc