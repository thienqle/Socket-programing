/*
 * Thien Le 
 */
import java.net.Socket;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;

/*This code is reference to the instruction in-class */
public class DataProcessor extends Thread{
	
	public String localTime;
	public String OS;
	
	public DataProcessor(Socket s){
		try {
			
			 DataOutputStream SendToServer = new DataOutputStream(s.getOutputStream());
		     BufferedReader ReceiveFromServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
		     
		     /*Send Message to Server*/
		     String SendMessage = "Message from Manager\n";
		     SendToServer.write(SendMessage.getBytes());
		     
		     /*Get message from server */
		     localTime = ReceiveFromServer.readLine();
		     OS = ReceiveFromServer.readLine();

		     s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Cannot connect TCP");
			e.printStackTrace();
		} 

	}

}
