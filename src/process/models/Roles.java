package Process.models;

import java.util.Date;

public class Roles {
		
	private Integer roleId;
	private String  BDFLrole;
	private String PEPAuthor;
	private Date dateElected;
	
	//Roles (){
	//	role = "";
	//}
	//bdfl
	// bdfl delegate
	
}

class Persons {			
	String name;
	Persons() {}
	public void setName(String v_name){ name = v_name; }
	public String getName()	{ return name; }			
}

class Role {
	String RoleName;
	Persons[] p = new Persons[5];
	Integer personCounter;
	
	Role() {
		for (int i=0; i< 5;i++){
			p[i] = new Persons();
		}
	}
	public void addPersonsToRole(String v_personName) {
		p[personCounter+1].setName(v_personName); 
	}
	public void setRoleName(String v_roleName){ RoleName = v_roleName; }
	public String getRoleName()	{ return RoleName; }	
		
	
}


class call {
	
	// System.out.println(" i accept pep new");

    call() {	
    // CALLING CODE
    	
    //Populate
	Role r1 = new Role();
	
	r1.setRoleName("bdfl");
	r1.addPersonsToRole("guido");

	//add forloop here to output persons in the role


	//bdfl heartily approve

	String roles[] = {"delegate", "co-delegate", "bdfl"};

	//later assign persons to their role 

		// maybe for each role
		for (String r: roles) {           
		    //Do your stuff here
		    System.out.println(r); 
		}
	
    }
	
}


