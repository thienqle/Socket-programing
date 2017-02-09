/*
 * @Student: Thien Le 
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;



public class AgentMonitor implements Runnable{
	HashMap<Integer,AgentInfo> aAgentList;
	int MaximumDelayTime = 20;
	ArrayList<Integer> DeadAgent;
	
	public AgentMonitor(){
		aAgentList = new HashMap<Integer,AgentInfo>();
		DeadAgent = new ArrayList<Integer>(); 
	}
	
	public boolean isContain(int AgentID){
		if(aAgentList.isEmpty()){
			return false;
		}
		return aAgentList.containsKey(AgentID);
	}
	
	public void updateCurrentBeacon(int AgentID, long currentBeacon){
		//aAgentList.get(AgentID).setLastBeacon(aAgentList.get(AgentID).getCurrBeacon());
		aAgentList.get(AgentID).setLastBeacon(currentBeacon);
		aAgentList.get(AgentID).setCurrBeacon(currentBeacon);
	}
	
	public void updateLocalOSBeacon(int AgentID, String OS){
		aAgentList.get(AgentID).setLocalOS(OS);
	}
	
	public String getLocalOS(int AgentID){
		if(!aAgentList.containsKey(AgentID)){
			System.out.println("There is no " + AgentID);
			return "";
		}
		return aAgentList.get(AgentID).getLocalOS();
	}
	

	public int getLocalTime(int AgentID){
		if(!aAgentList.containsKey(AgentID)){
			System.out.println("There is no " + AgentID);
			return 0;
		}
		return aAgentList.get(AgentID).getLocalTime();
	}
	
	public void InsertNewAgent(int AgentID){
		aAgentList.put(AgentID,new AgentInfo());
	}
	
	public AgentInfo getAgentInfo(int AgentID){
		return this.aAgentList.get(AgentID);
	}
	
	public void updateLocalTime(int AgentID, int time){
		aAgentList.get(AgentID).setLocalTime(time);
	}
	
	public void updateActiveList(){
		for(int i=0;i<this.DeadAgent.size();i++){
			if(this.aAgentList.containsKey(DeadAgent.get(i) )){
				System.out.println("\nAgent " + DeadAgent.get(i) + " is dead");
				this.aAgentList.remove(DeadAgent.get(i));
			}
		}
		this.DeadAgent.clear();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		//while(true){
			//System.out.println("Agent Monitor is checking!" + this.aAgentList.size());
			synchronized(this.aAgentList){
				if(this.aAgentList.isEmpty()){
					//return;
				} else {
					for(int key : this.aAgentList.keySet()){
						if((System.currentTimeMillis() - this.getAgentInfo(key).getCurrBeacon())/1000 > MaximumDelayTime){
							//System.out.println("\n" + System.currentTimeMillis() + " - " +  this.getAgentInfo(key).getCurrBeacon() + "/1000 = " + (System.currentTimeMillis() - this.getAgentInfo(key).getCurrBeacon())/1000);
							//System.out.println("\nAgent " + key + " is dead");
							//this.aAgentList.remove(key);
							if(!this.DeadAgent.contains(key)){
								this.DeadAgent.add(key);
							}
						}
					}
					this.updateActiveList();
				}
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		//}
	}
	
}
