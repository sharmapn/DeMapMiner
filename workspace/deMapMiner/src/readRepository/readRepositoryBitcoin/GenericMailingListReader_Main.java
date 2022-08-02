package readRepository.readRepositoryBitcoin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import miner.process.ProcessingRequiredParameters;
import connections.PropertiesFile;

//April 2021. I commneted some block which read how we read from mailing lists
//              also added few lined to specify bip numbers read from qouted lines

//AFTER RUNNING THIS, the `MatchProposalTitlesToProposalNumbersBIPs.JAVA' script needs to be run 

//March 2021. we use this script to read in BItcoin data

//This is the main script to read in data ..Nov 2018
//What are the steps to read in a new repository: JEPs for example.
// create the db tables....export the peps_new datasase. into sql script, change all pep to jep, create the table using the script
//then change dbname, mailing lists to read and proposal identifier in prop file..better have  a diffrent prop file

//Before running, set BaseDir value depending on which repository we going to use, placed win which directory

public class GenericMailingListReader_Main extends GenericMailingListReader_Methods {
	
	static String tableName = "allmessages";
	//02 April 2021 ...if we want to ignore bip numbers in quoted lines
	//no need to change anyything elsewhere, this flag caters fro all, even for choosing the separate tablename to write to
	static Boolean ignoreQuotedLine = false; //normally put this as false and everything will be same as before, but true does not considers proposals from those lines where lines are quoted 
	
