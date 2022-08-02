package GUI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import connections.MysqlConnect;
import miner.process.ProcessingRequiredParameters;


//After extracting reasons manually, how does it seem we can match the reasons 
//maybe we can somehow match sentences
//find candidate sentences and using out training sentences match and see what comes out
//question is how to find candidate sentences?
//also question is that do we only look at adjacent cause and effect sentences and inter-sentence cause and effect (both in the same sentence), 
//Maybe in the extreme case extend this to  sentences in the same message..but not too far apart..max separated by two paragraphs

//at the moment we only look at intra-sentence and adjacent cause and effect sentence
//first we look for candidate effects ..accepted, rejected, etc and then match the cause nearby
	//we look for the state and additional sub state terms (accepted, rejected, vote, poll)
	//and identify sentences which contain these as candidate sentences

//to match reasons for acceptance, etc we have to look at more factors to limit our search space  which would also reduce the amount of data we have to process and decrease uncertainity,
// who normally pronounces - bdfl or delegate - therefore to find the cuase, effect, etc we can look into those messsges by these people
// when - very near the date of teh official state change - stae change does not exist in jeps so we maybe have to depend of triples for state capture
// where acceptance reasons normally in python dev/ lists/ python 3000 - and who knows where so we wont consider these
// 		first 20 lines of message -- lets see by judgeing few acceptance, etc


public class MatchReasonsInText {
	static ProcessingRequiredParameters prp = new ProcessingRequiredParameters();
	static Connection conn;
	
	public static void main(String[] args) throws SQLException {
		String[] labels = {"draft","accepted","rejected","final","deferred","superseded","withdrawn"};
        
		connections.MysqlConnect mc = new MysqlConnect();		conn = mc.connect();	
		Statement stmt = null;
		stmt = conn.createStatement();	
		
		for (String l : labels) {
			extractCauseEffectForLabel(stmt, l);			
		}
	}
	public static String extractCauseEffectForLabel(Statement stmt,  String l) throws SQLException {
		int id,proposal;
		String label="",causeSentence="",effectSentence="";
		String sql = "SELECT * from trainingdata where label = '"+l + "' order by pep asc, messageid asc"; //, datetimestamp asc";
		// results table or postprocessed table																				 
		ResultSet rs = stmt.executeQuery(sql);  //date asc
		int totalRows = prp.mc.returnRSCount(sql, conn), processedCounter=0;	System.out.println("\nLabel: " + l + " Number of proposals: " +totalRows);
		int arrayCounter=0, recordSetCounter=0;
		System.out.println("label : " + label);
		while (rs.next() && totalRows < 1000) {		//dont process those peps which have too many messages
			recordSetCounter++;	
			id = rs.getInt("id"); proposal = rs.getInt("pep"); label = rs.getString("label");	causeSentence = rs.getString("causeSentence");	effectSentence = rs.getString("effectSentence");
			System.out.println("Proposal : " + proposal + " causeSentence: " + causeSentence);
			System.out.println("\t\tEffectSentence : " + effectSentence);
//			populateCauseEffectCleaned(id,causeSentence,effectSentence);	//clean
		}
		return label;
	}
	public static void populateCauseEffectCleaned(int rowid, String causeSentence, String effectSentence) throws SQLException {
		PreparedStatement preparedStmt;
		String causeSentence_cleaned="",effectSentence_cleaned="";

		//have to find someway of identifying headers and removing them
		
		//clean
		//remove plus and equal signs 
		causeSentence_cleaned = causeSentence.replaceAll(Pattern.quote("+"), "").replaceAll(Pattern.quote("="), "").trim();
		effectSentence_cleaned = effectSentence.replaceAll(Pattern.quote("+"), "").replaceAll(Pattern.quote("="), "").trim();
		//removeDoubleSpacesAndTrim
		causeSentence_cleaned = prp.psmp.removeDoubleSpacesAndTrim(causeSentence_cleaned);
		effectSentence_cleaned = prp.psmp.removeDoubleSpacesAndTrim(effectSentence_cleaned);
				
		System.out.println("Cleaned : " + " causeSentence_cleaned: " + causeSentence_cleaned);
		System.out.println("\t\tEffectSentence_cleaned : " + effectSentence_cleaned);
		
		String updateQuery = "update trainingData set causeSentence_cleaned = ?, effectSentence_cleaned = ? where id = ?";//start updating 'messageNumberInFile' as already ordered
		preparedStmt = conn.prepareStatement(updateQuery);
		preparedStmt.setString(1, causeSentence_cleaned);	  preparedStmt.setString(2, effectSentence_cleaned); 	preparedStmt.setInt(3, rowid);
		int i = preparedStmt.executeUpdate();
		if(i>0)   {
			//System.out.println("success");
		}  else{
			System.out.println("stuck somewhere mid " + rowid + " file " + rowid);
		}
		updateQuery =null;
	}
	
}
