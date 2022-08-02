package Process.models;

import java.util.Date;

public class States {
	String State;
	String vDate;
	
	public States(){
		this.State = null;
		this.vDate = null;
	}
	
    public void setState(String v_state) {
        this.State = v_state;
    }
    public void setDate(String v_Date) {
        this.vDate = v_Date;
    }
    public String getState(){
	    return State;
    }
    public String getDate(){
	    return vDate;
    }
    
}



	
	
	