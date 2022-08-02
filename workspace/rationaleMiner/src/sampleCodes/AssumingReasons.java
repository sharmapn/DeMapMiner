package sampleCodes;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import readRepository.readRepository.ReadLabels;
import callIELibraries.JavaOllieWrapperGUIInDev;
import callIELibraries.ReVerbFindRelations;
import connections.MysqlConnect;
import de.mpii.clause.driver.ClausIEMain;
import miner.process.LabelTriples;
import miner.process.ProcessingRequiredParameters;
import miner.processLabels.CheckAndGetReasons;
import miner.processLabels.ProcessLabels;
import miner.processLabels.TripleProcessingResult;
import utilities.StateAndReasonLabels;

//what is this script for?
//Assuming reasons, especially when they are not explicitly stated
// 1. based on dates and community feedback
// for instance if a proposal goes from draft to accepted very quickly and no reason is stated, and also there were no objections, we can assume there was a general consensus...

//july 2018 attempt to add ability to do sentiment analysis of core developers on each proposal, based on +1/-1
//for each coredeveloper, we look at the last messages in between state/substate pairs e.g. 
//draft-accepted or draft-rejected or active-accepted and active-rejected 
//problem is a core developer may not say things just before accepted, but just before sub state, vote or poll, etc

//for each proposal, we create a list for all core-developers who took part in discussion
//for each proposal we get all states and substates, for each major discussion stage, like, accepted, rejected and vote, poll (if a proposal has these states)
// and for each core devoper, we look at his last 5 message and get his sentiment..in not last, we look at first messages ..or if not in these we look at all his messages
//we tally all this and say for each proposal, for all core developers, what was the percentage positive and negative towards the proposal

//sept 2018 ..script hardly used

public class AssumingReasons {
	
	static String statesToGetRolesAndReasons[]  = {"proposal","open","active", "pending", "closed", "final","accepted", "deferred","replaced","rejected","postponed", "incomplete" ,  "superseded", 
		"update", "vote", "poll"};//removed "consensus" 			//<-new states changes by user which i want to capture
	
	static String negationTerms[] = {"if","not","do not","will be","can","can be","is n't","nor","should","should be","would","needs to be","may","will","hav n't","have n't","might", "n't","whether","zero","no","never","never been","need","would need","before","zero"}; // added later for test												// "should","should be"
	static String conditionalList[] = {"if","should","should be","when","can","can be","unless","i hope","whether","once","would","might","why"};
	static String reasonIdentifierTerms[] = {"because","since", "based on","due to","thanks to","accordingly","as a result of","through"};
	// implementing this will help as not all reasons are triples
	static String ActualReasons[] = {"discussion","debate","bdfl pronouncement","poll result","voting result","vote result","consensus","no consensus","feedback","favorable feedback",
			"support","no popular support","favourable feedback","poor syntax","limited utility","difficulty of implementation","bug magnet","controversy","majority","wrangling",
			"superseded"};  //rejected reason
	static List<String>  positiveWords = new ArrayList<String>(); 
	static List<String>  negativeWords = new ArrayList<String>();	
	//maybe we can extend to get these reason terms from database table "leserstates and reasons"
	static Connection conn =null;
	static StateAndReasonLabels l = new StateAndReasonLabels();
	
	public static void main(String[] args) {
		positiveWords = l.getPositiveWords();
		negativeWords = l.getNegativeWords();
		//ProcessingRequiredParameters prp = new ProcessingRequiredParameters();
		ProcessLabels pt = new ProcessLabels();		//create new triple processing reult object	
		connections.MysqlConnect mc = new MysqlConnect();
		conn = mc.connect();
		System.out.format("%5s, %10s, %25s, %10s, %25s, %5s, %5s, %5s, %10s, %10s, %10s, %10s, %10s, %10s, %5s", "PEP", "StartState", "Start Timestamp","End State","End Timestamp","Days","Hours","Mins",
				"Total Msgs","BDFL","Delegate","PEP Author","PEP Ed.","Core Dev","Other");	System.out.println();
		String startState="draft",endState="accepted"; //add rejected and final
		for(int i=308;i<309; i++) { //for each proposal ... 0 - 4000
			getStaticticsBetweenTwoStates(i,startState,endState);
		}	  //end for loop
	}

