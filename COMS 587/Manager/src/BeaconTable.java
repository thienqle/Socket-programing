/*
 * @Student: Thien Le 
 */
import java.util.ArrayList;

public class BeaconTable {
	private ArrayList<Beacon> aBeaconTable;
	
	public BeaconTable(){
		aBeaconTable = new ArrayList<Beacon>();
	}
	
	public void InsertBeacon(Beacon aBeacon){
		aBeaconTable.add(aBeacon);
	}
	
	public void printTable(){
		for(int i=0;i<aBeaconTable.size();i++){
			System.out.println(aBeaconTable.get(i).toString());
		}
	}
	
	/*Return true if successfully remove, if not false*/
	public boolean RemoveBeacon(Beacon aBeacon){
		if(this.aBeaconTable.contains(aBeacon)){
			aBeaconTable.remove(aBeacon);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isContain(Beacon A){
		return aBeaconTable.contains(A);
	}
}
