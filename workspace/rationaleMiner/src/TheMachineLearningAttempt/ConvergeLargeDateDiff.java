package TheMachineLearningAttempt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import connections.MysqlConnect;
import miner.process.ProcessingRequiredParameters;
// dec 2018
// check to see the which peps have lage values for datediff and if we can find a message with lower one
// main aim, since there are several proposals where main reason sentence is too far from state commit, this affects teh ML
// so here we want to differentiate teh dates and find all those sentences messages which are closest to datediff =0 
// so we can have additioanl column which denotes the sentence/message is reasontypemessage as well as as closest to datediff  
public class ConvergeLargeDateDiff {
	static ProcessingRequiredParameters prp = new ProcessingRequiredParameters();
	static MysqlConnect mc = new MysqlConnect();	static Connection conn;
	static String tableName = "autoextractedreasoncandidatesentences_dec";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		conn = mc.connect();
		try		{	
			String sentence="";
			Integer mid = null,  proposal = null, containsReason=null,dateDiff=null;
			String sql0;		Statement stmt0;		ResultSet rs0, rs4,rs5;	
			//System.out.format("%5s,%2s, %120s,%20s", p, cr, s,ms); 
			//System.out.println("Sentences for Proposal where ContainsReason=1 and datediff > 69, regardless of ReasonMessage");
			//System.out.println("All Sentences for Same Proposal where ContainsReason=1 and datediff > 0 also exist, regardless of ReasonMessage");
			//System.out.println("All Sentences for Same Proposal where ReasonMessage=1 and datediff > 0 also exist, regardless of ContainsReason");
			System.out.println("PEP,MID,DD,CR,RM,AR,TM, SENTENCE");
			for (int i = 0;i< 5000;i++) //(int i = 214;i> 213;i--)  //pepsWithLabel) //5000  = 414; i< 415;i++)  (int i = 5000;i> 0;i--)
			{
				sql0 = "SELECT * from "+tableName+" where proposal = "+i+" and containsReason = 1 and datediff > -15 "
					+ " order by proposal ";  //where pep != -1 order by pep asc LIMIT 5000 "; //and proposal = 386
				stmt0 = conn.createStatement();			rs0 = stmt0.executeQuery( sql0 );
				int count = prp.mc.returnRSCount(sql0, conn);
				//for each row we get similar rows
				while (rs0.next()) {
					proposal = rs0.getInt("proposal");		dateDiff = rs0.getInt("datediff");			containsReason = rs0.getInt("containsReason"); mid = rs0.getInt("messageid");	
					sentence = rs0.getString("sentence");	int msgID = rs0.getInt("messageID");
					System.out.println(proposal +"," + msgID+"," + dateDiff + ", " + rs0.getInt("containsReason") + ", "+ rs0.getInt("messageTypeIsReasonMessage") + ","+ returnAuthorRole(i,msgID) +"," + rs0.getString("termsMatched").replaceAll(",", " ")   + "," +sentence.replaceAll(",", " "));
									
				}
				if (count>0) {
					//now find if these peps have a sentence from a message much closer to datediff = 0
					//if no, fine, lets just see if the above message having teh sentence is a reasontype message
					//if yes, hope it is a reasontypemessage and we dont count the above sentence and considere it as anamoly
					String query = "SELECT * from "+tableName+" where proposal = "+i+" and containsReason = 1 and datediff > -15 order by datediff asc ";
			        Statement stmt5 = conn.createStatement();	
			        rs5 = stmt5.executeQuery(query);
			        while (rs5.next()) {	
						System.out.println(","+rs5.getInt("messageID")+", "+ rs5.getInt("datediff") + ", " + rs5.getInt("containsReason") + ","+ rs5.getInt("messageTypeIsReasonMessage") + ", "+ returnAuthorRole(i,rs5.getInt("messageID")) + ","+  rs5.getString("termsMatched").replaceAll(",", " ")  +", "+rs5.getString("sentence").replaceAll(",", " "));	
			        }
			        //get all reasontype message sentences closer to datediff = 0
			        String query4 = "SELECT * from "+tableName+" where proposal = "+i+" and messageTypeIsReasonMessage = 1 and datediff < 69 order by datediff asc ";
			        Statement stmt4 = conn.createStatement();	
			        rs4 = stmt4.executeQuery(query4);
			        while (rs4.next()) {	
						System.out.println(","+rs4.getInt("messageID")+"," + rs4.getInt("datediff")+", " + rs4.getInt("containsReason")  +" ,"+rs4.getInt("messageTypeIsReasonMessage") + ", "+ returnAuthorRole(i,rs4.getInt("messageID")) + ", "+ rs4.getString("termsMatched").replaceAll(",", " ")  +", "+ rs4.getString("sentence").replaceAll(",", " "));	
			        }
				}
				else{
					//get all reasontype message sentences closer to datediff = 0
			        String query4 = "SELECT * from "+tableName+" where proposal = "+i+" and messageTypeIsReasonMessage = 1 and datediff < 69 order by proposal ";
			        Statement stmt4 = conn.createStatement();	
			        rs4 = stmt4.executeQuery(query4);
			        while (rs4.next()) {	
//						System.out.println("\t\t RM:"+rs4.getInt("messageTypeIsReasonMessage") + ", DD:" + rs4.getInt("datediff") + ", "+ rs4.getString("sentence"));	
			        }
				} 
			}
		}   catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");			System.out.println(StackTraceToString(se)  );	
		}   catch (Exception e)	    {
			System.err.println("Got an exception! ");			System.out.println(StackTraceToString(e)  );	
		}
		
		mc.disconnect();
	}
	public static String returnAuthorRole(int proposal, int mid) {
		Statement stmt5; String role="";
		try {
			String query = "SELECT authorsRole from allmessages where pep = "+proposal+" and messageID = "+mid + ";";	        
			stmt5 = conn.createStatement();
			ResultSet rs5 = stmt5.executeQuery(query);
	        if (rs5.next()) {	
				//System.out.println("\t DD:"+ rs5.getInt("datediff") + ", CR:" + rs5.getInt("containsReason") + " RM:"+ rs5.getInt("messageTypeIsReasonMessage") +", "+ "TM: ("+ rs5.getString("termsMatched")  +"), "+rs5.getString("sentence"));	
	        	role = rs5.getString("authorsRole");
	        }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
        return role;
	}
	
	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}
}
