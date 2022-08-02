package GUI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import connections.MysqlConnectForQueries;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;

//retrieve cause effect given sentence and paragraph number, direct from file - not database 
//refer to UpdateMessageNumberInFile.java
public class InsertUpdateGet_CauseEffectSentences {
	
	static MysqlConnectForQueries mc = new MysqlConnectForQueries();
	static String extractedCauseSentence, extractedEffectSentence;
	static Connection conn;
	
	public static void main(String args[])
	{	
		String label = "accepted"; int pep = 3144;
		initDB();
		callExtractor(pep, label);
		System.out.println(getExtractedCauseSentence());
		System.out.println(getExtractedEffectSentence());
	}
	
	public static void callExtractor(int v_pep, String v_label) {
		int rowid, msgNumInFile,messageCounterinFileTracker=0;
		String sql,pep,folder,file,finalFileLocation, rootFolder = "C:\\datasets\\", addurl = "\\srv\\mailman\\archives\\private\\"; 
		int causeParagraphNum,causeSentenceNum, effectParagraphNum, effectSentenceNum,msgInFileCounter=0; String extractMainMessage="";;
		Statement stmt; 		ResultSet rs;
		BufferedReader br = null; String line; 
		try
		{
//			Connection conn = mc.connect();	
			//for all entries in trainigData table //get details for each row //get folder and file and paragraph and sentence
			sql = "select * from trainingData where pep = "+v_pep+ " and label = '"+v_label+"';";  
			stmt = conn.createStatement();
			rs = stmt.executeQuery( sql );			
			
			while (rs.next()){	//for each folder
				pep = rs.getString("pep"); 	folder = rs.getString("folder"); file = rs.getString("file");  rowid = rs.getInt("id");
				causeParagraphNum= rs.getInt("causeParagraphNum");   causeSentenceNum = rs.getInt("causeSentenceNum");	effectParagraphNum= rs.getInt("effectParagraphNum"); effectSentenceNum = rs.getInt("effectSentenceNum");
				msgNumInFile =  rs.getInt("msgNumberInFile");
				//folder= folder.replace("C:\\datasets\\","");
				finalFileLocation = rootFolder + folder + "\\" + file + addurl + folder + "\\" + file;
				System.out.println("Started processing for mailing List "+ finalFileLocation );
				System.out.println("msgNumberInFile "+ msgNumInFile+ " causeParagraphNum  " + causeParagraphNum  + " causeSentenceNum  " + causeSentenceNum 
																   + " effectParagraphNum " + effectParagraphNum + " effectSentenceNum " + effectSentenceNum );				
				//open file //read entire file contents to a string
				String content = new String(Files.readAllBytes(Paths.get(finalFileLocation)));				
				br = new BufferedReader(new FileReader(finalFileLocation));	//go to the msg in file
				boolean msgFound=false;
				while ((line = br.readLine()) != null) {
					if (line.startsWith("From ")) {
						msgInFileCounter++;						
						if(!msgFound && msgInFileCounter==msgNumInFile) {
							msgFound=true;						//System.out.println("msgFound=true");
						}
						if(msgFound && msgInFileCounter>msgNumInFile) {
							msgFound=false;						//System.out.println("msgFound=false");
						}					
					}
//					if(line.contains("satisfactorily resolved")) {
//						System.out.println("msgInFileCounter "+msgInFileCounter);
//					}					
					if(msgFound) {
						extractMainMessage = extractMainMessage + line + "\n";	//System.out.println("adding line to msg " + line);
					}					
				}
//				System.out.println("Total messages in File: "+ msgInFileCounter);
				msgInFileCounter=0;
				//System.out.println(extractMainMessage);
				//pass it to function which would return/update two sentences				
				extractedCauseSentence = getCauseEffectSentences_givenParagraphAndSentence(extractMainMessage,causeParagraphNum,causeSentenceNum);	//get cause				  
				extractedEffectSentence = getCauseEffectSentences_givenParagraphAndSentence(extractMainMessage,effectParagraphNum,effectSentenceNum);//get effect
				populateCauseEffectSentences(rowid, file);
			}			
			//also have another way of retrieving using messageid
		}
		catch (Exception e)	    {
			System.out.println(StackTraceToString(e)  ); System.err.println("Got an exception! "); 	System.err.println(e.getMessage() + " pep ");
		}
	}

