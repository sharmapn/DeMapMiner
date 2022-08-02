package TopicModellingPEPsJEPs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import connections.MysqlConnect;
import stanfordParser.StanfordNLPTools;
import stanfordParser.pair;

public class ExtractTopicModellingSentences {
	//Main script to extract sentences for Topic Modelling
	//SentenceExtractionForTopicModelling
	//To get ground truth, we extract data from results table (for substates) and trainingdata table (for reasons) 
	//JAN 2019, Refined approach. We use the sentences extracted in the Relation Extraction section
	// we select from database table rather than file
	// we select only sentences from first and last paragraph and 
	//as a first attempt look at those sentences which contain the term 'jep'
	
	//we write to a table here and then have another script to select from allsenetnces table
	//where we do the processing,  included in this section, like removing punctuation
	static Connection conn;
	//static ProcessingRequiredParameters prp = new ProcessingRequiredParameters();
	static File file = new File ("D:\\DeMap_Miner\\datafiles\\outputFiles\\TMOutFile.txt");
	static PrintWriter pwForTopicModellingSentences = null;
	static Statement stmt,stmt0,stmt1;
	static CodesInText cd = new CodesInText();
	static ArrayList<String> dictList = new ArrayList<String>();
	
	static boolean establishGroundTruth=true, firstStage = false, secondStage=false, thirdStage=false, fourthStage=false;
	//This  script is memory intensive so has been divided into steps
	//1. read sentences and write to database table -- //store in sentences column
	//2. Lemmatize and extract verbs and nouns, adds another column to the table
	//3. 
	
