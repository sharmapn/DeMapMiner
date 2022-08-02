package readRepository.readRepository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import readRepository.readMetadataFromWeb.GetProposalDetailsWebPage;
import connections.MysqlConnect;
import miner.process.LabelTriples;
import miner.process.SinglesDoublesTriples;
import miner.processLabels.TripleProcessingResult;
import utilities.TableBuilder;

public class ReadLabels {	
	static String proposalIdentifier = "pep";
	public static void main(String[] args) {
		MysqlConnect mc = new MysqlConnect();		Connection conn = mc.connect();
		
		//Which one DO YOU WANT TO READ ..LABELS OR REASON LABELS
		boolean mainLabels  = true,reasonLabels= false;
		
		String tableName= "", inputFile="";		
		//filenames		
		String labelsFilename		 = "C://Users//psharma//Google Drive//PhDOtago//Code//inputFiles//input-Labels.txt";	//exactly same as the one in google drive, but modified as well
		String reasonLabelsFilename  = "C://Users//psharma//Google Drive//PhDOtago//Code//inputFiles//input-Reasons.txt";	//also maybe old version here "C:/Users/psharma/Google Drive/PhDOtago/Code/inputFiles/"
				
		if(mainLabels) {
			tableName = "labels";			inputFile= labelsFilename;
		}
		if(reasonLabels) {
			tableName = "reasonlabels";			inputFile= reasonLabelsFilename;
		}
		//dec 2018, just making sure they are not deleted once this script is run
		//deleteLabelsInDB(tableName,conn);	//first empty labels table
		
		//just for main
		ArrayList<LabelTriples> labels = new ArrayList<LabelTriples>();	
		//ArrayList<Label> reasonLabels = new ArrayList<Label>();	
		
		Integer labelCounter=0;				
		try {
			labels  = readLabelsFromFile(inputFile,labels, "Label into Database",tableName,conn,true);
			System.out.println("labels list ");
			if(!labels.isEmpty()){
	  			int counter =0;
	  			for (int x=0; x <labels.size(); x++){
		  				if(labels.get(x).getIdea() != null)		{
		  					String idea = labels.get(x).getIdea().toLowerCase();
			  				System.out.println("idea "+ idea);
		  				}
		  			}
	  		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//now we read and show labels from that database table
		ArrayList<LabelTriples> labelsFromDB = new ArrayList<LabelTriples>();		
		//old way below . ...now we must already have the database table for labels populated and just read from it
		//labels = b.readLabelsFromFile(baseIdeasFilename,labels,"Triples Matching",conn, true);	//false as dont output
		labelsFromDB = getallLabelsFromTable(tableName,conn);
		
		String idea ="", subject="",verb = "", object="", sentence="",newIdea ="", newSubject="",newVerb = "", newObject="";
		Boolean output = true;
		if(labels.isEmpty())
			System.err.println("Labels from database empty "+labels.size());
		else{
			System.out.println("Labels from database "+labels.size());
			int counter =0;
			Integer labelSize = labels.size(), lineNumber;
			for (int x=0; x < labelSize; x++){
					idea =""; subject="";verb = ""; object="";
					newIdea =""; newSubject="";newVerb = ""; newObject="";
	  				if(labels.get(x).getIdea() != null && !labels.get(x).getIdea().isEmpty())
	  				{
	  					labelCounter++;
	  					lineNumber = labels.get(x).getLineNumber();				idea 	= labels.get(x).getIdea();
	  					subject = labels.get(x).getSubject();	  				verb 	= labels.get(x).getVerb();		//no verb egual to pep
	  					object 	= labels.get(x).getObject();	  				sentence= labels.get(x).getSentence();
	  					if (output){
							//System.out.println("line: "+line);
							System.out.printf("%-30s %-60s %-30s %-30s %-30s \n",lineNumber,  idea, subject, verb, object);
						}
	  				}
			}
		}
		System.out.println("Total Number of Labels Read In: "+ labelCounter);
	}

	public static ArrayList<LabelTriples> createLabelsForPEPUsingPEPTitle(Integer pepNumber, ArrayList<LabelTriples> labels,String v_message,Connection conn) throws IOException {
		//use pep number query database and get pep title
		//use pep title to create new list of labels for that particular pep only and remove it afterwards
		//
		System.out.println("*******HERE: ");
		GetProposalDetailsWebPage gpd = new GetProposalDetailsWebPage();
		String tableName = proposalIdentifier+"details";
		String title = gpd.getProposalTitleForProposal(pepNumber,proposalIdentifier,tableName,conn);
		LabelTriples lbl = new LabelTriples();
//		if (LabelIsDouble){
//			lbl.setLabel("g",2, null,null, v_idea, s, v, null);
//		}
//		else if (LabelIsTriple){
//			lbl.setLabel("g",3, null,null, v_idea, s, v, o);
//		}
		labels.add(lbl);		
		return labels;
	}
	
	public static boolean removeLabelsForPEPUsingPEPTitle(Integer pepNumber, ArrayList<LabelTriples> labels, String v_message)  {
		//use pep number to remove label after processing for pep is finished
		// return true if finished sucessfully	
		return true;
	}
	
	//reads into database
	public static ArrayList<LabelTriples> readLabelsFromFile(String v_filename,ArrayList<LabelTriples> labels, String v_message, String tableName, Connection conn, boolean output) {
		// The name of the file to open.
	      String fileName = v_filename, line = ""; // This will reference one line at a time
	      List<String> temps = new ArrayList<String>();	     
	      Integer lineReadInCounter = 0;
	      boolean LabelIsDouble = false,LabelIsTriple = false;	      
	      List<String> emptyLinesNotRead = new ArrayList<String>(),comments = new ArrayList<String>();
	      String sentence="";
	      
	      try {
	    	  // FileReader reads text files in the default encoding.
	          FileReader fileReader = new FileReader(fileName);
	          // Always wrap FileReader in BufferedReader.
	          BufferedReader bufferedReader =  new BufferedReader(fileReader);
	          //Integer lineReadInCounter = 0;
	          Integer emptyLineCounter =0, lineNumber=0;
	          	          
	          while((line = bufferedReader.readLine()) != null) {
	        	  line= line.trim();	        	  
	        	  lineNumber++;
	        	  LabelIsDouble = false; LabelIsTriple = false;
	        	  
	        	  if(line.startsWith("%")||line.startsWith("@")){
	        		  comments.add("Line: "+lineNumber + " comments = [" + line+ "]");
	        		  //parameters of below function(Integer lineNumber,String comments, String idea,String subject,String verb, String object, Connection conn
	        		  //dont need comments in table
	        		  //insertLabelsInDB(lineNumber,line,"","", "", "",conn);	//insert comments	//1,2,-1,"k"
	        	  }
	        	  else{
	        		  sentence="";
	        		  //lines in inputfile contains senetences from which the triple was extracted - we leave them out
	        		  if (line.contains("$")) {
	        			  sentence = line.split("\\$")[1];	  line 	   = line.split("\\$")[0];   			  line     = line.trim(); 			  sentence=sentence.trim();
	        		  }
	        		  
		        	  if (line.trim().length()<2)	//empty spaces, 2 is just a small number, could have been 1 , and highly unlikely triple or double can be coded in 2 characters 
		        	  {
		        		  emptyLinesNotRead.add("Line: "+lineNumber + " string = [" + line+"]");
		        		  emptyLineCounter++;
		        		  //System.out.println("Empty or almost empty Line not read, linenumber "+lineNumber); 
		        	  }
		        	  else{
		        		  //System.out.println(line);
	//	        		  line = processLine(line);
		        		  //temps.add(line);		        		  
		        		    line = line.trim();		        		 
		        		    String terms[] = line.split(",");
		        			String v_idea = "",subject = "",verb = "", object = "";
		        			//incomplete labels, have to check into this where just and idea  and subject is specified, 
		        			//e.g.co_bdfl_delegate_accepted_pep,co-bdfl-delegate
		        			if(terms.length<3 ){
		        				//do nothing		        			
		        			}
		        			else if(terms.length==3 ){
		        				v_idea =  terms[0];	subject =   terms[1]; 	verb =  terms[2];	LabelIsDouble = true;		        			
		        			}
		        			//capture object as well - form triples
		        			else if(terms.length==4){
		        				v_idea =  terms[0] ;		subject =   terms[1];	verb =  terms[2];   	object =  terms[3];   				LabelIsTriple = true;		        			
		        			}				
		        			//capture the second set of triple, if more columns are provided in the inputfile
		        			else if (terms.length>5){
		        				v_idea =  terms[0] ;		subject =   terms[1];		verb =  terms[2];	
	//	        				System.out.println("words length > 5");
		        			}		        			
		        			//check
		        			String[] subjectOptions ={""};		String[] verbOptions = {""};		String[] objectOptions = {""};
		        			//subject
		        			if (subject.contains("-"))
		        				subjectOptions = subject.split("-");
		        			
		        			else 
		        				subjectOptions[0] = subject;	//System.out.println("here");		        			
		        			//verb
		        			if (verb.contains("|"))	verbOptions = verb.split("\\|");		        		
		        			else verbOptions[0] = verb;
		        			//object
		        			if (object.contains("|"))		objectOptions = object.split("\\|");		        			
		        			else		objectOptions[0] = object;
		        			
		        			for (String s :  subjectOptions){
		        				for(String v :  verbOptions){
		        					for(String o :  objectOptions){
		        						//old way below
		        						line = v_idea + " " + s + " " + v + " " +o;
		        						temps.add(line);
		        						//new way - create new label object
		        						LabelTriples lbl = new LabelTriples();
		        						//added check to make sure idea is not empty
		        						if (LabelIsDouble && !v_idea.isEmpty()){
		        							lbl.setLabel(lineNumber,v_idea, s, v, "",sentence);		//1,2, null,null,
		        							insertLabelsInDB(lineNumber,"",v_idea, s, v, "",sentence,tableName,conn);	//.1,2,-1,"k",		//pass empty string as comments
		        						}
		        						else if (LabelIsTriple && !v_idea.isEmpty()){
		        							lbl.setLabel(lineNumber,v_idea, s, v, o,sentence);			//1,3, null,null,
		        							insertLabelsInDB(lineNumber,"",v_idea, s, v, o,sentence,tableName,conn);		//1,3, -1,"k", 		//pass empty string as comments
		        						}	
		        						labels.add(lbl);
	        						
		        						/*LETS NOT OUTPUT*/	
		        						if (output && !v_idea.isEmpty()){
		        							//System.out.println("line: "+line);
		        							System.out.printf("%-30s %-60s %-30s %-30s %-30s \n",lineNumber,  v_idea, s, v, o);
		        						}
		        					}
		        				}
		        			}
	    		  
		        		  lineReadInCounter++;
		        	  }// end else
	          } //end else
	        	  	//System.out.println("\nRead in " + lineReadInCounter + " lines into File " + fileName + " for " + v_message);
	          }
	          System.out.println("\nRead in " + lineReadInCounter + " labels into File " + fileName + " for " + v_message);
	          String[] tempsArray = temps.toArray(new String[0]);
    		  System.out.println("Empty Lines not read: "+emptyLineCounter);
	    		  for (String lnr :  emptyLinesNotRead){
	    			  System.out.println(lnr);
	    		  }
    		  System.out.println("Comment Lines not read below: "+comments.size());
	    		  for (String lnr :  comments){
	    			  System.out.println(lnr);
	    		  }
	          // Always close files.
	          bufferedReader.close(); 
	          return labels;
	      }
	      catch(FileNotFoundException ex) {
	          System.out.println("Unable to open file '" +  fileName + "'");                
	      }
	      catch(IOException ex) {
	          System.out.println("Error reading file '"  + fileName + "'");                  
	          // Or we could just do this: 
	          // ex.printStackTrace();
	      }
	      catch(Exception e) {
	    	  System.out.println(StackTraceToString(e)  );
	      }
		return null;	      
	  }
	
	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}
	
	public static ArrayList<LabelTriples> getallLabelsFromTable(String tableName, Connection conn){
		String type = "", pepTypeString =null;
		Boolean showOutput = false;
		ArrayList<LabelTriples> labels = new ArrayList<LabelTriples>();
		System.out.println("Get all Labels From Table");
		try
		{																   //ordering not required
			String query="SELECT lineNumber, idea, subject, verb, object,sentence FROM "+tableName+" where toCheck=1 order by idea asc";	//linenumber asc
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			Integer counter=0;  
			while (rs.next()) {	
				Integer lineNumber = rs.getInt(1);				String idea = rs.getString(2);			String subject = rs.getString(3);	
				String verb = rs.getString(4);					String object = rs.getString(5);				String sentence = rs.getString(6);
				LabelTriples lbl = new LabelTriples();				//added check to make sure idea is not empty
				if (!idea.isEmpty())
					lbl.setLabel(lineNumber,idea, subject, verb, object,sentence);								
				labels.add(lbl);
				counter++;
			}
			System.out.println("\n\nTotal number of labels returned from table: "+ tableName +": "+ labels.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
		}		
		return labels;
	}
	
	public static String[] getallLabelsFromTableAsString(Connection conn){
		String type = "", pepTypeString =null;
		Boolean showOutput = false;
		ArrayList<LabelTriples> labels = new ArrayList<LabelTriples>();
		String labelsAsStringArray[] = null;
		try
		{																   //ordering not required
			String query=" SELECT lineNumber, idea, subject, verb, object FROM labels "
					   + " where toCheck=1 order by linenumber asc";  //dec 2018, added filter to check only for selected labels
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			Integer counter=0;  
			while (rs.next()) {	
				Integer lineNumber = rs.getInt(1);				String idea = rs.getString(2);				String subject = rs.getString(3);	
				String verb = rs.getString(4);					String object = rs.getString(5);	
				labelsAsStringArray[counter] = subject + " " + verb + " " + object;
				counter++;
			}
			System.out.println("\n\nTotal number of labels returned from table" + labels.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
		}		
		return labelsAsStringArray;
	}
	
	public static ArrayList<LabelTriples> getUniqueLabelSubjectsFromTable(Connection conn){
		String type = "", pepTypeString =null;
		Boolean showOutput = false;
		ArrayList<LabelTriples> labels = new ArrayList<LabelTriples>();		
		try
		{																   //ordering not required
			String query="SELECT DISTINCT(subject) FROM labels;";
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			Integer counter=0;  
			while (rs.next())	{	
				Integer lineNumber = rs.getInt(1);
				String idea = rs.getString(2);				String subject = rs.getString(3);				String verb = rs.getString(4);
				String object = rs.getString(5);				String sentence = rs.getString(6);
				LabelTriples lbl = new LabelTriples();
				//added check to make sure idea is not empty
				if (!idea.isEmpty())
					lbl.setLabel(lineNumber,idea, subject, verb, object,sentence);				
				labels.add(lbl);				counter++;
			}
			System.out.println("\n\nTotal number of labels returned from table" + labels.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}		
		return labels;
	}	
	
	public static ArrayList<SinglesDoublesTriples> readSinglesDoublesTriplesFromFile(String singlesAndDoublesFilename,ArrayList<SinglesDoublesTriples> singlesAndDoublesList, String v_message, Connection conn, boolean output) {
		// The name of the file to open.
	      String fileName = singlesAndDoublesFilename, line = null; // This will reference one line at a time
	      List<String> temps = new ArrayList<String>();	     
	      Integer lineReadInCounter = 0;
	      boolean LabelIsDouble =false,LabelIsTriple = false;
	      
	      try {
	    	  //empty labels table
//	    	  deleteLabelsInDB("labels",conn);
	          // FileReader reads text files in the default encoding.
	          FileReader fileReader = new FileReader(fileName);
	          // Always wrap FileReader in BufferedReader.
	          BufferedReader bufferedReader =  new BufferedReader(fileReader);
	          //Integer lineReadInCounter = 0;
	          Integer emptyLineCounter =0;
	          while((line = bufferedReader.readLine()) != null) {
	        	  
	        	  LabelIsDouble = LabelIsTriple = false;
	        	  
	        	  if(line.startsWith("%")||line.startsWith("@")){}
	        	  else if (line.trim().length()==0)  {
	        		  emptyLineCounter++;
	        		  //System.out.println("Empty Line not read "); 
	        	  }
	        	  else if(line.trim().length()>2){	
	        		    //System.out.println("Line read " + line); 
	        		    String words[] = line.split(",");
	        			String label = "",singleOrDouble = "";	        				        			
	        			label =  words[0] ;	        			singleOrDouble =   words[1];	        			
	        			SinglesDoublesTriples sd = new SinglesDoublesTriples();						
						sd.setLabel(label);												
						Integer length = line.split(",").length;
						if(length==2){
	        				sd.setSingle(singleOrDouble);
	        			//	System.out.println("\tsingle: "+singleOrDouble);
						}
//	        			else if(length==3){
//	        				sd.setDouble(words[1],words[2]);
//	        				System.out.println("\tDouble: "+words[1] + " " + words[2]);
//	        			}
	        			else if(length==4){
	        				sd.setTriple(words[1],words[2],words[3]);
	        			//	System.out.println("\tDouble or Triple: "+words[1]+" "+words[2]+" "+words[3]);
	        			}
						
						singlesAndDoublesList.add(sd);
						/*LETS NOT OUTPUT*/	
						//if (output)
						//System.out.println("line: "+line);	//since we are outputting
    		  
	        		  lineReadInCounter++;
	        	  }
	        	  	//System.out.println("\nRead in " + lineReadInCounter + " lines into File " + fileName + " for " + v_message);
	          }
	          System.out.println("\nRead in " + lineReadInCounter + " singles and doubles from File " + fileName + " for " + v_message);
	          //String[] tempsArray = temps.toArray(new String[0]);
    		  System.out.println("Empty Lines not read: "+emptyLineCounter); 
	          // Always close files.
	          bufferedReader.close(); 
	          return singlesAndDoublesList;
	      }
	      catch(FileNotFoundException ex) {
	          System.out.println("Unable to open Singles and Doubles file '" +  fileName + "'");                
	      }
	      catch(IOException ex) {
	          System.out.println("Error reading Singles and Doubles file '"  + fileName + "'");                  
	          // Or we could just do this: 
	          // ex.printStackTrace();
	      }
		return null;	      
	  }
														//Integer generalOrPEPSpecific, Integer type,Integer pepNumber, String pepTitle,
	public static void insertLabelsInDB(Integer lineNumber,String comments, String idea,String subject,String verb, String object,String sentence, String tableName,Connection conn)
	  {
		try {			
			String query = " insert into "+tableName+" (lineNumber,comment,idea,subject,verb, object,sentence)"			//generalOrPEPSpecific, type, pepNumber,pepTitle,
					+ " values (?,?,?,?,?,?,?)";
			// create the mysql insert preparedstatement
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setInt(1, lineNumber);			preparedStmt.setString(2, comments);
			//preparedStmt.setInt(2, generalOrPEPSpecific);			//preparedStmt.setInt(3, type);
			//preparedStmt.setInt(4,pepNumber);			//preparedStmt.setString(4, type);
			//preparedStmt.setString(5, pepTitle);
			preparedStmt.setString(3, idea);	preparedStmt.setString(4, subject);	preparedStmt.setString(5, verb);
			preparedStmt.setString(6, object);	preparedStmt.setString(7, sentence);
			// preparedStmt.setBoolean(4, false); // preparedStmt.setInt (5, 5000);		// execute the preparedstatement
			preparedStmt.execute();
		} catch (SQLException e) {			e.printStackTrace();
		} catch (Exception f) {
			System.err.println("Got an exception!");			System.err.println(f.getMessage());
		}
	  }
	public static void deleteLabelsInDB(String tableName, Connection conn)
	 {
		try {			
			String query = " delete from "+tableName+";";
			// create the mysql insert preparedstatement
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.execute();
		} catch (SQLException e) {			e.printStackTrace();
		} catch (Exception f) {
			System.err.println("Got an exception!");			System.err.println(f.getMessage());
		}
	}
}
