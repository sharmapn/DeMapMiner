package GUI.helpers2;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTable;

public class GUIGenerateCsv
{
   
	  static List<String> wordsList = new ArrayList<String>();
	  static List<String> voteWordsList = new ArrayList<String>();
	
	
   public GUIGenerateCsv() throws IOException
   {
	   initWordsList(wordsList); 	
	//   getAllPEPMessages(v_pep, wordsList);
	   
	   //generateCsvFile("c:\\scripts\\test.csv"); 
   }
   
   public static int  number_of_cccurrences_of_char_in_string_java(String stringToSearch) {	    
	    String letter = "\n>";
	    int i = 0, count = 0;
	    while ((i = stringToSearch.indexOf(letter, i++)) != -1) {
	        count++;
	        i += letter.length();
	    }
	    return count;
	}
   
   public static String  removeQuotedText(String v_message) {	    
	   Integer start =0, end=0, countchar = 0;
       String sub = null;
       
      // countchar = number_of_cccurrences_of_char_in_string_java(Message);
       while (v_message.contains("\n>")) {
          // System.out.println(countchar);
          // for (int h=0; h<countchar; h++) { 
	           //  if (Message.contains("\n>")){
	            	 start =  v_message.indexOf("\n>");
	            	 end = v_message.indexOf("\n", start+1);
	            	 	if (start != -1 && end != -1){
	            	 		sub = v_message.substring(start, end);
	  //          	 		System.out.println(start + " " + end + " sub" + sub);
	            	 		v_message = v_message.replace(sub,"");
//	 //           	 		System.out.println(countchar);
//	 //           	 		System.out.println("start end " + start + end + " replaced" + sub);
	            	 	}           	 
	          //   }
          // }
       }
       return v_message;
	}
      