	public static void main(String args[]) throws Exception { 
		try {	
			//pwForTopicModellingSentences.println ("hello");
			connections.MysqlConnect mc = new MysqlConnect();		conn = mc.connect();
								
			//feb 2019, after TM for all peps and all sentences within it, the results are not clear because of so many code terms
			//so we try make some sense, by selecting those sentences which have term 'pep' mentioned - 
			//this is not representative pf all the opics but can be a starting point 
			//prp.setProposalIdentifier("pep");
			ArrayList<Integer> UniquePeps = returnUniqueProposalsInDatabase("pep");
			ResultSet rs, rs0, rs1;
			int totalRows, arrayCounter=0, recordSetCounter=0, messageid,proposal,sentenceCounterForTopicModelling = 0,paragraphCounter=0,totalRows0;
			String label="",CurrentSentenceString="",effectSentence="";
			//String sentenceTermsForCheckingDots[]= {};
			List<String> sentenceTermsForCheckingDots = new ArrayList<String>();
			String lemmatisedSentence = "", sql0="",sql1="";
			ArrayList<String> lemmatisedSentenceList = new ArrayList<String>();
			Integer index=0,processedCounter=0;
			
			////Mar 2019 - we create topic model of ground truth 
			if(establishGroundTruth) //store in sentences column
			{
				for(Integer u : UniquePeps) {	
//$$				if(!u.equals(272)) {	continue;	}
					//else {}
					//System.out.println("Proposal : " + u);
					//FIRST WE GET THE INITIAL SENTENCE
										
					//March 2019, to establish ground truth we extract sentences from the results_post_processed table for substates
					//			  to establish ground truth we extract sentences from the results_post_processed table for reasons
					String sql = "SELECT pep, messageid,currentsentence as sentence from results_postprocessed "
							+ " where  pep = "+u+"  "
							//+ " sentence like '% pep %' OR '% proposal %' '% idea %' "   //we don't consider instances like 'pep308.txt' therefore we look for where sentence contains term 'pep' on its own
//							+ " sentence like '% pep %' and isenglishorcode = 1 "
//							+ " and (lastdir like '%dev%' or lastdir like '%idea%') "
//							+ " order by pep asc, messageid asc" //, datetimestamp asc";
							+ " UNION "
							+ " SELECT pep, messageid, causesentence as sentence from trainingdata "
							+ " where  pep = "+u+"  ";
							//+ " sentence like '% pep %' OR '% proposal %' '% idea %' "   //we don't consider instances like 'pep308.txt' therefore we look for where sentence contains term 'pep' on its own
		//					+ " and (lastdir like '%dev%' or lastdir like '%idea%') "
//							+ " order by pep asc, messageid asc"; //, datetimestamp asc";
					
					// Feb2019, we consider allterms in all sentences in the paragraph of the sentence 
					// we have no data in the allparagraphs table, so we contruct it based on numbers in the allsentences table
					// we get all sentences which belong to same messageid (distinct - consider once) and same paragraph counter
					
					// results table or postprocessed table
					stmt = conn.createStatement();	rs = stmt.executeQuery(sql);  //date asc
					totalRows = mc.returnRSCount(sql, conn); processedCounter=0;	
					System.out.println("Proposal: " + u + " First Layer - Number of Sentences: " +totalRows);
					
					arrayCounter=0; recordSetCounter=0;		sentenceCounterForTopicModelling = 0; paragraphCounter=0;
					label=""; CurrentSentenceString=""; effectSentence="";	lemmatisedSentence = "";
					//sentenceTermsForCheckingDots[]= {};
					//ArrayList<String> lemmatisedSentenceList = new ArrayList<String>();
					sql0="";	index=0;	
					//System.out.println("Proposal : " + proposal + " causeSentence: " + causeSentence);
					
					while (rs.next()) {// && totalRows < 1000) {		//dont process those peps which have too many messages				
						messageid = rs.getInt("messageid"); proposal = rs.getInt("pep"); 		
//						paragraphCounter= rs.getInt("paragraphCounter");
						CurrentSentenceString = rs.getString("sentence");
						//System.out.println("\t\tEffectSentence : " + effectSentence);
						//	populateCauseEffectCleaned(id,causeSentence,effectSentence);	//clean
						lemmatisedSentence = null;
						// Feb2019, we consider allterms in all sentences in the paragraph of the sentence 
						// we have no data in the allparagraphs table, so we contruct it based on numbers in the allsentences table
						// SECOND Swe get all sentences which belong to same messageid (distinct - consider once) and same paragraph counter
						/*
						sql0 = "SELECT DISTINCT sentence from allsentences "  //distinct so that the middle paragrapgh does not get selected twice if all three paragraphs contain the key term (since 5 paragrapghs would be selected)
								+ " where proposal = "+proposal+" and messageid = "+messageid+" "
								+ " and (paragraphCounter BETWEEN "+ (paragraphCounter-1)  + " AND "+ (paragraphCounter+1)  + ");";
						stmt0 = conn.createStatement();
						rs0 = stmt0.executeQuery(sql0);  //date asc
						totalRows0 = mc.returnRSCount(sql0, conn);
						//System.out.print("Second Layer - Number of Sentences: " +totalRows0);
						while (rs0.next()) {
							recordSetCounter++;
							CurrentSentenceString = rs0.getString("sentence"); */
							//remove all terms within sentence with dots - mostly links
							try {	//HERE WE WANT TO DO WHAT WE WANT TO DO
								CurrentSentenceString = CurrentSentenceString.replaceAll("[^a-zA-Z ]", "").toLowerCase();	//remove all punctuation
								CurrentSentenceString = CurrentSentenceString.replaceAll("\\s+", " ").trim(); //remove double spaces and trim	
								//sentenceTermsForCheckingDots = CurrentSentenceString.split(" ");
								//sentenceTermsForCheckingDots = Arrays.asList(CurrentSentenceString.split(" "));
								String query = " insert into topicModellingSentences (proposal, messageid, sentence) values (?, ?, ?)";
								      // create the mysql insert preparedstatement
								      PreparedStatement preparedStmt = conn.prepareStatement(query);
								      preparedStmt.setInt (1, proposal);      preparedStmt.setInt (2, messageid);	      preparedStmt.setString   (3, CurrentSentenceString);							      
								      preparedStmt.execute();								      preparedStmt.close();
							}  //end try       
							catch (Exception e){ 
								System.out.println(messageid + " error inserting record______here 2311  " + e.toString() + "\n" ); //System.out.println(Thread.currentThread().getStackTrace()  );		//+  " \n" + e.printStackTrace()
								System.out.println(StackTraceToString(e)  );									//continue;
							} 
						/*}
						stmt0.close();
						*/
					}
					stmt.close();
				}				
			} //end first stage
			
			
			if(firstStage) //store in sentences column
			{
				for(Integer u : UniquePeps) {	
//$$				if(!u.equals(272)) {	continue;	}
					//else {}
					//System.out.println("Proposal : " + u);
					//FIRST WE GET THE INITIAL SENTENCE
					String sql8 = "SELECT * from allsentences "
						+ " where  proposal = "+u+" and "
						//+ " sentence like '% pep %' OR '% proposal %' '% idea %' "   //we don't consider instances like 'pep308.txt' therefore we look for where sentence contains term 'pep' on its own
						+ " sentence like '% pep %' and isenglishorcode = 1 "
//						+ " and (lastdir like '%dev%' or lastdir like '%idea%') "
						+ " order by proposal asc, messageid asc"; //, datetimestamp asc";
					
					//March 2019, to establish ground truth we extract sentences from the results_post_processed table for substates
					//			  to establish ground truth we extract sentences from the results_post_processed table for reasons
					String sql = "SELECT * from results_postprocessed "
							+ " where  pep = "+u+" and "
							//+ " sentence like '% pep %' OR '% proposal %' '% idea %' "   //we don't consider instances like 'pep308.txt' therefore we look for where sentence contains term 'pep' on its own
//							+ " sentence like '% pep %' and isenglishorcode = 1 "
//							+ " and (lastdir like '%dev%' or lastdir like '%idea%') "
							+ " order by proposal asc, messageid asc"; //, datetimestamp asc";
					
					// Feb2019, we consider allterms in all sentences in the paragraph of the sentence 
					// we have no data in the allparagraphs table, so we contruct it based on numbers in the allsentences table
					// we get all sentences which belong to same messageid (distinct - consider once) and same paragraph counter
					
					// results table or postprocessed table
					stmt = conn.createStatement();	
					rs = stmt.executeQuery(sql);  //date asc
					totalRows = mc.returnRSCount(sql, conn); processedCounter=0;	
					System.out.println("Proposal: " + u + " First Layer - Number of Sentences: " +totalRows);
					
					arrayCounter=0; recordSetCounter=0;		sentenceCounterForTopicModelling = 0; paragraphCounter=0;
					label=""; CurrentSentenceString=""; effectSentence="";	lemmatisedSentence = "";
					//sentenceTermsForCheckingDots[]= {};
					//ArrayList<String> lemmatisedSentenceList = new ArrayList<String>();
					sql0="";	index=0;	
					//System.out.println("Proposal : " + proposal + " causeSentence: " + causeSentence);
					
					while (rs.next()) {// && totalRows < 1000) {		//dont process those peps which have too many messages				
						messageid = rs.getInt("messageid"); proposal = rs.getInt("proposal"); 		paragraphCounter= rs.getInt("paragraphCounter");
						CurrentSentenceString = rs.getString("sentence");
						//System.out.println("\t\tEffectSentence : " + effectSentence);
						//	populateCauseEffectCleaned(id,causeSentence,effectSentence);	//clean
						lemmatisedSentence = null;
						// Feb2019, we consider allterms in all sentences in the paragraph of the sentence 
						// we have no data in the allparagraphs table, so we contruct it based on numbers in the allsentences table
						// SECOND Swe get all sentences which belong to same messageid (distinct - consider once) and same paragraph counter
						sql0 = "SELECT DISTINCT sentence from allsentences "  //distinct so that the middle paragrapgh does not get selected twice if all three paragraphs contain the key term (since 5 paragrapghs would be selected)
								+ " where proposal = "+proposal+" and messageid = "+messageid+" "
								+ " and (paragraphCounter BETWEEN "+ (paragraphCounter-1)  + " AND "+ (paragraphCounter+1)  + ");";
						stmt0 = conn.createStatement();
						rs0 = stmt0.executeQuery(sql0);  //date asc
						totalRows0 = mc.returnRSCount(sql0, conn);
						//System.out.print("Second Layer - Number of Sentences: " +totalRows0);
						while (rs0.next()) {
							recordSetCounter++;
							CurrentSentenceString = rs0.getString("sentence");
							//remove all terms within sentence with dots - mostly links
							try {	//HERE WE WANT TO DO WHAT WE WANT TO DO
								CurrentSentenceString = CurrentSentenceString.replaceAll("[^a-zA-Z ]", "").toLowerCase();	//remove all punctuation
								CurrentSentenceString = CurrentSentenceString.replaceAll("\\s+", " ").trim(); //remove double spaces and trim	
								//sentenceTermsForCheckingDots = CurrentSentenceString.split(" ");
								//sentenceTermsForCheckingDots = Arrays.asList(CurrentSentenceString.split(" "));
								String query = " insert into topicModellingSentences (proposal, messageid, sentence) values (?, ?, ?)";
								      // create the mysql insert preparedstatement
								      PreparedStatement preparedStmt = conn.prepareStatement(query);
								      preparedStmt.setInt (1, proposal);      preparedStmt.setInt (2, messageid);	      preparedStmt.setString   (3, CurrentSentenceString);							      
								      preparedStmt.execute();
								      preparedStmt.close();
							}  //end try       
							catch (Exception e){ 
								System.out.println(messageid + " error inserting record______here 2311  " + e.toString() + "\n" ); //System.out.println(Thread.currentThread().getStackTrace()  );		//+  " \n" + e.printStackTrace()
								System.out.println(StackTraceToString(e)  );									//continue;
							} 
						}
						stmt0.close();
					}
					stmt.close();
				}
				
			} //end first stage
			
			if(secondStage) {	//lemmatise
				try {
					StanfordLemmatizer stl = new StanfordLemmatizer();
					stmt1 = conn.createStatement();
					
					//read dictionary terms into arraylist, we will check each extracted sentence and only consider if they are in this list 
					Scanner s = new Scanner(new File("C:\\DeMap_Miner\\datafiles\\inputFiles\\englishWords\\corncob_lowercase.txt"));				
					while (s.hasNext()){
						dictList.add(s.next());
					}
					s.close();
					Collections.sort(dictList);	/* Sort statement*/
								
					//read code terms from file
					BufferedReader in = new BufferedReader(new FileReader("C:\\DeMap_Miner\\workspaces\\DeMapMiner3\\mallet\\stoplists\\CodeTerms.txt"));
					String str;
					List<String> list = new ArrayList<String>();
					List<String> ignoredTermsList = new ArrayList<String>();
					
					while((str = in.readLine()) != null){
					    list.add(str);
					}
					String[] stringArr = list.toArray(new String[0]);
					
					int id;
					List<String> lemmatizeSentence = new ArrayList<String>();
					for(Integer u : UniquePeps) {
						
//						if(!u.equals(272)) {	continue;	}
						
						sql1 = "SELECT id, sentence from topicModellingSentences "  //distinct so that the middle paragrapgh does not get selected twice if all three paragraphs contain the key term (since 5 paragrapghs would be selected)
								//+ " where sentence like '%pep%' and proposal = "+u ; 
								+ " where proposal = "+u ; 
						//+ " and (paragraphCounter BETWEEN "+ (paragraphCounter-1)  + " AND "+ (paragraphCounter+1)  + ");";
						rs1 = stmt1.executeQuery(sql1);  //date asc
						totalRows0 = mc.returnRSCount(sql1, conn);
						System.out.println("Proposal: " + u + " Second Layer - Number of Sentences: " +totalRows0);
						lemmatizeSentence.clear();
						String finalSentence="",ignoredTerms="";
						while (rs1.next()) {
							recordSetCounter++;
							id = rs1.getInt("id");	CurrentSentenceString = rs1.getString("sentence");
//							System.out.println(" original sentence " + CurrentSentenceString);
							CurrentSentenceString = stl.lemmatizeReturnString(CurrentSentenceString);	//CurrentSentenceString = stl.lemmatizeReturnString(CurrentSentenceString);												
							//lemmatizeSentence.add(CurrentSentenceString); 
//							System.out.println(" lemma sentence 1 " + CurrentSentenceString);
							//System.out.println(" lemma sentence 2 " + lemmatizeSentence.toString()); //this does not work
							
							//remove all terms within sentence with dots - mostly links
							try {	//HERE WE WANT TO DO WHAT WE WANT TO DO
								CurrentSentenceString = CurrentSentenceString.replaceAll("[^a-zA-Z ]", "").toLowerCase();	//remove all punctuation
								CurrentSentenceString = CurrentSentenceString.replaceAll("\\s+", " ").trim(); //remove double spaces and trim	
								sentenceTermsForCheckingDots = Arrays.asList(CurrentSentenceString.split(" "));

								//String finalString="";
								index=0; finalSentence=""; ignoredTerms="";
								for(String se : sentenceTermsForCheckingDots) {
									//System.out.println("term " + se);
									//we do two things here
									//first we check that the terms must be in dictionary list
									//find stemmed version of term
									//s = stl.lemmatizeReturnString(s);
									// Search string binary method. If string is not present, returns index of where string would have been inserted at position. So the function returns (-4-1)  
									// which is -5.
									index = Collections.binarySearch(dictList, se); 	//System.out.println(index); 						        	
									//find stemmed version of term
									if(index > 0) { //we consider these terms
										finalSentence = finalSentence + " " + se;	
										//System.out.println(" s " + se);		//we write these terms to a file for checking
									}
									else { //remove these terms
										ignoredTerms = ignoredTerms + " " + se; 
										ignoredTermsList.add(se);	//add to list
										//System.out.println("\t t " + se);
//										CurrentSentenceString = CurrentSentenceString.replace(se, " ");  //remove these terms nt in dictionary
									}	
									//finalString = finalString+ " " + s;
									//second check for dots
									if (se.length() > 2 && se.contains(".")) {	//end of sentence would contain a dot and we dont want to replace that ...maybe its needed in CIE
										CurrentSentenceString = CurrentSentenceString.replace(se, " ");
									}
								}
								CurrentSentenceString = finalSentence;
								sentenceTermsForCheckingDots = Arrays.asList(CurrentSentenceString.split(" "));
								String query = " update topicModellingSentences set lemmasentence = ?, ignoredTerms = ? where id =?";
								// create the mysql insert preparedstatement
								PreparedStatement preparedStmt = conn.prepareStatement(query);
								preparedStmt.setString(1, CurrentSentenceString); preparedStmt.setString(2, ignoredTerms);      preparedStmt.setInt(3, id);	      						      
								preparedStmt.execute();
								preparedStmt.close();
							}  //end try       
							catch (Exception e){ 
								System.out.println(id + " error inserting record______here 231  " + e.toString() + "\n" ); //System.out.println(Thread.currentThread().getStackTrace()  );		//+  " \n" + e.printStackTrace()
								System.out.println(" error 23 " + StackTraceToString(e)  );									//continue;
							} //end catch
						} //end while
						
						try {
							//for each proposal we insert the ignored terms (collected with all sentences)
							//insert all ignoredterms into another table
							HashSet<String> uniqueValues = new HashSet<>(ignoredTermsList);
							for(String d: uniqueValues) {
								if(d==null || d.equals("") || d.isEmpty()) {}
								else {
									String query3 = " insert into topicModellingIgnoredTerms (ignoredterm) values (?)";
									// create the mysql insert preparedstatement
									PreparedStatement preparedStmt3 = conn.prepareStatement(query3);
									preparedStmt3.setString(1, d);           						      
									preparedStmt3.execute();
									preparedStmt3.close();
								}
							}
						}
						catch (Exception e){ 
							System.out.println(" error inserting record______here 2319  " + e.toString() + "\n" ); //System.out.println(Thread.currentThread().getStackTrace()  );		//+  " \n" + e.printStackTrace()
							System.out.println(" error 239 " + StackTraceToString(e)  );									//continue;
						} //end catch
						
					} //end for each unique peps
					stmt1.close();
				} //end try
				catch (Exception e){ 
					System.out.println(" error inserting record______here 2314  " + e.toString() + "\n" ); //System.out.println(Thread.currentThread().getStackTrace()  );		//+  " \n" + e.printStackTrace()
					System.out.println(" error 24 " +StackTraceToString(e)  );									//continue;
				} 
			}
			
			if(thirdStage){  //extract verbs and nouns
				try {
					int id;
					stmt1 = conn.createStatement();
					StanfordNLPTools snlp = new StanfordNLPTools();
					snlp.init();
					for(Integer u : UniquePeps) 
					{
						//if(!u.equals(272)) {	continue;	}
						sql1 = "SELECT id, lemmasentence from topicModellingSentences "  //distinct so that the middle paragrapgh does not get selected twice if all three paragraphs contain the key term (since 5 paragrapghs would be selected)
								+ " where proposal = "+u ; 
						rs1 = stmt1.executeQuery(sql1);  //date asc
						totalRows0 = mc.returnRSCount(sql1, conn);
						while (rs1.next()) {
							recordSetCounter++;
							id = rs1.getInt("id");	CurrentSentenceString = rs1.getString("lemmasentence");
							//remove all terms within sentence with dots - mostly links
							try {	//HERE WE WANT TO DO WHAT WE WANT TO DO
								if(CurrentSentenceString.split(" ").length >5) {
									CurrentSentenceString = CurrentSentenceString.replaceAll("\\s+", " ").trim();	//remove double spaces and trim
									//write to file
									//Feb 2019, get only the verbs and nouns form the sentence
									pair p = new pair(); p = snlp.returnAllVerbsAndNounsInSentence(CurrentSentenceString,p);
									//System.out.println("Return All Verbs In Sentence: ("+CurrentSentenceString+") " + p.getVerb());
			//$$					CurrentSentenceString = p.getVerb(); //lets just try verns for now
									String query = " update topicModellingSentences set verbsinsentence = ?, nounsinsentence = ? where id =?";
									// create the mysql insert preparedstatement
									PreparedStatement preparedStmt = conn.prepareStatement(query);
									preparedStmt.setString(1, p.getVerb()); preparedStmt.setString(2, p.getNoun());      preparedStmt.setInt(3, id);	      						      
									preparedStmt.execute();
								}
								//index=0; 
								//sentenceTermsForCheckingDots = Arrays.asList(CurrentSentenceString.split(" "));
							}  //end try       
							catch (Exception e){ 
								System.out.println(id + " error inserting record______here 231  " + e.toString() + "\n" ); //System.out.println(Thread.currentThread().getStackTrace()  );		//+  " \n" + e.printStackTrace()
								System.out.println(" error 23 " + StackTraceToString(e)  );									//continue;
							} //end catch
						} //end while
					} //end for each unique peps
					stmt1.close();
					System.out.println("Third Stage Finished Processing");
				} //end try
				catch (Exception e){ 
					System.out.println(" error inserting record______here 2314  " + e.toString() + "\n" ); //System.out.println(Thread.currentThread().getStackTrace()  );		//+  " \n" + e.printStackTrace()
					System.out.println(" error 24 " +StackTraceToString(e)  );									//continue;
				}
			}//end third stage
			
			if(fourthStage) { //write to file from database
				pwForTopicModellingSentences= new PrintWriter (file); 
				System.out.println("OutputFile created Successfully");
				try {
					int id;
					stmt1 = conn.createStatement();
					for(Integer u : UniquePeps) 
					{
						//if(!u.equals(272)) {	continue;	}
						sql1 = "SELECT id, lemmasentence from topicModellingSentences "  //distinct so that the middle paragrapgh does not get selected twice if all three paragraphs contain the key term (since 5 paragrapghs would be selected)
								+ " where proposal = "+u ; 
						rs1 = stmt1.executeQuery(sql1);  //date asc
						totalRows0 = mc.returnRSCount(sql1, conn);
						System.out.println("Proposal: " + u + " Fourth Layer - Number of Sentences: " +totalRows0);
						while (rs1.next()) {
							recordSetCounter++;
							id = rs1.getInt("id");	CurrentSentenceString = rs1.getString("lemmasentence");
							//remove all terms within sentence with dots - mostly links
							try {	//HERE WE WANT TO DO WHAT WE WANT TO DO
								if(CurrentSentenceString.split(" ").length >5) {
									sentenceCounterForTopicModelling++;
									CurrentSentenceString = CurrentSentenceString + ".";	///add fullstop as removed in earlier code
									CurrentSentenceString = CurrentSentenceString.replaceAll("\\s+", " ").trim();	//remove double spaces and trim
									//write to file
									StringBuilder sb = new StringBuilder();
									sb.append(sentenceCounterForTopicModelling);	sb.append(" 	X	");		sb.append(CurrentSentenceString);			sb.append('\n');
									//prp.getPwForTopicModellingSentences().write(sb.toString());	//write to file
									pwForTopicModellingSentences.write(sb.toString());
									//jan 2019..remove sentences with coding terms
				//					if(cd.textHasCodingTerms(CurrentSentenceString,stringArr)) {}
				//					else {
										//StringBuilder sb = new StringBuilder();
										
				//					}
								}
								//index=0; 
								//sentenceTermsForCheckingDots = Arrays.asList(CurrentSentenceString.split(" "));
							}  //end try       
							catch (Exception e){ 
								System.out.println(id + " error inserting record______here 231  " + e.toString() + "\n" ); //System.out.println(Thread.currentThread().getStackTrace()  );		//+  " \n" + e.printStackTrace()
								System.out.println(" error 23 " + StackTraceToString(e)  );									//continue;
							} //end catch
						} //end while
					} //end for each unique peps
					pwForTopicModellingSentences.close ();
					stmt1.close();
					System.out.println("Fourth Stage Finished Processing");
				} //end try
				catch (Exception e){ 
					System.out.println(" error inserting record______here 2314  " + e.toString() + "\n" ); //System.out.println(Thread.currentThread().getStackTrace()  );		//+  " \n" + e.printStackTrace()
					System.out.println(" error 24 " +StackTraceToString(e)  );									//continue;
				}
			}
			
		}catch (Exception e) {
			System.out.println(" ERROR CREATING FILE  " + e.toString() + "\n" ); //System.out.println(Thread.currentThread().getStackTrace()  );		//+  " \n" + e.printStackTrace()
			System.out.println(StackTraceToString(e)  );
		}
	}
	
	public static ArrayList returnUniqueProposalsInDatabase(String proposalIdentifier){
		   //Utilities u = new Utilities();
		   MysqlConnect mc = new MysqlConnect();		   Connection connection = mc.connect();   
		   //mc.testConnection();       
	       Integer counter =0,pepNumber;	       
	       ArrayList<Integer> uniquePeps = new ArrayList<>();
		   //numbers.add(308);		            
	       String sql = "SELECT distinct("+proposalIdentifier+") from "+proposalIdentifier+"details;"; 
	       try {
	    	   Statement stmt = connection.createStatement(); 	    	   ResultSet rs = stmt.executeQuery( sql );
	           	 while (rs.next()){     //check every message       
	           		 pepNumber = rs.getInt(1);	           		 uniquePeps.add(pepNumber);
	           	 }
	           	 System.out.println("Unique Proposal Returned = " + uniquePeps.size());
	           	stmt.close();
	       }
	       
	       catch (SQLException e){	           System.out.println( e.getMessage() );	       }	       
		return uniquePeps;
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
