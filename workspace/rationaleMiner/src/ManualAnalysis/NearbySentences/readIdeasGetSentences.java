package ManualAnalysis.NearbySentences;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

import readRepository.readRepository.ReadLabels;
import connections.MysqlConnect;
import miner.process.LabelTriples;

public class readIdeasGetSentences {
	
	public static void main (String[] args) throws IOException{
		MysqlConnect mc = new MysqlConnect();
		Connection conn = mc.connect();
		String labelTableName = "labels";
		
		//read base ideas to match in string matching
		ReadLabels b = new ReadLabels();
		String baseIdeasFilename = "C:/Users/admin/workspace/dev/src/semanticSimilarity/input-BaseIdeas.txt";
		ArrayList<LabelTriples> labels = new ArrayList<LabelTriples>();
		labels = b.readLabelsFromFile(baseIdeasFilename,labels,"",labelTableName,conn,false);

		//initialise variables
		String v_idea = "" , subject = "", verb = "", object = "", PrevSentence = "", CurrSentence = "";

		//	   for (String sw : searchWords) {
		//		   String words[] = sw.split(" ");


		if(!labels.isEmpty()){
			int counter =0;
			for (int x=0; x <labels.size(); x++)
			{
				v_idea = labels.get(x).getIdea();
				subject = labels.get(x).getSubject();
				verb = labels.get(x).getVerb();		//no verb egual to pep
				object = labels.get(x).getObject();

				//			v_idea =   words[0];
				//entity = words[1];
				//action = words[2];
				//pep = 	 words[3];

//				String str = sw.toString();
//				str = str.replaceFirst(v_idea,"");
				String str = subject + " " + verb + " " +object;
				//System.out.println(sw.toString() + " || " + str);
				System.out.println("words in string to match >> " + str);
				ManualAnalysis.NearbySentences.NearbySentencesForTerms_Extract_GUI gs = new ManualAnalysis.NearbySentences.NearbySentencesForTerms_Extract_GUI();		  
				//gs.exportdata3(str, -1);
			}
		}
	}	//end method
}