   public static String getAllPEPMessages(Integer v_PEP) throws IOException {
	    
   	//Integer v_pep = 451;
	   
	   String output = null;
	   FileWriter writer = new FileWriter("c:\\scripts\\test.csv");
       final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
       try {
           Class.forName("com.mysql.jdbc.Driver").newInstance();
         } catch (Exception e) {
           System.err.println("Unable to find and load driver");
           System.exit(1);
         }
       
       Integer pepNumber = v_PEP;
       String Message;
       String author;
       String v_date;
       String statusFrom;
       String statusTo;
       String status;
       Integer message_ID = null;
       Matcher m5 = null;
       Matcher mw = null;
       String wordsFoundList = null;
       
       //  Connect to an MySQL Database, run query, get result set
       String url = "jdbc:mysql://localhost:3306/peps";
       String userid = "root";
       String password = "root";
       String sql = "SELECT date2, sendername, analysewords, statusFrom, statusTo, statusChanged, messageID, required	 from allpeps WHERE pep = " + pepNumber + " order by date2;"; 
       //can add messageid later
       
       Integer counter =0;
       String newMessage = null;
       
       // Java SE 7 has try-with-resources
       // This will ensure that the sql objects are closed when the program 
       // is finished with them
       try (Connection connection = DriverManager.getConnection( url, userid, password );
           Statement stmt = connection.createStatement();
           ResultSet rs = stmt.executeQuery( sql ))
       {
       	 while (rs.next())     //check every message
            {
       		counter++;
   	    	 //add chaild node
       		Message = rs.getString(3);
       		v_date = rs.getString(1);
       		author = rs.getString(2);
       		statusFrom = rs.getString(4);
       		statusTo = rs.getString(5);
       		status = rs.getString(6);
       		message_ID = rs.getInt(7);
       		//1. classify text
            String textToClassify = Message;
 //XXX           TextClassifier tc = new TextClassifier(textToClassify);        
            //JOptionPane.showMessageDialog(null, tc.returnClassification(), "InfoBox: " + "Classification", JOptionPane.INFORMATION_MESSAGE);
//            System.out.println( v_date + ", " + author  + " , " + tc.returnClassification()); 
            
             Message = removeQuotedText(Message);  //remove quoted text from email message
             
            
         //   Message = Message.replace("\n",""); 
            wordsFoundList = null;
            wordsFoundList = check ( Message,  wordsFoundList,  author,  statusFrom,  statusTo, message_ID );
            if (wordsFoundList !=null){
    //        	System.out.println(Message);
            	System.out.println(v_date + ", " + author  + " , " + " " + wordsFoundList + " " ); //+ statusFrom + " -> " + statusTo);
            	output += (v_date + ", " + author  + " , " + " " + wordsFoundList + "\n" );
            }
            //2. Split message text into sentences
            //String str = "This is how I tried to split a paragraph into a sentence. But, there is a problem. My paragraph includes dates like Jan.13, 2014 , words like U.S and numbers like 2.2. They all got splitted by the above code.";
            Pattern re = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE | Pattern.COMMENTS);
            Matcher reMatcher = re.matcher(Message);
            if (reMatcher.find()) {
                //  System.out.println("REMATCHER " + reMatcher.group());  
                  Message = reMatcher.group();
                  wordsFoundList = check ( Message,  wordsFoundList,  author,  statusFrom,  statusTo , message_ID);
  //xxx               if (wordsFoundList !=null)
  //xxx                  System.out.println( v_date + ", " + author  + " , " + " " + wordsFoundList + " " ); //+ statusFrom + " -> " + statusTo);
           }   
            /*
            //3. normal regex matching
            wordsFoundList = null;
            for (String patternW : v_wordsList){
//      		  System.out.println("pattern " + pattern);
                Pattern pw = Pattern.compile(patternW);
                mw = pw.matcher(Message);  
          	  if (mw.find()) //  while (m.find() || m9.find()) 
          	  {
 //         		  System.out.println("found  " + patternW);
          		  if (wordsFoundList == null)
          			  wordsFoundList = patternW;
          		  else
          			  wordsFoundList += ("- " + patternW);    //add pattern to word found list
          	  }
	        }
 */           
            
         // tc.returnClassification() +
        //   wordsFoundList.replace("null"," ");
   //         if (wordsFoundList !=null && statusFrom==null)
   //         	System.out.println( v_date + ", " + author  + " , " + " " + wordsFoundList);
  //          else if (wordsFoundList !=null && statusFrom!=null)
  //          	System.out.println( v_date + ", " + author  + " , " + " " + wordsFoundList + " " + statusFrom + " -> " + statusTo);
          
            //write to csv file
            writer.append(v_date);
            writer.append(',');
//    	    writer.append(tc.returnClassification());
    	    writer.append(',');
    	    writer.append(author);
		    writer.append('\n');
            
	    		// System.out.println("pep number " + pepNumber);
//   	    	 String sql2 = "SELECT email from allpeps WHERE pep = " + pepNumber + ";";  //there will be many rows since there is no seprate table to store this information
//   	    	 Statement stmt2 = connection.createStatement();
 //  	    	 ResultSet rs2 = stmt2.executeQuery( sql2 );
//   	    	 if (rs2.next()){    
   	    		// numberMessages = rs2.getInt(1);
 //  	    		 System.out.println("PEP " + pepNumber + " message = " + rs.getString(1) );
 //  	    	 }
   	          //create the child nodes
   	          //create the tree by passing in the root node  	          	    	 
            }
       	 System.out.println("PEP " + pepNumber + " has " + counter + " messages ");
       	 writer.flush();
		    writer.close();
		    
       }
       catch (SQLException e)
       {
           System.out.println( e.getMessage() );
       }
       return output;
       
   }
   
   private static int indexOf(String string) {
	// TODO Auto-generated method stub
	return 0;
}

