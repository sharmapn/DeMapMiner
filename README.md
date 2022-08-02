# DeMaPMiner

**Replication Package for DeMaP Miner** DEcision Making Process Miner works on OSSD Repositories. Using the DeMap Miner requires configuring the database (in MySQL) and the code (Java). 
In brief, the basic tasks in these two parts are as follows.  

A. Database <br/>
  - install the required version of the MySQL DBMS <br/>
  - restore the ‘peps_new’ database <br/>

B. Code <br/>
  - download the code from this repository.  <br/>
  - install the Eclipse IDE for the code, and <br/>
  - configure the projects in Eclipse <br/>
  

C. Generating the results. This includes the following aspects: <br/>
  - TO DO.


In detail, the steps to set up the Database and code are as follows.

**A. Database Installation and Setup**

The database is named `peps_new'. A 6GB zipfile is hosted on kaggle at this address. It contains the entire MySQL data directory 
and when unzipped is 35GB in size. <br/>
This is easiest way to recreate the MySQL database: to recover the database on another machine. <br/>
For this carry out the following steps: <br/>
1. First install the MySQL Community Server database version 5.6.25 from this link https://downloads.mysql.com/archives/community/. 
Choose the Product version 5.6.25, Microsoft Windows, and Windows (x86, 64 bit).
While installing the database, in the 'Setup Type' screen, <br/> 
   - choose Full installation, and <br/>
   - retain the default 'Installation Path' `C:\Program Files\MySQL\` , and default 'Data Path' as `C:\ProgramData\MySQL\MySQL Server 5.6\data` <br/> 
   - Install any sample or system databases that come with it. <br/>
   - ensure the `root` user password is `root` <br/>
As a guide you can follow the steps from this tutorial: `https://www.mysqltutorial.org/install-mysql/`.  <br/>
However, in this guide the installation is carried out using MySQL installer and where the MySQL software is automatically downloaded,   <br/>
while in our approach, we directly install the software from the (`https://downloads.mysql.com/archives/community/`) website,  <br/>
and therefore Step 3 is not required and wont be part of the installation steps, <br/>

2. Download the `data` folder containing the database files for the 'peps_new' database and other MySQL database from here: <br/>
`www.kaggle.com/dataset/5842ed555067749f67cb91a87244c70fab0f877091dc4618686296700e6763dd`

  Then, stop the MySQL database service. <br/>
  In the `C:\ProgramData\MySQL\MySQL Server 5.6\data` directory of your machine, rename the existing `data` folder to some other name, e.g. `dataOriginal`
  Store the downloaded folder in the MySQL data directory **thus, we replace the existing 'data' folder**. <br/>
  For instance, on my computer, this 'data' folder is in the following directory `C:\ProgramData\MySQL\MySQL Server 5.6\data`.

3. Then you have to recover this database.  First shutdown the MySQL database. 
   Then add the following lines in the `my.ini` file (on my machine, it exists in `C:\ProgramData\MySQL\MySQL Server 5.6` directory), and save the file.

   `innodb_force_recovery=1` 

   Then start the database via the Windows service and the database will be recovered. 
   
   Then stop the database, comment the above line and start the database again. 
   This will start the database in normal mode, and it will be ready for queries. 

**B. Code**

All of DeMaP Miner tool's code is within this repository. 
All the different projects within the DeMap Miner repository is needed to execute the DeMap Miner. An exception is the Rationale Miner project, which is dependent on project within the DeMap Miner. 

1. Download all the code from this repository, i.e. `https://github.com/sharmapn/DeMaPMiner` repository. <br/>
Then, create a folder named `C:\DeMapMiner` folder and transfer the code in that directory. <br/>
The DeMap Miner folder consists of; <br/>
   - the Java code for the DeMap Miner folder `DeMap Miner\workspace\deMapMiner` <br/>
   - the dependent libraries in the `lib` folder. Two libraries are larger than the 100MB Github limit and their download links are below. <br/>
   - the input and output files in the folders with the respective names <br/>
<br/>
stanford-corenlp-3.8.0-models.jar (350MB) from https://repo1.maven.org/maven2/edu/stanford/nlp/stanford-corenlp/3.8.0/ <br/>
stanford-corenlp-3.5.2-models.jar (329MB) from https://repo1.maven.org/maven2/edu/stanford/nlp/stanford-corenlp/3.5.2/ <br/>
<br/>
2. The Java code is run using the Eclipse IDE. Install the Eclipse IDE from `https://www.eclipse.org/downloads/`. A direct link to the current installer executable on that page is <br/> `https://www.eclipse.org/downloads/download.php?file=/oomph/epp/2022-06/R/eclipse-inst-jre-win64.exe`
