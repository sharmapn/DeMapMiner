package readCommits.structs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class FileChange {
	
	String name, content,author, date,proposalTitle, entireMessage;
	//TODO move author and date to commit only
	//added
	Date dateTimeStamp;
	boolean stateChange;
	
	public FileChange(String name, String author, String date2,Date v_dateTimeStamp, String content, String proposalTitle, String entireMessage) {

		this.name = name;		this.content = content;
		this.date = date2;		this.dateTimeStamp = v_dateTimeStamp;
		this.author = findAuthor(author);		this.proposalTitle = proposalTitle;		this.entireMessage = entireMessage;
		if(content.contains("\n+Status:"))
			stateChange = true;
		else 
			stateChange = false;
	}
	
	public Date getDateTimeStamp() {
		return dateTimeStamp;
	}

	public void setDateTimeStamp(Date dateTimeStamp) {
		this.dateTimeStamp = dateTimeStamp;
	}

	public boolean isStateChange() {
		return stateChange;
	}

	public void setStateChange(boolean stateChange) {
		this.stateChange = stateChange;
	}

	public String getProposalTitle() {
		return proposalTitle;
	}

	public void setProposalTitle(String proposalTitle) {
		this.proposalTitle = proposalTitle;
	}

	public String getEntireMessage() {
		return entireMessage;
	}

	public void setGetEntireMessage(String entireMessage) {
		this.entireMessage = entireMessage;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setDate(String date) {
		this.date = date;
	}

	private String findAuthor(String author){
		
		String auth = author;
		
		if(content.contains("\n+Author: ")){
			
			//get author line
			author = content.substring(content.indexOf("\n+Author: ") + 10, content.indexOf("\n", content.indexOf("\n+Author: ") + 3)); //+3 gets past inital /n
			author = author.trim();
			author = author.replaceAll("<.*>", "");	//remove <> content
				
			//split sentence into words
			List<String> authWord = new ArrayList<String>(Arrays.asList(author.split(" ")));
	
			//remove emails
			for (Iterator<String> iter = authWord.listIterator(); iter.hasNext(); ) {
			    String word = iter.next();
			    if (word.contains("@")) {
			        iter.remove();
			    }
			}
			
			
			if(authWord.size() == 1){
				auth = authWord.get(0);
			}
			else{
				//start 1 to skip first name
				StringBuilder sb = new StringBuilder();
				for(int c = 1; c < authWord.size(); c++)
					sb.append(authWord.get(c) + " ");
				auth = sb.toString();
			}
				
			//remove invalid tokens
			auth = auth.replace("<", "");			auth = auth.replace(">", "");
			auth = auth.replace("(", "");			auth = auth.replace(")", "");
			auth = auth.replace(",", "");
			
			/*for(String s)
			//get author last name from line
			this.author = author.substring(author.lastIndexOf(" "), author.length() - 1);*/
		}
		else 
			auth = author;
		return auth.trim();
	}
	
	public boolean getStateFlag(){
		return stateChange;
	}
	
	public String getName() {
		return name;
	}
	
	public String getContent() {
		return content;
	}
	
	public String getAuthor() {
		return author;
	}

	public Date getDateStamp() {
		return dateTimeStamp;
	}
	
	public String getDate() {
		return date;
	}

	public String toString(){
		return "------------------------------\n"
				+ "Name: " + name + "\nConent = " + content + "\n";
	}	
}
