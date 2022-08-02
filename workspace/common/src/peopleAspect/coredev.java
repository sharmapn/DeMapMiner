package peopleAspect;

import java.util.Date;

public class coredev{
	String coredev;
	Date dateaddedtocoredevlist;
	
	//public () { //String v_coredev, Date v_dateaddedtocoredevlist){
		//this.coredev = v_coredev;	
		//this.dateaddedtocoredevlist = v_dateaddedtocoredevlist;	
	//}
	
	public void setCoredev(String coredev) {			
		this.coredev = coredev;		
	}
	
	public Date getDateaddedtocoredevlist() {			
		return dateaddedtocoredevlist;		
	}
	
	public void setDateaddedtocoredevlist(Date dateaddedtocoredevlist) {		
		this.dateaddedtocoredevlist = dateaddedtocoredevlist;		
	}
	
	public String getCoredev() {			
		return coredev;		
	}
}
