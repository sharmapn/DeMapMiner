-- updates sendername - have to run another script to update inreplytouser afterthis
-- trim to make sure they match

-- after running for the allmessages table, then run for allmessages_dev, ideas, checkins, committers, then lists etc

update allmessages set sendername = trim(sendername);
update allmessages set sendername = 'guido.van.rossum' where sendername = 'gvanrossum';
update allmessages set sendername = 'guido.van.rossum' where sendername = 'gvr';
update allmessages set sendername = 'barry.a.warsaw' where sendername = 'barry.warsaw';
update allmessages set sendername = 'andrew.m.kuchling' where sendername = 'a.m.kuchling';
update allmessages set sendername = 'andrew.m.kuchling' where sendername = 'andrew.kuchling';
update allmessages set sendername = 'brett.cannon' where sendername = 'brett.c';
update allmessages set sendername = 'eric.v.smith' where sendername = 'eric.smith';
update allmessages set sendername = 'fred.l.drake' where sendername = 'fred.drake';
update allmessages set sendername = 'jim.j.jewett' where sendername = 'jim.jewett';
update allmessages set sendername = 'greg.wilson' where sendername = 'greg.w';
update allmessages set sendername = 'martin.von.loewis' where sendername = 'martin.v.loewis';
-- update allmessages set sendername = 'martin.von.loewis' where sendername = 'martin';
-- added later
update allmessages set sendername = 'martin.sjögren' where email like 'From martin@strakt.com%';
update allmessages set sendername = 'skip.montanaro' where sendername = 'skip';
-- just.van.rossum
update allmessages set sendername = 'm.a.lemburg' where sendername = 'm.-a.lemburg';
-- trim table column at the end
update allmessages set sendername = trim(sendername);

-- added if email starts with
From martin@strakt.com  = martin.Sjgren
From martin@v.loewis.de = martin.v.loewis