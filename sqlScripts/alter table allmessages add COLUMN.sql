alter table allmessages add COLUMN statusFrom TINYTEXT, 
								add COLUMN statusTo TINYTEXT;
							
alter table allmessages add COLUMN statusChanged INTEGER;