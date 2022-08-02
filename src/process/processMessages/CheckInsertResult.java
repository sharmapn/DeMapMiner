package Process.processMessages;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import Process.processLabels.TripleProcessingResult;
import Read.readMetadataFromWeb.GetProposalDetailsWebPage;
import connections.MysqlConnect;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CheckInsertResult {
	java.sql.Connection conn = null;
	
	GetProposalDetailsWebPage gpd = new GetProposalDetailsWebPage();

	public CheckInsertResult(){
		MysqlConnect mc = new MysqlConnect();
		conn = mc.connect();
	}
		
	//308	 2003-10-22 	8307	 Raymond Hettinger 	 pep_resurrected 	 
	//After , much discussion on the python-dev list , the PEP has been resurrected its present form .
	// pep resurrected had been identified before, so here we just add the before label = discussion
	//better example 
	//308	 2003-09-09 	32931	 Michael Geary 	 vote_after_discussion 	 
	// `` Following the discussion , a vote was held .	 R:	vote_after_discussion
	public void InsertLabelBefore(Integer pepNumber, String author, Date date, String label, String reverb, String cie, String ollie,
			String previousSentence, String nextSentence, String previousParagarph, String entireParagraph, String nextParagraph){
		//we have state and 
	}
	
	//public void checkInsertResult(Integer pepNumber, String author, Date date, String label, String reverb, String cie, String ollie,
	//		String previousSentence, String nextSentence, String previousParagarph, String entireParagraph, String nextParagraph){
	public void checkInsertResult(TripleProcessingResult resultObject, ProcessingRequiredParameters prp)
	{	 
//		//Now output to file and Console
		boolean repeatedSentenecAndLabel = false,repeatedLabel = false, repeatedSentence = false, emptyRow = false;	
		//Main insertion..we insert false positive as well as we want to evaluate..so these would be sentences which have these terms
		
		String sql = "INSERT INTO results (pep,pepType, messageID, author,authorRole," //numberOfPEPsMentionedInMessage,
				+ "folder,file, date, datetimestamp, messageSubject," //10
				+ "subject, relation, object, currentSentence, reverb, " //15
				+ "clausie, ollie,repeatedLabel,label,labelFoundInMsgSubject," //20	identifierCount
//				+ "ps,ns,pp,ep,np, role, allReasons,reasonInSentence,reasonTermsInMatchedTriple,reasonTermsInNearbySentencesParagraphs, reasonTriplesInNearbySentencesParagraphs,
//				+ "reasonTermsInPreviousSentence,reasonTermsInNextSentence,reasonTriplesInPreviousSentence,reasonTriplesInNextsentence,reasonTriplesInSameSentence,lineNumber," 
				+ "isFirstParagraph, isLastParagraph, paragraphCounter, sentenceCounterInMessage, sentenceCounterInParagraph,role,lineNumber) " //26
				+ "VALUES (?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,  ?,?)"; //  ,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?)"; //44
		 
		try {	
			//Checking Number Of PEPs Mentioned In Message
//			Integer IdentifierCount = checkingNumberOfPEPsMentionedInMesssage(resultObject);			
			Integer pepNumber = resultObject.getPepNumber();
			String pt = gpd.getPepTypeForPep(pepNumber, conn);
			PreparedStatement statement = conn.prepareStatement(sql);
			//first 5
			statement.setInt	(1, resultObject.getPepNumber());			statement.setString(2, pt);			
			Integer mid = resultObject.getMessageID(); 				if (mid ==null)					mid=0;
			
			statement.setInt	(3, mid);									statement.setString (4, resultObject.getAuthor());	
			String AR = resultObject.getAuthorsRole(); 		System.out.println();				statement.setString (5, AR);
			//Second 5
			statement.setString (6, resultObject.getFolder());			//statement.setInt (5, resultObject.getNumberOfPEPsMentionedInMessage());
//			statement.setInt (7, IdentifierCount);			
			statement.setString (7, resultObject.getFile());			
			//sqlDate ...	
			java.util.Date utilDate = resultObject.getDate();			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());		java.sql.Date finalDate = sqlDate;				
			statement.setDate	 (8, finalDate);	
			//if datetimestamp field is empty, then convert existing date to timestamp
			Timestamp timestamp = resultObject.getDateTimeStamp();
			if(timestamp==null){				timestamp = new java.sql.Timestamp(finalDate.getTime());			}			
			statement.setTimestamp(9,timestamp);	statement.setString	 (10, resultObject.getMessageSubject());
			//Third 5
			statement.setString	 (11, resultObject.getLabelSubject());		statement.setString	 (12, resultObject.getLabelRelation());			statement.setString	 (13, resultObject.getLabelObject());			
			statement.setString  (14, resultObject.getCurrentSentence()); 	statement.setString (15, resultObject.getReverb());
			//statement.setString (7, resultObject.getClausIE());	
			
//			String out = null;
//			if(resultObject.getFinalLabel() !=null ){
//				out = resultObject.getFinalLabel();
//				if (resultObject.getFinalReason()!=null){
//					out = out + "_BECAUSE_" + resultObject.getFinalReason();
//				}				
//			}
			String finalString = "";
			//getFinalReason()					//&& resultObject.getFinalReason() ==null
			//System.out.println("CLausIE to be Inserted " +resultObject.getClausIE());
			if (resultObject.getClausIE() == null || resultObject.getClausIE().isEmpty()) {
				finalString = "";
			} 
			else {				
				if (resultObject.getFinalReasonListArrayAsString() == null  || resultObject.getFinalReasonListArrayAsString().isEmpty()) {
					finalString = resultObject.getClausIE();
				} else {	finalString = resultObject.getClausIE();
					//make sure not any terms of reason in label
				    /*if (resultObject.getLabel().contains( resultObject.getFinalReasonListArrayAsString() ) )   {}
				    else    {
				    	//reasons commented playing up ppp
				    	//finalString = resultObject.getClausIE() + "_because_" + resultObject.getFinalReason();
				    	finalString = resultObject.getClausIE();
				    } */
				}
			}
//			System.out.println("CLausIE Inserted " +finalString);			 	
			//statement.setString (7,out);
			
			//fourth set of 5 , clausie, ollie,repeatedLabel,identifierCount,label,
			statement.setString (16,finalString);	statement.setString (17, resultObject.getOllie());			//statement.setString (8,"insertDoubleHere");
			//statement.setInt  (13, IdentifierCount); 
			statement.setBoolean (18, repeatedLabel);	
			statement.setString	 (19, resultObject.getLabel());		statement.setBoolean (20, resultObject.getMsgSubjectContainsLabel() );	
			/*
			statement.setString  (20, resultObject.getPrevSentence() );		statement.setString  (21, resultObject.getNextSentence()  );
			statement.setString  (22, resultObject.getPrevParagraph() );		statement.setString  (23, resultObject.getCurrParagraph() );
			statement.setString  (24, *resultObject.getNextParagraph() );				
			statement.setString  (25, resultObject.getRole());				statement.setString  (26, resultObject.getReasonObject().getDistinctReasons() );			
			//reasons
			statement.setString  (27, resultObject.getReasonsInSentence());
			statement.setString  (28, resultObject.getReasonObject().getReasonTermsInMatchedTriple());
			statement.setString  (29, resultObject.getReasonObject().getReasonTermsInNearbySentencesParagraphs());
			statement.setString  (30, resultObject.getReasonObject().getReasonTriplesInNearbySentencesParagraphs());
			
			
			//if datetimestamp field is empty, then convert existing date to timestamp
			Timestamp timestamp = resultObject.getDateTimeStamp();
			if(timestamp==null){				timestamp = new java.sql.Timestamp(finalDate.getTime());			}			
			statement.setTimestamp(32,timestamp);
			statement.setString  (33, resultObject.getReasonObject().getReasonTermsInPreviousSentence());	statement.setString  (34, resultObject.getReasonObject().getReasonTermsInNextSentence());
			statement.setString  (35, resultObject.getReasonObject().getReasonTriplesInPreviousSentence());	statement.setString  (36, resultObject.getReasonObject().getReasonTriplesInPreviousSentence());
			statement.setString  (37, resultObject.getReasonObject().getReasonTriplesInSentence());			
					
			*/ 
//			statement.setString  (20, resultObject.getRole());
			statement.setBoolean (21, resultObject.isFirstParagraph());	//was 40th parameter before
			statement.setBoolean (22, resultObject.isLastParagraph());
			//dec 2018 ..sentence and paragrapgh counter
			statement.setInt (23, resultObject.getParagraphCounter());	
			statement.setInt (24, resultObject.getSentenceCounter());
			Integer h =resultObject.getSentenceCounterInParagraph();
			if(h==null) h=0;
			statement.setInt (25, h);
			statement.setString  (26, resultObject.getRole());	statement.setInt  (27, resultObject.getLineNumber());
			//System.out.println("\t\t\tdatetimestamp resultObject.getDateTimeStamp(): "+ resultObject.getDateTimeStamp() );
			
			int rowsInserted = statement.executeUpdate();
			if (rowsInserted > 0) {
				if(prp.getOutputfordebug())
				System.out.println("SVO Inserted in DB successfully!, AR: "+ AR+ ", " +resultObject.getLabelSubject() + " , " + resultObject.getLabelRelation() + " , " + resultObject.getLabelObject());
			    //System.out.println("A new result record was inserted in DB successfully!");
			}
		} catch (SQLException e) {		
			e.printStackTrace();
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			System.out.println("Exception while inserting SVO...1239" + ex.toString());
			ex.printStackTrace();
		}
	}
	
	//if x contains any terms in y,(any y in x)
	//make sure not any terms of reason in label
    public static Boolean NotAnyMatches(String xLabel, String yLabel){
    	//Boolean found = false;
    	if (xLabel != null && yLabel != null){
    		//x = x.replace("_", " ");
			//y = y.replace("_", " ");
    		String[] x = xLabel.split("_");    		String[] y = yLabel.split("_");
    		//if x contains all y   		
    			for (String y1 :y)  {
        			if (xLabel.contains(y1))
        				return false;        				
        		}    		
    	}
    	return true;
    }
	
	public void insertDoubleResultRecordInDatabase(TripleProcessingResult resultObject, Boolean repeatedLabel) 
	{
		//Main insertion
		String sql = "INSERT INTO results (pep, messageID, author, folder, numberOfPEPsMentionedInMessage,file, date,messageSubject,label,subject, relation, object, "
				+ "currentSentence, reverb, clausie, ollie, repeatedLabel,ps,ns,pp,ep,np, role, allReasons, reasonInSentence,reasonTermsInMatchedTriple, "
				+ "reasonTermsInNearbySentencesParagraphs, reasonTriplesInNearbySentencesParagraphs,datetimestamp,lineNumber) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		try {			
			//Checking Number Of PEPs Mentioned In Message
			checkingNumberOfPEPsMentionedInMesssage(resultObject);
			
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt	(1, resultObject.getPepNumber());			//comment tis temprary but include this later
			statement.setInt	(2, resultObject.getMessageID());		statement.setString (3, resultObject.getAuthor());
			statement.setString (4, resultObject.getFolder());			statement.setInt    (5, resultObject.getNumberOfPEPsMentionedInMessage());
			statement.setString (6, resultObject.getFile());	//		System.out.println("here 2222");
			//sqlDate ...	
				java.util.Date utilDate = resultObject.getDate();				java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());					java.sql.Date finalDate = sqlDate;
			statement.setDate	 (7, finalDate);			statement.setString	 (8, resultObject.getMessageSubject());				statement.setString	 (9, resultObject.getLabel());
			statement.setString	 (10, resultObject.getLabelSubject());			statement.setString	 (11, resultObject.getLabelRelation());			statement.setString	 (12, resultObject.getLabelObject());
			statement.setString  (13, resultObject.getCurrentSentence());		statement.setString  (14, resultObject.getReverb());			statement.setString  (15,resultObject.getClausIE());
			//statement.setString (8,"insertDoubleHere");
			statement.setString  (16, resultObject.getOllie());					statement.setBoolean (17, repeatedLabel);			statement.setString  (18, resultObject.getPrevSentence());
			statement.setString  (19, resultObject.getNextSentence());			statement.setString  (20, resultObject.getPrevParagraph());				statement.setString  (21, resultObject.getCurrParagraph());
			statement.setString  (22, resultObject.getNextParagraph());			statement.setString  (23, resultObject.getRole());			statement.setString  (24, resultObject.getReasonObject().getDistinctReasons());
			
			//reasons
			statement.setString  (25, resultObject.getReasonObject().getReasonsInSentence());			statement.setString  (26, resultObject.getReasonObject().getReasonTermsInMatchedTriple());
			statement.setString  (27, resultObject.getReasonObject().getReasonTermsInNearbySentencesParagraphs());
			statement.setString  (28, resultObject.getReasonObject().getReasonTriplesInNearbySentencesParagraphs());
			statement.setTimestamp(29,resultObject.getDateTimeStamp());			statement.setInt  (30, resultObject.getLineNumber());
			
			int rowsInserted = statement.executeUpdate();
			if (rowsInserted > 0) {
			    System.out.println("A new result record was inserted in DB successfully 2011!" + " SVO Inserted! "  +resultObject.getLabelSubject() + " " + resultObject.getLabelRelation() + " " + resultObject.getLabelObject());
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception ex) {
			// TODO Auto-generated catch block			System.out.println("Exception: " + ex.toString());
		}		
	}
	
	public void insertResultStatesInDatabase(TripleProcessingResult resultObject, Boolean repeatedLabel, ProcessingRequiredParameters prp) 
	{			
		//Main insertion
		String sql = "INSERT INTO results (pep, author,folder, file, date, label,subject, relation, object, currentSentence, reverb, clausie, ollie, role,datetimestamp,messageID,lineNumber) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";		 
		try {
			
			//Checking Number Of PEPs Mentioned In Message
			checkingNumberOfPEPsMentionedInMesssage(resultObject);			
			PreparedStatement statement = conn.prepareStatement(sql);			
			statement.setInt	 (1, resultObject.getPepNumber());			statement.setString  (2, resultObject.getAuthor());	
			statement.setString  (3, resultObject.getFolder());				statement.setString  (4, resultObject.getFile());	
			//sqlDate ...	
				java.util.Date utilDate = resultObject.getDate();				java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
				java.sql.Date finalDate = sqlDate;
			statement.setDate	 (5, finalDate);			statement.setString	 (6, resultObject.getLabel());			statement.setString	 (7, resultObject.getLabelSubject());
			statement.setString	 (8, resultObject.getLabelRelation());			statement.setString	 (9, resultObject.getLabelObject());
			System.out.println("SVO Inserted! "  +resultObject.getLabelSubject() + " " + resultObject.getLabelRelation() + " " + resultObject.getLabelObject());
				
			statement.setString  (10, resultObject.getCurrentSentence());	statement.setString  (11, resultObject.getReverb());				
			statement.setString  (12,resultObject.getClausIE());			statement.setString  (13, resultObject.getOllie());			statement.setString  (14, resultObject.getRole());
			//System.out.println("resultObject.getDateTimeStamp()!"+resultObject.getDateTimeStamp());	
			//System.out.println("resultObject.getMessageID()!"+resultObject.getMessageID());	
			statement.setTimestamp(15,resultObject.getDateTimeStamp());		
			Integer mid = resultObject.getMessageID();
			statement.setInt	(16, mid);			
			//jan 2019, while checking final results
			Integer pp = 5000000;
			if(mid>pp) { 
				if(prp.getOutputfordebug())
					System.out.println(" mid > not inserting returning: "+pp);
				return; //we dont need commit states
			}else System.out.println(" mid < continuing : "+pp);
			
			statement.setInt  (17, resultObject.getLineNumber());	
			int rowsInserted = statement.executeUpdate();
			if (rowsInserted > 0) {			    System.out.println("A new state result record was inserted in DB successfully!");			}
			else{				System.out.println("No record inserted in db!");			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			  System.out.println("Error inserting a new state result record in DB ");
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			  System.out.println("Error inserting a new state result record in DB ");
		}
	}
	
	private Integer checkingNumberOfPEPsMentionedInMesssage(TripleProcessingResult resultObject) {
		Integer count = 1;
		//System.out.println("Checking Number Of PEPs Mentioned In Message "+ resultObject.getPepNumber() + " " + resultObject.getLabel());
		try
		{
			//if you dont want to store repeated labels, mark the repeated label column as true
			//select label fro that pepnumber if that exists, then set column is true
			String sqlSelect = "SELECT IdentifierCount FROM allmessages where messageID=" + resultObject.getMessageID() + " ;";		 
			Statement statement = conn.createStatement();			ResultSet result = statement.executeQuery(sqlSelect);	
			//check for repeated sentence here				 
			if (result.next()){		   
				count = result.getInt(1);
			    System.out.println("Number Of PEPs Mentioned In This Message "+count);
			    resultObject.setNumberOfPEPsMentionedInMessage(count);
	//		    insertResultRecordInDatabase(pepNumber, author, date, label, reverb, cie, ollie, true,
	//		    		previousSentence, nextSentence, previousParagarph, entireParagraph, nextParagraph);
			    //insertResultRecordInDatabase(resultObject, true);
			}
			else {		   
			    //System.out.println("no repeated label found, inserting with flag with false");
	//		    insertResultRecordInDatabase(pepNumber, author, date, label, reverb, cie, ollie, false,
	//		    		previousSentence, nextSentence, previousParagarph, entireParagraph, nextParagraph);
			    //insertResultRecordInDatabase(resultObject,false);
			}
		}
		catch(SQLException e){
			System.out.println(" SQL Error" + e.toString());
		}		
		return count;
	}
	
	public void outputAllResultsToFile(Integer pepNumber){	}	
	Integer counter;	
	public void incrementCounter(){		counter = counter +1;	}
	public void clear(){		counter=0;	}	
	public Integer getCounter(){		return counter;	}	
	public void finalize() {	    //System.out.println("Probability instance is getting destroyed"); 
	}
}
