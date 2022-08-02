package Process.models;

import java.util.Date;

public class Thread {
	Date date;
	String From;
	String Status;
	//Message mesage;
	
	public Thread(Date v_date, String v_From, String v_Status) {
		this.date = v_date;
		this.From = v_From;
		this.Status = v_Status;
	}
	
	
	public void setDate(Date v_Date){
		 this.date = v_Date;
	}
	
	public void setFrom(String v_From){
		 this.From = v_From;
	}
	
	public void setStatus(String v_Status){
		 this.Status = v_Status;
	}
	
	public Date getDate(){
		return date;
	}
	
	public String getFrom(){
		return From;
	}
	
	public String getStatus(){
		return Status;
	}
	
	public String toString(){
		return date + ", " + From + ", " + Status + ", ";
	}

}
