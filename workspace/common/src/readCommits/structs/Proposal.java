package readCommits.structs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import readCommits.Input.OutputWritter;

public class Proposal {
	
	String name;
	String finalStatus;
	String type;
	String proposalTitle;
	String entireMessage;
	List<State> states;
	
	public Proposal(String name){
		this.name = name;
		states = new ArrayList<State>();
	}
	
	public Proposal(String name, State state){
		this.name = name;
		states = new ArrayList<State>();
		states.add(state);
	}
	
	public void addState(State add){
		states.add(add);
	}
	
	public void finalize(){
		finalStatus = states.get(states.size() - 1).getType();
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder(name + ", ");
		
		//reverse ordering to match xl
		Collections.reverse(states);
		
		for(State s : states){
			sb.append(s);
		}
		
		sb.append("\n");
		return sb.toString();
	}
	
	public String toString2() throws Exception{
		StringBuilder sb = new StringBuilder();
		
		//reverse ordering to match xl
		Collections.reverse(states);
		
		name = name.replace(".txt","");
		name = name.replace("/proposal-","");
		name = name.replaceFirst("^0+(?!$)", "");
		if(name.contains("/proposaldraft-"))
		{
			name = name.replace("/proposaldraft-","");
		}	
		if(name.contains(".orig"))
		{
			name = name.replace(".orig","");
		}
		
		if(name.contains("/constants.py"))
		{
			name = name.replace("/constants.py","999999");
		}
		
		
		
		for(State s : states){
			//
			if(s.author.contains("'"))
			{
				s.author = s.author.replace("'"," ");
			}
			
			//format date
//			java.util.Date date = new Date(s.date);
//			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//			String format = formatter.format(date);
//			System.out.println(format);
//			//System.out.println(format);
//			
//			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			
//			try {
//
//				Date date2 = formatter.parse(format);
//				//System.out.println(date);
//				System.out.println(formatter.format(date));
//
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
			//DateFormat formatter = null;
	        //Date convertedDate = null;
//
//	
			//formatter =new SimpleDateFormat("yyyy-MMM-dd");
	        //convertedDate =(Date) formatter.parse(s.date);
			
//			String[] t = DateParser.getDate(s.date);
//			String year = t[0];
//			String month = t[1];
//			String day = t[2];
			
			//String str = year+"-"+month+"-"+day;
			try {
				System.out.println(" orig date " +  s.date);
				
				DateFormat formatter = new SimpleDateFormat("dd MMM yyyy  HH:mm:ss");
				Date myDate = formatter.parse(s.date);
				java.sql.Date sqlDate = new java.sql.Date(myDate.getTime());
				
				  //convert ate to java.sql date
		        java.util.Calendar cal = Calendar.getInstance();
		        cal.setTime(sqlDate);
			    cal.set(Calendar.HOUR_OF_DAY, 0);
			    cal.set(Calendar.MINUTE, 0);
			    cal.set(Calendar.SECOND, 0);
			    cal.set(Calendar.MILLISECOND, 0);
			    java.sql.Date sqlDate2 = new java.sql.Date(cal.getTime().getTime()); // your sql date
			    
			    String emailMessage = "Status : "+ s.type.toUpperCase();
				
				insertIntoDB db = new insertIntoDB();	
				//String proposal, Date date, Date v_dateTimeStamp, String author, String status, String proposalTitle, String entireMessage)
				//not sure this code is reached at all
//###			db.insert(name, sqlDate2,s.dateTimeStamp, s.author, s.type.toUpperCase(), s.proposalTitle, s.entireMessage); 	//maybe proposaltitle changes during states and thats what we also want to capture
//##				System.out.println(name + ", " + sqlDate2 + ", " + s.dateTimeStamp + ", " + s.author + ", " + s.type.toUpperCase());
		    }
		    catch (ParseException e) {
		        e.printStackTrace();
		    }
//			System.out.println(s.date);
	        
    		//Date dateNew = findDate(s.date);
    		//System.out.println(dateNew);
									//date
//			sb.append(name + "," + convertedDate + "," + s.author + "," + s.type.toUpperCase() + "\n");
//			System.out.println(name + "," + formattedDate + "," + s.author + "," + s.type.toUpperCase() + "\n");
			//sb.append("\n");
			
			//insert into database
			
			 
			
		}
		
		
		return sb.toString();
	}
	
//	public Date convertStringToDate(String dateString)
//	{
//	    Date date = null;
//	    Date formatteddate = null;
//	    DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
//	    try{
//	        date = df.parse(dateString);
//	        formatteddate = df.format(date);
//	    }
//	    catch ( Exception ex ){
//	        System.out.println(ex);
//	    }
//	    return formatteddate;
//	}
	
	
	
	public void toFile(){
		OutputWritter ow = new OutputWritter();
		
		String line= "";
		
		for(State s : states){
			line = name + "," + s.dateTimeStamp + "," + s.author + "," + s.type;
		    ow.write(line, "c:\\scripts\\outout.txt");
		}
		
	}
}
