update distinctauthorspepnumbers
 JOIN allmessages
       ON allmessages.sendername = distinctauthorspepnumbers.sendername
set firstpostdate = (select min(date2) from allmessages)