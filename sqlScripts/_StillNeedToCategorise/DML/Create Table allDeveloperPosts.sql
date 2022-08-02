create table allDeveloperPosts
AS Select * from allmessages
where folder = 'C:\\datasets\\python-dev'
and pep != -1