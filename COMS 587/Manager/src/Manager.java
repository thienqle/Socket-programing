/*
 * @Student: Thien Le 
 */

import java.io.*;
import java.net.*;
class Manager
{
	public static void main(String args[]) throws Exception
	{
		AgentMonitor aAgentMonitor = new AgentMonitor();
		
			
		BeaconListener aBeaconListener = new BeaconListener(aAgentMonitor);
		Thread aThread = new Thread(aBeaconListener);
		aThread.start();
		
		while(true){
			Thread aAMThread = new Thread(aAgentMonitor);
			aAMThread.start();
			aAMThread.join();
		}
		
	}
}