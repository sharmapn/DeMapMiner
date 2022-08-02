package miner.processLabels;

public class SearchCriteria {
	String entity,artifact,action,reason;
	
	public SearchCriteria(){}	
	
    public void setEntity(String v_entity){    	this.entity = v_entity;    }
    public void setAction(String v_action){    	this.action = v_action;    }
    public void setArtifact(String v_artifact){    	this.artifact = v_artifact;    }    
    public void setReason(String v_artifact){    	this.artifact = v_artifact;    }    
    String getAction(){    	return this.action;    }
    String getEntity(){    	return this.entity;    }
    String getArtifact(){    	return this.artifact;    }
    String getReason(){    	return this.reason;    }
    
    //public void setWatheverYouThinkIsImportant(String str){...}
}