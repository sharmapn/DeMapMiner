package miner.stringMatching;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import connections.MysqlConnect;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import miner.models.Message;
import miner.models.Pep;
import utilities.Utilities;

public class checkMessage {
	public static void main(String[] args){
		 Matcher reMatcher = null;
	       String filename = "c:\\scripts\\testin.txt";
	       
	       String fileContent="";
	       
	       try {
	               File f = new File(filename);
	               FileInputStream inp = new FileInputStream(f);
	               byte[] bf = new byte[(int)f.length()];
	               inp.read(bf);
	               fileContent = new String(bf, "UTF-8");
	               System.out.println(fileContent);
	           } catch (FileNotFoundException e) {
	               e.printStackTrace();
	           } catch (IOException e) {
	               e.printStackTrace();
	           }
	       
	           try {       
	        
	           		//System.out.println("method...if");
//	           		PrintWriter out = new PrintWriter(filename);
//	           		out.println(v_message);
	        	   String[] paragraphs = fileContent.split("\r\n\r\n", -1);
	        	   for (String g: paragraphs)
	        	   {
	           		Reader reader = new StringReader(g);
	         		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
	         		//dp.setSentenceDelimiter("\n\n");
	         		//  System.out.println("method.." + v_message);
	         			System.out.println("paragrapgh......................" + g);
	         		   for (List<HasWord> eachSentence : dp) {
	         			  String sentenceString = Sentence.listToString(eachSentence);
	         		//	  p[v_pepNumber].messages[v_counter].setSentence(sentenceString); 
	         	    	   //sentence = sentence.toString();         	    	 
	         	    //	  wordsFoundList = checkEachSentence ( sentenceString,  wordsFoundList,  author,  statusFrom,  statusTo ,  v_pep, v_date, v_pepNumber, v_counter );
	         			 System.out.println("sentence "+sentenceString);
	         		   }
	        	   }
	    
	           }
		            catch (Exception e){ 
		  	            	System.out.println( " ____________________________________________________________heer  " + e.toString() + "\n");
		            	//continue;
		            }             	   
	}

	 	  
	  public static String checkEntireMessage (Pep[] p, String Message, String wordsFoundList, String author, String statusFrom, String statusTo ,Integer v_pep, Date v_date, String v_subject, Integer v_messageID, Integer v_pepnumber, Integer v_counter){
	   	  
		   //proposal
		//   got fairly positive responses
		//   (more positive than I'd expected, in fact) but I'm bracing myself for
		//   fierce discussion here on python-dev. It's important to me that if if
		//   this is accepted it is a "rough consensus" decision (working code we
		//   already have plenty of :-), not something enforced by a vocal minority
		//   or an influential individual such as myself. If there's too much
		//   opposition I'll withdraw the PEP so as not to waste everybody's time
		//   with a fruitless discussion.	   
		   
		//took place. Everyone on the SIG was positive with the idea so I wrote a
		//   PEP, got positive feedback from the SIG again, and so now I present to you
		//   PEP 488 for discussion.
		   
		  /* Given that if-then-else expressions keep being requested, I hereby put
		   forward a proposal.  I am neutral on acceptance of the proposal; I'd
		   like the c.l.py community to accept or reject it or come up with a
		   counter-proposal.  Please understand that I don't have a lot of time
		   to moderate the discussion; I'll just wait for the community to
		   discuss the proposal and agree on some way to count votes, then count
		   them. */
		
	    
	    //check for python checkins messages		       
	   // if (v_subject.contains("[Python-checkins]")){
	  //  	if(Message.contains("Modified:") && Message.contains("Log Message:") && Message.contains("***************"))  
	   // 		System.out.println("-----------------------------------message id " + v_messageID +  " contains ***************");
	   // }
		
		//bdfl heartily approve
			
			
			//pep accepted - no reasons in these messages
			 if ( Message.toLowerCase().contains("guido ") && Message.toLowerCase().contains("pep ")   && Message.toLowerCase().contains("i heartily approve") )
		       {   
				 wordsFoundList += "bdfl-heartly-approves-pep &";			
				 p[v_pepnumber].messages[v_counter].setMessageGist("bdfl-heartly-approves-pep"); 
		       }
			 
		   if (Message.contains("Acceptance")) 	{		   
			 Integer a = Message.indexOf("Acceptance");	 
		//	 Integer k = Message.indexOf("Rationale", a);	 
			 
			 Integer j = Message.indexOf("\n", a);	
			   String lines = "";
			   
			   Integer o = Message.substring(a, j).length();
			   Integer y = ("Acceptance").length();
			   if (a>0 && (o == y)){   //if the word acceptance on its own as a heading - not a word in a sentence
				 //  System.out.println("\nhello "+ a +  "  "+ k);			   
				 //  System.out.println(Message.substring(a, j));
				   
				   for (int z=0; z<12;z++){
					   //Integer s = Message.indexOf("Acceptance");	 
						 Integer t = Message.indexOf("\n", j+1);
						 //Integer n = Message.indexOf("\n\n", j+1);
						 lines += Message.substring(j+1, t);
						 j=t;
				   }
//				   System.out.println(lines);			   
			   }
			   
			   /*
			    * +This PEP was accepted by the BDFL on November 22.  Because of the
					+exceptionally short period from first draft to acceptance, the main
					+objections brought up after acceptance were carefully considered and
					+have been reflected in the "Alternate proposals" section below.
					+However, none of the discussion changed the BDFL's mind and the PEP's
					+acceptance is now final.  (Suggestions for clarifying edits are still
					+welcome -- unlike IETF RFCs, the text of a PEP is not cast in stone
					+after its acceptance, although the core design/plan/specification
					+should not change after acceptance.)
			    */
			 //Acceptance
			 if (lines.toLowerCase().contains("pep was accepted by the bdfl") || lines.toLowerCase().contains("none of the discussion changed the b's mind")  ||  lines.toLowerCase().contains("voting procedure")  ) 
				 wordsFoundList += " BDFL accepted pep after discussion";
		   }
		   
		   if (Message.contains("BDFL Pronouncements")) 	{		   
				 Integer a = Message.indexOf("BDFL Pronouncements");	 
			//	 Integer k = Message.indexOf("Rationale", a);	 
				 
				 Integer j = Message.indexOf("\n", a);	
				   String lines = "";
				   
				   Integer o = Message.substring(a, j).length();
				   Integer y = ("BDFL Pronouncements").length();
				   if (a>0 && (o == y)){   //if the word acceptance on its own as a heading - not a word in a sentence
					 //  System.out.println("\nhello "+ a +  "  "+ k);			   
					 //  System.out.println(Message.substring(a, j));
					   
					   for (int z=0; z<12;z++){
						   //Integer s = Message.indexOf("Acceptance");	 
							 Integer t = Message.indexOf("\n", j+1);
							 //Integer n = Message.indexOf("\n\n", j+1);
							 lines += Message.substring(j+1, t);
							 j=t;
					   }
//					   System.out.println(lines);			   
				   }
				   
				   /*
				    * +This PEP was accepted by the BDFL on November 22.  Because of the
						+exceptionally short period from first draft to acceptance, the main
						+objections brought up after acceptance were carefully considered and
						+have been reflected in the "Alternate proposals" section below.
						+However, none of the discussion changed the BDFL's mind and the PEP's
						+acceptance is now final.  (Suggestions for clarifying edits are still
						+welcome -- unlike IETF RFCs, the text of a PEP is not cast in stone
						+after its acceptance, although the core design/plan/specification
						+should not change after acceptance.)
				    */
				 //Acceptance
				 if (lines.toLowerCase().contains("bdfl pronouncements")   ) 
					 wordsFoundList += "b-bdfl-pronouncements";  
				 	 p[v_pepnumber].messages[v_counter].setMessageGist("b-bdfl-pronouncements");
			   }
		   
		   
		   
			 //how  to vote
			// Message.toLowerCase().contains("remind you"); 
		   /*  NOT THAT IMPORTANT
			 if (Message.toLowerCase().contains("how to vote") || Message.toLowerCase().contains("A copy of the ballot, and instructions on how to vote")  || Message.toLowerCase().contains("and instructions on how to vote") 
					  || Message.toLowerCase().contains("voter preferences") ||  Message.toLowerCase().contains("voting procedure")  ) 
				 wordsFoundList += " how to vote &";
			 */
			 
		//	 Message.toLowerCase().contains("Proposals are welcome")
	       
	       
	       //call for alternate vote
		   if (Message.toLowerCase().contains("please participate") || Message.toLowerCase().contains("may vote")) {
			   if (Message.toLowerCase().contains("advisory poll") || Message.toLowerCase().contains("unofficial vote") || Message.toLowerCase().contains(" complementary vote")) { 
				   if (Message.toLowerCase().contains("remind")) { 
			       	   wordsFoundList += " reminder-for-alternate-voting &";
			       	   p[v_pepnumber].messages[v_counter].setMessageGist("reminder-for-alternate-voting");
			       	  // System.out.println("here ------------------- remind alternate voting called " + v_messageID);	
			       	  // p[v_pep].setalternateVotingStartDate(v_date);
					  // p[v_pep].setalternateVotingEndDate(v_date);
		           }
				   else {
			       	   wordsFoundList += " alternate-voting-called &";  
			       	   p[v_pepnumber].messages[v_counter].setMessageGist("alternate-voting-called");
			       	   //System.out.println("here a------------------- m alternate voting called " + v_messageID);	
			       	   //p[v_pep].setalternateVotingStartDate(v_date);
					   //p[v_pep].setalternateVotingEndDate(v_date);
					   //System.out.println("england ------------------- m alternate voting called " + v_messageID);	
				   }
	           }
		   }
		   //System.out.println("fiji ------------------- m alternate voting called " + v_messageID);	
		 //alternate advisory poll
		   //ADD IMP 
		   //USE THIS CODE BELOW TO IMPEMENT LAST VOTE Date
//		   if (Message.toLowerCase().contains("advisory poll") || Message.toLowerCase().contains("unofficial vote") || Message.toLowerCase().contains(" complementary vote"))  
//	       	   wordsFoundList += " m alternate poll &";

	        
		   
			 //results
			 if (Message.toLowerCase().contains("result in favour of the status-quo") || Message.toLowerCase().contains("favour of the status-quo") || Message.toLowerCase().contains("status-quo") ||
			 Message.toLowerCase().contains("unprecedented community response") ||  Message.toLowerCase().contains("community response") || Message.toLowerCase().contains("posted the results of the complementary vote") ||
			 Message.toLowerCase().contains("published the ballots from the official vote") || Message.toLowerCase().contains("single winner") || 		 Message.toLowerCase().contains("social ranking") || Message.toLowerCase().contains("pairwise comparisons"))
				{ wordsFoundList += "m1z-vote-results &";  		p[v_pepnumber].messages[v_counter].setMessageGist("m1z-vote-results"); }			
	       
			//python checkins
//			 Modified Files:
			//	pep-0308.txt 
			// 	pep-0308.txt 
			//Log Message:
			//Minor nits.
			//Index:
			 
			 if (Message.toLowerCase().contains("modified files:") && Message.toLowerCase().contains("log message:")  && Message.toLowerCase().contains("index:"))  {
//				 System.out.println(); //"here ------------------- checkins");	
			 }
			
		   // HAVE TO WORK ON THE CODE BELOW
		   // VVV	 
	       // vote results analysis
	       if (Message.toLowerCase().contains("pep was") )  {
	    	   if (Message.toLowerCase().contains("vot") && ( Message.toLowerCase().contains("result") || Message.toLowerCase().contains("summarized")))  
	    	   {    	wordsFoundList += " m2-vote-results &";  					p[v_pepnumber].messages[v_counter].setMessageGist("m2-vote-results"); }  	      
	    	   if (Message.toLowerCase().contains("following the discussion"))
	    	   {   wordsFoundList += " after-discussion &";							p[v_pepnumber].messages[v_counter].setMessageGist("after-discussion"); }
	    	   if (Message.toLowerCase().contains("vote was held"))
	    	   {   wordsFoundList += " vote-was-held &";							p[v_pepnumber].messages[v_counter].setMessageGist("vote-was-held"); }
	    	   if (Message.toLowerCase().contains("draw majority support"))
	    	   {   wordsFoundList += " draw-majority-support &";					p[v_pepnumber].messages[v_counter].setMessageGist("draw-majority-support"); }
	    	   if (Message.toLowerCase().contains("rejected"))
	    	   {   
	    		   //wordsFoundList += "m-pep-rejected &";							
	    		   //p[v_pepnumber].messages[v_counter].setMessageGist("m-pep-rejected");
	    		   if (Message.toLowerCase().contains("due to ") && Message.toLowerCase().contains("lack of") && Message.toLowerCase().contains("overwhelming majority for change"))
	        	   {   
	    			    wordsFoundList += " pep-rejected-lack-of-overwhelming-majority &";			
	        	   		p[v_pepnumber].messages[v_counter].setMessageGist("pep-rejected-lack-of-overwhelming-majority"); 
	        	   }
	    	   }					
	    	   if (Message.toLowerCase().contains("accepted"))
	    	   {   
	    		   wordsFoundList += " pep-accepted &";								
	    		   p[v_pepnumber].messages[v_counter].setMessageGist("pep-accepted"); 
	    	   }
	    	  
	    	 
	       }
	       
	 //      Following the discussion, a vote was held.  While there was an overall
	 //      +     interest in having some form of if-then-else expressions, no one
	 //      +     format was able to draw majority support.  Accordingly, the PEP was
	 //      +     rejected due to the lack of an overwhelming majority for change.
	  //     +     Also, a Python design principle has been to prefer the status quo
	 //      +     whenever there are doubts about which path to take.
	       
	       //
	       
	      
	       
	       
	        
	      // I'm sorry I won't be able to come to the language summit, but I would like if
	      // possible to expedite a pronouncement on PEP 391 (configuring logging using
	      //dictionaries). I believe I addressed all the comments made on the discussion
	     // threads mentioned in the PEP and so I'm not sure what more I need to do to get a pronouncement. I guess the stdlib slot gives an opportunity for people to air their views and so I'd be grateful if you added it to the agenda.
	       //status changed

	   //    if (Message.toLowerCase().contains("Status:") && Message.toLowerCase().contains("Status:") )  {
	    	  
	    //   }
	       //System.out.println("end of method - wordsFoundList " + wordsFoundList + " message id " + v_messageID); //"here ------------------- checkins");	
	    return wordsFoundList;
	   }
	  
