select * from autoextractedreasoncandidatesentences;
SELECT * FROM autoextractedreasoncandidateparagraphs;
SELECT * FROM autoextractedreasoncandidatemessages;
SELECT * FROM pepstates_danieldata_datetimestamp;

SELECT * FROM autoextractedreasoncandidatesentences_nodupsranked_sent_b2

SELECT COUNT(*) FROM allmessages;

OPTIMIZE TABLE allmessages;

SELECT * FROM trainingdata;

TRUNCATE autoextractedreasoncandidatesentences_nodupsranked_sent_b2;
TRUNCATE autoextractedreasoncandidatesentences_nodupsranked_msg_b2;