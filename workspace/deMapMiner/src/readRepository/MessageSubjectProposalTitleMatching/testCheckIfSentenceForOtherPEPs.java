package readRepository.MessageSubjectProposalTitleMatching;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import connections.MysqlConnect;
import miner.process.ProcessMessageAndSentence;

public class testCheckIfSentenceForOtherPEPs {
	
	static ProcessMessageAndSentence pms = new ProcessMessageAndSentence();
	
	 public static void main (String[] args) {
		 
		MysqlConnect mc = new MysqlConnect();
		Connection conn = mc.connect(); 
		
		//pep matching pep 0 to pep 308 ..
		//pep 8 in pep 308....
		//pep 308 in pep 308
		 
		Integer pep = 8, messageID,pepMessageCounter=0,PEPMessagesSkipped=0,PEPMessagesProcessed=0; 
		String subject="";
		String sql = "SELECT subject,messageid from allmessages WHERE pep = " + pep  + " order by messageid;"; //and messageid = 1217260
		//	and messageid IN (12296,28297,33230,32118,113951,119653,148855,162845,192994,218292,351518,118129,2006533,2102118) "
		
		try {
			/*
			Statement stmt = conn.createStatement();
			// results table or postprocessed table										//5											//10
			ResultSet rs = stmt.executeQuery(sql); 
			while (rs.next()){     //check every message 
				subject = rs.getString(1);
				messageID = rs.getInt(2);
				//System.out.print(" Message subject: " + subject);
				System.out.println("MID: "+ messageID + " Message Subject: " + subject);
				boolean ifSubjectContainsOnlyOtherPepNumbers = false;					
				ifSubjectContainsOnlyOtherPepNumbers = pms.checkIfTextContainsOnlyOtherPepNumbers(subject.toLowerCase(), pep);
	    		
				if(ifSubjectContainsOnlyOtherPepNumbers) {	//we skip this message and process the next one
					PEPMessagesSkipped++;
					System.out.println("\tMessage Subject contains only other PEPs, we skip. MID: ");//+ messageID + " Message Subject: " + subject);
					//continue;	
				}
				else {
					PEPMessagesProcessed++;
					System.out.println("\tWe go ahead and process"); 
					System.out.println("\tWe go ahead and process. MID: "+ messageID + " Message Subject: " + subject);
				}
				pepMessageCounter++;
			}
			System.out.println("Total message in PEP: "+pepMessageCounter);
			System.out.println("Messages skipped "+PEPMessagesSkipped  + " processed: " + PEPMessagesProcessed);
			*/
			
			 
			 String t = "[python-dev] (my) revisions to pep318 ad pep 8 finally done.";
			 int option =0;
			 	boolean allowZero = true, ifSubjectContainsOnlyOtherPepNumbers = false;					
				ifSubjectContainsOnlyOtherPepNumbers = pms.checkTextContainsProposalNumber_WithOptions(t.toLowerCase(), pep,option,allowZero);
											
				if(ifSubjectContainsOnlyOtherPepNumbers) {	//we skip this message and process the next one
					PEPMessagesSkipped++;
					System.out.println("\tMessage Subject contains only other PEPs, we skip. MID: ");//+ messageID + " Message Subject: " + subject);
					//continue;	
				}
				else {
					PEPMessagesProcessed++;
					System.out.println("\tWe go ahead and process"); 
					//System.out.println("\tWe go ahead and process. MID: "+ messageID + " Message Subject: " + subject);
				}
			
		}
//		catch (SQLException e){
//			System.out.println( e.getMessage() );
//		}
		catch (Exception e){
			//continue;
			System.out.println(e.toString());
		}		
	}


}
