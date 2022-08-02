package miner.process;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import miner.models.Pep;
import readRepository.readMetadataFromWeb.GetProposalDetailsWebPage;
import connections.PropertiesFile;

public class Process_MessageSelection {

	static ArrayList<Integer> allPepPerTypes;
	static String proposalIdentifier;  // = "jep";  //should be read in from file ..line 46

	// 389 and 450 added later											//479, has one message which gives error  //{279}; //9,294,308}; //0,8,283,433};
	Integer[] selectedListItems = {279,289,308,313,318,336,340,345,376,388,389,391,414,428,435,441,450,484,488,498,485,471,505,3003,3103,3144}; //, };
	//THIS LIST HAS BEEN UPDATED, 465 HAS BEEN REMOVED, NEW ONESD ADDED AS DATE FOR THEM IS EXPLICIT (STATES AND REASONS). 
	//PEP 3131 was discussed in 3000 mailing list and also reason for acceptance is not clear and thus been removed

	//jan 2019
	public boolean inList(Integer s) {
		for (Integer h: selectedListItems) {	//we alredy processed this set, so we skip
			if(s.equals(h))
				return true;
		}
		return false;
	}
	
	// process all peps of a type
	public void selectMessages(ProcessingRequiredParameters prp) {
		PropertiesFile wpf = new PropertiesFile();	
		Connection conn = prp.getConn();		Instant start = Instant.now();
		ArrayList<LabelTriples> labels = prp.getLabels();

		
		//dec 2019 this is better read from prop file
		proposalIdentifier = prp.getProposalIdentifier();
		
		// for selected peps in list -- mostly i use this section
		/* dec 2018, we move this code in next section to cater for restart and stuck processes
		if (prp.isSelectedList()) {
			System.out.println("Selected List---------------------------------------------------------");			
			for (int j = 0; j < selectedListItems.length; j++) { // -2 because last 2 values are too  big
				Integer i = selectedListItems[j]; // For the all unique peps
				if(i> -1 && i <=1000){	//chunking data	
					try {
						// System.out.println("Pep " + i + "---------------------------------------------");
						ArrayList<LabelTriples> updated_labels = (ArrayList<LabelTriples>)labels.clone();	// to hold all labels above and the pep title specific labels for each pep - one by one
						//new addition july 2017, add the pep title to triple list - just wherever there are terms pep or proposal mentioned in triple S,V,O
						System.out.println(" here ");
						updated_labels = AppendTriplesWithCurrentPEPTitle(updated_labels,i,prp.getPd(),conn, false);						
						if (updated_labels.isEmpty()){
							System.out.println("no pep title for pep "+i);
							//try go onto next element in loop here
						}						
						//assign 
						prp.setLabels(updated_labels);	prp.setPEPNumber(i);	prp.setConnections(prp.getMc(),conn);	//set additional parameters for passing to this function		
						prp.getGpm().getAllPEPMessages(prp);						
					} catch (Exception e3) {
						System.out.println(e3.toString());
					}
				}
			}			
		}
		*/
		
		
		// for all peps
		if (prp.isSelectedAll() || prp.isSelectedList()) {
			//we want to do processing for all peps, but 25 at at time
			//chunking data ..50 at a time, remember the last one and continue from there, as its included
			
			ArrayList<Integer> UniquePeps = prp.getPu().returnUniqueProposalsInDatabase(prp.getProposalIdentifier());
			
			//Read variable values from properties file
			// wpf.WriteToPropertiesFile("includeEmptyRows", includeEmptyRows.toString());
			//$$$....IMPORTANT..SET THESE 
			//includeStateData
			//Integer pepNumberForRestart = Integer.parseInt(wpf.readFromPropertiesFile("pepNumberForRestart",false));
			//Integer messageIDForRestart = Integer.parseInt(wpf.readFromPropertiesFile("messageIDForRestart",false));
			
			//FOR all different purposes, these functions get the max pep and max message id
			System.out.println("here c1");
			Integer pepNumberForRestart =	prp.getPd().getMaxProcessedProposalFromStorageTable(conn, prp); //select max pep from table
			System.out.println("pepNumberForRestart="+pepNumberForRestart);
			Integer messageIDForRestart =	prp.getPd().getMaxProcessedMessageIDForProposalFromStorageTable(conn, pepNumberForRestart,prp);  //select max messageid from tabkle for that particular pep
			System.out.println("messageIDForRestart="+messageIDForRestart);
			prp.setPepNumberForRestart(pepNumberForRestart);
			prp.setMessageIDForRestart(messageIDForRestart);	//if there was an error, we dont want to attempt to proces sit again, thats why we add 1
			if (prp.isSelectedAll())	System.out.println("Chosen Selected All---------------------------Unique Peps count = "+ UniquePeps.size() );
			if (prp.isSelectedList())	System.out.println("Chosen Selected List---------------------------");
			System.out.println(" Restart: ProposalNumberForRestart: " +pepNumberForRestart + ", messageIDForRestart+1: " +messageIDForRestart);
			
			for (int j = 0; j < UniquePeps.size(); j++) { // -2 because last 2 values are too big
				// for (int j = 308; j < 309; j++) {
				Integer i = UniquePeps.get(j); // For the all unique peps
				//BELOW CODE JUST TO DECIDE IF WE WANT TO PROCESS THE PEP
				//IF RESTART REQUIRED
				//431 gives error so skipping
				
				//jan 2019, if we already processed some rows
//				if(i.equals(572) || i.equals(308)) {}
//				else {
//				if (inList(i) || i<300)
//					continue;	//skip these peps as we have already processed these set
//				}
				if(i > 0) { //we dont consider peps less than 6
					//proceed = false; //System.out.println("prp.getRestart() "+prp.getRestart());					
					boolean proceed = true;	//CHECK WHAT WE HAVE PROCESSED ALREADY, WE DONT WANT TO PROCESS AGAIN
					if(prp.getRestart()){		//System.out.println("pepNumberForRestart "+pepNumberForRestart);
						if(i >= pepNumberForRestart) 
							proceed =true;
						else 	//if no restart, we just process all
							proceed=false; //System.out.println("else ");
					}
					if(proceed && prp.isSelectedList()) {		//for selected lists only			
						if(contains(selectedListItems,i)){	//check
				            proceed = true; System.out.println("PEP: "+i+" Found in SelectedList ");
				        }else proceed= false;
					}
					//march 2020 ...we restart after each proposal
					//only one of these two variables should be used 
					if(prp.getRestartAfterEachProposal()){	
						if(i > pepNumberForRestart)  //only bigger than max proposal in results 
							proceed =true;
						else 	//if no restart, we just process all
							proceed=false; 
					}
					
					//if(prp.isSelectedAll()) {	}
					if(proceed) {
						try {
							ArrayList<LabelTriples> updated_labels = (ArrayList<LabelTriples>)labels.clone();	// to hold all labels above and the pep title specific labels for each pep - one by one
							//new addition july 2017, add the pep title to triple list - just wherever there are terms pep or proposal mentioned in triple S,V,O
							System.out.println("here a prp.getProposalTableName(): "+prp.getProposalTableName());
							updated_labels = AppendTriplesWithCurrentProposalTitle(updated_labels,i,proposalIdentifier,prp.getPd(), prp.getProposalTableName(), conn, false);					
							System.out.println("here a prp.getProposalTableName(): "+prp.getProposalTableName());
							if (updated_labels.isEmpty()){
								System.out.println("no pep title for pep "+i);
								//try go onto next element in loop here
							}	
							System.out.println("here B");
							//assign 
							prp.setLabels(updated_labels);
							prp.setPEPNumber(i);
							prp.getGpm().getAllPEPMessages(prp);
							// pm.outputVotes(i); not required vote starts with "call for votes" and end with " vote results"					
						} catch (Exception e3) {
							System.out.println(e3.toString());
						}
					}
				}
				//if relation extraction we exit after each pep is finished processing as memory is full, teh batch process should run the jar file again and it would carry on from there
				//this wont work as the way retsar feature is setup, it will always go in loop
			}
		}

		if (prp.isSelectedType()) {
			System.out.println("Selected Type---------------------------------------------------------");
			// populate pep
			if (prp.isAllStandard()) {			System.out.println("---------Standard");			allPepPerTypes = prp.getPd().getallPepsForPepType(1, false, conn);		} 
			else if (prp.isAllProcess()) {			allPepPerTypes = prp.getPd().getallPepsForPepType(2, false, conn);		} 
			else if (prp.isAllInformational()) {		allPepPerTypes = prp.getPd().getallPepsForPepType(3, false,conn);		} 
			else {   allPepPerTypes = null;		}
			for (int j: allPepPerTypes) { // -2 because last 2 values are too big
				Integer i = allPepPerTypes.get(j); // For the all unique peps
				if (i!=435)		{
					try {  
						//p[i] = new Pep();						p[i].setPepNumber(i);	
						prp.setPEPNumber(i);
						prp.getGpm().getAllPEPMessages(prp);
						//p[i].destruct();
					} catch (Exception e3) {
						System.out.println(e3.toString());
					}
				}
			}
		}

		// process just a dummy file
		if (prp.isReadDummyFile()) {
			System.out.println("Read Dummy---------------------------------------------------------");
			Integer i = prp.getProposal();
			try {
				//p[i] = new Pep();
				//p[i].setPepNumber(i);
				prp.setPEPNumber(i);
				FileInputStream fis = new FileInputStream(prp.getDummyFileLoc());
				prp.getPdf().processDummyFile(prp, prp.isReadEntireFileAsMessage(), prp.getCandidateSentencesList(), prp.getDummyFileLoc(), fis);							
				//p[i].destruct();
			} catch (Exception e1) {
				// continue;
				// System.out.println(" hello");
				System.out.println(e1.toString());
			}
		}

		// System.out.println("End of processing ");
		// START PROCESSING
		if (prp.isRerunResults()) {
			System.out.println("Rerun Results---------------------------------------------------------");			
			try	{
				String query= "SELECT pep, messageID FROM results order by pep,date asc";
				// create the java statement
				Statement st = conn.createStatement();					ResultSet rs = st.executeQuery(query);
				Integer counter=0;

				List<Integer[]> List = new LinkedList<Integer[]>();
				//System.out.println("PEP Type " + pepTypeString);  
				while (rs.next())	{	
					Integer pep1 = rs.getInt(1), mid1 = rs.getInt(2);

					Integer[] currentRow = new Integer[] {pep1,mid1};
					List.add(currentRow);
				}
				//st.close();
				//iterate on list
				for(Integer[] k: List){
					Integer pep2 = k[0], mid2 = k[1];
					counter++;			
					//ArrayList<String> al2 = (ArrayList<String>)al.clone();
					ArrayList<LabelTriples> updated_labels = (ArrayList<LabelTriples>)labels.clone();	// to hold all labels above and the pep title specific labels for each pep - one by one					
					//new addition july 2017, add the pep title to triple list - just wherever there are terms pep or proposal mentioned in triple S,V,O
					updated_labels = AppendTriplesWithCurrentProposalTitle(updated_labels,pep2,proposalIdentifier,prp.getPd(),prp.getProposalTableName(), conn, false);
								   //AppendTriplesWithCurrentProposalTitle(updated_labels,i,proposalIdentifier,prp.getPd(),conn, false);
					//assign 
					prp.setLabels(updated_labels);						prp.setPEPNumber(pep2);						prp.setConnections(prp.getMc(),conn);	//set additional parameters for passing to this function
					//main here temp commented
					//BELOW LINE TREMP COMMNETED HAVE TO INTEGERATE WITH TEH PROCES ALL SCRIPT
					//prp.getGm().getMessage(mid2,prp);
				}
				System.out.println("\n\nTotal number of Records in Rerun " + counter);
				//st.close();
				//delate 
			}	catch (Exception ex)	{
				System.err.println("Got an exception 34! ");
				System.err.println(ex.getMessage());
			}			
		}	

		if(prp.isProcessMessageID()){
			System.out.println("Procesing Specific MID : "+prp.getMessageID()+" --------------------------------");		
			Integer i = 308;	prp.setProposal(i); System.out.println("Proposal: "+i);	//just a dummy proposal value
			try {
				Integer pepNumberForRestart = 0;
				Integer messageIDForRestart = 0;
				prp.setPepNumberForRestart(pepNumberForRestart);
				prp.setMessageIDForRestart(messageIDForRestart);	
				//ArrayList<String> al2 = (ArrayList<String>)al.clone();
				ArrayList<LabelTriples> updated_labels = (ArrayList<LabelTriples>)labels.clone();	// to hold all labels above and the pep title specific labels for each pep - one by one					
				//new addition july 2017, add the pep title to triple list - just wherever there are terms pep or proposal mentioned in triple S,V,O
				//updated_labels = AppendTriplesWithCurrentPEPTitle(updated_labels,i,prp.getPd(),conn, false);
				updated_labels = AppendTriplesWithCurrentProposalTitle(updated_labels,i,proposalIdentifier,prp.getPd(),prp.getProposalTableName(), conn, false);
				//assign 
				prp.setLabels(updated_labels);
				prp.setPEPNumber(i);
				//main here temp commented
				//prp.getGm().getMessage(prp.getMessageID(),prp);	//define set and get for this later
				prp.getGpm().getAllPEPMessages(prp);
			} catch (Exception e1) {
				System.out.println(e1.toString());
			}
		}

		if (prp.isSelectedSelected()) {
			System.out.println("Selected Selected---------------------------------------------------------");
			Integer i =  prp.getProposal();
			try {
				//p[i] = new Pep();				p[i].setPepNumber(i);				prp.setPEPNumber(i);		
				prp.getGpm().getAllPEPMessages(prp);
				// "call for votes" and end with " vote results"
				//p[i].destruct();
				System.out.println("END OF PROCESSING");
			} catch (Exception e1) {
				System.out.println(e1.toString());
			}
		}
		// selected pep
		if (prp.isSelectedMinMax()) {
			System.out.println("Selected Min Max");

			//we want to do processing for all peps, but 25 at at time
			ArrayList<Integer> UniquePeps = prp.getPu().returnUniqueProposalsInDatabase(proposalIdentifier);
			//ArrayList<Integer> UniquePeps = null;

			//get last inserted pep number in results table			
			Integer lastPEPprocessed = prp.getPu().returnLastPEPInResultsTable();
			Integer chunkSize=25;
			/*
			 * Integer[] nextLot = returnNextLot(UniquePeps,lastPEPprocessed,chunkSize); //return the next lot to do

			//Integer toStart=null;
			if(last==null){
				toStart=0;
			}

			//get pep number to start

			//for peps from min to max
			for (int j=0; j<nextLot.length; j++){ //FOR min and max peps
				try {
					Integer i = nextLot[j];
					// System.out.println("Pep " + i + "---------------------------------------------");
					//new addition july 2017, add the pep title to triple list - just wherever there are terms pep or proposal mentioned in triple S,V,O
					updated_labels = AppendTriplesWithCurrentPEPTitle(updated_labels,i,pd,conn, false);
					if (updated_labels==null){
						System.out.println("no pep title for pep "+i);
						//try go onto next element in loop here
					}
					//assign 
					prp.setLabels(updated_labels);
					System.out.println("Pep all");
					p[i] = new Pep();
					p[i].setPepNumber(i);
					prp.setPEPNumber(i);
					prp.setConnections(mc,conn);	//set additional parameters for passing to this function
					gpm.getAllPEPMessages(prp);

					p[i].destruct();
				} catch (Exception e2) {
					System.out.println(e2.toString());
				}
				updated_labels = null;
			}
			 */
		}

		prp.DBdisconnect();
		//prp.getSCNLP().closeSocket();	//disconnect from socket used for Stanford NLP. Coreference, NER and Double/Triple matching

		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		System.out.println("Processing Finished, Time taken: " + (timeElapsed.toMillis() / 1000) / 60 + " minutes");
	}

