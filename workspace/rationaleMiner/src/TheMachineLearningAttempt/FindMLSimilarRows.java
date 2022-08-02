package TheMachineLearningAttempt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import connections.MysqlConnect;
//check to see the problems..the rows that ML has identified as zero, which should be 1, we check to see whats the problem
public class FindMLSimilarRows {
	static MysqlConnect mc = new MysqlConnect();	static Connection conn;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		conn = mc.connect();
		try
		{
			String sentence="",msgSubject="",authorRole="", p ="P", cr= "CR", s= "SENT", ms="MS";
			String termsmatched ="", messageSubjectStatesSubstatesList,messageSubjectDecisionTermList,messageSubjectIdentifiersTermList,
			messageSubjectEntitiesTermList ="",messageSubjectSpecialTermList ="",currSentenceReasonsTermsFoundList ="", currSentenceReasonsIdentifierTermsList ="", 
			currSentenceStatesSubstatesList ="", currSentenceIdentifiersTermList ="",currSentenceEntitiesTermList ="", currSentenceSpecialTermList ="", 
			currSentenceDecisionTermList ="", restOfParagraphStateTermList ="",restOfParagraphEntityTermList ="",
			restOfParagraphIdentifiersTermList ="", restOfParagraphSpecialTermList ="",	restOfParagraphDecisionTermList ="", restOfParagraphReasonsTermsList ="";
			
			Integer mid = null,  proposal = null, containsReason=null,dateDiff=null;
			String sql0;		Statement stmt0;		ResultSet rs0, rs;	
			System.out.format("%5s,%2s, %120s,%20s", p, cr, s,ms); System.out.println("");
			sql0 = "SELECT * from autoextractedreasoncandidatesentences where containsReason = 1 order by proposal ";  //where pep != -1 order by pep asc LIMIT 5000 "; //and proposal = 386
			stmt0 = conn.createStatement();			rs0 = stmt0.executeQuery( sql0 );
			//for each row we get similar rows
			while (rs0.next()) {
				sentence = rs0.getString("sentence"); 			msgSubject	= rs0.getString("messagesubject").replaceAll(",", "");		dateDiff = 	rs0.getInt("datediff");		authorRole	= rs0.getString("authorRole");	
				proposal = rs0.getInt("proposal");				containsReason = rs0.getInt("containsReason"); mid = rs0.getInt("messageid");	
				Integer authorroleweight= rs0.getInt("authorroleweight"); 					 Integer datediffweight = rs0.getInt("datediffweight");
				//terms
				termsmatched= rs0.getString("termsmatched").replaceAll(",", ""); 										messageSubjectStatesSubstatesList = rs0.getString("messageSubjectStatesSubstatesList").replaceAll(","," "); 
				messageSubjectDecisionTermList = rs0.getString("messageSubjectDecisionTermList").replaceAll(","," ");  	messageSubjectIdentifiersTermList = rs0.getString("messageSubjectIdentifiersTermList").replaceAll(","," "); 
				messageSubjectEntitiesTermList = rs0.getString("messageSubjectEntitiesTermList").replaceAll(","," ");  	messageSubjectSpecialTermList 	  = rs0.getString("messageSubjectSpecialTermList").replaceAll(","," ");
				currSentenceReasonsTermsFoundList   = rs0.getString("currSentenceReasonsTermsFoundList").replaceAll(","," ");  	 currSentenceReasonsIdentifierTermsList = rs0.getString("currSentenceReasonsIdentifierTermsList").replaceAll(","," "); 
				currSentenceStatesSubstatesList 	= rs0.getString("currSentenceStatesSubstatesList").replaceAll(","," ");  	 currSentenceIdentifiersTermList = rs0.getString("currSentenceIdentifiersTermList").replaceAll(","," ");  
				currSentenceEntitiesTermList = rs0.getString("currSentenceEntitiesTermList").replaceAll(","," ");  		 		currSentenceSpecialTermList = rs0.getString("currSentenceSpecialTermList").replaceAll(","," ");  
				currSentenceDecisionTermList = rs0.getString("currSentenceDecisionTermList").replaceAll(","," ");  
				restOfParagraphStateTermList = rs0.getString("restOfParagraphStateTermList").replaceAll(","," ");  			restOfParagraphEntityTermList= rs0.getString("restOfParagraphEntityTermList").replaceAll(","," "); 
				restOfParagraphIdentifiersTermList = rs0.getString("restOfParagraphIdentifiersTermList").replaceAll(","," ");  restOfParagraphSpecialTermList= rs0.getString("restOfParagraphSpecialTermList").replaceAll(","," "); 
				restOfParagraphDecisionTermList= rs0.getString("restOfParagraphDecisionTermList").replaceAll(",",""); 		restOfParagraphReasonsTermsList= rs0.getString("restOfParagraphReasonsTermsList").replaceAll(","," "); 
				//curr sentence
				Integer currSentencereasonstermsfoundcount = rs0.getInt("currSentencereasonstermsfoundcount"); 	 Integer currSentencereasonsIdentifierTermsCount = rs0.getInt("currSentencereasonsIdentifierTermsCount");
				Integer currSentencestatesSubstatesCount   = rs0.getInt("currSentencestatesSubstatesCount");   	 Integer currSentenceidentifiersTermCount = rs0.getInt("currSentenceidentifiersTermCount");
				Integer currSentenceentitiesTermCount	   = rs0.getInt("currSentenceentitiesTermCount"); 		 Integer currSentencespecialTermCount = rs0.getInt("currSentencespecialTermCount"); 
				Integer currSentencedecisionTermCount      = rs0.getInt("currSentencedecisionTermCount");  		 
				//message subject
				Integer messageSubjectStatesSubstatesListCount	= rs0.getInt("messageSubjectStatesSubstatesListCount"); 							 
				Integer messageSubjectEntitiesTermListCount = rs0.getInt("messageSubjectEntitiesTermListCount"); 		 Integer messageSubjectDecisionTermListCount = rs0.getInt("messageSubjectDecisionTermListCount");
				Integer messageSubjectProposalIdentifiersTermListCount = rs0.getInt("messageSubjectProposalIdentifiersTermListCount"); 		Integer messageSubjectSpecialTermListCount = rs0.getInt("messageSubjectSpecialTermListCount");
				//rest of paragraph
				Integer restOfParagraphStateTermsCount     = rs0.getInt("restOfParagraphStateTermsCount");       Integer restOfParagraphIdentifiersTermsCount = rs0.getInt("restOfParagraphIdentifiersTermsCount"); 
				Integer restOfParagraphEntityTermsCount    = rs0.getInt("restOfParagraphEntityTermsCount");      Integer restOfParagraphDecisionTermsCount  = rs0.getInt("restOfParagraphDecisionTermsCount");
				Integer restOfParagraphSpecialTermsCount   = rs0.getInt("restOfParagraphSpecialTermsCount");     Integer restOfParagraphReasonsTermsCount  = rs0.getInt("restOfParagraphReasonsTermsCount");				
				//additional
				Integer messsageTypeIsReasonMessage 	   = rs0.getInt("messsageTypeIsReasonMessage");          Integer sentenceLocationInFirstLastParagraph = rs0.getInt("sentenceLocationInFirstLastParagraph");
				Integer SameMsgSubAsStateTriple 		   = rs0.getInt("SameMsgSubAsStateTriple");
				System.out.print(//"%5d,%2d, %120s,%20s,%10s,%5d, %2d, %2d, %2d, %2d, %2d, %2d, %2d, %2d, %2d, %2d, %2d, %2d, %2d, %2d, %2d, %2d, %2d, %2d, %2d, %2d, %2d, %2d, %2d, ", 
						proposal+","+ mid+ ","+ containsReason+","+ sentence+","+msgSubject+","+ authorRole+","+ dateDiff+","+ authorroleweight+","+datediffweight+","+	
						currSentencereasonstermsfoundcount+","+currSentencereasonsIdentifierTermsCount+","+
						currSentencestatesSubstatesCount+","+ currSentenceidentifiersTermCount+","+currSentenceentitiesTermCount+","+ currSentencespecialTermCount+","+ currSentencedecisionTermCount+","+ 
						messageSubjectStatesSubstatesListCount+","+ messageSubjectEntitiesTermListCount+","+ messageSubjectDecisionTermListCount+","+ messageSubjectProposalIdentifiersTermListCount+","+messageSubjectSpecialTermListCount+","+
						restOfParagraphStateTermsCount+","+ restOfParagraphIdentifiersTermsCount+","+ restOfParagraphEntityTermsCount+","+ restOfParagraphDecisionTermsCount+","+restOfParagraphSpecialTermsCount+","+
						restOfParagraphReasonsTermsCount+","+
						messsageTypeIsReasonMessage+","+ sentenceLocationInFirstLastParagraph+","+SameMsgSubAsStateTriple+","
						//terms
						+ termsmatched + ","+ messageSubjectStatesSubstatesList+ ","+ messageSubjectDecisionTermList+ ","+ messageSubjectIdentifiersTermList+ ","
						+ messageSubjectEntitiesTermList+ "," + messageSubjectSpecialTermList+ ","+ currSentenceReasonsTermsFoundList+","+ currSentenceReasonsIdentifierTermsList+","
						+ currSentenceStatesSubstatesList + ","+ currSentenceIdentifiersTermList+","+ currSentenceEntitiesTermList+ ","+ currSentenceSpecialTermList +","
						+ currSentenceDecisionTermList+ ","+ restOfParagraphStateTermList + ","+ restOfParagraphEntityTermList + ","+ restOfParagraphIdentifiersTermList + ","
						+ restOfParagraphSpecialTermList + ","+ restOfParagraphDecisionTermList + ","+ restOfParagraphReasonsTermsList);
				System.out.println("");
				//now we select similar rows but are zeros
		        String query = " select * from autoextractedreasoncandidatesentences where  containsReason = 0 and 195870" //and proposal = "+proposal+"; ";
				+ " authorroleweight = " + authorroleweight + " and datediffweight = " + datediffweight  
				//curr sentence
				+ " and currSentencereasonstermsfoundcount = "		+ currSentencereasonstermsfoundcount
				+ " and currSentencereasonsIdentifierTermsCount = " + currSentencereasonsIdentifierTermsCount + " and currSentencestatesSubstatesCount = " 	+ currSentencestatesSubstatesCount
				+ " and currSentencedecisionTermCount = " + currSentencedecisionTermCount 				+ " and  currSentenceidentifiersTermCount = "		+ currSentenceidentifiersTermCount
				+ " and currSentenceentitiesTermCount = " + currSentenceentitiesTermCount 				+ " and  currSentencespecialTermCount = " 			+ currSentencespecialTermCount								
				//message subject
				+ " and messageSubjectStatesSubstatesListCount = " + messageSubjectStatesSubstatesListCount				
				+ " and messageSubjectEntitiesTermListCount = " + messageSubjectEntitiesTermListCount 	+ " and  messageSubjectDecisionTermListCount = " 	+ messageSubjectDecisionTermListCount 
				+ " and messageSubjectProposalIdentifiersTermListCount = " + messageSubjectProposalIdentifiersTermListCount + " and  messageSubjectSpecialTermListCount = " + messageSubjectSpecialTermListCount 
				//rest of paragraph
				+ " and restOfParagraphStateTermsCount = " + restOfParagraphStateTermsCount				+ " and  restOfParagraphIdentifiersTermsCount = " 	+ restOfParagraphIdentifiersTermsCount 
				+ " and restOfParagraphEntityTermsCount = " + restOfParagraphEntityTermsCount 			+ " and  restOfParagraphDecisionTermsCount = " 		+ restOfParagraphDecisionTermsCount 
				+ " and restOfParagraphSpecialTermsCount = " + restOfParagraphSpecialTermsCount 		+ " and  restOfParagraphReasonsTermsCount = " 		+ restOfParagraphReasonsTermsCount 				
				+ " and messsageTypeIsReasonMessage = " + messsageTypeIsReasonMessage 					+ " and  sentenceLocationInFirstLastParagraph = " 	+ sentenceLocationInFirstLastParagraph 
				+ " and SameMsgSubAsStateTriple = " + SameMsgSubAsStateTriple 				
				+ " and sentence != '"+ sentence + "' order by proposal asc;";
		        Statement stmt = conn.createStatement();	
		        rs = stmt.executeQuery(query);
		        while (rs.next()) {
		        	String sent = rs.getString("sentence");			Integer prop = rs.getInt("proposal");				Integer creason = rs.getInt("containsReason"); mid = rs.getInt("messageid");	
		        	msgSubject	= rs.getString("messagesubject").replaceAll(",", ""); 		dateDiff = 	rs.getInt("datediff");		authorRole	= rs.getString("authorRole");
		        	//authorroleweight= rs.getInt("authorroleweight"); 					  datediffweight = rs.getInt("datediffweight");
		        	//terms
		        	termsmatched= rs.getString("termsmatched").replaceAll(",", "");  										messageSubjectStatesSubstatesList = rs.getString("messageSubjectStatesSubstatesList").replaceAll(","," ");  
					messageSubjectDecisionTermList = rs.getString("messageSubjectDecisionTermList").replaceAll(","," ");  	messageSubjectIdentifiersTermList = rs.getString("messageSubjectIdentifiersTermList").replaceAll(","," "); 
					messageSubjectEntitiesTermList = rs.getString("messageSubjectEntitiesTermList").replaceAll(","," ");  	messageSubjectSpecialTermList 	  = rs.getString("messageSubjectSpecialTermList").replaceAll(","," "); 
					currSentenceReasonsTermsFoundList   = rs.getString("currSentenceReasonsTermsFoundList").replaceAll(","," ");  	 currSentenceReasonsIdentifierTermsList = rs.getString("currSentenceReasonsIdentifierTermsList").replaceAll(","," ");  
					currSentenceStatesSubstatesList 	= rs.getString("currSentenceStatesSubstatesList").replaceAll(","," "); 	 currSentenceIdentifiersTermList = rs.getString("currSentenceIdentifiersTermList").replaceAll(","," "); 
					currSentenceEntitiesTermList = rs.getString("currSentenceEntitiesTermList").replaceAll(","," ");  		 		currSentenceSpecialTermList = rs.getString("currSentenceSpecialTermList").replaceAll(","," "); 
					currSentenceDecisionTermList = rs.getString("currSentenceDecisionTermList").replaceAll(","," ");  
					restOfParagraphStateTermList = rs.getString("restOfParagraphStateTermList").replaceAll(","," "); 			restOfParagraphEntityTermList= rs.getString("restOfParagraphEntityTermList").replaceAll(","," "); 
					restOfParagraphIdentifiersTermList = rs.getString("restOfParagraphIdentifiersTermList").replaceAll(","," ");  restOfParagraphSpecialTermList= rs.getString("restOfParagraphSpecialTermList").replaceAll(","," "); 
					restOfParagraphDecisionTermList= rs.getString("restOfParagraphDecisionTermList").replaceAll(","," "); 	 	restOfParagraphReasonsTermsList= rs.getString("restOfParagraphReasonsTermsList").replaceAll(","," "); 
		        	System.out.print(prop+ ","+ mid+"," + creason+ ","+ sent + ","+msgSubject+","+ authorRole+","+ dateDiff //+","+ authorroleweight+","+datediffweight	
		        			+ ",,,,,,,,,,,,,,,,,,,,,,,,"
		        			+ termsmatched + ","+ messageSubjectStatesSubstatesList+ ","+ messageSubjectDecisionTermList+ ","+ messageSubjectIdentifiersTermList
							+ ","+ messageSubjectEntitiesTermList+ "," + messageSubjectSpecialTermList+ ","+ currSentenceReasonsTermsFoundList+","+ currSentenceReasonsIdentifierTermsList+","
							+ currSentenceStatesSubstatesList + ","+ currSentenceIdentifiersTermList+","+ currSentenceEntitiesTermList+ ","+ currSentenceSpecialTermList +","
							+ currSentenceDecisionTermList+ ","+ restOfParagraphStateTermList + ","+ restOfParagraphEntityTermList + ","+ restOfParagraphIdentifiersTermList
							+ ","+ restOfParagraphSpecialTermList + ","+ restOfParagraphDecisionTermList + ","+ restOfParagraphReasonsTermsList);
		        	System.out.println("");
		        	
		        }				
			}			
		}   catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");			System.out.println(StackTraceToString(se)  );	
		}   catch (Exception e)	    {
			System.err.println("Got an exception! ");			System.out.println(StackTraceToString(e)  );	
		}
		
		mc.disconnect();
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
