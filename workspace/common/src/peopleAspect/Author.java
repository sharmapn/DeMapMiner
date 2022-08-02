package peopleAspect;

public class Author {
	String author;
	String firstName;
	String lastName;
	
	public void setData(String a, String f, String l){
		author =a;
		firstName=f;
		lastName=l;
	}
	public String getName(){
		return author;
	}
	public String getFirstName(){
		return firstName;
	}
	public String getLastName(){
		return lastName;
	}	
}