public static String check (String Message, String wordsFoundList, String author, String statusFrom, String statusTo , Integer message_ID ){
   	  
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

	   //proposal after discusions
	   if (Message.contains("positive") && Message.contains("discussion"))  {
		   if (Message.contains("responses") || Message.contains("feedback")) 
			   if ((Message.contains("propose") ) || (Message.contains("present") ))
				   wordsFoundList += " new pep proposal";
	       }
	   
	   
	   
	   //proposal
       if (Message.contains("new PEP") && Message.contains("discussions") &&  (Message.contains("proposes") || Message.contains("proposal")))  {
       	wordsFoundList += " new pep proposal from another list,";
       }
       //new proposal
       if (Message.contains("I"))  {
    	   if (Message.contains(" put ") && Message.contains(" proposal "))
    		   wordsFoundList += " puts forward a proposal,";
       		if (Message.contains("community") && Message.contains("discuss") && Message.contains("proposal"))  
       			wordsFoundList += " puts formward a proposal for community discussion,";       	  
            if (Message.contains("would like") && Message.contains("pronouncement on PEP") )   //author of pep asking for a pronouncement
            	wordsFoundList += " author asking for pronouncement,";            
       }
      
       //vote
       if ( (Message.contains("Please participate") || Message.contains("please indicate"))   )  {
    	   if ( (Message.contains("vote") || Message.contains("voting") || Message.contains("poll") || Message.contains("consensus") || Message.contains("complementary vote")  || Message.contains("ballot")))
    		   wordsFoundList += " asking to vote,";
       }
       
       //&& 
       //||
       
       //asking for feedback
       if (Message.contains("your") )  {
    	   if (Message.contains("feedback"))
    		   wordsFoundList += " asking for feedback,";
    	   if (Message.contains("comments"))				 //asking for comments
    		   wordsFoundList += " asking for comments,"; 	
    	   if (Message.contains("ideas"))				 	//asking for ideas
    		   wordsFoundList += " asking for ideas,"; 	
    	   if (Message.contains("pronouncement"))				 	//asking for pronouncement
    		   wordsFoundList += " asking for pronouncement,";
       }

       //Thanking for feedback
       if (Message.contains("Thank") && Message.contains("you") && Message.contains("feedback"))  {
       	wordsFoundList += " Thanking for feedback,";
       }
      
       
       //asking to vote
       if ((Message.contains("vote") && (Message.contains("Please participate")) || Message.contains("please indicate")))  {
       	wordsFoundList += " asking to vote,";
       }
       
		 //how  to vote
		 Message.contains("remind you"); 
		 if (Message.contains("HOW TO VOTE") || Message.contains("A copy of the ballot, and instructions on how to vote")  || Message.contains("and instructions on how to vote") 
				  || Message.contains("how to vote") || Message.contains("voter preferences") ||  Message.contains("voting procedure")  ) 
			 wordsFoundList += " how to vote,";
		 
		 
	//	 Message.contains("Proposals are welcome")
       
       
       //alternate advisory poll
       if (Message.contains("advisory poll") || Message.contains("UNOFFICIAL vote") || Message.contains(" COMPLEMENTARY vote"))  {
       	wordsFoundList += " alternate advisory poll,";
       }
       
       //vote results
       if (Message.contains("vot") && Message.contains("result") && Message.contains("summarized"))  {
          	wordsFoundList += " vote results,";
          }
       
		 //results
		 if (Message.contains("result in favour of the status-quo") || Message.contains("favour of the status-quo") || Message.contains("status-quo") ||
		 Message.contains("unprecedented community response") ||  Message.contains("community response") || Message.contains("posted the results of the COMPLEMENTARY vote") ||
		 Message.contains("published the ballots from the OFFICIAL vote") || Message.contains("SINGLE WINNER") || 		 Message.contains("SOCIAL RANKING") || Message.contains("PAIRWISE COMPARISONS"))
			 wordsFoundList += " vote results,";
       
       // vote results analysis
       if (Message.contains("pep was") )  {
    	   if (Message.contains("rejected"))
    		   wordsFoundList += " pep rejected,";
    	   if (Message.contains("accepted"))
    		   wordsFoundList += " pep accepted,";
    	   if (Message.contains("vot") && ( Message.contains("result") || Message.contains("summarized")))  
    	       	wordsFoundList += " vote results,";    	      
    	   if (Message.contains("Following the discussion"))
    		   wordsFoundList += " after discussion,";
    	   if (Message.contains("vote was held"))
    		   wordsFoundList += " vote was held,";
    	   if (Message.contains("draw majority support"))
    		   wordsFoundList += " draw majority support,";
    	   if (Message.contains("due to ") && Message.contains("lack of") && Message.contains("overwhelming majority for change"))
    		   wordsFoundList += " lack of overwhelming majority,";
       }
       
 //      Following the discussion, a vote was held.  While there was an overall
 //      +     interest in having some form of if-then-else expressions, no one
 //      +     format was able to draw majority support.  Accordingly, the PEP was
 //      +     rejected due to the lack of an overwhelming majority for change.
  //     +     Also, a Python design principle has been to prefer the status quo
 //      +     whenever there are doubts about which path to take.
       
       //
       if ( Message.contains("blogs") || Message.contains("survey") || Message.contains("offline") || Message.contains("twitter") || Message.contains("plus.google.com"))
    	   wordsFoundList += " offline discussion,";
      
       // decision analysis
       if (Message.contains("I want") && Message.contains("reject") && Message.contains("PEP") )  {
       	wordsFoundList += " PEP rejected,";
       }
       //BDFL asking if there is consensus
//       if (author.contains("Guido") &&  Message.contains("Is") && Message.contains("pep") && Message.contains("stage") && Message.contains("consensus") && Message.contains("?") )  {
//       	wordsFoundList += " BDFL asking if there is consensus,";
//       }
       //BDFL decision
       if (Message.contains("BDFL") )  {
    	   if (Message.contains("decision"))
    		   wordsFoundList += " BDFL decision,";
    	   if (Message.contains("pronouncement"))
    		   wordsFoundList += " BDFL pronouncement,";
    	   if (Message.contains("reject"))
    		   wordsFoundList += " BDFL rejection,";
    	   if (Message.contains("accepted"))
    		   wordsFoundList += " BDFL accepted,";
    	   if (Message.contains("No argument") && Message.contains("totally convincing"))
    		   wordsFoundList += " BDFL finds not convincing,";
       }  
        
      // I'm sorry I won't be able to come to the language summit, but I would like if
      // possible to expedite a pronouncement on PEP 391 (configuring logging using
      //dictionaries). I believe I addressed all the comments made on the discussion
     // threads mentioned in the PEP and so I'm not sure what more I need to do to get a pronouncement. I guess the stdlib slot gives an opportunity for people to air their views and so I'd be grateful if you added it to the agenda.
       //status changed

   //    if (Message.contains("Status:") && Message.contains("Status:") )  {
    	    if (statusFrom != null || statusTo != null ) {
    	    	wordsFoundList += " Status Changed " + statusFrom + " " + statusTo;
    	    }
    //   }
	return wordsFoundList = wordsFoundList + " " + message_ID.toString();
   }
   
   private static void generateCsvFile(String sFileName)
   {
		try
		{
		    FileWriter writer = new FileWriter(sFileName);
			 
		    writer.append("DisplayName");
		    writer.append(',');
		    writer.append("Age");
		    writer.append('\n');
	
		    writer.append("MKYONG");
		    writer.append(',');
		    writer.append("26");
	            writer.append('\n');
				
		    writer.append("YOUR NAME");
		    writer.append(',');
		    writer.append("29");
		    writer.append('\n');
				
		    //generate whatever data you want
				
		    writer.flush();
		    writer.close();
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} 
    }
   
   public static void initWordsList(List<String> v_wordsList){
		 wordsList.add("BDFL");
		 wordsList.add("BDFL decision");
		 wordsList.add("BDFL comments");
		 wordsList.add("BDFL rejection");
		 wordsList.add("feedback");
		 wordsList.add("comment");
		 wordsList.add("updated PEP"); 
		 wordsList.add("indicate your vote"); 
		 //proposal
		 wordsList.add("a proposal");
		 wordsList.add("put forward a proposal");
		 wordsList.add("I hereby put forward a proposal");
		 //discusss
		 wordsList.add("community to discuss the proposal and"); 
		 //vote
		 wordsList.add("agree on some way to count votes");
		 wordsList.add("agree on some way to count votes, then count them");
		 //asking to vote
		 wordsList.add("remind you"); 
		 wordsList.add("HOW TO VOTE");
		 wordsList.add("UNOFFICIAL vote"); 
		 wordsList.add("Please participate if you have the time");
		 wordsList.add("Please participate");
		 wordsList.add("A copy of the ballot, and instructions on how to vote"); 
		 wordsList.add("and instructions on how to vote"); 
		 wordsList.add("how to vote");
		 wordsList.add("voter preferences");
		 wordsList.add("voting procedure");
		 wordsList.add("This is simply an advisory poll");
		 wordsList.add("advisory poll");
		 wordsList.add("Proposals are welcome");
		 
		 //Important findings
		// Based on my experience and previous research, I have
		// selected what I consider to be the best available methods to gather
		// voter opinions and arrive at an optimal social choice.  It is unlikely
		// that any discussion that might take place here would persuade me
		// otherwise.

		 
		 
		 wordsList.add("count votes");
		 wordsList.add("vote");		
		 wordsList.add("voting");		
		 wordsList.add("poll");
		 wordsList.add("consensus");
		 wordsList.add("complementary vote");
		 wordsList.add("ballot");
		 //results
		 wordsList.add("result in favour of the status-quo");
		 wordsList.add("favour of the status-quo");
		 wordsList.add("status-quo");
		 wordsList.add("unprecedented community response");
		 wordsList.add("community response");
		 
		 wordsList.add("posted the results of the COMPLEMENTARY vote"); 
		 wordsList.add("published the ballots from the OFFICIAL vote");
		 wordsList.add("SINGLE WINNER");
		 wordsList.add("SOCIAL RANKING"); 
		 wordsList.add("PAIRWISE COMPARISONS");
		 
//		 wordsList.add("\\+1");
//		 wordsList.add("\\-1");
//		 wordsList.add("\\+0");
		 wordsList.add("vote 'yes'");
		 wordsList.add("I'm neutral on that");
		 wordsList.add("I'm neutral");
		 wordsList.add("I'd say no");
		 wordsList.add("vote results"); 
		 wordsList.add("vote is summarized ");
		 
		 //discussion
		 wordsList.add("I'd appreciate it if we dicuss before voting");  
		 wordsList.add("dicuss before voting");  
		 wordsList.add("proposal as is looks reasonable");
		 wordsList.add("reasonable");
		 wordsList.add("figure out which election method to use to vote for");
		 wordsList.add("election method to use to vote for");
		 wordsList.add("election method");
		 wordsList.add("which election method to use to vote on the PEP and we'll be done");

		 
		 wordsList.add("pronouncement"); 
		 wordsList.add("pronouncement on PEP"); 
		 wordsList.add("been accepted");
		 wordsList.add("has been accepted");
		 wordsList.add("grateful");
		 wordsList.add("appreciate");
		 wordsList.add("support");
		 wordsList.add("I want to reject this PEP");
		 wordsList.add("reject this PEP");
		 wordsList.add("reject");
		 wordsList.add("majority");
		 wordsList.add("no change");
		 wordsList.add("collectively asking for a change");
		 wordsList.add("asking for a change");
		 
		 //decision
		 wordsList.add("Following the discussion, a vote was held");
		 wordsList.add("Following the discussion");
		 wordsList.add("a vote was held");
		 wordsList.add("While there was an overall interest ");
		 wordsList.add("no one format was able to draw majority support");
		 wordsList.add("draw majority support");
		 wordsList.add("majority support");
		 wordsList.add("Accordingly, the PEP was rejected due to the lack of an overwhelming majority for change");
		 wordsList.add("lack of an overwhelming majority for change");
		 wordsList.add("majority for change");
		 wordsList.add("Also, a Python design principle has been to prefer the status quo whenever there are doubts about which path to take");
		 wordsList.add("prefer the status quo"); 
		 wordsList.add("doubts about which path to take");
		 
		 wordsList.add("Indicate the rejection of PEP");
		   
		 	
		 wordsList.add("blogs");
		 wordsList.add("survey");
		 wordsList.add("offline");
		 wordsList.add("twitter");
		 wordsList.add("plus.google.com");
		 
	//	 {"+1","I like the PEP", "-1" , "I do not like the PEP","feedback",		 "BDFL pronouncement",  "BDFL decision", 	"indicate your vote","vote", "voting","votes", "offline discussion", 	"offline", "twitter", "plus.google.com"};
		  /* statusList.add("Replaced");
		  statusList.add("Rejected");
		  statusList.add("Postponed");
		  statusList.add("Incomplete"); 
		  statusList.add("Superseded");
		  */ 	  		
	  }
   
}
