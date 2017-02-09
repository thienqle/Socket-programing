/*
 * @Student: Thien Le 
 */

import java.net.Socket;
import java.io.IOException;
import java.net.ServerSocket;

/*This code is reference to the instruction in-class */

public class ClientAgent  implements Runnable {

	private Socket aTCPSocket;
	private Beacon aBeacon;
	private AgentMonitor aAgentMonitor;
		
	public ClientAgent(Beacon aBeacon,AgentMonitor aAgentMonitor){
		this.aBeacon = aBeacon;
		this.aAgentMonitor = aAgentMonitor;
	}
		
	public String timeFormat(int input){
		String result = "";
		int hour = (input/3600);
		int minandsec = input%3600;
		int min = minandsec/60;
		int sec = (input - (hour*3600 + min*60));
		result += hour + ":" + min + ":" + sec;
		return result;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
	 		aTCPSocket = new Socket(aBeacon.getIP(),aBeacon.getCmdPort());
	    	DataProcessor aDataProcessor = new DataProcessor(aTCPSocket);
	    	//new DataProcessor(s).start();  // launch a thread to handle the current client
		    aDataProcessor.start();
		    aDataProcessor.join();
		    aAgentMonitor.InsertNewAgent(aBeacon.getID());
		    aAgentMonitor.updateLocalTime(aBeacon.getID(), Integer.parseInt(aDataProcessor.localTime));
		    aAgentMonitor.updateLocalOSBeacon(aBeacon.getID(),aDataProcessor.OS);
		    aAgentMonitor.updateCurrentBeacon(aBeacon.getID(),System.currentTimeMillis());
		    
			System.out.println("\nThere is active agent with ID: " + aBeacon.getID());
			System.out.println("OS information: " + aAgentMonitor.getLocalOS(aBeacon.getID()));
			System.out.println("Start up time: " + timeFormat(aAgentMonitor.getLocalTime(aBeacon.getID())) + "(" + aAgentMonitor.getLocalTime(aBeacon.getID()) + ")");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

}
