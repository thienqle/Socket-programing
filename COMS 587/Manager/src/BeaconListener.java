/*
 *	Thien Le 
 */
import java.io.*;
import java.net.*;

public class BeaconListener implements Runnable{

	public BeaconTable aBeaconTable;
	public AgentMonitor aAgentMonitor; 
	
	public DatagramSocket ds;
	
	public BeaconListener(AgentMonitor aAgentMonitor) throws SocketException{
		ds = new DatagramSocket(4444);
		this.aAgentMonitor = aAgentMonitor;
		aBeaconTable = new BeaconTable();
	}
				
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			System.out.println("Manager applicaion is running!");
			while(true)
			{
			
				byte[] buffer = new byte[1024];
				DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
				ds.receive(incoming);
				
				byte[] data = new byte[incoming.getLength()];
				System.arraycopy(incoming.getData(), 0, data, 0, data.length); 

				String sClientBeacon = new String(data,0, incoming.getLength());
				
				Beacon aBeacon = new Beacon(sClientBeacon);
				synchronized(this.aAgentMonitor){			
					if(!aAgentMonitor.isContain(aBeacon.getID())){
						if(aBeacon.getID()!=0){
							ClientAgent aAgentClient = new ClientAgent(aBeacon,aAgentMonitor);
							Thread aTCPThread = new Thread(aAgentClient);
							aTCPThread.start();
							//aTCPThread.join();
						}
					} else {
						if(aBeacon.getID()!=0){
							if(aAgentMonitor.getLocalTime(aBeacon.getID())!= aBeacon.getStartUpTime()){
								System.out.println("\nAgent " + aBeacon.getID() + " has been restarted");
								aAgentMonitor.updateLocalTime(aBeacon.getID(), aBeacon.getStartUpTime());
								aAgentMonitor.updateCurrentBeacon(aBeacon.getID(),System.currentTimeMillis());
							} else {
								aAgentMonitor.updateCurrentBeacon(aBeacon.getID(),System.currentTimeMillis());
							}
						}
					}
				}
				
		    }

		} catch (Exception e) {
		  	System.err.println("Error: " + e);
		}
	}

}
