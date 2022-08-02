SELECT count(pep) from allmessages WHERE messageid = 320847

alter table allmessages_dev add COLUMN IdentifierCount INTEGER;

SELECT IdentifierCount,COUNT(*) as count FROM allmessages GROUP BY IdentifierCount ORDER BY count DESC;