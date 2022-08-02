CREATE TABLE allmessages_dev 		 LIKE allmessages;
CREATE TABLE allmessages_ideas 	 LIKE allmessages;
CREATE TABLE allmessages_committers  LIKE allmessages;
CREATE TABLE allmessages_checkins LIKE allmessages;
CREATE TABLE allmessages_lists 	 LIKE allmessages;
-- added later
CREATE TABLE allmessages_patches 	 LIKE allmessages;
CREATE TABLE allmessages_bugs_list 	 LIKE allmessages;
-- CREATE TABLE allmessages_patches 	 LIKE allmessages

INSERT INTO allmessages_dev 		SELECT * FROM allmessages WHERE folder = 'c:\\datasets\\python-dev';
INSERT INTO allmessages_ideas 	SELECT * FROM allmessages WHERE folder = 'c:\\datasets\\python-ideas';
INSERT INTO allmessages_committers 	SELECT * FROM allmessages WHERE folder = 'c:\\datasets\\python-committers';
INSERT INTO allmessages_checkins SELECT * FROM allmessages WHERE folder = 'c:\\datasets\\python-checkins';
INSERT INTO allmessages_lists 	SELECT * FROM allmessages WHERE folder = 'c:\\datasets\\python-lists';
-- added later
INSERT INTO allmessages_patches 	SELECT * FROM allmessages WHERE folder = 'c:\\datasets\\python-patches';
INSERT INTO allmessages_bugs_list 	SELECT * FROM allmessages WHERE folder = 'c:\\datasets\\python-bugs-list'

-- check mwther each list has any rows/was read in the databse
-- select * from allmessages where folder = 'c:\\datasets\\python-patches' limit 2