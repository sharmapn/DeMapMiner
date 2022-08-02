use peps_new;  -- data till mar 2018, so shoudl be fine
CREATE TABLE allmessages_dev 		 LIKE allmessages;
INSERT INTO allmessages_dev SELECT * FROM allmessages WHERE folder = 'c:\\datasets\\python-dev';