	private static void getStaticticsBetweenTwoStates(Integer i, String startState, String endState) {
		Statement stmt = null;
		Timestamp start_ts = null, end_ts=null;
		boolean startFound=false, endFound=false,finalFound=false, showMessagesPerDate= true;		
		//community member roles in almessages db table - bdfl, pepeditor, coredeveloper, othermembers 
		try {
			//for each pep, see if there is accepted or rejected as one of the states
			//if so, then outline date difference

			stmt = conn.createStatement();
			// for each state.... results table or postprocessed table										//5											//10
			ResultSet rs = stmt.executeQuery("SELECT pep,  date2,datetimestamp, email from pepstates_danieldata_datetimestamp where pep = "+ i +" order by pep asc, datetimestamp asc");  //date asc
			//we go through each state, once draft or either accepted or rejected is found
			while (rs.next()) {					
				Integer pepNumber = rs.getInt("pep"); 	Date dt = rs.getDate("date2"); 	Timestamp dts = rs.getTimestamp("datetimestamp");	String email = rs.getString("email");
				//LOOK FOR TERM AND IF FOUND, check for reasons
				if (email.toLowerCase().contains(startState)){
					start_ts = dts;	startFound = true;
				}
				if (email.toLowerCase().contains(endState)){
					end_ts = dts;			endFound= true;			
				}					
				//end while
				//we go through each state, once draft or either accepted or rejected has been found
				/* if (startFound && !(endFound)) {  //only for cases where final state exists but without accepted or rejected
						if (finalFound){
								long diff = end_ts.getTime() - start_ts.getTime();
								long diffSeconds = diff / 1000 % 60;		long diffMinutes = diff / (60 * 1000) % 60;		long diffHours = diff / (60 * 60 * 1000) % 24;		
								long diffDays = diff / (24 * 60 * 60 * 1000) ;	float diffYears = diff / (365*24 * 60 * 60 * 1000);							
								String difference = diffDays + " d " + diffHours + " h " + diffMinutes+ " m";

								//select no of messages in between the two timestamps
								int total 	=			computeTotalNumberMesssages(i,start_ts,final_ts);
								int bdfl = 				computeNumberMesssagesByCommunityRole(i,"bdfl",start_ts,end_ts);
								int bdfl_delegate = 	computeNumberMesssagesByCommunityRole(i,"bdfl_delegate",start_ts,end_ts);
								int pepauthor =			computeNumberMesssagesByCommunityRole(i,"pepauthor",start_ts,end_ts);
								int pepeditor = 		computeNumberMesssagesByCommunityRole(i,"pepeditor",start_ts,end_ts);
								int coredeveloper = 	computeNumberMesssagesByCommunityRole(i,"coredeveloper",start_ts,end_ts);
								int othercommunitymember = computeNumberMesssagesByCommunityRole(i,"othercommunitymember",start_ts,end_ts);
								System.out.format("%5s, %10s, %25s, %10s, %25s, %5s, %5s, %5s, %10s, %10s, %10s, %10s, %10s, %10s, %5s", i, "Draft", start_ts,"Final",end_ts, diffDays,diffHours,diffMinutes,
										total,bdfl,bdfl_delegate,pepauthor,pepeditor,coredeveloper,othercommunitymember);
								System.out.println();
						}
					}
				 */
				//we go through each state, once draft or either accepted or rejected has been found
				if(startFound && endFound) {
					// get time difference in seconds				    
					//if(acceptedFound) {
					long diff = end_ts.getTime() - start_ts.getTime();
					long diffSeconds = diff / 1000 % 60;		long diffMinutes = diff / (60 * 1000) % 60;		long diffHours = diff / (60 * 60 * 1000) % 24;		
					long diffDays = diff / (24 * 60 * 60 * 1000) ;			float diffYears = diff / (365*24 * 60 * 60 * 1000);			
					String difference = diffDays + " d " + diffHours + " h " + diffMinutes+ " m";
					int total 	=			computeTotalNumberMesssages(i,start_ts,end_ts);
					int bdfl = 				computeNumberMesssagesByCommunityRole(i,"bdfl",start_ts,end_ts);
					int bdfl_delegate = 	computeNumberMesssagesByCommunityRole(i,"bdfl_delegate",start_ts,end_ts);
					int pepauthor =			computeNumberMesssagesByCommunityRole(i,"pepauthor",start_ts,end_ts);
					int pepeditor = 		computeNumberMesssagesByCommunityRole(i,"pepeditor",start_ts,end_ts);
					int coredeveloper = 	computeNumberMesssagesByCommunityRole(i,"coredeveloper",start_ts,end_ts);
					int othercommunitymember = computeNumberMesssagesByCommunityRole(i,"othercommunitymember",start_ts,end_ts);

					//for each pep, as soon as 'accepted' or 'rejected' is found
					//1. for each distinct coredeveloper, get his last 5 messages before accepted or rejected
					//get all distinct coredeveloper
					//get sendername cluster and then also make sure he is either coredeveloper, i.e. everything other than 'other' member
					//for each, get his last 5 message before accepted of rejected
					//'clusterBySenderFullName'
					Statement stmtCD = conn.createStatement();
					// results table or postprocessed table										//5											//10
					System.out.println("start: " +start_ts + " end_ts: "+end_ts); //System.out.println("start: " +start_ts + " end_ts: "+end_ts);
					Integer allMembersPositiveSentiment=0, allMembersNegativeSentiment=0,allMembersPositiveWord=0,allMembersNegativeWord=0;
					//get each distinct decision member
					ResultSet rsCD = stmtCD.executeQuery("SELECT distinct(clusterBySenderFullName) as decisionMember from allmessages where pep = "+i+" and authorsrole NOT like '%other%' "
							+ " AND datetimestamp BETWEEN '"+ start_ts +"' and '"+end_ts + "'; ");
					//for each core developer, get the last five nmessages before the end state
					while (rsCD.next()) {	
						String decisionMember = rsCD.getString("decisionMember"); 	 //System.out.format("%25s",decisionMember);							 System.out.println();
						//get all messages of this member for that proposal and get the sentiment
						//the last five messages wont work as its not guaranteed 
						Statement stmtS = conn.createStatement();
						// results table or postprocessed table										//5											//10
						
						Integer positiveSentiment=0, negativeSentiment=0, positiveWord=0, negativeWord=0;
						ResultSet rsS = stmtS.executeQuery("SELECT analyseWords from allmessages where pep = "+i+" and clusterBySenderFullName = '"+decisionMember+"'"
								//we start looking from the back to get the last sentiment of each member, if found we quit		
								+ " AND datetimestamp <  '" + end_ts + "' order by datetimestamp desc "); //limit 5
						//we get all the messages before the end state,for each core developer,
						Integer count=0;
						while (rsS.next()) {	
							String analyseWords = rsS.getString("analyseWords"); 	 //System.out.format("%25s",decisionMembers);							 System.out.println();
							//get sentiment, +ve or negative whichever comes first (he may change sentyiment, but we want teh last one)
							if(analyseWords.contains(" +1 ")) { positiveSentiment++;  }
							if(analyseWords.contains(" -1 ")) { negativeSentiment++;  }
							for (String pw: positiveWords) {	if(analyseWords.contains(pw)) { positiveWord++;  }		}
							for (String nw: negativeWords) {    if(analyseWords.contains(nw)) { negativeWord++;  }		}
							//allMembersPositiveSentiment++; break;
							//allMembersNegativeSentiment++; break; 
							count++;
						}
						System.out.println("Member("+count+" msgs): "+ decisionMember +", +ve sentiment: "+positiveSentiment + " -ve sentiment: "+ negativeSentiment 
								 									  +", +ve word: "+positiveWord + " -ve word: "+ negativeWord );
						if(positiveSentiment>0) allMembersPositiveSentiment++;
						if(positiveSentiment>0) allMembersNegativeSentiment++;
						if(positiveWord>0) allMembersPositiveWord++;
						if(negativeWord>0) allMembersNegativeWord++;
					}
					System.out.println("Proposal: "+ i +", all +ve sentiment: "+allMembersPositiveSentiment + " all -ve sentiment: "+ allMembersNegativeSentiment
													   +", all +ve word: "+allMembersPositiveWord + " all -ve word: "+ allMembersNegativeWord);

//					Integer count=0;
//					while (rs.next()) {	
//						count = rs.getInt("cnt");
//					}		
					//2. in each of these messages,starting form the last one before accepted/rejected,  get the sentiment...compute a final sentiment
					//3. tally up how many core developers are positive and negative towards the proposal 

					System.out.format("%5s, %10s, %25s, %10s, %25s, %5s, %5s, %5s, %10s, %10s, %10s, %10s, %10s, %10s, %5s", i, "StartState", start_ts,"EndState",end_ts, diffDays,diffHours,diffMinutes,
							total,bdfl,bdfl_delegate,pepauthor,pepeditor,coredeveloper,othercommunitymember);
					System.out.println();

					//get list of dates between two dates
					if (showMessagesPerDate) {
						Statement stmt6 = conn.createStatement();
						// results table or postprocessed table										//5											//10
						ResultSet rs6 = stmt6.executeQuery("select date2, authorsrole, count(messageid) as cnt from allmessages where pep = " + i 
								+ " AND datetimestamp BETWEEN '"+ start_ts +"' and '"+end_ts + "' "
								+ " group by date2,authorsrole; ");
						//Date dt; 
						String ro; int cnt;
						System.out.format("%25s, %25s, %25s","Date","Role","Count");	System.out.println();
						while (rs6.next()) {	
							dt = rs6.getDate("date2");	ro = rs6.getString("authorsrole"); cnt = rs6.getInt("cnt");
							System.out.format("%25s, %25s, %25s",dt,ro, cnt);
							System.out.println();
						}						
					}
					//}
					/* if(rejectedFound) {
						long diff = rejected_ts.getTime() - draft_ts.getTime();
						long diffSeconds = diff / 1000 % 60;		long diffMinutes = diff / (60 * 1000) % 60;		long diffHours = diff / (60 * 60 * 1000) % 24;		
						long diffDays = diff / (24 * 60 * 60 * 1000) ;	float diffYears = diff / (365*24 * 60 * 60 * 1000);						
						String difference = diffDays + " d " + diffHours + " h " + diffMinutes+ " m";
						int total 	=			computeTotalNumberMesssages(i,draft_ts,final_ts);
						int bdfl = 				computeNumberMesssagesByCommunityRole(i,"bdfl",draft_ts,rejected_ts);
						int bdfl_delegate = 	computeNumberMesssagesByCommunityRole(i,"bdfl_delegate",draft_ts,rejected_ts);
						int pepauthor =			computeNumberMesssagesByCommunityRole(i,"pepauthor",draft_ts,rejected_ts);
						int pepeditor = 		computeNumberMesssagesByCommunityRole(i,"pepeditor",draft_ts,rejected_ts);
						int coredeveloper = 	computeNumberMesssagesByCommunityRole(i,"coredeveloper",draft_ts,rejected_ts);
						int othercommunitymember = computeNumberMesssagesByCommunityRole(i,"othercommunitymember",draft_ts,rejected_ts);
						System.out.format("%5s, %10s, %25s, %10s, %25s, %5s, %5s, %5s, %10s, %10s, %10s, %10s, %10s, %10s, %5s", i, "Draft", draft_ts,"Rejected",rejected_ts, diffDays,diffHours,diffMinutes,
								total,bdfl,bdfl_delegate,pepauthor,pepeditor,coredeveloper,othercommunitymember);
						System.out.println();
					} */
					break;	//if start and end found , we finish
				}
			}
			//firstFound = acceptedFound = rejectedFound = finalFound = false;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Calendar getCalendar(Timestamp draft_ts) {
	    Calendar cal = Calendar.getInstance(Locale.US);
	    cal.setTime(draft_ts);
	    return cal;
	}
	
	public static List<LocalDate> getDatesBetweenUsingJava8(LocalDate startDate, LocalDate endDate) {
	    long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
	    return IntStream.iterate(0, i -> i + 1).limit(numOfDaysBetween).mapToObj(i -> startDate.plusDays(i)).collect(Collectors.toList()); 
    }
	
	public static List<java.util.Date> getDatesBetweenUsingJava7( Timestamp startDate, Timestamp endDate) {
	    List<java.util.Date> datesInRange = new ArrayList<>();
	    Calendar calendar = new GregorianCalendar();
	    calendar.setTime(startDate);
	    Calendar endCalendar = new GregorianCalendar();			    endCalendar.setTime(endDate);

	    while (calendar.before(endCalendar)) {
	        java.util.Date result =  calendar.getTime();
	        datesInRange.add(result);
	        calendar.add(Calendar.DATE, 1);
	     }
	    return datesInRange;
	}
	
	public static Integer computeNumberMesssagesByCommunityRole(Integer proposal, String role, Timestamp begin, Timestamp end) throws SQLException {
		Statement stmt = conn.createStatement();
		// results table or postprocessed table										//5											//10
		ResultSet rs = stmt.executeQuery("SELECT count(messageid) as cnt from allmessages where pep = "+proposal+" and authorsrole like '%"+role+"%' AND datetimestamp BETWEEN '"+ begin +"' and '"+end + "'; ");
			
		Integer count=0;
		while (rs.next()) {	
			 count = rs.getInt("cnt");
		}		
		return count;
	}

	public static Integer computeTotalNumberMesssages(Integer proposal, Timestamp begin, Timestamp end) throws SQLException {
		Statement stmt = conn.createStatement();
		// results table or postprocessed table										//5											//10
		ResultSet rs = stmt.executeQuery("SELECT count(messageid) as cnt from allmessages where pep = "+proposal+" AND datetimestamp BETWEEN '"+ begin +"' and '"+end + "'; ");
			
		Integer count=0;
		while (rs.next()) {	
			 count = rs.getInt("cnt");
		}		
		return count;
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
