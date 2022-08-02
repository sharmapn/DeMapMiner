package relationExtraction;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

import connections.MysqlConnect;
import callIELibraries.JavaOllieWrapperGUIInDev;
import readRepository.readRepository.ReadLabels;
import callIELibraries.ClausIECaller;
import callIELibraries.ReVerbFindRelations;
import de.mpii.clause.driver.ClausIEMain;
import miner.process.LabelTriples;

public class GetRelationsFromLibraries {
	
	static String sentencesToMatch[];
	
	static String labelTableName = "labels";
	
	public static void main(String [] args) throws IOException{
		
		MysqlConnect mc = new MysqlConnect();
		Connection conn = mc.connect();
		
		//ClausIE		
		ClausIECaller cie = null;
		//System.out.println("CLAUSSSSSSSSSSSSSSSSSSS");
		ClausIEMain cm = new ClausIEMain();
		//System.out.println("CLAUSSSSSSSSSSSSSSSSSSS");
		//ReVerb
		ReVerbFindRelations rr  = new ReVerbFindRelations();
		
		//Ollie
		JavaOllieWrapperGUIInDev jw = new JavaOllieWrapperGUIInDev();
		
		//ReadTextToMatchFromFile d = new ReadTextToMatchFromFile();
		//String sentencesFilename = "C:/Users/psharma/Google Drive/PhDOtago/scripts/pep44.small.arff";
		//sentencesToMatch = d.readBaseIdeasFromFile(sentencesFilename, " sentences");
		
		ReadLabels b = new ReadLabels();
		String sentencesFilename = "C:/Users/psharma/Google Drive/PhDOtago/scripts/pep44.small.arff";
		ArrayList<LabelTriples> sentencesToMatch = new ArrayList<LabelTriples>();
		sentencesToMatch = b.readLabelsFromFile(sentencesFilename,sentencesToMatch, "Keyword matching using these base concepts",labelTableName,conn,false);

		String labels;
		String CurrentSentenceString = null;
		
		
		
		//for (String bi : sentencesToMatch) {	
  		for (int x=0; x <sentencesToMatch.size(); x++)	
			if (sentencesToMatch !=null && sentencesToMatch.contains(",'")){	
				
				//System.out.println("\n" +"bi: "+bi);
				//System.out.println("HERE DOUBLE SENTENCES");
				
				//TRIED TO CORRECT WHILE CONVERTING - BUT STILL NEEDS WORK
				//String sections[] = sentencesToMatch.split(",'");
				String sections[] = {null, null};
				labels = 		   sections[0];
				CurrentSentenceString = sections[1];
				
				if (CurrentSentenceString.contains("\\")){
				//	CurrentSentenceString= CurrentSentenceString.replace("\\","");
				}
					
				System.out.println("\n----------------------------------");
				System.out.println("Label: " + labels + " Sentence: "+CurrentSentenceString);
				
				
				//System.out.println("\n" +"labels: "+labels);
				//System.out.println("\n" +"CurrentSentenceString: "+CurrentSentenceString);
				//CurrSentence = sections[2];
				
				try {
					System.out.println("Reverb Output");
					rr.findRelationsOnly(CurrentSentenceString);
					System.out.println("ClausIE Output t");
					cm.computeClausIEOnly(CurrentSentenceString);
					System.out.println("Ollie Output");
					jw.computOllieOnly(CurrentSentenceString);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			

		}
		
	}


