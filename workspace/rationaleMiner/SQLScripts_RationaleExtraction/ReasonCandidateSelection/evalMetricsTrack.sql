drop table trackForEachRow;
create table trackForEachRow (
	 proposal INTEGER,	 state	 TEXT, 	 messageID INTEGER,	 ind      INTEGER,	 rank		 					INTEGER,		 rankByTP INTEGER,
	 preAtK   DOUBLE,	    recAtK   DOUBLE,	 dcg   	  DOUBLE,	 idcg     DOUBLE, ndcg     DOUBLE,	 precision_ForAVGPreMean DOUBLE,  deno DOUBLE,deno2 DOUBLE, currentRecall_ForAVGPreMean DOUBLE, 
	 previousSumPreAtTop10 DOUBLE, previousSumRecAtTop10 DOUBLE,
	 matchedAtIndex TINYINT
)
DELETE FROM trackForEachRow;

DROP TABLE trackForEachProposal;
create table trackForEachProposal (
	 proposal INTEGER,	 state	 TEXT, preAtTop5 DOUBLE, preAtTop10 DOUBLE, preAtTop15 DOUBLE, preAtTop30 DOUBLE, preAtTop50 DOUBLE, preAtTop100 DOUBLE, preOutsideTop100 DOUBLE,
	 recAtTop5 DOUBLE, recAtTop10 DOUBLE, recAtTop15 DOUBLE, recAtTop30 DOUBLE, recAtTop50 DOUBLE, recAtTop100 DOUBLE, recOutsideTop100 DOUBLE,	
	 sumPreAtTop5 DOUBLE, sumPreAtTop10 DOUBLE, sumPreAtTop15 DOUBLE, sumPreAtTop30 DOUBLE, sumPreAtTop50 DOUBLE, sumPreOutsideTop100 DOUBLE,	
	 preAtK   DOUBLE,	    recAtK   DOUBLE,	 dcg   	  DOUBLE,	 ndcg     DOUBLE,	 sum_dcg DOUBLE,  sum_ndcg DOUBLE, finalAvgPre DOUBLE, sumOfAllPre DOUBLE, matchedInProposal TINYINT
)
DELETE FROM trackForEachProposal;

select * from trackForEachRow
where state = 'accepted' and proposal = 308  and rank <= 5
order by proposal, state, ind, rankbytp, rank;
-- to see whether recall is inc or dec, it shoud increase
select * from trackForEachRow
where state = 'accepted'  and rank <= 5
order by proposal, state, ind, rankbytp, rank;

select * from trackForEachProposal
select proposal, state, ROUND(preattop5,2) as p5, ROUND(preattop10,2) as p10, ROUND(preattop15, 2) as p15, ROUND(preattop30,2) as p30, ROUND(preattop50,2) as p50, ROUND(preattop100,2) as p100, 
		 ROUND(preOutsideTop100,2) as pout100,
		 ROUND(sumPreAtTop5,2) as sump5, ROUND(sumPreAtTop10,2) as sum10, ROUND(sumPreAtTop15,2) as sump15, ROUND(sumPreAtTop30,2) as sum30, ROUND(sumPreAtTop50,2) as sump50, 
		 ROUND(sumPreOutsideTop100,2) as sumpout100,	
	    preAtK, recAtK,ROUND(dcg,2) AS dcg, ROUND(ndcg,2) as ndcg,	ROUND(sum_dcg,2) as sum_dcg, ROUND(sum_ndcg,2) as sum_ndcg, finalAvgPre, sumOfAllPre,sumOfAllRec, 
from trackForEachProposal
where state = 'accepted'  and proposal >0
order by proposal

select proposal,  max(recatk)
from trackForEachProposal
where state = 'accepted'  and proposal >0
group by proposal
order by proposal

select proposal, state, ind, rank,rankByTP,preatk,recatk,dcg,deno,deno2,idcg,ndcg,precision_foravgpremean,currentrecall_foravgpremean 
from trackForEachRow
where state = 'accepted'  -- and matchedatindex=1
order by proposal, state, rank, rankbytp, ind;

select * from trackForEachProposal
where state = 'accepted' and matchedinproposal =1

