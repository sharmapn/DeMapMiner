package readRepository.readRepositoryDistinctSenders;

public class DistinctSendersDetails {
	public Integer id;		
	public String emailAddress;	
	public String FullName;	
	public String FirstName;	
	public String LastName;	
	public String emailFirstSegment;
	
	public Integer getId() { 		return id;	}
	public void setId(Integer id) {		this.id = id;	}	
	public String getEmailAddress() {		return emailAddress;	}
	public void setEmailAddress(String emailAddress) {		this.emailAddress = emailAddress;	}
	public String getFullName() {		return FullName;	}
	public void setFullName(String fullName) {		FullName = fullName;	}
	public String getFirstName() {		return FirstName;	}
	public void setFirstName(String firstName) {		FirstName = firstName;	}
	public String getLastName() {		return LastName;	}
	public void setLastName(String lastName) {		LastName = lastName;	}
	public String getEmailFirstSegment() {		return emailFirstSegment;	}
	public void setEmailFirstSegment(String emailFirstSegment) {		this.emailFirstSegment = emailFirstSegment;	}
}
