/*
 * @Student: Thien Le 
 */

public class Beacon {
	private int ID;
	private int StartUpTime;
	private String IP;
	private int CmdPort;
	
	public Beacon(int ID, int StartUpTime, String IP,int CmdPort){
		this.ID = ID;
		this.StartUpTime = StartUpTime;
		this.IP = IP;
		this.CmdPort = CmdPort;
	}

	/*Initial a Beacon with input String*/
	public Beacon(String input){
		String []infos = input.split("#");
		if(infos.length<4){
			this.ID = 0; 
			this.StartUpTime = 0;
			this.IP = "0.0.0.0";
			this.CmdPort = 0;
		} else {
			this.ID = Integer.parseInt(infos[0]); 
			this.StartUpTime = Integer.parseInt(infos[1]);
			this.IP = infos[2];
			this.CmdPort = Integer.parseInt(infos[3]);
		}
	}
	
	public void decodeInputBeacon(String input){
		String []infos = input.split("#");
		this.ID = Integer.parseInt(infos[0]); 
		this.StartUpTime = Integer.parseInt(infos[1]);
		this.IP = infos[2];
		this.CmdPort = Integer.parseInt(infos[3]);
	}
	
	public int getID(){
		return this.ID;
	}
	
	public int getStartUpTime(){
		return this.StartUpTime;
	}
	
	public String getIP(){
		return this.IP;
	}
	
	public int getCmdPort(){
		return this.CmdPort;
	}
	
	public String toString(){
		StringBuffer result = new StringBuffer();
		result.append(Integer.toString(ID) + " ");
		result.append(Integer.toBinaryString(this.StartUpTime) + " ");
		result.append(this.IP);
		result.append(" "+ Integer.toString(this.CmdPort));
		return result.toString();
	}
}