	//this function adds pep titles to the labels list, as pep titles also match
	//go through the existing set of labels list - already populated and check for pep or proposal
	//For cases like "conditional expressions" are accepted instead of "pep 308 is accepted"
	public static ArrayList<LabelTriples> AppendTriplesWithCurrentProposalTitle(ArrayList<LabelTriples> labels, Integer proposal,String proposalIdentifier, GetProposalDetailsWebPage gpd, 
			String proposalTableName, Connection conn, boolean output){
		//return the pep title for the pep		
		String pepTitle = gpd.getProposalTitleForProposal(proposal,proposalIdentifier, proposalTableName, conn); 
		System.out.println("updating labels list (labels size = "+labels.size()+") with current proposal title, for proposal " + proposal + " " + pepTitle + " proposalIdentifier: "+ proposalIdentifier + " proposalTableName "+proposalTableName);

		if(pepTitle == null || pepTitle.isEmpty()){
			System.out.println("No Proposal Title for proposal " + proposal + " " + pepTitle);	
			return null;			
		}
		else{
			pepTitle = pepTitle.trim().toLowerCase();
			if (pepTitle.contains(" "))
				pepTitle = pepTitle.replace(" ", "_");	//for cie matching, will be separated later in triple matching anyway
		}

		//ArrayList<Label> newlabelsList = new ArrayList<Label>();
		//newlabelsList = labels;
		Boolean addNew = false;
		String idea ="", subject="",verb = "", object="",newIdea ="", newSubject="",newVerb = "", newObject="";		
		System.out.println();

		if(labels.isEmpty()){
			System.out.println("labels empty "+labels.size());	
			return null;			
		}
		try {
			int counter =0;
			Integer labelSize = labels.size(), lineNumber;
			for (int x=0; x < labelSize; x++){
				addNew= false;
				idea =""; subject="";verb = ""; object=""; 	newIdea =""; newSubject="";newVerb = ""; newObject="";
				if(labels.get(x).getIdea() != null && !labels.get(x).getIdea().isEmpty())
				{
					lineNumber = labels.get(x).getLineNumber();
					idea 	= labels.get(x).getIdea();  					subject = labels.get(x).getSubject();  					verb 	= labels.get(x).getVerb();		//no verb egual to pep
					object 	= labels.get(x).getObject();
					//		  					System.out.println("here 3333");
					//if (object.isEmpty() || object == null)
					//	object="";
	
					//default values
					newSubject = subject;  					newObject = object;
					//there will always be a value for the subject therefore dont ahev to check for nulls or empty string
					if (subject.equals("pep") || subject.equals("proposal")){
						newSubject = pepTitle;
						//addNew= true;
						LabelTriples l = new LabelTriples();
						l.setLabel(lineNumber,idea, newSubject, verb, newObject,"");  //keep sentence empty	  						
						labels.add(l);	  						
						if(output)
							System.out.println("Appended proposal "+proposal+" with new label idea ("+ idea + ") newsubject (" + newSubject + ") verb (" + verb + ") newObject ("+newObject +")");
						//to match peptile has 's' and message woudl not have that 's'
						//eg 'conditional expression' to 'conditional expressions' in subject
						if (newSubject.endsWith("s")){
							newSubject = newSubject.substring(0, newSubject.length() - 1);	//remove 's' from title for matching
							LabelTriples m = new LabelTriples();
							m.setLabel(lineNumber,idea, newSubject, verb, newObject,"");
							labels.add(m);
							if(output)
								System.out.println("Appended proposal "+proposal+" with new label idea ("+ idea + ") newsubject (" + newSubject + ") verb (" + verb + ") newObject ("+newObject +")");
						}
					}
					//		  					else{
					//		  						newSubject = subject;
					//		  					}
					//sometimes object = null or empty
					if(object != null && !object.isEmpty())
					{
						if (object.equals("pep") || object.equals("proposal")){
							newObject = pepTitle;
							//addNew= true;
	
							//			  					else{
							//			  						newObject = object;
							//			  					}
							LabelTriples l = new LabelTriples();
							l.setLabel(lineNumber,idea, newSubject, verb, newObject,"");
							labels.add(l);	
							if(output)
								System.out.println("Appended proposal "+proposal+" with new label idea ("+ idea + ") newsubject (" + newSubject + ") verb (" + verb + ") newObject ("+newObject +")");
							//to match peptile has 's' and message woudl not have that 's'
							//eg 'conditional expression' to 'conditional expressions' in subject
							if (newObject.endsWith("s")){
								newObject = newObject.substring(0, newObject.length() - 1);	//remove 's' from title for matching
								LabelTriples n = new LabelTriples();
								n.setLabel(lineNumber,idea, newSubject, verb, newObject,"");
								labels.add(n);
								if(output)
									System.out.println("Appended proposal "+proposal+" with new label idea ("+ idea + ") newsubject (" + newSubject + ") verb (" + verb + ") newObject ("+newObject +")");
							}
						}
					}
					//		  					System.out.println("here 99999");
					/*
		  					if(addNew){
		  						Label l = new Label();
		  						l.setLabel(idea, newSubject, verb, newObject);
		  						newlabelsList.add(l);	  						
		  						System.out.println("Appended pep "+pep+" with new label idea ("+ idea + ") newsubject (" + newSubject + ") verb (" + verb + ") newObject ("+newObject +")");
		  						//to match peptile has 's' and message woudl not have that 's'
		  						//eg 'conditional expression' to 'conditional expressions' in subject
		  						if (newSubject.endsWith("s")){
		  							newSubject = newSubject.substring(0, newSubject.length() - 1);	//remove 's' from title for matching
		  							Label m = new Label();
			  						m.setLabel(idea, newSubject, verb, newObject);
			  						newlabelsList.add(m);
			  						System.out.println("Appended pep "+pep+" with new label idea ("+ idea + ") newsubject (" + newSubject + ") verb (" + verb + ") newObject ("+newObject +")");
		  						}
		  						if (newObject.endsWith("s")){
		  							newObject = newObject.substring(0, newObject.length() - 1);	//remove 's' from title for matching
		  							Label n = new Label();
			  						n.setLabel(idea, newSubject, verb, newObject);
			  						newlabelsList.add(n);
			  						System.out.println("Appended pep "+pep+" with new label idea ("+ idea + ") newsubject (" + newSubject + ") verb (" + verb + ") newObject ("+newObject +")");
		  						}
	
		  					}
					 */
				}
			}	
		}
		catch(Exception ex) {
			System.out.println(" Error in Appending Labels 323 " + ex.toString() + "\n" ); //System.out.println(Thread.currentThread().getStackTrace()  );		//+  " \n" + e.printStackTrace()
			System.out.println(StackTraceToString(ex)  ); 
		}
		return labels;		
	}
	
	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}
	
	//dec 2018..check in pep array
	public static boolean contains(final Integer[] array, final Integer v) {
        boolean result = false;
        for(Integer i : array){
            if(i.equals(v)){
                result = true;
                break;
            }
        }
        return result;
    }

	//for some states, we want to create labels with triples on the fly
	//we create all the combinations using the states and reasons
	//we use the states like    state_reason as label
	//						    state as subject
	//						   reason as verb	

	//eg accepted_because accepted because 
	public static String[] addStateReasonsToTriples(String[] tripleLabels, String[] statesToGetRolesAndReasons, String[] reasonIdentifierTerms)
	{		
		List<String> temps = new ArrayList<String>();
		for (String s: statesToGetRolesAndReasons){
			for(String r: reasonIdentifierTerms){				
				//add underscore
				if (r.contains(" ")){
					r = r.replace(" ", "_");
				}				
				temps.add(s + "_" + r + " " + s + " " + r);
			}
		}	
		String[] tempsArray = temps.toArray(new String[0]);		
		return combine(tripleLabels,tempsArray);
	}

	public static String[] combine(String[] arg1, String[] arg2) {
		String[] result = new String[arg1.length + arg2.length];
		System.arraycopy(arg1, 0, result, 0, arg1.length);
		System.arraycopy(arg2, 0, result, arg1.length, arg2.length);
		return result;
	}

	public static String seperateTerms(String terms) {
		return terms.replaceAll("-", " ");
	}
}
