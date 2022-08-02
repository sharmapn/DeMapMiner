package miner.postProcess;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import connections.MysqlConnect;
import miner.processLabels.TripleProcessingResult;

//Purpose
//shows all states between two specified states for all peps, or specified peps
//shows reasons and tally for specified states

public class showTally_StatesSubstatesReasons {
	//when we only want data in between 2 states
//		static Boolean onlyBetweenStates = false;
//		static String startState = "draft", endState = "accepted";
	
	public static void main(String[] args) throws SQLException {		
		MysqlConnect mc = new MysqlConnect();
		Connection conn = mc.connect();
		
		ArrayList<TripleProcessingResult> tprList = new ArrayList<TripleProcessingResult>();
		// get all results for all peps.... clean this arraylist after each pep	
		System.out.println("\n------------------\nPopulating postprocessed results from results table ");
		populateTPRList(conn,tprList);		
		
		//Find substates - only want data between two states
		String[] states_to_find_substates = {"draft-accepted","draft-rejected"};	//draft to accepted, or draft to rejected
		//BELOW IS WORKING
 //##   	showSubStates(conn,states_to_find_substates,tprList);
		
    	//this function finds all unique peps and then output all states for a pep
		System.out.println("\n------------------\nShowing all States for all Unique PEPs: ");
    	showAllStates(conn,tprList);
    	
		///find reasons - will only show the reasons captured
		System.out.println("\n------------------\nShowing PEP, reasons for states: ");
		//get list of all unique states
		Set<String> uniqueStates = returnUniqueStates(tprList);
		String[] all_states_to_find_reasons = uniqueStates.toArray(new String[uniqueStates.size()]);
		showReasonTally(conn,all_states_to_find_reasons,tprList);
		
		System.out.println("\n------------------\nShowing PEP, reasons for provided states: ");
		String[] states_to_find_reasons = {"accepted","rejected","withdrawn","vote"};
		//show reasons for states provided
		showReasonTally(conn,states_to_find_reasons,tprList);
		
	}
	