	public static void main(String[] args) throws IOException {	
		try {
			//read from properties file
			PropertiesFile wpf = new PropertiesFile();
			ProcessingRequiredParameters prp = new ProcessingRequiredParameters();
			prp.setScriptPurpose("reading");
	        // wpf.WriteToPropertiesFile("includeEmptyRows", includeEmptyRows.toString());
	        //includeStateData
			proposalIdentifier = wpf.readFromPropertiesFile("proposalIdentifier",false).toLowerCase();
			System.out.println("proposalIdentifier: " +proposalIdentifier);
			
			//read mailing lists to read into database table
			String mailing_lists[] = wpf.readFromPropertiesFile("mailing_list",false).split(" ");
			for (String ml: mailing_lists) {
				System.out.println("mailing_list: " +ml);
			}
			
			populateStopWordlist(); 
			
			//02 April 2021 ...if we want to ignore bip numbers in quoted lines
			// This is declared in the GenericMailingListReader_methods.java file 
			//Boolean ignoreQuotedLine = true;
			//april 2021, we store messages where proposals are not i quoted text, in another table
			if(ignoreQuotedLine)
				tableName = "allmessages_iq";
			
			
			//populate the status list from prperties file
			String proposalStatuses =  wpf.readFromPropertiesFile("proposalStatuses",false);
			//System.out.println("proposalStatuses: " +proposalStatuses);
			initStatusLists(statusList,  proposalStatuses);	
			
			//if we are starting from scratch
			//redundant();		
			
			// emptyDatabase_gmane();
		    //## temp uncomment	
			// initialise proposal numbers, in proposalNumberSearchKeyList and ignoreList
			initproposalNumberSearchKeyLists(ignoreList, proposalNumberSearchKeyList);
			//show all patterns being used to match
			for (String proposal : proposalNumberSearchKeyList) {
				//System.out.println(proposal);
			}
			
			//jan 2019
			Integer messageIDForRestart =	prp.getPd().getMaxProcessedMessageIDForProposalFromStorageTable(conn,0, prp);  //0 is dummy value //select max messageid from tabkle for that particular pep
			String mailinglistDir = "";
			
			if(messageIDForRestart==null || messageIDForRestart==0)	{
				messageIDForRestart=0; mailinglistDir= ""; 
				markFound=true; System.out.println("First Time reading");//  //important  
			}
			else {  System.out.println("Restart= true");
				mailinglistDir   =   	prp.getPd().getMaxProcessedMailingListForProposalFromStorageTable(conn,messageIDForRestart, prp);  //select max messageid from tabkle for that particular pep
				String file      = 		prp.getPd().getMaxProcessedFileForProposalFromStorageTable(conn,messageIDForRestart, prp); 
				if (mailinglistDir==null || mailinglistDir.equals("") || mailinglistDir.isEmpty() || mailinglistDir.length()==0 
						||    file==null || file.equals("") 		  || file.isEmpty()			  || file.length()==0          ) 
				{	
					mailinglistDir = "";	System.out.println("Mailing List is  empty");
					markerFile = "";		System.out.println("MarkerFile is empty");
				}
				else {  
					messageID = messageIDForRestart;  
					markerFolder= mailinglistDir; 			String lastDir = mailinglistDir.replace(baseDir,"");
					markerFile= file;
					System.out.println("Restart messageID: "+messageID + ", markerFolder: "+markerFolder + ", markerFile: "+markerFile);
				}
			}
			
			// initialise proposalTitles
			proposalTitlesSearchKeyList.clear();
			
			// map to store proposal titles and numbers, WE Have to pre-poipulate this if we want to use this feature
			Map<Integer, String> proposalDetails = populateAllproposalTitleandNumbers(new HashMap<Integer, String>(), conn);
			System.out.println("Show all proposal titles");
			for (Map.Entry<Integer, String> entry : proposalDetails.entrySet()) {				
				proposalTitlesSearchKeyList.add(entry.getValue());
			    System.out.println(entry.getKey() + "/" + entry.getValue());//show all proposal titles
			}
	//#temp uncomment		
			
			int i = 0;
			// System.out.println(searchKeyAllA.get(1));
			File statText = new File("c:/scripts/ProposalOutput" + i + ".txt");
			// output file declaration
			FileOutputStream is = new FileOutputStream(statText);			OutputStreamWriter osw = new OutputStreamWriter(is);
			Writer w = new BufferedWriter(osw);
			// DEBUG File
			File debugText = new File("c:/scripts/ProposalDebug.txt");
			// output file declaration
			FileOutputStream debug = new FileOutputStream(debugText);			OutputStreamWriter debugosw = new OutputStreamWriter(debug);
			//this function has the folders being specified and then processed
			if (test) {
				System.out.println(" ______here ");
				//get message using id
				 getMessageUsingMessageID(messageID,conn); 
			}
			if(process) {
				searchMailingLists(i, proposalDetails, w, new BufferedWriter(debugosw),mailing_lists);
			}
			w.close();	
		    // outputResults(conn);
			System.out.println("Finished processing - Total Elapsed time =" + (t7 - t1) / 1000 + " seconds");
		}
		catch (Exception e){ 
			System.out.println(" ______here  " + e.toString() + "\n" );
			//System.out.println(Thread.currentThread().getStackTrace()  );		//+  " \n" + e.printStackTrace()
			System.out.println(StackTraceToString(e)  );	
			//continue;
		} 
	}
	
