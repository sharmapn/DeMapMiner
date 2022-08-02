

update results set clausie = 'ReDraft'  where currentsentence like '%second draft%' or currentsentence like '%2nd draft%' or currentsentence like '%round 2%' or currentsentence like '%round two%';
update results set clausie = 'ReDraft'   where currentsentence like '%third draft%'  or currentsentence like '%3rd draft%' or currentsentence like '%round 3' or currentsentence like '%round three%';
update results set clausie = 'ReDraft'  where currentsentence like '%fourth draft%' or currentsentence like '%4th draft%' or currentsentence like '%round 4%' or currentsentence like '%round four%';
update results set clausie = 'ReDraft'   where currentsentence like '%fifth draft%'  or currentsentence like '%5th draft%' or currentsentence like '%round 5%' or currentsentence like '%round five%';
update results set clausie = 'ReDraft'   where currentsentence like '%sixth draft%'  or currentsentence like '%6th draft%' or currentsentence like '%round 6%' or currentsentence like '%round six%';
update results set clausie = 'ReDraft' where currentsentence like '%seventh draft%' or currentsentence like '%7th draft%' or currentsentence like '%round 7%' or currentsentence like '%round seven%';