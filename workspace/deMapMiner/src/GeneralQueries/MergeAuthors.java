package GeneralQueries;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import connections.MysqlConnect;
import miner.processLabels.TripleProcessingResult;

//NOTE TIS CODE IS NOT BEING USED AT THE MOMENT IS IS REPLACED BY ..

// This code is to merge same developers in all developers table. 
// As distinct authors from allmessages table returns so many authors which are duplicates.
// Examples guido.van.rossum == gvanrossum
// This program tries to merge the same author.
// BEFORE RUNNING THIS RUN DISTINCT AUTHOR ON ALLMESSAGES. THEN POPULATE DISTINCT AUTHORS IN ALLDEVELOPERS

public class MergeAuthors {	
	public static void main(String args[])
	{
		MysqlConnect mc = new MysqlConnect();
		Connection connection = mc.connect();
		
		//create an arraylist of authors and replytocount - update this as you find more replies to this author
		ArrayList<String> allAuthors = new ArrayList<String>();		
		allAuthors = getAllAuthors(connection,"authormatching");
		Integer counter=0,mid;
		String sender, replySender,replySubject;
		long t0, t1, t2;
		Integer countReplies=0;
		t0 = System.currentTimeMillis();
		try{
			//select
			for (String first : allAuthors){  
				String firstName="",middleName="",lastName="";				
				
				sender= first;
				if(sender.trim().length()==0)
				{}
				else{				
					System.out.println("SenderName " + sender);				
					//get first letter
					char firstLetter = sender.charAt(0);
					//split name
					if (sender.contains(".")){
						String names[] = sender.split("\\.");
						Integer x=0;
						//System.out.print("\t\t");
						for (String n : names) {						
//							System.out.print(names[x]+" ");
							x++;
						}						
						if(names.length==2){
							firstName = names[0];
							lastName = names[1];
						}
						else if(names.length==3){
							firstName = names[0];
							middleName = names[1];
							lastName = names[2];
						}
//						System.out.println();
					}
//					else {
//						System.out.println();
//					}
					
					//match against list again ..get close matching ones and insert
					for(String second: allAuthors){
						String firstName2="",middleName2="",lastName2="";	
						if(second.trim().length()==0)
						{}
						else{
							char firstLetter2 = second.charAt(0);
							if (second.contains(".")){
								String names2[] = second.split("\\.");
								Integer y=0;
	//							System.out.print("\t\t");
								for (String n : names2) {						
	//								System.out.print(names2[y]+" ");
									y++;
								}
								if(names2.length==2){
									firstName2 = names2[0];
									lastName2 = names2[1];
								}
								else if(names2.length==3){
									firstName2 = names2[0];
									middleName2 = names2[1];
									lastName2 = names2[2];
								}
								//System.out.println();
							}
						
							//just make sure no null matches
							if (firstName2==null || firstName2.isEmpty() || firstName2.equals(""))
								firstName2="xxxxxxxxxxxxxxzzzzzzzzzzzzzzzzz";
							if (middleName2==null || middleName2.isEmpty() || middleName2.equals(""))
								middleName2="xxxxxxxxxxxxxxzzzzzzzzzzzzzzzzz";
							if (lastName2==null   || lastName2.isEmpty() || lastName2.equals(""))
								lastName2="xxxxxxxxxxxxxxzzzzzzzzzzzzzzzzz";
							
							//COMPARE
							//firstletter matches 
							//first string contains middlename and lastname of secondstring
							if (!first.equals(second) && firstLetter==firstLetter2 && first.contains(middleName2) && first.contains(lastName2)){
								System.out.println("\t\t MATCHED " + second);
							}
						}
						second=null;
					}
					
					
					//merge
					/*
					String query = "update distinctauthor set RepliesTo = (SELECT count(emailMessageID) from all_distinctmessages WHERE inReplyTo = '"+emailMessageID+"' order by date2) where messageID = ?";
				    PreparedStatement preparedStmt = connection.prepareStatement(query);
				    //preparedStmt.setInt   (1, countReplies);
				    preparedStmt.setInt   (1, mid);
				    preparedStmt.executeUpdate();
				    */				
				}
				
				//iterate merge
	//			compareAuthors(distinctAuthors, connection);
				
				first=null;	
			}
			t1 = System.currentTimeMillis();
			System.out.println("\nFinished author - Elapsed time =" + (t1 - t0) / 1000 + " minutes");
		}
		
		catch (Exception ex)	{
			System.out.println( ex.getMessage() );
		}
	}
	
	//martin.von.loewis (martin, martin.v.lã¶wis, martin.von.lã¶wis, martin.von.lã¶wis)
	//guido.van.rossum (gvanrossum, gvr)
	// steven.daprano (<=steven.d'aprano)
	
