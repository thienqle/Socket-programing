/*
 * @Student: Thien Le 
 */

public class AgentInfo {
	//private int AgentID;
	private long lastBeacon;
	private long currBeacon;
	private boolean active;
	private int localTime;
	private String localOS;
	
	public AgentInfo(){
		//AgentID = 0;
		lastBeacon= 0;
		currBeacon = 0;
		localTime = 0;
		localOS = "";
		active = false;
	}
	
	public long getLastBeacon(){
		return this.lastBeacon;
	}
	
	public void setLastBeacon(long input){
		this.lastBeacon = input;
	}
	
	public long getCurrBeacon(){
		return this.currBeacon;
	}
	
	public void setCurrBeacon(long input){
		this.currBeacon = input;
	}
	
	public void setLocalTime(int input){
		this.localTime = input;
	}
	
	public int getLocalTime(){
		return this.localTime;
	}
	
	public String getLocalOS(){
		return this.localOS;
	}
	
	public void setLocalOS(String OS){
		this.localOS = OS;
	}
	
	public boolean getStatus(){
		return this.active;
	}
	
	public void setStatus(boolean status){
		this.active = status;
	}
}