	public static void populateCauseEffectSentences(int rowid, String file) throws SQLException {
		PreparedStatement preparedStmt;
		String updateQuery = "update trainingData set causeSentence = ?, effectSentence =? where id = ?";//start updating 'messageNumberInFile' as already ordered
		  preparedStmt = conn.prepareStatement(updateQuery);
		  preparedStmt.setString(1, extractedCauseSentence);	  preparedStmt.setString(2, extractedEffectSentence); 	preparedStmt.setInt(3, rowid);
		  int i = preparedStmt.executeUpdate();
		  if(i>0)   {
		          //System.out.println("success");
		  }  else{
		         System.out.println("stuck somewhere mid " + rowid + " file " + file);
		  }
		  updateQuery =null;
	}
	
	private static void insertRecordIntoTable(Integer pep, String label,String folder,String file,Integer msgNumberInFile,Integer messageID, 
			Integer causeParagraphNum, Integer causeSentenceNum,Integer effectParagraphNum, Integer effectSentenceNum) throws SQLException {		
		PreparedStatement preparedStatement = null;
		String insertTableSQL = "insert into trainingdata (pep, label,folder,file,msgNumberInFile,messageID, causeParagraphNum, causeSentenceNum, effectParagraphNum, effectSentenceNum) "
				+ " VALUES (?,?,?,?,?,?,?,?,?,?)";
		try {
			preparedStatement = conn.prepareStatement(insertTableSQL);
			preparedStatement.setInt(1, pep);			preparedStatement.setString(2, label);
			preparedStatement.setString(3, folder);		preparedStatement.setString(4, file);
			preparedStatement.setInt(5, msgNumberInFile);		preparedStatement.setInt(6, messageID);
			preparedStatement.setInt(7, causeParagraphNum);		preparedStatement.setInt(8, causeSentenceNum);
			preparedStatement.setInt(9, effectParagraphNum);	preparedStatement.setInt(10,effectSentenceNum);
			preparedStatement.executeUpdate();			// execute insert SQL stetement
			System.out.println("Record is inserted into DBUSER table!");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} 
	}
	
	public static String getCauseEffectSentences_givenParagraphAndSentence(String msg, Integer v_paragraphCounter, Integer v_sentenceCounter) {
		String[] paragraphs = msg.split("\\n\\n");
		String newMessage = "",CurrentSentenceString="";
		//System.out.println("----- Now Processing new Message ID: ("+v_message_ID+") total paragraphs.." + paragraphs.length);
		Integer paragraphCounter=1,sentenceCounterinParagraph=1;        
		
		for (String entireParagraph: paragraphs)
		{		
				//sometimes a paragrapgh has sentences but are not captured as sentences. So if sentence identified then check, so we just add a full-stop at end of paragraph	        				   
				if(!entireParagraph.endsWith("."))
				entireParagraph = entireParagraph + ".";	        				   
				if(paragraphCounter == v_paragraphCounter) {
					entireParagraph = entireParagraph + ".";
					Reader reader = new StringReader(entireParagraph);
					DocumentPreprocessor dp = new DocumentPreprocessor(reader);
					entireParagraph="";sentenceCounterinParagraph=1;
					for (List<HasWord> eachSentence : dp) 		        	
					{  
						if(sentenceCounterinParagraph== v_sentenceCounter){
							CurrentSentenceString = Sentence.listToString(eachSentence);
							
							//System.out.println(" here c CurrentSentenceString: "+CurrentSentenceString);
							//remove unnecessary words like "Python Update ..."																	
			//				CurrentSentenceString = prp.psmp.removeLRBAndRRB(CurrentSentenceString);								
			//				CurrentSentenceString = prp.psmp.removeDivider(CurrentSentenceString);
			//				CurrentSentenceString = prp.psmp.removeUnwantedText(CurrentSentenceString);	
			//				CurrentSentenceString = prp.psmp.removeDoubleSpacesAndTrim(CurrentSentenceString);
							
//$$							System.out.println("CurrentSentenceString: "+CurrentSentenceString);
							return  CurrentSentenceString;
						}
						sentenceCounterinParagraph++;
					}
				}
				newMessage =newMessage+entireParagraph + "{PC: " +paragraphCounter + "}" + "\n\n";
				paragraphCounter++;
		}
		return CurrentSentenceString;
	}
	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}
	
	static void initDB() {
		try{
			conn = mc.connect();	
		}
		catch(Exception e){}
	}
	
	public static String getExtractedCauseSentence() {
		return extractedCauseSentence;
	}

	public static void setExtractedCauseSentence(String extractedCauseSentence) {
		extractedCauseSentence = extractedCauseSentence;
	}

	public static String getExtractedEffectSentence() {
		return extractedEffectSentence;
	}

	public static void setExtractedEffectSentence(String extractedEffectSentence) {
		extractedEffectSentence = extractedEffectSentence;
	}
}