	//if authors stored in a table already
	public static ArrayList<String> getAllAuthors(Connection conn, String tablename){
		ArrayList<String> distinctAuthors = new ArrayList<String>();
		
		try	{
			//String query= "SELECT distinct(senderName) FROM allpeps;";
			//ordered by most posts author										//filter when stopped in middle - senderName like 'ge%' AND 
												//distinctauthorsallmessages	// senderName like 'gui%' AND //AND senderName  = 'guido.van.rossum'
			String query = "select senderName from "+tablename+" where senderName IS NOT NULL Order By SenderName ASC ;";
			
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);			
			 
			while (rs.next())  {
				distinctAuthors.add(rs.getString(1));
			}
			//System.out.println("\n\nTotal number of peps in "+ pepTypeString + " is " + counter + " list size " + peps.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}		
		return distinctAuthors;
	}	
	
	
	public static ArrayList <String> compareAuthors(ArrayList <String> authorList, Connection conn) throws SQLException
  	{ 
  		Boolean repeatedSentence = false;
  		Boolean swap = false;
  		
  		ArrayList<Integer> al = new ArrayList<Integer>(); 
  		
  		if(authorList.isEmpty()){
  			return null; 
  		}
  		
		int counter =0;
		for (int x=0; x <authorList.size()-1; x++){
			swap=false;
  				if(authorList.get(x) != null)
  				{
  					//for the moment just try to show the rest of authors whose name starts with same letter
  					for (int y=0; y <authorList.size()-1; y++){
  						String xValue = authorList.get(x).toLowerCase();		//to be merged into
  	  					String yValue = authorList.get(y).toLowerCase();		//to be deleted
  	  					
  	  					String firstName = xValue.split(".")[0];
  					}
  					
  					//try code so that whichever is bigger to be merged into and the one shorter in length to be deleted
  					
  					String xValue = authorList.get(x).toLowerCase();		//to be merged into
  					String yValue = authorList.get(x+1).toLowerCase();		//to be deleted
  						  					
  					//try to assign which one is bigger - this will be merged into
  					String xx[] = null;
  					String yy[] = null;
  					if(!xValue.isEmpty())
  						xx = xValue.split(" ");	  					
  					if(!yValue.isEmpty())
  						yy = yValue.split(" ");	  					
  					Integer yLen = yy.length;
  					Integer xLen= xx.length;
  					
  					//swap and store bigger one in x
  					if (yLen > xLen){
  						String temp = yValue;
  						yValue = xValue;
  						xValue = temp;
  					}
  					//start comparing code
  					//author name field has been trimmed in database
  					//if smaller one firstname length ==1 and biggerone firstname is same 
  					if (yLen==1 && xx[0].equals(yy[0]))
  					{
//		  	        		merge(authorList, conn, x, xValue, yValue);		  			        
	  				}
  					else if (yLen==2 && xx[0].equals(yy[0]))
  					{
//		  	        		merge(authorList, conn, x, xValue, yValue);		  			        
	  				}
  				}	  			
		}
  			
  		
  	    
  		return authorList;
  	}

	private static void merge(ArrayList<String> authorList, Connection conn, int x, String xValue, String yValue) throws SQLException {

		//delete from list in memory
		System.out.println("Author matched--removing, x: "+xValue + " y: "+ yValue + " removed: " + yValue);
		authorList.remove(yValue);
		x--;
		
		//select the record to be merged into from table
		Integer xcountSeedingPosts=0, xcountRepliesToThisAuthorsPosts =0, xcountThisAuthorsRepliesToOthersPosts=0;
		String sqlx = "SELECT author,countSeedingPosts, countRepliesToThisAuthorsPosts, countThisAuthorsRepliesToOthersPosts from distinctAuthors WHERE author = "+ xValue +";";  
		PreparedStatement stmtx = conn.prepareStatement(sqlx);
		ResultSet rsx = stmtx.executeQuery( sqlx );	
		while (rsx.next()){
			xcountSeedingPosts					= rsx.getInt(1);
			xcountRepliesToThisAuthorsPosts		= rsx.getInt(2);
			xcountThisAuthorsRepliesToOthersPosts= rsx.getInt(3);
		}
		
		//select the record to be deleted from table
		Integer ycountSeedingPosts=0, ycountRepliesToThisAuthorsPosts =0, ycountThisAuthorsRepliesToOthersPosts=0;
		String sqly = "SELECT author,countSeedingPosts, countRepliesToThisAuthorsPosts, countThisAuthorsRepliesToOthersPosts from distinctAuthors WHERE author = "+ yValue +";";  
		PreparedStatement stmty = conn.prepareStatement(sqly);
		ResultSet rsy = stmty.executeQuery( sqly );	
		while (rsy.next()){
			ycountSeedingPosts					= rsy.getInt(1);
			ycountRepliesToThisAuthorsPosts		= rsy.getInt(2);
			ycountThisAuthorsRepliesToOthersPosts= rsy.getInt(3);
		}
		//update values
		xcountSeedingPosts = xcountSeedingPosts + ycountSeedingPosts;
		xcountRepliesToThisAuthorsPosts = xcountRepliesToThisAuthorsPosts + ycountRepliesToThisAuthorsPosts;
		xcountThisAuthorsRepliesToOthersPosts = xcountThisAuthorsRepliesToOthersPosts + ycountThisAuthorsRepliesToOthersPosts;
		
		//delete
		String sql0 = "DELETE FROM distinctAuthors WHERE author = " + yValue + ";"; 
		PreparedStatement stmt0 = conn.prepareStatement(sql0);
		ResultSet rs0 = stmtx.executeQuery( sql0 );
		//update
		String updateQuery = "update distinctAuthors set countSeedingPosts = ?,countRepliesToThisAuthorsPosts=?,countThisAuthorsRepliesToOthersPosts=? where author = ?";
		PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
		preparedStmt.setInt   (1, xcountSeedingPosts);
		preparedStmt.setInt   (2, xcountRepliesToThisAuthorsPosts);
		preparedStmt.setInt   (3, xcountThisAuthorsRepliesToOthersPosts);
		preparedStmt.setString(4, xValue);
		// execute the java preparedstatement
		preparedStmt.executeUpdate();

	}
	
}
