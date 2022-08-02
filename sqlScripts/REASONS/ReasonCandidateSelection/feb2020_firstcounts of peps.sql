
SELECT peptype, state, COUNT(DISTINCT PEP) from pepstates_danieldata_datetimestamp
GROUP BY peptype, state

SELECT  state, peptype, COUNT(DISTINCT PEP) from pepstates_danieldata_datetimestamp
GROUP BY  state, peptype

SELECT  state, COUNT(DISTINCT PEP) from pepstates_danieldata_datetimestamp
GROUP BY  state

SELECT  peptype, COUNT(DISTINCT PEP) from pepstates_danieldata_datetimestamp
GROUP BY  peptype


