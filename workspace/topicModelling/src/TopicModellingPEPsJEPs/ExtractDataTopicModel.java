package TopicModellingPEPsJEPs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import connections.MysqlConnect;
import test.StanfordParser;

public class ExtractDataTopicModel {
	static StanfordParser sp = null;
	static Connection conn = null;
	static FileWriter fw = null;
	static String outFileNameMain = "C:\\CRF++\\example\\reasons\\evaluationReasonsMain.txt";
	static Integer evaluationColumnCount = 2; 
	static boolean includeTermOnly = true, includeTermAndPOS = false, includePOSOnly =false;
	
	public static void main(String[] args) {		
		//procedure for causal extraction
		//get all sentences from results_postprocessed table
		//for each of the result rows where label is matched try to run the causal relation extracter
		//where found, output pep, label, causal connector and reason
		//match reason and label
		//output 
		
		//jan 2019, topic modelling data extraction for automatci concept extraction - dont have to manually analyse proposals manually like we did for 30 peps		
		MysqlConnect mc = new MysqlConnect();		conn = mc.connect();
		Statement stmt = null;
			
		sp = new StanfordParser();		sp.init();		//initialise stanford parer	
		String tmpsentence ="";
		
		//Causal Extraction Results		
		try {
			fw = new FileWriter(outFileNameMain,true); //labelsInputFilename //the true will append the new data
			stmt = conn.createStatement();
			// results table or postprocessed table										//5											//10
			ResultSet rs = stmt.executeQuery(" SELECT proposal, messageid, sentence,isEnglishOrCode,msgSubject "
											+" from allsentences where proposal > -1 and isEnglishOrCode = 1 %' " //and pep =259 "		//from results_postprocessed
											+" and (islastparagraph =1 or isfirstparagraph =1) "  //date asc
											+" order by proposal asc, datetimestamp asc");  //date asc
			while (rs.next()) {					
				Integer pepNumber = rs.getInt(1); 				String label = rs.getString(2); 				String cs = rs.getString(3);
				//System.out.println("Showing Causal Relation for pep "+pepNumber + " Label (" + label + ") Paragraph(" + pp +")");
				//extractCausalFromParagraphs(lp, tokenizerFactory, pp);
				String ps = rs.getString(4);
	//					System.out.println("Showing Causal Relation for pep "+pepNumber + " Label (" + label + ") CurrSentence(" + cs +")");
	//					extractCausalFromParagraphs(lp, tokenizerFactory, cs, pepNumber);
				String ns = rs.getString(5);
				//System.out.println("Showing Causal Relation for pep "+pepNumber + " Label (" + label + ") Paragraph(" + np +")");
				//Check Previous and Next Paragraph 
				//extractCausalFromParagraphs(lp, tokenizerFactory, np);				
				//filter
				if(cs.startsWith("Status")) { }
				else {
					//parse the sentence
		  			  ArrayList<String> parsedSentence =new ArrayList<String>(); 
		  			  
		  			  
		  			//append into file the previous sentence
//		  			  parsedSentence = sp.parseSentence(ps,includeTerm,includePOS); 
		  			  for(String sent : parsedSentence) {
		//$$      				System.out.println(sent);
//		  			  	fw.write(sent +"\n");	//appends the string to the file 
		  			  }
		  			  //append into file the current sentence
		  			  parsedSentence = sp.parseSentence(cs,includeTermOnly,includeTermAndPOS,includePOSOnly); 
		  			  String lineToWrite="";
		  			  for(String sent : parsedSentence) {
		//$$      				System.out.println(sent);
		  				sent = sent.trim().replaceAll(" +", " ");  
		  			  	fw.write(sent +"\n");	//appends the string to the file 
		  			  }   
		  			  //append into file the current sentence
//		  			  parsedSentence = sp.parseSentence(ns,includeTerm,includePOS); 
		  			  for(String sent : parsedSentence) {
		//$$      				System.out.println(sent);
//		  			  	fw.write(sent +"\n");	//appends the string to the file 
		  			  }
		  			  
		  			  
		  			  //for test debug data
		  			  //for each label we make a list, where we have fields, label, all sentences where their is keywords from table
		  			  
				}
			}
			fw.close();
			
			 //we want to make sure the outputfile has 3 or n number of columns
			System.out.println("Started column count checking");
			 String line = "";
			 Integer lineCounter=0, columnCount=0;
			 FileReader fileReader = new FileReader(outFileNameMain);		            // Always wrap FileReader in BufferedReader.
		            BufferedReader bufferedReader =   new BufferedReader(fileReader);
		            while((line = bufferedReader.readLine()) != null) {
		            	lineCounter++;
		            	columnCount = line.split(" ").length;
		            	if (columnCount != evaluationColumnCount) {
		            		System.out.println("Error in column count in line: "+ lineCounter+ " Line: " + line);
		            	}
		            }   
		            // Always close files.
		            bufferedReader.close();  
			
			
		} catch(IOException ioe)
		{
		    System.err.println("IOException: " + ioe.getMessage());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		String[] cmd = {"C:\\CRF++\\crf_learn","C:\\CRF++\\example\\reasons\\template","C:\\CRF++\\example\\reasons\\inputMain.txt"};
/*			try {
				Process p = Runtime.getRuntime().exec(cmd);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
*/
	}

}