	  public static Boolean checkParent(Integer v_message_ID, Integer pepNumber, String v_subject, String v_inReplyTo, Connection v_connection){
		  MysqlConnect mc = new MysqlConnect();
		   mc.testConnection();       
	       //  Connect to an MySQL Database, run query, get result set
	       
	       String sql0 = "SELECT pep, messageID from allpeps where messageID = '" + v_message_ID + "' order by date2;"; 
	       
	       //check for if more child messages
	       Integer v_v_v_pep = null;
	       Integer v_v_v_messageID = null;
	       Statement stmt0 = null;
	       String child_message_ids =null;
	       Integer child_count = 0; 
	       try{		   
			   Connection connection2 = v_connection;
	           stmt0 = connection2.createStatement();// ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	           ResultSet rs0 = stmt0.executeQuery( sql0 ); 
		   	   while (rs0!=null && rs0.next()) {
		   		   v_v_v_pep = rs0.getInt(1);
		   		   v_v_v_messageID = rs0.getInt(2);
		   		   child_count++;  /*
		   		   if (v_v_pep == null)
		   			   System.out.println("no parent ");
		   		   else {
					   if(v_v_pep == pepNumber){
							System.out.println("parent with same pepnumber"); }
					   else {
							System.out.println("parent with diff pepnumber, child pep = " + pepNumber + " parent pep = " + v_v_pep); }
		   		   }
		   		   */
		   		child_message_ids += ", " + v_v_v_pep.toString();
		       }
		   	  // if (count> 1)
		   		 //  System.out.println("for pep " + pepNumber + " and message id "  + " count " + count);
	       }
	       catch (SQLException e0){
	          System.out.println( e0.getMessage() );
	       }
		   catch(Exception e0){
		        e0.printStackTrace();
		   }
	       finally{
	              try{
			        	   stmt0.close();  //System.out.println("g ");
	              	}
	              catch(SQLException se2){
	            	 System.out.println("h ");
	              }// nothing we can do
	          } 	   
	       
	       if(child_count > 1){			// if more than 1 pep then identify
	 			System.out.println(" child message " + v_message_ID + " has been associated with several peps " + child_count + " with peps " + child_message_ids);
			  // return false;
	 		}
	 		else if(child_count == 1 || child_count ==0){			// if = 0 or 1
//	 			System.out.println("message parent message " + v_message_ID + " has been associated with 1 pep " + count + " with messageid " + message_ids);
	 			//return true;
	 		}
	       

	       //check for parent messages - i.e. he message to which this message is a reply
	       //get the message id of these messages				//pep = " + pepNumber + " and
	       String sql = "SELECT pep, messageID from allpeps where EmailmessageID = '" + v_inReplyTo + "' order by date2;"; 
	       
	       /*  if no resultset, then raise alert - not serious but just need to think how to cater for this -- number wud be small anyway
	        * select pep from allpeps where message id = message id from above
	        * 
	        */       
	       
	       Integer v_Message;
	       Integer count = 0;       
	       Integer v_v_pep = null;
	       Integer v_v_messageID = null;
	       String message_ids =null;
	       Statement stmt = null;
		   try{		   
			   Connection connection = v_connection;
	           stmt = connection.createStatement();// ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	           ResultSet rs = stmt.executeQuery( sql ); 
		   	   while (rs!=null && rs.next()) {
		   		   v_v_pep = rs.getInt(1);
		   		   v_v_messageID = rs.getInt(2);
		   		   count++;  /*
		   		   if (v_v_pep == null)
		   			   System.out.println("no parent ");
		   		   else {
					   if(v_v_pep == pepNumber){
							System.out.println("parent with same pepnumber"); }
					   else {
							System.out.println("parent with diff pepnumber, child pep = " + pepNumber + " parent pep = " + v_v_pep); }
		   		   }
		   		   */
		   		message_ids += ", " + v_v_pep.toString();
		       }
		   	  // if (count> 1)
		   		 //  System.out.println("for pep " + pepNumber + " and message id "  + " count " + count);
	       }
	       catch (SQLException e1){
	          System.out.println( e1.getMessage() );
	       }
		   catch(Exception e){
		        e.printStackTrace();
		   }
	       finally{
	              try{
			        	   stmt.close();  //System.out.println("g ");
	              	}
	              catch(SQLException se2){
	            	 System.out.println("h ");
	              }// nothing we can do
	          } 	   
		   
		   if(count > 1){			// if more than 1 pep then identify
	  			System.out.println("message parent message " + v_message_ID + " has been associated with several peps " + count + " with peps " + message_ids);
			   return false;
	  		}
	  		else if(count == 1 || count ==0){			// if = 0 or 1
	 // 			System.out.println("message parent message " + v_message_ID + " has been associated with 1 pep " + count + " with messageid " + message_ids);
	  			return true;
	  		}
		   return true;
	   }
	  
	  
}

