package Process.processLabels;

public class LabelsTypes {
	
	//pep_accepted pep_accepted pep accepted
	//pep_accepted pep_accepted pep was accepted
	//pep_accepted pep_updated  pep was updated
	
	String labelType;
	String labels[];
	
	public void addLabelType(String v_labelType){
		labelType = v_labelType;
	}
	
	public void addLabelsToType(String v_label){
		//add v_label to array
	}
	
	//checl new label 
	//if it belongs to same labeltype, retrn true
	public Boolean sameLabelType(String v_label){
		//check which labeltype the label belongs to
		//go through all labels in 
		return true;
	}
	
}
