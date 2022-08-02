package readRepository.postReadingUpdates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import GeneralQueries.GetMessageStatisticsFunctions;
import connections.MysqlConnectForQueries;
import readRepository.readRepository.MessageReadingUtils;
import connections.MysqlConnect;
import miner.process.PythonSpecificMessageProcessing;
import utilities.ReturnNattyTimeStamp;

//Here we set the fields required for social network analysis. This includes inReplyToUser field and the messageid.
//These is done in 3 parts.

//1. Some messages dont have the inreplyTofield in correct format. 
// If any information is available about the messageid to which its a reply, then that is assigned here
// But there are some messages where the inreplyto field does not have any information about the message being replied to
// but has information about the user to which its a reply.
// in that case, this class just assigns a user to the inReplyToUser field

//2. Now the remaining messages, which dont have a inreplytouser set from the above process, 
//   were already set using a recursive query using the in replytofield (where available) and look up
//This is done using below query
//	UPDATE allmessages a1
//	JOIN allmessages a2 ON a2.emailMessageID = a1.inReplyTo
//	SET a1.inReplyToUser = a2.senderName
//	WHERE a1.inReplyTo IS NOT NULL

//Once this is done, we can create the alldevelopers table, as it would have these two above fields sorted out and do social network analysis for developers

public class UpdateForSocialNetworkAnalysis_AllMessages_inReplyTo {
	//query each message
	//compute SENDERNAME - should be in format firstname.lastname or firstname.midlename.lastname - all in lowercase
	//store in same message
	
	static MessageReadingUtils mru = new MessageReadingUtils();
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();
	
	static GetMessageStatisticsFunctions f = new GetMessageStatisticsFunctions();
	
	static MysqlConnectForQueries mc = new MysqlConnectForQueries();
	static String updateQuery = null;
	static int i;
	
