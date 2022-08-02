select * from autoextractedreasoncandidatesentences where proposal = 201

SELECT * FROM autoextractedreasoncandidatemessages where proposal = 201

update autoextractedreasoncandidatemessages AS T1 
INNER JOIN autoextractedreasoncandidatesentences AS T2
ON T1.messageID = T2.messageID
set T1.authorroleprobability = T2.authorroleprobability;

update autoextractedreasoncandidatemessages AS T1 
INNER JOIN autoextractedreasoncandidatesentences AS T2
ON T1.messageID = T2.messageID
set T1.dateDiffProbability = T2.dateDiffProbability;

-- UPDATE [table1_name] AS t1 INNER JOIN [table2_name] AS t2 ON t1.column1_name] = t2.[column1_name] SET t1.[column2_name] = t2.column2_name]