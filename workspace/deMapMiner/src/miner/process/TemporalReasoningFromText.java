package miner.process;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import callIELibraries.JavaOllieWrapperGUIInDev;
import callIELibraries.ReVerbFindRelations;
import de.mpii.clause.driver.ClausIEMain;

public class TemporalReasoningFromText {
	//dec 2018, temporal reason is okay but problematic as no dates are assigned to any event in the storyline and we need dates in the process diagram
	//..so we ignored them altogether ...as most of them exist in pep summary messages we don't include them in processing
	
	//This class is to handle the after events
	//For instance, "Following the discussion, a vote was held."
	//using cie, the triple captured is "a vote"	"was held"	"Following the discussion"
	//When the vote label is captured, 
	// we check S, V and O for the storyline terms and 
	// retrieve the leftovers and check in them for story terms, like discussion as in above case
	// if found we write to file db 
	
	//this occurs only if triple has been matched, so we pass the subject, object and verb
	public static String getstoryLineTerms(String v_tripleArray[][], ClausIEMain cm, ReVerbFindRelations rvr, JavaOllieWrapperGUIInDev jw, String sentence, 
			String subject, String object, String verb, String v_idea, Integer v_pepNumber, 
			Date v_date, Integer v_message_ID, String author, Integer sentenceCounter, FileWriter writerForDisco, 
			FileWriter writerAll, String conditionalStrings[], String v_negationTerms[], String [] reasonTerms, String reasonLabels[], 
			Integer libraryToCheck, String [] storyLineTerms, String [] storyLineElements) throws IOException 
	{
		Integer y_counter=0;
		String library = "", v_subject = null,v_relation = null,v_object = null,text = null,leftOverStoryLine = null,story = null,r_text = null;		
		boolean labelConditional = false,subjectFound = false, relationFound = false, objectFound = false,v_conditional = false,foundReasonOnce = false;//IF MATCHED, TRY CHECKING CONDITIONAL HERE
		
		//System.out.println("\tInside Check Temporal Reasoning");
		for (String slt : storyLineTerms) 
		{
//			System.out.println("\tCheck storyline");
			if (v_subject != null && v_subject.toLowerCase().contains(slt)) {
				//subjectFound = true;
				r_text = v_subject;
//				System.out.println("\tstoryline term in subject");
				int spaceIndex = r_text.toLowerCase().indexOf(slt) + slt.length();
				if (spaceIndex != -1) { // -2 to handle these 2 characters:											
					leftOverStoryLine = r_text.toLowerCase().substring(spaceIndex,	v_subject.length() - 2);
					//check if leftover contains storylineItems
					for (String stele: storyLineElements){
						if(leftOverStoryLine.contains(stele)){
							System.out.println("\t Found storyline term in subject:  " + stele);
							//foundStoryLineOnce = true;
							writerAll.write(" Storyline: "+ stele);
							
							//set storyline
						//XXX	r.setStoryLine(st);
							story = stele;
						}
					}
				}
				// leftOver = text.replace(v_subject,"");
//					System.out.println("subjectFound, leftOver: " + leftOver);
			}
			if (v_relation != null && v_relation.toLowerCase().contains(slt)) {
				//relationFound = true;
				r_text = v_relation;
//				System.out.println("\tstoryline in relation");
				int spaceIndex = r_text.toLowerCase().indexOf(slt) + slt.length();
				if (spaceIndex != -1) { // -2 to handle these 2 characters:
										// "}
					leftOverStoryLine = r_text.toLowerCase().substring(spaceIndex,	v_relation.length() - 2);
					//check if leftover contains storylineItems
					for (String stele: storyLineElements){
						if(leftOverStoryLine.contains(stele)){
							System.out.println("\tFound Storyline term in relation: " + stele);
							//foundStoryLineOnce = true;
							writerAll.write(" Storyline: "+ stele);
							
							//set storyline
							//XXX	r.setStoryLine(st);
							story = stele;
						}
					}
				}
//					System.out.println("relationFound, leftOver: " + leftOver);
			}
			if (v_object != null && v_object.toLowerCase().contains(slt)) {
				//objectFound = true;
				r_text = v_object;					
//				System.out.println("\tstoryline in object");					
				int spaceIndex = r_text.toLowerCase().indexOf(slt) + slt.length();
				if (spaceIndex != -1) { // -2 to handle these 2 characters:											
					leftOverStoryLine = r_text.toLowerCase().substring(spaceIndex,	v_object.length() - 2);
					//check if leftover contains storylineItems
					for (String stele: storyLineElements){
						if(leftOverStoryLine.contains(stele)){
							System.out.println("\tFound Storyline term in object: " + stele);
							//foundStoryLineOnce = true;
							writerAll.write(" Storyline: "+ stele);
							
							//set storyline
							//XXX	r.setStoryLine(st);
							story = stele;
						}
					}
				}
//					System.out.println("objectFound, leftOver: " + leftOver);
			}

			// if contains, then subtract it from the next proposition
			// section = remaining
			//if (subjectFound || relationFound || objectFound) {
			//	foundStoryLineOnce = true;
			//}
		} //end storyline for loop		
		return story;
	}
}