/*
//3. normal regex matching
wordsFoundList = null;
for (String patternW : v_wordsList){
//    		  System.out.println("pattern " + pattern);
  Pattern pw = Pattern.compile(patternW);
  mw = pw.matcher(Message);  
	  if (mw.find()) //  while (m.find() || m9.find()) 
	  {
//       		  System.out.println("found  " + patternW);
		  if (wordsFoundList == null)
			  wordsFoundList = patternW;
		  else
			  wordsFoundList += ("- " + patternW);    //add pattern to word found list
	  }
}
*/           

//tc.returnClassification() +
// wordsFoundList.replace("null"," ");
//       if (wordsFoundList !=null && statusFrom==null)
//       	System.out.println( v_date + ", " + author  + " , " + " " + wordsFoundList);
//        else if (wordsFoundList !=null && statusFrom!=null)
//        	System.out.println( v_date + ", " + author  + " , " + " " + wordsFoundList + " " + statusFrom + " -> " + statusTo);



	// System.out.println("pep number " + pepNumber);
// 	    	 String sql2 = "SELECT email from allpeps WHERE pep = " + pepNumber + ";";  //there will be many rows since there is no seprate table to store this information
// 	    	 Statement stmt2 = connection.createStatement();
//	    	 ResultSet rs2 = stmt2.executeQuery( sql2 );
// 	    	 if (rs2.next()){    
		// numberMessages = rs2.getInt(1);
//	    		 System.out.println("PEP " + pepNumber + " message = " + rs.getString(1) );
//	    	 }
   //create the child nodes
   //create the tree by passing in the root node  


