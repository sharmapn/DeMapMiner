package postProcess;

public class StatesReasons {
	String state;
	String lesserState;
	String reason;
	
	void setData(String v_state, String lesserState, String v_reason) {
		this.state = v_state;
		this.reason =v_reason;
	}
	String getState() {
		return this.state;
	} 
	String getLesserState() {
		return this.lesserState;
	}
	String getReason() {
		return this.reason;
	}
}
