package miner.process;

import java.util.ArrayList;

public class StatusResult {
	Boolean statusFound;
	ArrayList<String> statusProcessingResult;
	
	StatusResult(){
		statusFound = false;
		ArrayList<String> statusProcessingResult = null;
	}
	
	void setStatus(Boolean v_statusFound){
		statusFound = v_statusFound;
	}
	
	public Boolean getStatus(){
		return statusFound;
	}
	
	void setArray(ArrayList<String> v_statusProcessingResult){
		statusProcessingResult = v_statusProcessingResult;
	}
	
	public ArrayList<String> getArray(){
		return statusProcessingResult;
	}

}
