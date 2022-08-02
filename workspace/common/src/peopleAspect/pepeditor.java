package peopleAspect;

import java.util.Date;

public class pepeditor{
	String pepeditor;
	Date dateaddedtopepeditorlist;
	
	//public () { //String v_coredev, Date v_dateaddedtocoredevlist){
		//this.coredev = v_coredev;	
		//this.dateaddedtocoredevlist = v_dateaddedtocoredevlist;	
	//}
	
	public void setPEPEditor(String pe) {			
		this.pepeditor = pe;		
	}
	
	public Date getDateaddedtopepeditorlist() {			
		return dateaddedtopepeditorlist;		
	}
	
	public void setDateaddedtopepeditorlist(Date dateaddedtopepeditorlist) {		
		this.dateaddedtopepeditorlist = dateaddedtopepeditorlist;		
	}
	
	public String getPEPeditor() {			
		return pepeditor;		
	}
}
