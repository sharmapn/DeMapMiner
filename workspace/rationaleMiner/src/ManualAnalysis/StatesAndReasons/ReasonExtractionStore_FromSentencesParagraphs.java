package ManualAnalysis.StatesAndReasons;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import com.google.common.collect.ObjectArrays;

import readRepository.readRepository.ReadLabels;
import callIELibraries.JavaOllieWrapperGUIInDev;
import callIELibraries.ReVerbFindRelations;
import connections.MysqlConnect;
import de.mpii.clause.driver.ClausIEMain;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import GUI.helpers.GUIHelper;
import miner.process.LabelTriples;
import miner.process.ProcessMessageAndSentence;
import miner.process.ProcessingRequiredParameters;
import miner.process.PythonSpecificMessageProcessing;
import miner.processLabels.CheckAndGetReasons;
import miner.processLabels.ProcessLabels;
import miner.processLabels.TripleProcessingResult;
import utilities.StateAndReasonLabels;
import utilities.ParagraphSentence;
//This script extracts candidates reasons for all peps and populates a db table (tableToStore = "manualReasonExtraction")
//The table is shown as a JTable in the main GUI. 
//This table data is used as an initial starting point.

//april 2018 ... can not be integrated as part of process message smain script and also there is too much overhead with statrup libraries...
// first reason ..enire mesage vs analyse words...we use entire message for manual reason extraction and this is not part of automated process 
//as here we look at entire messge but in automated proces we look at analyse words only 
//here alos its different from automatic process as states would normally be contained in a sentence but reasons would span sentences and more

// july a lot of code has been changed because of the change to "ArrayList<ArrayList<String>>  getAllStatesList_StatesSubstates "
//and therefore may not work properly
public class ReasonExtractionStore_FromSentencesParagraphs {
		
	static ProcessingRequiredParameters prp = new ProcessingRequiredParameters();
	static ProcessMessageAndSentence pms = new ProcessMessageAndSentence();
	static ParagraphSentence ps = new ParagraphSentence();
	static PythonSpecificMessageProcessing pm = new PythonSpecificMessageProcessing();
	static Connection conn;
	static ArrayList<reasonRow> rr = new ArrayList<reasonRow>();	//contains extraction rows ... only one for each pep, should be emptied after each pep
	static boolean messageInsertedIntoTable = false;
	static String tableToStore = "manualReasonExtraction";
	
