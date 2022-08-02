CREATE TABLE exams (
        pkey int(11) NOT NULL auto_increment,
        name varchar(15),
        exam int,
        score int,
        PRIMARY KEY  (pkey)
);

insert into exams (name,exam,score) values ('Bob',1,75);
insert into exams (name,exam,score) values ('Bob',2,77);
insert into exams (name,exam,score) values ('Bob',3,78);
insert into exams (name,exam,score) values ('Bob',4,80);

insert into exams (name,exam,score) values ('Sue',1,90);
insert into exams (name,exam,score) values ('Sue',2,97);
insert into exams (name,exam,score) values ('Sue',3,98);
insert into exams (name,exam,score) values ('Sue',4,99);

select * from exams;

select name,
sum(score*(1-abs(sign(exam-1)))) as exam1,
sum(score*(1-abs(sign(exam-2)))) as exam2,
sum(score*(1-abs(sign(exam-3)))) as exam3,
sum(score*(1-abs(sign(exam-4)))) as exam4
from exams group by name;