	private static void populateTPRList(Connection conn,ArrayList<TripleProcessingResult> tprList) throws SQLException {
		Integer counter=0;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT pep, author,authorsRole,messageSubject,numberOfPEPsMentionedInMessage, date,folder,file, messageID, label, subject, relation, object, currentSentence, "
											+ "reverb, clausie, ollie, repeatedLabel,role, "
											+ "allReasons, ps,ns,pp,ep, "
											+ "np,reasonsInSentence,reasonTermsInMatchedTriple, reasonTermsInNearbySentencesParagraphs, reasonTriplesInNearbySentencesParagraphs,timestamp, "
											+ "reasonTermsInPreviousSentence,reasonTermsInNextSentence,reasonTriplesInPreviousSentence,reasonTriplesInNextSentence,reasonTriplesInSentence, "
											+ "labelFoundInMsgSubject,isFirstParagraph, isLastParagraph "
											+ "from results_postprocessed "										   
											+ "order by pep asc, date asc, messageID asc");		
		while (rs.next()) {
			counter++;
			TripleProcessingResult tpr = new TripleProcessingResult();
			//set the data here,
			//also gets reasons here so see this part within the function....addToFinalReasonListArray()
			//also 'setReasonInSentence' in cie is called - search fir this function in clausie project
			tpr.setData(rs.getInt(1),rs.getString(2),rs.getString(3),  rs.getString(4),rs.getInt(5),rs.getDate(6),rs.getTimestamp(30),  rs.getString(7),rs.getString(8),rs.getInt(9),rs.getString(10), rs.getString(11), 
					 rs.getString(12),  rs.getString(13), rs.getString(14), 
					rs.getString(15), rs.getString(16), rs.getString(17),rs.getBoolean(18),rs.getString(19),
					rs.getString(20), rs.getString(21), rs.getString(22), rs.getString(23), rs.getString(24), 
					rs.getString(25),rs.getString(26),rs.getString(27),rs.getString(28),rs.getString(29),
					rs.getString(30), rs.getString(31),rs.getString(32),rs.getString(33),rs.getString(34),
					rs.getBoolean("labelFoundInMsgSubject"),rs.getBoolean("isFirstParagraph"),rs.getBoolean("isLastParagraph") );
			tprList.add(tpr);
		}
		System.out.println("Post-processed Results records read in: "+counter);
	}
	
	public static void showSubStates(Connection conn, String[] states_to_find_substates, ArrayList<TripleProcessingResult> tprList){
		// date of output
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		String filename = "C:\\Users\\psharma\\Google Drive\\PhDOtago\\scripts\\showSubStates"+  dateFormat.format(date)  +".csv";    	  	
    	
    	//populate
    	try {
	    	Statement stmt = null;
			stmt = conn.createStatement();
			// the mysql insert statement
			
			Set<Integer> uniquePEP = returnUniquePEPs(tprList);
			
			//Collections.sort(uniquePEP);			
			//go through the list and for each unique pep, show states
			for (String pair: states_to_find_substates){
				String startState = pair.split("-")[0];
	  			String endState = pair.split("-")[1];
	  			
	  			System.out.println();
				System.out.println("PEP,"+startState + ", "+endState);
				
				for (Integer e : uniquePEP)	{
					if (PEPContainsStates(e,tprList, startState, endState)){					
						showInBetweenStatesForPEP(e,tprList, startState, endState);
					}
				}
			}
			
    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void showAllStates(Connection conn, ArrayList<TripleProcessingResult> tprList){		
		// date of output
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		String filename = "C:\\Users\\psharma\\Google Drive\\PhDOtago\\scripts\\showAllStates"+  dateFormat.format(date)  +".csv";    	  	
    	
    	//populate
    	try {
	    	Statement stmt = null;
			stmt = conn.createStatement();
			
			Set<Integer> uniquePEP = returnUniquePEPs(tprList);
			
			//Collections.sort(uniquePEP);			
			//go through the list and for each unique pep, show states
			
			for (Integer e : uniquePEP)
			{	
				System.out.print(e + ", ");
				for (int x=0; x <tprList.size(); x++){
					Integer pep = tprList.get(x).getPepNumber();
					String state = tprList.get(x).getClausIE();
					if(pep.equals(e) && !state.equals("")){ 
							System.out.print(state+ ", ");						
					}
				}
				System.out.println();				
			}		
			
    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	//show reasons for states provided
	public static void showReasonTally(Connection conn, String[] states_to_find_reasons, ArrayList<TripleProcessingResult> tprList){
		// date of output
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		String filename = "C:\\Users\\psharma\\Google Drive\\PhDOtago\\scripts\\showReasonsTally"+  dateFormat.format(date)  +".csv";
    	
    	//ArrayList<TripleProcessingResult> tprList = new ArrayList<TripleProcessingResult>();
		// clean this arraylist after each pep	
    	
    	try {
	    	Statement stmt = null;
			stmt = conn.createStatement();
			// the mysql insert statement		
			//populateTPRList(tprList, stmt);
			 
			Set<Integer> uniquePEPs = returnUniquePEPs(tprList);
			//sort
			List<Integer> sortedList= new ArrayList<Integer>(uniquePEPs);			
			
			//Collections.sort(uniquePEP);			
			//go through the list and for each unique pep, show states
			System.out.println("\nPEP,  State, Reason ");
			for (String state: states_to_find_reasons){				  			
	  			//System.out.println();
				System.out.println("State: ("+state + ")");
				
				for (Integer e : uniquePEPs)
				{
					//if (PEPContainsReason(e,tprList, reason)){					
					//	showReasonTallyinAllPEPs(e,tprList, reason);
					//}
					for (int x=0; x <tprList.size(); x++){	
						Integer pep = tprList.get(x).getPepNumber();					
						if (pep.equals(e)){						
							String finalState = tprList.get(x).getClausIE();							
							if (finalState.equals(state)){
								//stateFound=true;
								String reason = tprList.get(x).getFinalReasonListArrayAsString();
								//not showing empty and null reasons
								if(reason != null &&! reason.equals(""))
									System.out.println(pep + "," + finalState +"," + reason);
								
								//if(state.equals(stateA))
								//	startFound =true;		
								//if(state.equals(stateB))
								//	endFound =true;
							}
						}
					}
				}
			}
			
    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void showInBetweenStatesForPEP(Integer unique_pep, ArrayList<TripleProcessingResult> tprList, String stateA, String stateB) {
		System.out.print(unique_pep + ", ");
		
		boolean startFound = false, endFound= false;		
		
		for (int x=0; x <tprList.size(); x++){
			Integer pep = tprList.get(x).getPepNumber();
			String state = tprList.get(x).getClausIE();
			if(pep.equals(unique_pep) && !state.equals("")){ 
				if(state.equals(stateA))
					startFound =true;
				if(startFound && !endFound)			//while startstate found and endstate not found, keep on outputting
					System.out.print(state+ ", ");
				if(state.equals(stateB))
					endFound =true;	
			}
		}
		System.out.println();
	}
	
	private static boolean PEPContainsStates(Integer unique_pep, ArrayList<TripleProcessingResult> tprList, String stateA, String stateB) {
		boolean startFound = false, endFound= false, pepFound=false;		
		
		for (int x=0; x <tprList.size(); x++){	
			Integer pep = tprList.get(x).getPepNumber();
			if (pep.equals(unique_pep)){
				pepFound=true;
				String state = tprList.get(x).getClausIE();			
				if(state.equals(stateA))
					startFound =true;		
				if(state.equals(stateB))
					endFound =true;
			}			
		}
		if (pepFound){
			if (startFound && endFound)
				return true;
		}
		else 
			return false;
		
		return false;
	}
	
//	public static void showSubStatesOLD(Connection conn){
//		// date of output
//		Date date = new Date();
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
//		String filename = "C:\\Users\\psharma\\Google Drive\\PhDOtago\\scripts\\showSubStates"+  dateFormat.format(date)  +".csv";
//    	
//    	ArrayList<TripleProcessingResult> tprList = new ArrayList<TripleProcessingResult>();
//		// clean this arraylist after each pep	
//    	
//    	//populate
//    	try {
//	    	Statement stmt = null;
//			stmt = conn.createStatement();
//			// uncomment this -- populateTPRList(tprList, stmt);
//			
//			PostProcessOptions oerr = new PostProcessOptions();
//	    	
//	    	//only want data between two states
//			String[] startState_endStatePairs = {"BDFL_Pronouncement-bdfl_accepted_pep","active-bdfl_accepted_pep"};
//	  		//String[] endState = {"accepted"};
//			
//	  		for (String pair: startState_endStatePairs){	  			
//	  			String startState = pair.split("-")[0];
//	  			String endState = pair.split("-")[1];
//	  			System.out.println("pair (" + startState + ","+endState+")");
//	  			tprList = oerr.returnDataOnlyBetweenTwoStates(tprList, startState, endState);
//	  			
//	  			//for each record in tprlist, outpt state
//	  			if(!tprList.isEmpty()){
//		  			int counter =0;
//		  			for (int x=0; x <tprList.size(); x++){		  				
//			  				if(tprList.get(x).getClausIE() != null) {
//			  					String cie = tprList.get(x).getClausIE();
//			  					if (x==0)
//			  						System.out.print("\t"+cie+ ",");
//			  					else
//			  						System.out.print(cie+ ",");
//			  				}
//		  			}
//		  			System.out.println();
//	  			}
//	  		}
//			
//    	} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//		catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}

	private static Set<String> returnUniqueStates(ArrayList<TripleProcessingResult> tprList) {
		List<String> StateList = new ArrayList<String>(); // create list with duplicates...
		if(!tprList.isEmpty()){				
			for (int x=0; x <tprList.size(); x++){
				//String foundState = tprList.get(x).getClausIE();
				String pep = tprList.get(x).getClausIE();
				//System.out.println("PEP: " +pep);
				if(pep != null){
					StateList.add(pep);
				}	  				
			}
		}
		//get unique PEPs
		Set<String> uniqueStates = new HashSet<String>(StateList);
		System.out.println("Unique States count: " + uniqueStates.size());
		for (String s : uniqueStates) {
		    System.out.print(s + ", ");
		}
		return uniqueStates;
	}

	private static Set<Integer> returnUniquePEPs(ArrayList<TripleProcessingResult> tprList) {
		List<Integer> PEPList = new ArrayList<Integer>(); // create list with duplicates...
		if(!tprList.isEmpty()){				
			for (int x=0; x <tprList.size(); x++){
				//String foundState = tprList.get(x).getClausIE();
				Integer pep = tprList.get(x).getPepNumber();
				//System.out.println("PEP: " +pep);
				if(pep != null){
					PEPList.add(pep);
				}	  				
			}
		}
		//get unique PEPs
		Set<Integer> uniquePEP = new HashSet<Integer>(PEPList);
		System.out.println("Unique PEP count: " + uniquePEP.size());
		return uniquePEP;
	}
	public void finalize() {	    //System.out.println("Probability instance is getting destroyed"); 
	}
}
