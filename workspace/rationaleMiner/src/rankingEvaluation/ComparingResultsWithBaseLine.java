package rankingEvaluation;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import connections.MysqlConnect;

public class ComparingResultsWithBaseLine {
	static Connection connection;
	static String results = "dynamicweightallocation";
	static String features[] = {"sentenceHintProbablity","sentenceLocationHintProbability","restOfParagraphProbabilityScore","messageLocationProbabilityScore","messageSubjectHintProbablityScore", //5
			"dateDiffProbability","authorRoleProbability","messageTypeIsReasonMessageProbabilityScore","sentenceLocationInMessageProbabilityScore","sameMsgSubAsStateTripleProbabilityScore", //10
			"reasonLabelFoundUsingTripleExtractionProbabilityScore","messageContainsSpecialTermProbabilityScore","prevParagraphSpecialTermProbabilityScore","negationTermPenalty"};
	static String labels[] = {"accepted","rejected"};
	static String evalLevels[] = {"sentencesfromdistinctmessages","sentences"};
	
	public static void main(String[] args) throws SQLException, IOException {
		connections.MysqlConnect mc = new MysqlConnect();		
		connection = mc.connect();	
		
		String column_to_update="", prefix="",suffix="";
		for (String label: labels) {  //for each label -- accepted or rejected 
			column_to_update="";
			if(label.equals("accepted")) 	
				prefix ="acc_";
			else   	
			  	prefix ="rej_";
			
			for (String evalLevel: evalLevels) {	//sentence or message based		
				if(evalLevel.equals("sentencesfromdistinctmessages")) 
					suffix = "msg";
			    else  	
			    	suffix = "sent";
				
				column_to_update = prefix + suffix;
				
				//distinct evallevel, label 
				
				//get baseline results
				//for that evallevel and for that label
				Integer top5_bs = 0, top10_bs=0, top15_bs=0, top30_bs=0, top50_bs, top100_bs,outsidetop100_bs,notfound_bs;
				String queryj = "SELECT * from "+results+" where evalLevel = '"+evalLevel+"' and label = '"+label+"' and run < 7;";
				PreparedStatement psj = connection.prepareStatement(queryj);
				ResultSet rsj = psj.executeQuery();
				String v0_evalLevel="", v0_label = "";
				while ( rsj.next() )    { 
					v0_evalLevel= rsj.getString("evalLevel");  
					if(v0_evalLevel.equals("sentencesfromdistinctmessages"))  //we just shorten the field
						v0_evalLevel  ="messages";
					else
						v0_evalLevel  ="sentences";
					
					v0_label = rsj.getString("label");
					top5_bs = rsj.getInt("top5"); 		top10_bs = rsj.getInt("top10"); top15_bs = rsj.getInt("top15");				
					top30_bs = rsj.getInt("top30");    top50_bs = rsj.getInt("top50"); top100_bs = rsj.getInt("top100"); 
					outsidetop100_bs = rsj.getInt("outsidetop100"); notfound_bs = rsj.getInt("notfound");
					System.out.println("Baseline results for that evallevel and label: " + v0_evalLevel +", " + label + ", "  +", "+ top5_bs +  ", " + top10_bs + ", "+ top15_bs +
							", "+ top30_bs + ", "+ top50_bs + ", "+ top100_bs + ", "+ outsidetop100_bs + ", "+ notfound_bs);
				}				
				
				for (String feature: features) {	 //for each feature 
									
					
					// now we compare with teh results
					String queryk = "SELECT * from "+results+" where "+feature+" = -1  and evalLevel = '"+evalLevel+"' and label = '"+label+"';";
					PreparedStatement psk = connection.prepareStatement(queryk);
					ResultSet rsk = psk.executeQuery();
					//ArrayList<String> labelsForProposal = new ArrayList<String>(); 
					String v_evalLevel="", v_label = "";
					Integer top5 = 0, top10, top15, top30, top50, top100,outsidetop100,notfound; //totalProbability,evalLevel
					while ( rsk.next() )    { 
						v_evalLevel= rsk.getString("evalLevel");  
						//if(v_evalLevel.equals("sentencesfromdistinctmessages"))  //we just shorten the field
						//	v_evalLevel  ="messages";
						//else
						//	v_evalLevel  ="sentences";
						
						label = rsk.getString("label");
						top5 = rsk.getInt("top5"); 		top10 = rsk.getInt("top10"); top15= rsk.getInt("top15");				
						top30 = rsk.getInt("top30");    top50 = rsk.getInt("top50"); top100 = rsk.getInt("top100"); 
						outsidetop100 = rsk.getInt("outsidetop100"); notfound = rsk.getInt("notfound");
						System.out.println("\t"+v_evalLevel +", " + label + ", " + feature +", "+ top5 +  ", " + top10 + ", "+ top15 + ", "+ top30 + ", "+ top50 + ", "+ top100 + ", "+ outsidetop100 + ", "+ notfound);
					}
					
					
					try //insert compared baselien results for each field
				    { 						
						//label and evallevel used
						/*
					    String query = "update comparebaseline set ? = ?  WHERE  field = ? ";
					    PreparedStatement preparedStmt = connection.prepareStatement(query);
					    column_to_update = "acc_sent";  
					    feature = "sentenceHintProbablity";
					    preparedStmt.setString (1, column_to_update);	    preparedStmt.setInt (2, top5);   preparedStmt.setString (3, feature);
					    preparedStmt.executeUpdate();
					    */
						Statement stmt = connection.createStatement();
					    String sql = "UPDATE comparebaseline SET "+column_to_update +" = "+(top5 - top5_bs)+" WHERE field = '" + feature + "'  ;"; 
					    // WHERE field = '"+ feature + " ;";
					    int updateCount = stmt.executeUpdate(sql);
				    }
				    catch (Exception e)
				    {
				      System.err.println("Got an exception! ");				      System.err.println(e.getMessage());
				    }
					
					
					
					rsk.close();
				}
			}
			
		}
	
	}
	/* This is what we want and the values should have been compared to baseline results
	 
					Sentence Based				Message Based	
					Accepted	Rejected	Accepted	Rejected
	shp			|			|			|			|
	slimps		|			|			|			|
	ddp			|			|			|			|
	ropps		|			|			|			|
	mlps		|			|			|
	mshps		|			|			|
	arp			|			|			|
	mtrmps		|			|			|
	smsstps		|			|			|
	rlfteps		|			|			|
	mcstps		|			|			|
	ppstps		|			|			|
	nps			|			|			|
	*/			

}