	public static void main(String args[])
	{	
		ReturnNattyTimeStamp rts = new ReturnNattyTimeStamp();
		Date dateTimeStamp = null, date9=null;
		String author=null, inReplyTo=null, senderName;
		String emid = null, comma=",";
		int pepNumber, messageID;
		String newName = null;
		String lines[];
		
		String sql,sql0;
		Statement stmt,stmt0;
		ResultSet rs,rs0;
		
		try
		{
			Connection conn = mc.connect();	
			
			f.totalNumberOfMessages(conn);
			f.totalNumberOfUniqueMessages(conn);
			
			//FIRST DO FOR EMAILMESSAGEID
			sql = "SELECT AUTHOR, emailmessageid, inreplyto, inreplytouser, messageID, senderName from allmessages WHERE emailmessageid like '% %';";  //where pep != -1 order by pep asc LIMIT 5000
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);			
			
			while (rs.next())    
			{
				author = rs.getString(1);
				emid = rs.getString(2);
				//inReplyTo = rs.getString(3); //.contains("From:"))					
				messageID = rs.getInt(5);
				//senderName = rs.getString(6);
				
				if (emid.isEmpty() || emid==null)
				{}
				else
				{
					if (emid.contains(" at ")){
						emid = emid.replace(" at ", "@");
						updateTableWithEmailMessageID(conn, messageID, emid);	
					}
				}				
			}
			
			//SECOND DO FOR INREPLYTO
			
			Integer counter=0;
//			if(counter%1000==0)
//				System.out.println("counter "+ counter + " messageid "+i);				
//			PROCESS one pep at a time as otherwise gives error....if we wanted to give the machine less recordsets to work with at a time			//LIMIT "+x+","+y+"
			sql0 = "SELECT AUTHOR, emailmessageid, inreplyto, inreplytouser, messageID, senderName from allmessages WHERE inreplyto like '% %';";  //where pep != -1 order by pep asc LIMIT 5000
			stmt0 = conn.createStatement();
			rs0 = stmt0.executeQuery( sql0 );			
			
			while (rs0.next())    
			{
				author = rs0.getString(1);
				emid = rs0.getString(2);
				inReplyTo = rs0.getString(3); //.contains("From:"))					
				messageID = rs0.getInt(5);
				senderName = rs0.getString(6);
				
				if (inReplyTo.isEmpty() || inReplyTo==null)
				{}
				else
				{
					String finalStr;
					//his reply is to one of his previous message, so replace with sendername himself/herself
					//eg  "Your message at 210308 on Tuesday 8 January 2008"
					if (inReplyTo.contains("Your message")){
//						System.out.format("%70s",inReplyTo);
//						System.out.println();
//						counter++;	
						//DONE
						updateTableWithinReplyToUser(conn, messageID, senderName);						
					}
					
					else if (inReplyTo.contains("Message from")){
//						System.out.format("%70s",inReplyTo);
//						System.out.println();
//						counter++;
//						
//**						updateTableWithinReplyToUser(conn, messageID, senderName);	
						String[] tokens = inReplyTo.split("\"");
						int c = (tokens.length - 1);
						//System.out.println("Number of apostrophe : " + c);
						if (inReplyTo.contains("\"") && c >1){
							String te = inReplyTo.split("\"")[1];							
//							System.out.format("%70s%50s",inReplyTo,te);
//							System.out.println();
//							counter++;
							//DONE
//							newName = pms.getAuthorFromString(newName);
							updateTableWithinReplyToUser(conn, messageID, newName);	
						}
						else{
							String emailAddress="";
							if (inReplyTo.contains("@") ){								
								emailAddress = returnStringWithCharacter("@",inReplyTo);
								String main = StringUtils.substringBetween(inReplyTo, "Message from",emailAddress).trim();
								newName = pms.getAuthorFromString(main);
								//DONE
								updateTableWithinReplyToUser(conn, messageID, newName);
								//System.out.format("%70s%50s",inReplyTo,newName);
								//System.out.println();
								//counter++;
							}
//							else{
//								System.out.format("%70s",inReplyTo);
//								System.out.println();
//								counter++;
//							}
							//DONE
//							updateTableWithinReplyToUser(conn, messageID, newName);	
//							System.out.format("%70s%50s",inReplyTo,newName);							
//							System.out.println();
//							counter++;
						}						
					}
					
					//Jason Tishler's message of "Thu 02 Jan 2003 164021 -0500"
					else if (inReplyTo.contains(" message of ")){						
//						System.out.format("%70s%50s",inReplyTo,newName);							
//						System.out.println();
//						counter++;
						if (inReplyTo.contains("@")){
//							String main = StringUtils.substringBetween(inReplyTo, emailAddress, "message of").trim();
							//get first string - the modified email address
							String emailAddress = returnStringWithCharacter("@",inReplyTo);
							//DONE
							updateTableWithinReplyTo(conn, messageID, emailAddress);	
						}
						else{
							//System.out.format("%70s",inReplyTo);							
							//System.out.println();
							//counter++;
							
							if (inReplyTo.contains("=iso-8859")){
							//	inReplyTo = inReplyTo.replace("'s", "");
								//replace entire string with the iso
							}
							String[] split = inReplyTo.split("message of");
							if (split[0]==null){
								
							}
							else{
								String firstSubString = split[0];
								
								String[] tokens = inReplyTo.split("\"");
								int c = (tokens.length - 1);
								if (firstSubString.contains("\"") && c >1){
									String te2 = inReplyTo.split("\"")[1];
									newName = pms.getAuthorFromString(te2);
									//System.out.format("%70s%50s",inReplyTo,newName);							
									//System.out.println();
									//counter++;									
									//DONE
									updateTableWithinReplyToUser(conn, messageID, newName);	
									
									//SOME LEFT second one is left
						            //"Lars Lundstedt"'s message of "Wed 03 May 2000 171235 GMT"                                    lars.lundstedt
									//"=iso-8859-2qPrzemys=B3aw_G._Gawro=F1ski="'s message of "Mon 29 May 2000 135140 +0200"          =iso-8859-2qprzemys=b3aw_g._gawro=f1ski=
								}
								else{
									firstSubString = firstSubString.replace("'s", "");
//									System.out.format("%70s%70s",firstSubString,inReplyTo);
//									System.out.println();
//									counter++;
									newName = pms.getAuthorFromString(firstSubString);
									//DONE
									updateTableWithinReplyToUser(conn, messageID, newName);
									
									//some left
									//Michael =iso-8859-1qStr=F6der= Michael =iso-8859-1qStr=F6der's= message of "Wed 14 Jun 2000 103649 +0200"
                                    //Courageous                   Courageous's message of "Sun 04 Jun 2000 212607 GMT"
                                    //=iso-8859-1qFran=E7ois= Pinard =iso-8859-1qFran=E7ois= Pinard's message of "06 Jun 2000 144605 -0400"
								}
							}							
						}
						
					}
					
					else if (inReplyTo.contains("Message by ")){
						if(inReplyTo.contains("@")){
							String emailAddress = returnStringWithCharacter("@",inReplyTo);						
							//retun string between two strings
							String main = StringUtils.substringBetween(inReplyTo, "Message by",emailAddress).trim();
							newName = pms.getAuthorFromString(main);
							//DONE
							updateTableWithinReplyToUser(conn, messageID, newName);
							//System.out.format("%70s%70s",main,inReplyTo);
							//System.out.println();
							//counter++;
							//some left
//							/Message by skip@pobox.com

						}
						else{
//							Message by =iso-8859-1qAndy=20Robinson=
							if (inReplyTo.contains("Message by "))
								inReplyTo =inReplyTo.replace("Message by ", "");
							if (inReplyTo.contains("=iso-8859-1q"))
								inReplyTo =inReplyTo.replace("=iso-8859-1q", "");
							if (inReplyTo.contains("=20"))
								inReplyTo =inReplyTo.replace("=20", " ");
							if (inReplyTo.contains("="))
								inReplyTo =inReplyTo.replace("=", "");
							//SET INREPLYTOUSER = COMPUTE SENDERNAME
							newName = pms.getAuthorFromString(inReplyTo);
							//DONE
							updateTableWithinReplyToUser(conn, messageID, newName);
						}

					}
					else if (inReplyTo.contains("message from ")){						
						//200302251213.52956.aleaxit@yahoo.com message from Alex
						if (inReplyTo.contains("message from")){
							int startingIndex = inReplyTo.indexOf("message from");
							String main = inReplyTo.substring(0, startingIndex);
							newName = pms.getAuthorFromString(main);
							//DONE
							updateTableWithinReplyToUser(conn, messageID, newName);
						}
//						else{
//							System.out.format("%70s",inReplyTo);
//							System.out.println();
//							counter++;
//						}
					}
					else if (inReplyTo.contains(" at ") && (inReplyTo.split(" ").length ==3) ){
						inReplyTo =inReplyTo.replace(" at ", "@");
						//System.out.format("%70s",inReplyTo);
						//System.out.println();
						//counter++;
						//DONE
						updateTableWithinReplyTo(conn, messageID, newName);						
					}
					else if (inReplyTo.trim().split(" ")[0].contains("@")) {
//						System.out.format("%70s",inReplyTo);
//						System.out.println();
//						counter++;
						//just get the first string
						String main = inReplyTo.split(" ")[0];
						//DONE
						updateTableWithinReplyTo(conn, messageID, main);	
					}	
					else if ((inReplyTo.split(" ").length ==3) && inReplyTo.trim().split(" ")[2].contains("@")) {							
						if (inReplyTo.contains("\"")){
							String te = inReplyTo.split("\"")[1];
//							System.out.format("%70s%50s",inReplyTo, te);
//							System.out.println();
//							counter++;
							//DONE
							updateTableWithinReplyToUser(conn, messageID, te);							
						}
						//here now
						else{
							String emailAddress = returnStringWithCharacter("@",inReplyTo);	
							String main = inReplyTo.replace(emailAddress, "");							
							newName = pms.getAuthorFromString(main);
							//System.out.format("%70s%50s",inReplyTo, main);
							//System.out.println();
							//counter++;
							//DONE
							updateTableWithinReplyToUser(conn, messageID, main);
						}
					}
					else if ((inReplyTo.split(" ").length ==4) && inReplyTo.trim().split(" ")[3].contains("@")) {
						if (inReplyTo.contains("\"")){
							String te = inReplyTo.split("\"")[1];
						//	System.out.format("%70s%50s",inReplyTo, te);
						//	System.out.println();
						//	counter++;
							newName = pms.getAuthorFromString(te);
							//DONE
    						updateTableWithinReplyToUser(conn, messageID, newName);							
						}
						else{							
							String emailAddress = returnStringWithCharacter("@",inReplyTo);	
							String main = inReplyTo.replace(emailAddress, "");
							newName = pms.getAuthorFromString(main);
							//DONE
							//send user for conversion into firstname.m.lastname format
     						updateTableWithinReplyToUser(conn, messageID, newName);
						}						
					}
					else if (inReplyTo.contains(" at ") && inReplyTo.trim().split(" ")[1].equals("at")){
						
						String te[] = inReplyTo.split(" ");						
						int count=0;
						String irt = te[0] + "@" + te[2];						
						//System.out.format(irt);
						//System.out.println();
						//counter++;
						//DONE
						updateTableWithinReplyTo(conn, messageID, irt);
					}					
					//from "Martin v. Loewis" at				
					
					else{						
						if (inReplyTo.contains("\"")){
							String te = inReplyTo.split("\"")[1];
							//System.out.format(te);
							//System.out.println();
							//counter++;	
							//DONE
							updateTableWithinReplyTo(conn, messageID, te);							
						}
						else if (inReplyTo.contains("@")){
							String list[] = inReplyTo.split(" ");
							for (String str : list){
								if (str.contains("@")){									
									//System.out.format("%70s",inReplyTo);
									//System.out.println();
									//counter++;
									//get the first email from inreplyto 
									String emailAddress = returnStringWithCharacter("@",inReplyTo);	
									//DONE
									updateTableWithinReplyTo(conn, messageID,emailAddress);
									break;
								}
							}							
						}
						// these cannot be further captured as no data here
						else{
							// these cannot be further captured as no data here
						}
//						updateTableWithinReplyToUser(conn, messageID,first);
					}
					
					
				}						
				
				lines=null;
				//email=null;
			  //end if	
			}	//end while
			stmt0.close();
			sql0=null;
			stmt0=null;
			rs0=null;
			System.out.println("Counter: "+counter);
		
			
		}   catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");
			System.err.println(se.getMessage() + " mid " + emid);
		}   catch (Exception e)	    {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage() + " mid " + emid + " inReplyTo "+inReplyTo);
		}
	}
	private static String returnStringWithCharacter(CharSequence c,String inReplyTo) {
		String te[] = inReplyTo.split(" ");
		String emailAddress = null;
		for (String t: te){								
			if (t.contains(c)){
				emailAddress=t;
			}
		}
		return emailAddress;
	}
	//
	private static void updateTableWithEmailMessageID(Connection conn, Integer mid, String str)
			throws SQLException {
		  updateQuery = "update allmessages set emailmessageID = ? where messageID = ?";
		  PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
		  preparedStmt.setString (1, str);
		  preparedStmt.setInt(2, mid);
		  i = preparedStmt.executeUpdate();
		  if(i>0)   {
		          //System.out.println("success");
		  }
		  else  {
		         System.out.println("stuck somewhere mid " + mid + " newName " + str);
		    }
		  updateQuery =null;
	}
	
	private static void updateTableWithinReplyTo(Connection conn, Integer mid, String str)
			throws SQLException {
		  updateQuery = "update allmessages set inReplyTo = ? where messageID = ?";
		  PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
		  preparedStmt.setString (1, str);
		  preparedStmt.setInt(2, mid);
		  i = preparedStmt.executeUpdate();
		  if(i>0)   {
		          //System.out.println("success");
		  }
		  else  {
		         System.out.println("stuck somewhere mid " + mid + " newName " + str);
		    }
		  updateQuery =null;
	}
	//eventually we would update the senderName column in the allmesaages table
	private static void updateTableWithinReplyToUser(Connection conn, Integer mid, String str)
			throws SQLException {
		  updateQuery = "update allmessages set inReplyToUser = ? where messageID = ?";
		  PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
		  preparedStmt.setString (1, str);
		  preparedStmt.setInt(2, mid);
		  i = preparedStmt.executeUpdate();
		  if(i>0)   {
		          //System.out.println("success");
		  }
		  else  {
		         System.out.println("stuck somewhere mid " + mid + " newName " + str);
		    }
		  updateQuery =null;
	}
}
