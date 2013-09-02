package rule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import util.Constants;

public class MasterResponse implements Runnable {
	
	private ArrayList<Socket> slaveSocketList;
	public MasterResponse(ArrayList<Socket> slaveSocketList) {
		this.slaveSocketList = slaveSocketList;
	}
	public void run() {
		try {
			while(true) {
				ArrayList<Integer> freeSlaveQueue = new ArrayList<Integer>();
				ArrayList<Integer> overloadQueue = new ArrayList<Integer>();
				for(int i = 0;i < slaveSocketList.size();i ++) {
					BufferedReader in = new BufferedReader(new InputStreamReader(slaveSocketList.get(i).getInputStream()));
					String response = in.readLine();
					if(response != null && Integer.parseInt(response) > Constants.CONN_MAX_PROCESS) {
						overloadQueue.add(i);
					}
					else {
						freeSlaveQueue.add(i);
					}
				}
				for(int m : overloadQueue) {
					if(freeSlaveQueue.size() != 0) {
						PrintWriter out = new PrintWriter(this.slaveSocketList.get(m).getOutputStream(), true);
						out.println(Constants.CONN_LEAVE);
						BufferedReader in = new BufferedReader(new InputStreamReader(this.slaveSocketList.get(m).getInputStream()));
						String serializedObj = in.readLine();
						PrintWriter emit = new PrintWriter(this.slaveSocketList.get(freeSlaveQueue.remove(0)).getOutputStream(), true);
						emit.println(serializedObj);
						emit.flush();
					}else {
						System.out.println("The whole System is overloaded.");
						break;
					}
				}
				Thread.sleep(Constants.CONN_POLL_INTERVAL);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
