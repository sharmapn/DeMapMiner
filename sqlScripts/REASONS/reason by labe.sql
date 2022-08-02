SELECT * 
FROM autoextractedreasoncandidatesentences

SELECT count(*), label , reasonsTermsFoundList
FROM autoextractedreasoncandidatesentences
where containsreason=1
GROUP BY label, reasonsTermsFoundList;