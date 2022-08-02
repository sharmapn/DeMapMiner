SELECT B.* FROM
(
    select pep,max(date) date
    from danieldata group by pep
) A INNER JOIN danieldata B USING (pep,date)
order by pep;

SELECT email,count(B.pep) cnt FROM
(
    select pep,max(date) date
    from danieldata group by pep
) A INNER JOIN danieldata B USING (pep,date)
group by email;