	public static void main(String[] args) {		
		connections.MysqlConnect mc = new MysqlConnect();		conn = mc.connect();		
		Statement stmt = null;
		Timestamp v_date=null;			Timestamp v_dateTimeStamp = null;
		int proposalNum, mid,v_pep=3001;		String message="", messageSubject="", authorsRole="";
		
		StateAndReasonLabels l = new StateAndReasonLabels();
		List<String>  verbs = l.getVerbs(); 	//ready for review/pronouncement
		List<String>  committedStates = l.getCommittedStates();	//main state changes  e.g. 'Status: Accepted'
//		System.out.print(" stateList >> ");  for (String line : states)     {                	System.out.print(line+ " ");            }		  System.out.println(" ");  
		ArrayList<ArrayList<String>>  getAllStatesList_StatesSubstates = l.getAllStatesList_StatesSubstates();
		ArrayList<ArrayList<String>> reasonsList = l.getReasonTerms();
		List<String>  reasonIdentifierTerms = l.getReasonIdentifierTerms(), proposalIdentifiers = l.getProposalIdentifierTerms();
		ArrayList<ArrayList<String>> specialOSCommunitySpecificTerms = l.getSpecialOSCommunitySpecificTerms();
		List<String>  entitiesList = l.getEntities();
		List<String>  subStates = l.getSubStates();
		
		//statesList = ObjectArrays.concat(statesList, decisionMechanismsSubStates, String.class); //we will combine statesList and decisionMechanismsSubStates
		//statesList.addAll(decisionMechanismsSubStates);
		//specialTerms = ObjectArrays.concat(specialTerms, entities, String.class);	//special terms and entities are combined
		//specialTerms.addAll(entities);
		boolean matchExactTerms = false;
		//terms to check for in message subject
		ArrayList<ArrayList<String>> messageSubjectCheck= new ArrayList<ArrayList<String>>();        
		messageSubjectCheck.addAll(getAllStatesList_StatesSubstates);   
		ArrayList<String> arrayListOfString = new ArrayList(reasonsList);
		messageSubjectCheck.add(arrayListOfString);
     
		//String[] messageSubjectCheck = ObjectArrays.concat(statesList, reasonsList, String.class);
		//have to think about how to add is the 'pep ready for review'  ?
		
		//state labels -- not needed
		// read triples labels to match in string matching 
		/* 
		ReadLabels b = new ReadLabels();								 
		//this in coded in ReadLabelsInDatabase- run that first				//scripts
		//String baseIdeasFilename = "C:/Users/psharma/Google Drive/PhDOtago/code/inputFiles/input-Labels.txt";	//input-BaseIdeasSept2017.txt
		ArrayList<LabelTriples> labels = new ArrayList<LabelTriples>();		
		//old way below . ...now we must already have the database table for labels populated and just read from it
		//labels = b.readLabelsFromFile(baseIdeasFilename,labels,"Triples Matching",conn, true);	//false as dont output
		String tableName ="labels";
		labels = b.getallLabelsFromTable(tableName,conn);
		*/
		
		try {
			for(int i=5000; i> 0; i--) {	//ended at 338 halfway   ...upper limit 5000
			//do for pep			
				stmt = conn.createStatement();															//and messageid = 330727, 25487, 24525   and pep < 3005
				String sql = "SELECT pep,messageid,dateTimeStamp, email,subject,  authorsRole from allmessages where pep = "+i + " order by pep asc, messageid asc, datetimestamp asc";
				// results table or postprocessed table										//5											 
				ResultSet rs = stmt.executeQuery(sql);  //date asc
				int totalRows = prp.mc.returnRSCount(sql, conn), processedCounter=0;	System.out.println("\nProposal: " + i + " Number of messages: " +totalRows);
				int arrayCounter=0, recordSetCounter=0;
				messageInsertedIntoTable = false;	//for each message id of each pep we track if the record is been inserted into table, if not=, we will insert an empty record at teh end
				while (rs.next() ) {		//&& totalRows < 1000   dont process those peps which have too many messages
					recordSetCounter++;	
					proposalNum = rs.getInt("pep");					message = rs.getString("email");	mid = rs.getInt("messageID");
					v_date = rs.getTimestamp("dateTimeStamp");		messageSubject  = rs.getString("subject");		authorsRole = rs.getString("authorsRole");
					message = message.toLowerCase(); //very important we do it here
					//System.out.format("%5d%30s%150s%150s%150s", pepNumber,label,ps,currentSentence,pp);
//					System.out.println("pep "+proposalNum+" , ("+label+"),CS: "+ currentSentence+"PS: "+ps+"NS: " +ns);
//					System.out.println("Processing mid: "+ mid);
					//check subject if it contains other PEP
					//if so, dont process teh message		--  its only abt other peps	
					
					//Handle commit messages, if message is from checkin mailing list, if it has  checkin messages we split it and process in between two checkin sections seprated by
					// diff --git a/pep-0405.txt b/pep-0405.txt
					//At the moment if the number of proposal identifiers (peps) are more than 5, the message is not processed ..we can do better than this 
					// if they are checkin messages, we can split the message by the number of lines strating by diff
					String commitDelimeters[] = {"diff --git a","diff -C2 -d","--- NEW FILE:","Modified: peps/trunk/pep-","Added: peps/trunk/","RCS file: /cvsroot/python/python/nondist/peps/"};
					if (messageSubject==null || messageSubject.isEmpty() ) {
						messageSubject= "";
					}
					//check proposalAtTheEndofMessage				
					
					if(messageSubject.contains("checkins")) {	//System.out.println("\tFolder is checkins ");						
						for (String del : commitDelimeters) {
							if(message.contains(del.toLowerCase())) {	//System.out.println("\n\tmessage contains commitDelimeter ");							
								message = message.replaceAll(del.toLowerCase(), "pankaj"+del.toLowerCase());//we add some charaters in front so that splitting wont loose the delimeter //System.out.println("\tmessage contains commitDelimeter ");						
							}
						}						
						for(String section: message.split("pankaj")){
							//Debugging...
//						System.out.println("\n\n\n\n\t START HERE sections first 50 chars: " + section.substring(0,50));		
//						System.out.println("\n\n\n\n\t START HERE sections section: " + section);	
//							if (section.startsWith(del)) {	//if first line contains current pep then go ahead and process or else dont process //System.out.println("\t commit sections ");
								int start = 0; //section.indexOf(del), 
								int end = section.indexOf(".txt");
								if(start == -1 || end == -1) { continue; }
								String line = section.substring(start,end) ;	//get everything in that line somehow				
								int option =1; //text shud only contain current proposal/pep
								boolean allowZero = false;
								if(prp.getPms().checkTextContainsProposalNumber_WithOptions(line.toLowerCase(), proposalNum,option,allowZero)) {	//check if its for the current pep, if no disregard	
									//process section 
//									System.out.println("\t Section Contains OnlyCurrent Pep Number ");// + section);
									processSectionOrMessage(matchExactTerms, messageSubject, messageSubjectCheck, committedStates, getAllStatesList_StatesSubstates, reasonsList, reasonIdentifierTerms,proposalIdentifiers,specialOSCommunitySpecificTerms, section, mid, v_date, proposalNum, authorsRole, verbs);	//System.out.print("\tProcessing done");
									orderReasonListForProposal(proposalNum);	//System.out.print("\tOrdering done");
									storePEPReasonsFromArrayListToDB(proposalNum);	//System.out.print("\tStoring done");
									System.out.println("Processing, ordering, storing Finished for mid = "+mid+" proposal: " + proposalNum + ", "+processedCounter+"/"+totalRows);
									processedCounter++;	
									rr.clear();		//clear the arraylis
								}
								else {	//System.out.println("\t ifSection Contains Only Other Pep Numbers "); 
								}						
						}//end for								
					}//end if								
					else {	//process message ... if its not a message from checkins mailing list
						if(messageSubject==null || messageSubject.length() ==0) { /*System.out.println("\tMessage Subject is null"); */ }
						else {
							//This block of code has been removed from checkin messages section above, as some checkin  message subjects dont contain the pep number we are looking for, but its the pep number does exist in the message body
							// but it is important here, to check in all other messages apart from checkin messages
							int option =0; //check if message subject contains other peps only 
							boolean allowZero = true;
							if(pms.checkTextContainsProposalNumber_WithOptions(messageSubject.toLowerCase(), proposalNum,option,allowZero)) {  
								//	|| messageSubject.toLowerCase().contains("python-patches")) {	//wasnt not sure why we dont consider bugs list messages
								System.out.println("\tMessage Subject contains only other PEPs b, we skip. MID: "+ mid + " Message Subject: " + messageSubject);
								continue;	//skip current message and process the next one
							} //else {System.out.println("\tMessage Subject contains current PEP"); }

						}	//System.out.println("\tMess");
						
						processSectionOrMessage(matchExactTerms, messageSubject, messageSubjectCheck, committedStates, getAllStatesList_StatesSubstates, reasonsList, reasonIdentifierTerms,proposalIdentifiers,specialOSCommunitySpecificTerms, message, mid, v_date, proposalNum, authorsRole, verbs); //System.out.print("\tProcessing done");					
						orderReasonListForProposal(proposalNum);	//System.out.print("\tOrdering done");
						storePEPReasonsFromArrayListToDB(proposalNum);	//System.out.print("\tStoring done");
						System.out.println("Processing, ordering, storing Finished for mid = "+mid+" proposal: " + proposalNum + ", "+processedCounter+"/"+totalRows);
						processedCounter++;	
						rr.clear();		//clear the arraylist
					}
					if(messageInsertedIntoTable == false) {
						//we insert an empty record into table as we would need to scroll this message as well 
						//if we want to look for reasons for which terms have not been identified and therefore not captured
						PreparedStatement pstmt = null;
					    try {
					      String query = "insert into "+tableToStore+"(proposal, dateTimeStamp, messageID, sentence,termsMatched,level,reason, author) values(?,?,?,?,?,?,?,?)";
					      pstmt = conn.prepareStatement(query); // create a statement
					      pstmt.setInt(1,proposalNum);  pstmt.setTimestamp(2, v_date);      			pstmt.setInt(3, mid); // set input parameter 2  pstmt.setDate(2, (Date) r.getDateVal());  
					      pstmt.setString(4, "");    pstmt.setString(5, "" ); 	pstmt.setString(6, ""); // set input parameter 3
					      pstmt.setBoolean(7, false); pstmt.setString(8, authorsRole );
					      pstmt.executeUpdate(); // execute insert statement	
					      messageInsertedIntoTable = true;
		//$$			      System.out.println("\t inserted row in db  combination: "+r.getCombination() + " sentence " + r.getSentenceString());
					    } catch (Exception e) {
					      e.printStackTrace();
					    }
					}
				}	//end while
				System.out.println("Finished for proposal: " + i );
			  //end for loop
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void storePEPReasonsFromArrayListToDB(int proposalNum) {		
		for(reasonRow r: rr) {
			if(r.getProposal()==proposalNum) {
				PreparedStatement pstmt = null;
			    try {
			      String query = "insert into "+tableToStore+"(proposal, dateTimeStamp, messageID, sentence,termsMatched,level,reason, author) values(?,?,?,?,?,?,?,?)";
			      pstmt = conn.prepareStatement(query); // create a statement
			      pstmt.setInt(1,r.getProposal());  pstmt.setTimestamp(2, r.getDateVal());      			pstmt.setInt(3, r.getMid()); // set input parameter 2  pstmt.setDate(2, (Date) r.getDateVal());  
			      pstmt.setString(4, r.getSentenceString());    pstmt.setString(5, r.getCombination() ); 	pstmt.setString(6, r.getLocation()); // set input parameter 3
			      pstmt.setBoolean(7, r.getReason()); pstmt.setString(8, r.getAuthorsRole() );
			      pstmt.executeUpdate(); // execute insert statement	
			      messageInsertedIntoTable = true;
//$$			      System.out.println("\t inserted row in db  combination: "+r.getCombination() + " sentence " + r.getSentenceString());
			    } catch (Exception e) {
			      e.printStackTrace();
			    }
			}
		}
		//delete everytghing from arraylist
	}	
	
	/*
	 * XXX Check for these states in entire  message, no need to check in the message subject as highly unlikey
	String[] states = {"Status: Draft"...};
	
	//TERMS
    String[] identifiers = {"pep","proposal","idea"};
    //verbs associated with states and decisionMechanismsSubStates
    String[] verbs = {"ready"};  //ready for review/pronouncement
    //states
	String[] mainStates =  {"accept","approve","reject",...};
  	//additional states we have found
    String[] decisionMechanismsSubStates =  {"poll","poll result","vote","vote result","voted","voting","survey","survey results","review","pronounce","pronouncement","request number","implement","resolution", "notice"}; //added reviewing as a state???
   
    //REASONS all below is on HOW pep was decided..reasons do exist on its own
  	String reasons[] = {rough consensus","community consensus"...				
  			};  
    //REASON IDENTIFIER TERMS
    String reasonIdentifierTerms[] = {"because","since", "based on","due to","thanks to","accordingly","as a result of","through"};
    
    //ENTITIES AND special terms used together  	
  	//Entities all below is WHO decided
    String[] entities = {"pep dictator","pep champion","bdfl delegate committee","committers vote core developers","czar","pep czar","ppe-bdfl","pep editor","bpd","bpc","flufl","committee decision","committee decisions","dictatorship","dictator","delegate"}; 	
    //Special terms .. additional 'points in time' which are important we have found
    String[] specialTerms = {"+pronouncement","quick pronouncement","+pep acceptance:","pep rejection","+pep deferral","+acceptance","+resolution:","rejection notice","bdfl pronouncement","+pep deferral","+rejection"};	//"resolution",
	//standard states changes by users i want to capture 
	//"draft" ,	dont need draft as its always by author
    
   	
	//This is what we want as all types of states
	public String[] getStatesList() {				return ObjectArrays.concat(mainStates, decisionMechanismsSubStates, String.class);		//return statesList;	}
	//This is what we want as all types of reasons
	public String[] getReasonsList() {				return ObjectArrays.concat(entities, reasons, String.class);		//return reasonsList;	}
	//reason identifier terms	public String[] getReasonIdentifierTerms() {		return reasonIdentifierTerms;	}
	
	*/

	//Main check are done here
	public static void processSectionOrMessage(boolean matchExactTerms, String messageSubject, ArrayList<ArrayList<String>> messageSubjectCheck, List<String>  committedStates,
			ArrayList<ArrayList<String>>  getAllStatesList_StatesSubstates, 
			ArrayList<ArrayList<String>>  reasonsList, List<String>  reasonIdentifierTerms, List<String>  identifiers,ArrayList<ArrayList<String>> specialOSCommunitySpecificTerms, 
			String messageOrSection, int mid,Timestamp dateVal, int proposalNum, String author, List<String>  verbs) {
		String[] paragraphs;		String location;
		
		//remove the plus signs..which make the entire lines part of the same sentence
		//+    way to chain exceptions explicitly.  This PEP addresses both.
		//+
		//+    Several attribute names for chained exceptions have been suggested
				
		//check message subject ..added june 2018
		/* for (String s: messageSubjectCheck) {	
			if(messageSubject.contains(s.toLowerCase())) {		//.toLowerCase()
					//model.addRow(new Object[]{dateVal, mid,"State Change",s, s,"Single line"});
					reasonRow r = new reasonRow();
					r.setData(proposalNum, mid, dateVal, messageSubject, "Message Subject", s, false, author);  //last 3 parameters are sentence, location and combination
					rr.add(r);					r=null;
					System.out.println("added Term: "+s + " from: "+messageSubject);
			}
		}	//end for loop for each state
		*/
		for (ArrayList<String> line : messageSubjectCheck) { 
    		for (String term : line) {
    			if(messageSubject.contains(term.toLowerCase())) {		//.toLowerCase()
					//model.addRow(new Object[]{dateVal, mid,"State Change",s, s,"Single line"});
					reasonRow r = new reasonRow();
					r.setData(proposalNum, mid, dateVal, messageSubject, "Message Subject", term, false, author);  //last 3 parameters are sentence, location and combination
					rr.add(r);					r=null;
					System.out.println("added Term: "+term + " from: "+messageSubject);
    			}
    		}
    	}
		
		//START messageOrSection LEVEL 
//		System.out.println("messageOrSection "+messageOrSection);
		for (String s: committedStates) {	
			if(messageOrSection.contains(s.toLowerCase())) {		//.toLowerCase()
					//model.addRow(new Object[]{dateVal, mid,"State Change",s, s,"Single line"});
					reasonRow r = new reasonRow();
					r.setData(proposalNum, mid, dateVal, "Entire messageOrSection", "Entire messageOrSection", s, false, author);  //last 3 parameters are sentence, location and combination
					rr.add(r);					r=null;
					System.out.println("added Status "+s);
			}
		}	//end for loop for each state
		
		for (ArrayList<String> ab: specialOSCommunitySpecificTerms) {	
			for (String st: ab) {
				if(messageOrSection.contains(st.toLowerCase())) {		//.toLowerCase()
	//					arrayCounter++;
						//model.addRow(new Object[]{dateVal, mid,"State Change",st, st,"Special Terms"});					
					    reasonRow r = new reasonRow(); 	r.setData(proposalNum, mid, dateVal, "Entire messageOrSection", "Entire messageOrSection", st, false, author); 		rr.add(r);					r=null;
						//System.out.println("added row in db");
					    System.out.println("added specialTerms "+st);
				}
			}
		}
		//END messageOrSection LEVEL
		
		//FINALLY DECIDED WE JUST STRIP THESE CHARACTERS OFF..BEST SOLUTION. We check for states and special terms before as they contain plus (+) signs which are removed here or else they wont be captured
		//this is only for manual reason extraction and wont be part of automated process as here we look at entire messge but in automated proces we look at analyse words only 
		messageOrSection = messageOrSection.replaceAll("\r?\n\\+","\r\n");		messageOrSection = messageOrSection.replaceAll("\r?\n\\-","\r\n");
		messageOrSection = messageOrSection.replaceAll("\r?\n>","\r\n");		messageOrSection = messageOrSection.replaceAll("\r?\n >","\r\n");
		messageOrSection = messageOrSection.replaceAll("\r?\n>>","\r\n");		messageOrSection = messageOrSection.replaceAll("\r?\n> >","\r\n");	messageOrSection = messageOrSection.replaceAll("\r?\n> > >","\r\n");
		messageOrSection = messageOrSection.replaceAll("\r?\n ","\r\n");
		
		//START PARAGRAPH LEVEL
		paragraphs = messageOrSection.split("\\r?\\n\\r?\\n"); 	int paragraphCounter = 0;	//"\r?\n\r?\n");
		Boolean foundInLastParagraph =false;

		for (String para: paragraphs) 
		{ 
			if (paragraphCounter >= 500)	//som,e messages like 328159 , have code and therefore about 10,000 paragraphs
				break;
			//System.out.println("mid "+mid+", processing paragraph "+paragraphCounter+" in recordCounter "+ recordSetCounter); 
			List<String> listofTermsMatched = new ArrayList<String>();
			Reader reader = new StringReader(para);
			DocumentPreprocessor dp = new DocumentPreprocessor(reader);
			
			//after we have processed each sentence in the paragraph, we check against the entire paragraph for terms spanning across
			//but at the same time dont include theose terms found together in the same sentence
			for (List<HasWord> eachSentence : dp) {		
				String CurrentSentenceString = Sentence.listToString(eachSentence);											
				//remove unnecessary words like "Python Update ..."
				CurrentSentenceString = pm.removeUnwantedText(CurrentSentenceString);
				CurrentSentenceString = pm.removeLRBAndRRB(CurrentSentenceString);
				String sentenceString = Sentence.listToString(eachSentence);

				//if we want to check in nearby paragraphs , previous, curr, and next

				//check current sentence for all combinations
				location= "sentence";
				//first we should check if sentence contains only that pep....and make sure not to include pep zero here...pep zero only in message subject
				int option =0; //check if text contains other peps only 
				boolean allowZero = false,ifSentenceContainsOnlyOtherPepNumbers = false;					
				ifSentenceContainsOnlyOtherPepNumbers = prp.getPms().checkTextContainsProposalNumber_WithOptions(sentenceString.toLowerCase(), proposalNum,option,allowZero);
				
				if(ifSentenceContainsOnlyOtherPepNumbers) {
					continue; //skip tghe sentence
				}
				//We dont want to repeat what has been found in sentence, in the paragraph again 
				//so for each sentence we add the terms found to list 
				listofTermsMatched= checkStringForAllCombinationsOf_StateProposalReasonAndReasonIdentifier(paragraphCounter,listofTermsMatched,matchExactTerms,	getAllStatesList_StatesSubstates, reasonsList, reasonIdentifierTerms, identifiers, mid,proposalNum, dateVal,sentenceString,location, author, verbs);
				
			}
			//check current paragraph for all combinations
			//maintain a list of terms matched within the sentences of paragraph and then dont add them if found in paragraph
			location= "paragraph";
			checkStringForAllCombinationsOf_StateProposalReasonAndReasonIdentifier(paragraphCounter,listofTermsMatched, matchExactTerms,	getAllStatesList_StatesSubstates, reasonsList, reasonIdentifierTerms, identifiers, mid,proposalNum, dateVal,	 para,location, author, verbs);

			//have to code adjacent paragraphs

			paragraphCounter++;						
			listofTermsMatched.clear(); //clear list for each paragraph
		}		
	}

	public static List<String> checkStringForAllCombinationsOf_StateProposalReasonAndReasonIdentifier(int paragraphCounter,List<String> listofTermsMatched, boolean matchExactTerms,
			ArrayList<ArrayList<String>>  statesList, ArrayList<ArrayList<String>> reasonsList, 
			List<String>  reasonIdentifierTerms, List<String> identifiers, int mid,int proposal, Timestamp dateVal,	String sentenceorParagrapghString, String location, String author,
			List<String> verbs) {
		String combination = "",finalCombination="";
		
		//too many results rows per paragraph sometimes, so we will show all terms combinations matched in one row
		reasonRow r1 = new reasonRow(); 		
		
		//some reasons exist on its own ina  sentence without any combination
		for (ArrayList<String> t : reasonsList ) {
			for (String r : t) {
				//check state,identifier, and reasons only
				if(r.split(" ").length ==1) { continue;}	//dont capture single words reasons here as too many false positives ..but they are in the list because they will be used in combination
				if (ps.containsSVO(r, r,r,sentenceorParagrapghString, matchExactTerms)) {	//subject+" "+verb+" "+object
					combination = r;						
					if(location.equals("paragraph") && listofTermsMatched.contains(combination))
					{}
					else {
						finalCombination= finalCombination + " " + combination; 
						//insertReasonTermsFoundRow(proposal,mid, dateVal, sentenceString, location, combination);
	//					reasonRow r1 = new reasonRow(); 	r1.setData(proposal, mid, dateVal, sentenceorParagrapghString, location, combination); 		rr.add(r1);	r1=null;
						listofTermsMatched.add(combination);
	//					System.out.println("\t mid "+mid+" new terms [" + t+ " "+ i + " " + r + "] found in "+location+". Paragraph "+paragraphCounter); 
					}					
				}
			}
		}
		
		//check identifier and reasons ..for cases...'is pep in consensus yet?'
		for (String i : identifiers ) {
			for (ArrayList<String> k : reasonsList) {
				for (String r :  k) {
					//check state,identifier, and reasons only
					if (ps.containsSVO(i, i,r,sentenceorParagrapghString, matchExactTerms)) {	//subject+" "+verb+" "+object
						combination = i+ " " + r;						
						if(location.equals("paragraph") && listofTermsMatched.contains(combination))
						{}
						else {
							finalCombination= finalCombination + " " + combination; 
							//insertReasonTermsFoundRow(proposal,mid, dateVal, sentenceString, location, combination);
	//						reasonRow r1 = new reasonRow(); 	r1.setData(proposal, mid, dateVal, sentenceorParagrapghString, location, combination); 		rr.add(r1);	r1=null;
							listofTermsMatched.add(combination);
	//						System.out.println("\t mid "+mid+" new terms [" + t+ " "+ i + " " + r + "] found in "+location+". Paragraph "+paragraphCounter); 
						}					
					}
				}
			}
		}
		
		//Check states,proposal, reasons and reason identifiers in Current Sentence
		//here alos its different from automatic process as states would normally be contained in a sentence but reasons would span sentences and more
		for (ArrayList<String> line : statesList ) {
			//check for verbs 'ready' for review/pronouncement in sentencess
			for (String t : line) {   //for each term in line
				
				for (String v : verbs ) {
					if (ps.containsSVO(t, v,v,sentenceorParagrapghString, matchExactTerms)) {	//subject+" "+verb+" "+object
						combination = t+ " " + v; // + " " + r;
						if(location.equals("paragraph") && listofTermsMatched.contains(combination))
						{}
						else {
							finalCombination= finalCombination + " " + combination;  //append the combination						
							listofTermsMatched.add(combination);
	//						System.out.println("\t mid "+mid+" new terms [" + t+ " " + i + " " + "] found in "+location+". Paragraph "+paragraphCounter); 
						}
					}
				}
				
				
				//check for state and identifiers [pep, proposal, idea]
				for (String i : identifiers ) {
					if (ps.containsSVO(t, i,i,sentenceorParagrapghString, matchExactTerms)) {	//subject+" "+verb+" "+object
						combination = t+ " " + i; // + " " + r;
						if(location.equals("paragraph") && listofTermsMatched.contains(combination))
						{}
						else {
							finalCombination= finalCombination + " " + combination;  //append the combination
							//insertReasonTermsFoundRow(proposal,mid, dateVal, sentenceString, location, combination);
	//						reasonRow r = new reasonRow(); 	r.setData(proposal, mid, dateVal, sentenceorParagrapghString, location, combination); 		rr.add(r);		r=null;
							listofTermsMatched.add(combination);
	//						System.out.println("\t mid "+mid+" new terms [" + t+ " " + i + " " + "] found in "+location+". Paragraph "+paragraphCounter); 
						}
						
					}
					for (ArrayList<String> g : reasonsList ) {
						for (String r : g) {
							//check state,identifier, and reasons only
							if (ps.containsSVO(t, i,r,sentenceorParagrapghString, matchExactTerms)) {	//subject+" "+verb+" "+object
								combination = t+ " "+i+ " " + r;						
								if(location.equals("paragraph") && listofTermsMatched.contains(combination))
								{}
								else {
									finalCombination= finalCombination + " " + combination; 
									//insertReasonTermsFoundRow(proposal,mid, dateVal, sentenceString, location, combination);
		//							reasonRow r1 = new reasonRow(); 	r1.setData(proposal, mid, dateVal, sentenceorParagrapghString, location, combination); 		rr.add(r1);	r1=null;
									listofTermsMatched.add(combination);
		//							System.out.println("\t mid "+mid+" new terms [" + t+ " "+ i + " " + r + "] found in "+location+". Paragraph "+paragraphCounter); 
								}
								
							}
						
							//Check states, identifier, reasons and reason identifiers in Current Sentence
							for (String rit : reasonIdentifierTerms ) {
								if (ps.containsSVO(t, i+ " " +r,rit,sentenceorParagrapghString, matchExactTerms)) {	//subject+" "+verb+" "+object
									combination = t+ " " +i + " "+ rit + " " + r;							
									//model.addRow(new Object[]{dateVal, mid,sentenceString,combination,location});	setBackground(java.awt.Color.pink);
									//model.addRow(new Object[]{dateVal, mid,sentenceString,combination,location});	setBackground(java.awt.Color.pink);
									
									if(location.equals("paragraph") && listofTermsMatched.contains(combination))
									{}
									else {
										finalCombination= finalCombination + " " + combination; 
										//insertReasonTermsFoundRow(proposal,mid, dateVal, sentenceString, location, combination);
		//								reasonRow r2 = new reasonRow(); 	r2.setData(proposal, mid, dateVal, sentenceorParagrapghString, location, combination); 		rr.add(r2);	r2=null;
										listofTermsMatched.add(combination);
		//								System.out.println("\t mid "+mid+" new terms [" + t+ " " + rit + " " + r + "] found in "+location+". Paragraph "+paragraphCounter); 
									}							
								}
							}
						}
					}
				}
				//dont check for state and identifiers [pep, proposal, idea]
				for (ArrayList<String> k : reasonsList) {
					for (String r : k ) {
						//check state, and reasons only
						if (ps.containsSVO(t, r,r,sentenceorParagrapghString, matchExactTerms)) {	//subject+" "+verb+" "+object
							combination = t+ " " + r;					
							//model.addRow(new Object[]{dateVal, mid,sentenceString,combination,location});					
							if(location.equals("paragraph") && listofTermsMatched.contains(combination))
							{}
							else {
								finalCombination= finalCombination + " " + combination; 
								//insertReasonTermsFoundRow(proposal,mid, dateVal, sentenceString, location, combination);
		//						reasonRow r3 = new reasonRow(); 	r3.setData(proposal, mid, dateVal, sentenceorParagrapghString, location, combination); 		rr.add(r3);					r3=null;
								listofTermsMatched.add(combination);
		//						System.out.println("\t mid "+mid+" new terms [" + t+ " " + r + "] found in "+location+". Paragraph "+paragraphCounter); 
							}					
						}
						//Check states, reasons and reason identifiers in Current Sentence
						for (String rit : reasonIdentifierTerms ) {
							if (ps.containsSVO(t, r,rit,sentenceorParagrapghString, matchExactTerms)) {	//subject+" "+verb+" "+object
								combination = t+ " " + rit + " " + r;						
								//model.addRow(new Object[]{dateVal, mid,sentenceString,combination,location});						
								if(location.equals("paragraph") && listofTermsMatched.contains(combination))
								{}
								else {
									finalCombination= finalCombination + " " + combination; 
									//insertReasonTermsFoundRow(proposal,mid, dateVal, sentenceString, location, combination);
		//							reasonRow r4 = new reasonRow(); 	r4.setData(proposal, mid, dateVal, sentenceorParagrapghString, location, combination); 		rr.add(r4);					r4=null;
									listofTermsMatched.add(combination);
		//							System.out.println("\t mid "+mid+" new terms [" + t+ " " + rit + " " + r + "] found in "+location+". Paragraph "+paragraphCounter); 
								}						
							}
						}
					}
			}
		}
		//for inserting one result row per paragraph
		//remove duplicates from string -- 
		if(finalCombination.length() >0) {
			finalCombination=  deDup(finalCombination);  //removeDuplicates(finalCombination, " ");
			System.out.println("\t mid "+mid+" new terms [" + finalCombination + "] found in "+location+". Paragraph "+paragraphCounter); 
			r1.setData(proposal, mid, dateVal, sentenceorParagrapghString, location, finalCombination, false, author);	//System.out.println("here b");
			rr.add(r1);
		}
		r1=null;	finalCombination="";
		
		}
		return listofTermsMatched;
	}
	
	public static String deDup(String s) {
	    return new LinkedHashSet<String>(Arrays.asList(s.split(" "))).toString().replaceAll("(^\\[|\\]$)", "").replace(", ", " ");

	}

	public static String removeDuplicates(String txt, String splitterRegex)
	{
	    List<String> values = new ArrayList<String>();
	    String[] splitted = txt.split(splitterRegex);
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < splitted.length; ++i) {
	        if (!values.contains(splitted[i]))  {
	            values.add(splitted[i]);
	            sb.append('-');
	            sb.append(splitted[i]);
	        }
	    }
	    return sb.substring(1);
	}

	public static void insertReasonTermsFoundRow(int proposalNum, int mid, Timestamp dateVal, String sentenceorParagrapghString, String location,
			String combination, String v_author) {
		PreparedStatement pstmt = null;
		try {
		  String query = "insert into "+tableToStore+"(proposal, dateTimeStamp, messageID, sentence,termsmatched,combination,level, author) values(?,?, ?,?,?,?,?,?)";
		  pstmt = conn.prepareStatement(query); // create a statement
		  pstmt.setInt(1,proposalNum);  	pstmt.setTimestamp(2, dateVal);       			pstmt.setInt(3, mid); // set input parameter 2
		  pstmt.setString(4, sentenceorParagrapghString);       pstmt.setString(5,  "State Change"); // set input parameter 1
		  pstmt.setString(6, combination); 	      			pstmt.setString(7, location); pstmt.setString(8, v_author);  // set input parameter 3
		  pstmt.executeUpdate(); // execute insert statement
		} catch (Exception e) {
		  e.printStackTrace();
		}
		//System.out.println("added row in db");
	}
	
	//example 
		//mid 330727 new terms [draft support]		 found in paragraph. Paragraph 757
		//mid 330727 new terms [draft since support] found in paragraph. Paragraph 757
	public static void orderReasonListForProposal(int proposalNum){
  		Boolean repeatedRow = false,found = true;  		
  		try {
	  		if(!rr.isEmpty()){
	  			int counter =0;
	  			for (int x=0; x <rr.size(); x++){	
		  			for (int y=0; y <rr.size(); y++){					        	
		  				if(x !=y && (rr.get(x).getProposal() == proposalNum && rr.get(y).getProposal()== proposalNum) && //for same proposal
		  				(rr.get(x).getMid() == rr.get(y).getMid())  									//make sure they refer to same mid 
		  				&&  (rr.get(x).getSentenceString() == rr.get(y).getSentenceString())			//make sure they refer to same (sentence or paragraph)
		  				&&  rr.get(y).getCombination() != null && !rr.get(y).getCombination().isEmpty())
		  				{	//System.out.println("looop");
		  					int xmid = rr.get(x).getMid();	int ymid = rr.get(y).getMid();
		  					String[] xTerms = rr.get(x).getCombination().trim().toLowerCase().split(" ");
		  					String[] yTerms = rr.get(y).getCombination().trim().toLowerCase().split(" ");	
		  					//yterms contains x terms and y no of terms > no of x terms 
		  					if( Arrays.asList(yTerms).containsAll(Arrays.asList(xTerms)) && yTerms.length >= xTerms.length )   	        				
			  	        	{
		  						repeatedRow= true;		  					found=true;	
			  					//mark for deletion by adding to array
		  						StringBuilder xbuilder = new StringBuilder(); 	StringBuilder ybuilder = new StringBuilder(); 
		  						for(String s : xTerms) {
		  						    xbuilder.append(s+" ");
		  						}
		  						String xstr = xbuilder.toString();
		  						for(String s : yTerms) {
		  						    ybuilder.append(s+ " ");
		  						}  						
		  						String ystr = ybuilder.toString();
		  						xstr = xstr.trim(); 	ystr = ystr.trim();
			  					System.out.println("sentence/paragraph matched-- removing from list mid: "+ xmid + " x terms: "+  xstr + " yTerms: " + ystr );
			  					rr.remove(x);		  					
			  					if (x==0){  }
			  					else {x--;}
			  				}
		  					//closed pep over close pep	  					
		  					//first make sure number of terms is the same, and atleast one term is common 
		  					ArrayList<String> xTermsA = new ArrayList<>(Arrays.asList(xTerms));
		  					ArrayList<String> yTermsB = new ArrayList<>(Arrays.asList(yTerms));
		  					ArrayList<String> yTermsBTemp = new ArrayList<>(Arrays.asList(yTerms));
	//	  					System.out.println("############ here before remove xa " + xTermsA); 						System.out.println("############ here before remove yb " + yTermsB);
		  					//eliminate similar terms
	  						yTermsB.removeAll(xTermsA);	xTermsA.removeAll(yTermsBTemp);
	 // 						System.out.println("############ here xa " + xTermsA);   						System.out.println("############ here yb " + yTermsB);
	  						//make sure only one term is left
		  					if(yTermsB.size()==1 && xTermsA.size()==1 && yTerms.length == xTerms.length) {	//xTermsA.contains(yTermsB)
		  							if(yTermsB.get(0).contains(xTermsA.get(0)) ){		//compare terms
		  								rr.remove(x);		System.out.println("\t\tstem term removed = x." + " 	x term: "+  xTermsA.get(0) + " yTerm: " + yTermsB.get(0) );	  					
		  			  					if (x==0){  }
		  			  					else {x--;}
		  							} 
		  							else if (xTermsA.get(0).contains(yTermsB.get(0)) ) {
		  								rr.remove(y);		System.out.println("\t\tstem term removed = y." + " 	x term: "+  xTermsA.get(0) + " yTerm: " + yTermsB.get(0) );  					
		  			  					if (y==0){  }
		  			  					else {y--;}
		  							}
		  					}
		  				}
		  			}
	  			}
	  	    }
  		}
  		catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(StackTraceToString(ex)  );	
			//System.out.println(m.getMessage_ID() + " ______here  " + e.toString() + "\n" );
		}
  		//return rr;
  	}

	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}
}