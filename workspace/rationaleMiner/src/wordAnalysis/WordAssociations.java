package wordAnalysis;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import peopleAspect.MysqlConnectForQueries;

//purpose is to create a wordcloud.- i guess

public class WordAssociations {
	
	//create csv file for word association scripot written in r (c:\scripts\r)
	//we need to do stopword removal
    public static void main(String[] args) throws Exception {

        //String csvFile = "c:\\scripts\\r\\abc.csv";
    	String csvFile = "C:\\Users\\psharma\\Google Drive\\PhDOtago\\Stages\\WordCloud\\abc.csv";
        FileWriter writer = new FileWriter(csvFile);
        
        MysqlConnectForQueries mc = new MysqlConnectForQueries();
		Connection connection = mc.connect();
		
		boolean showOutput = false;
		ArrayList<String> stopwords = new ArrayList<String>();		
		
		try
		{	
			//read stopwords from file
			//https://github.com/thunlp/THUCTC/blob/master/src/org/thunlp/language/english/stopwords.en.txt
			//it becomes hard if we remove all gthese words to connect nodes
			
			Scanner scanner = new Scanner(new File("c:\\scripts\\r\\stopwords-en.txt"));
			while (scanner.hasNextLine()) {
			   String line = scanner.nextLine();
			   stopwords.add(line);
			   // process the line
			}
			
			//stopwords.add("the"); //we have no stopwords
			//stopwords.add("The");
			
			//String query="SELECT DISTINCT(senderName) FROM alldevelopers order by senderName asc";
			//above column was somehow modified by myself into cluster representation, so new column is 'clusterBySenderFullName'
			String query = " SELECT pep, id,state, dmconcept, label, causesentence from trainingdata " + 
						   " WHERE length(causesentence ) > 0 AND consider = 1 " + 
					       " AND (state = 'accepted' OR state = 'rejected') ";
			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery(query);
			Integer counter=0;
			
			CSVUtils.writeLine(writer, Arrays.asList("pep","label", "causesentence"), ',', '"');

	        //custom separator + quote
//	        CSVUtils.writeLine(writer, Arrays.asList("aaa", "bb,b", "cc,c"), ',', '"');

	        //custom separator + quote
//	        CSVUtils.writeLine(writer, Arrays.asList("aaa", "bbb", "cc,c"), '|', '\'');

	        //double-quotes
	        //CSVUtils.writeLine(writer, Arrays.asList("aaa", "bbb", "cc\"c");
  
			while (rs.next())
			{	
				String pep = rs.getString(1);	int id = rs.getInt(2);	String state = rs.getString(3);	String dmconcept = rs.getString(4);
				String label = rs.getString(5);	String causesentence = rs.getString(6);
				
				//match each word
				String[] termsInSentence = causesentence.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
				//String termsInSentence[] = causesentence.split(" ");
				
				// removing stopwords removes links
				for(String term : termsInSentence) {  //for all terms
					term = term.toLowerCase();
						for(String s : stopwords) {
							if(term.equals(s)){  //if each word is stopword					
								causesentence = causesentence.replaceAll(" "+s+" " ," ");
//								System.out.println("\t"+s);
							}
						}					
				}
				
				
				if (showOutput){
					//System.out.println("\t"+pepNumber);
//					 System.out.print(pepNumber+ ((pepNumber%20==19) ? "\n" : " "));
				}
				CSVUtils.writeLine(writer, Arrays.asList(pep, label, causesentence), ',', '"');
				counter++;
			}
			//System.out.println("Total number of distint develoopers " + developers.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}		
		
		
        writer.flush();
        writer.close();
        
        mc.disconnect();

    }

}