	protected static void searchMailingLists(int i, Map<Integer, String> proposalDetails, Writer w, Writer wdebug, String mailing_lists[]) throws IOException {
		//searchFilesForFolder(dev_folder2, dev_folder2, foundInFile, searchKeyAll, fileCounter, w,	wdebug, conn, i, proposalTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, proposalDetails);
		
		t0 = System.currentTimeMillis();
		// search all lists, not only ideas folder as messagesd in others lists
		// too sometimes use proposal title and not proposal number
		// searchFilesForFolder(ideas_folder, ideas_folder, foundInFile,
		// searchKeyAll,fileCounter, w, wdebug, conn , i,proposalTitlesSearchKeyList,
		// ignoreList, statusList, messageID, wordsList,proposalDetails );
//		searchFilesForFolder(ideas_folder, ideas_folder, foundInFile, searchKeyAll, fileCounter, w, wdebug, conn, i,proposalTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, proposalDetails);
		t1 = System.currentTimeMillis();
//		System.out.println("Finished processing ideas folder - Elapsed time =" + (t1 - t0) / 1000 + " minutes");
//		searchFilesForFolder(dev_folder, dev_folder, foundInFile, searchKeyAll, fileCounter, w, wdebug, conn, i,proposalTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, proposalDetails);
		t2 = System.currentTimeMillis();
//		System.out.println("Finished processing dev folder - Elapsed time =" + (t2 - t1) / 1000 + " seconds");
		// searchFilesForFolder(patches_folder,patches_folder, foundInFile,
		// searchKeyAll,fileCounter, w ,wdebug, conn, i,proposalTitlesSearchKeyList,
		// ignoreList, statusList);
		// System.out.println("Finished processing patches folder");
//		searchFilesForFolder(lists_folder, lists_folder, foundInFile, searchKeyAll, fileCounter, w, wdebug, conn, i,proposalTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, proposalDetails);
		t3 = System.currentTimeMillis();
//		System.out.println("Finished processing lists folder - Elapsed time =" + (t3 - t2) / 1000 + " seconds");
		// searchFilesForFolder(bugs_list_folder,bugs_list_folder, foundInFile,
		// searchKeyAll,fileCounter, w ,wdebug, conn, i,proposalTitlesSearchKeyList,
		// ignoreList, statusList);
		// System.out.println("Finished processing bugs lists folder");
		
		//The below lists will atmost contain 100,000 messages
		
//		searchFilesForFolder(distutils_sig_folder, distutils_sig_folder, foundInFile, searchKeyAll, fileCounter, w,	wdebug, conn, i, proposalTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, proposalDetails);
		t4 = System.currentTimeMillis();
//		System.out.println(	"Finished processing distutils_sig_folder folder - Elapsed time =" + (t4 - t3) / 1000 + " seconds");
//		searchFilesForFolder(committers_folder, committers_folder, foundInFile, searchKeyAll, fileCounter, w, wdebug,	conn, i, proposalTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, proposalDetails);
		t5 = System.currentTimeMillis();
//		System.out.println("Finished processing committers folder - Elapsed time =" + (t5 - t4) / 1000 + " seconds");
//		searchFilesForFolder(announce_folder, announce_folder, foundInFile, searchKeyAll, fileCounter, w, wdebug, conn,	i, proposalTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, proposalDetails);
		t6 = System.currentTimeMillis();
//		System.out.println(	"Finished processing announce_folder list folder - Elapsed time =" + (t6 - t5) / 1000 + " seconds");
//		searchFilesForFolder(checkins_folder, checkins_folder, foundInFile, searchKeyAll, fileCounter, w, wdebug, conn,	i, proposalTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, proposalDetails);
		t7 = System.currentTimeMillis();
//		System.out.println("Finished processing checkins_folder folder - Elapsed time =" + (t7 - t6) / 1000 + " seconds");
	
		//these are also important for social network analysis
//		searchFilesForFolder(patches_folder, patches_folder, foundInFile, searchKeyAll, fileCounter, w, wdebug, conn,	i, proposalTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, proposalDetails);
//		t8 = System.currentTimeMillis();
//		System.out.println("Finished processing patches_folder folder - Elapsed time =" + (t7 - t6) / 1000 + " seconds");	
//		searchFilesForFolder(bugs_list_folder, bugs_list_folder, foundInFile, searchKeyAll, fileCounter, w, wdebug, conn,	i, proposalTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, proposalDetails);
		t8 = System.currentTimeMillis();
		for (String ml : mailing_lists) {
			File mailing_list = new File(ml);
			searchFilesForFolder(mailing_list, mailing_list, foundInFile, searchKeyAll, fileCounter, w, wdebug, conn,	i, proposalTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, proposalDetails, 
					tableName, ignoreQuotedLine);				
			t9 = System.currentTimeMillis();
			System.out.println("Finished processing "+mailing_list+" folder - Elapsed time =" + (t9 - t8) / 1000 + " seconds");
		}
	}
}
