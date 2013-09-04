package rule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import util.Constants;

public class MasterResponse implements Runnable {
	
	private ArrayList<Socket> slaveSocketList;
    private Map<Integer, Integer> freeSlaveMap;
    private Map<Integer, Integer> overloadMap;

	public MasterResponse(ArrayList<Socket> slaveSocketList) {
		this.slaveSocketList = slaveSocketList;
		this.freeSlaveMap  = new HashMap<Integer, Integer>();
		this.overloadMap  = new HashMap<Integer, Integer>();
	}

	@Override
	public void run() {
		try {
			while(true) {
				//System.out.println("size is " + this.slaveSocketList.size());
			    int totalProcessNum = fillSlaveMap();

			    if (totalProcessNum > this.slaveSocketList.size() * Constants.CONN_MAX_PROCESS) {
			        System.out.println("The whole System is overloaded.");
			        continue;
			    }

			    //do load balance
			    Iterator<Entry<Integer, Integer>> ito = overloadMap.entrySet().iterator();
			    Entry<Integer, Integer> oEntry;
				while (ito.hasNext()){
				    oEntry = ito.next();
				    for (Entry<Integer, Integer> fEntry : freeSlaveMap.entrySet()) {
				        while (fEntry.getValue() > 0) {
				            if (oEntry.getValue() > 0) {
				                migrateProcess(slaveSocketList.get(oEntry.getKey()), slaveSocketList.get(fEntry.getKey()));
				                oEntry.setValue(oEntry.getValue()-1);
				                fEntry.setValue(fEntry.getValue()-1);
				            } else if (ito.hasNext()) {
				                oEntry = ito.next();
				            } else {
				                break;
				            }
				        }
				    }
				}
				for(int i = 0;i < this.slaveSocketList.size();i ++) {
					PrintWriter sendLoadFinish = new PrintWriter(slaveSocketList.get(i).getOutputStream(), true);
					sendLoadFinish.println(Constants.CONN_LOADFINISH);
					sendLoadFinish.flush();
				}
				Thread.sleep(Constants.CONN_POLL_INTERVAL);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private int fillSlaveMap() throws NumberFormatException, IOException{
	    int totalProcessNum = 0;
	    if(slaveSocketList.size() == 0)
	    	return 0;
        for(int i = 0; i<slaveSocketList.size(); i++) {
        	PrintWriter sendReq = new PrintWriter(slaveSocketList.get(i).getOutputStream(), true);
        	sendReq.println(Constants.CONN_REQ);
        	sendReq.flush();
        	//System.out.println("Enter here");
        	String response = null;
        	long timer = System.currentTimeMillis();
        	while((response == null || response.equals(Constants.CONN_QUIT)) && 
        			System.currentTimeMillis() - timer < Constants.CONN_WAIT_TIME) {
        		BufferedReader in = new BufferedReader(new InputStreamReader(slaveSocketList.get(i).getInputStream()));
        		response = in.readLine();
        	}

        	//response timeout
        	if (response == null) {
        	    totalProcessNum += Constants.CONN_MAX_PROCESS;
        	    System.out.println("Can not get process number from "+ i + "th slave");
        	    continue;
        	}
        	//response received
            int processNum = Integer.parseInt(response); 
            if ( processNum > Constants.CONN_MAX_PROCESS) {
                overloadMap.put(i, processNum - Constants.CONN_MAX_PROCESS);
            }
            else if (processNum < Constants.CONN_MAX_PROCESS){
                freeSlaveMap.put(i, Constants.CONN_MAX_PROCESS - processNum);
            }
            totalProcessNum += processNum;
           
        }
        return totalProcessNum;
	}

	private void migrateProcess(Socket overloadSocket, Socket freeSocket) throws IOException {
        //connect to overload slave to write file
	    PrintWriter overloadOut = new PrintWriter(overloadSocket.getOutputStream(), true);
        overloadOut.println(Constants.CONN_LEAVE);
        overloadOut.flush();
        BufferedReader overloadIn = new BufferedReader(new InputStreamReader(overloadSocket.getInputStream()));
        String response = null;
        long time = System.currentTimeMillis();
        while ((response == null || response.equals(Constants.CONN_QUIT)) && 
        	System.currentTimeMillis() - time < Constants.CONN_WAIT_TIME) {
            response = overloadIn.readLine();
        }

        //connect to free slave to write file
        if (response != null) {
            PrintWriter emit = new PrintWriter(freeSocket.getOutputStream(), true);
            emit.println(Constants.CONN_GET + " "+ response);
            emit.flush();
        } else {
            throw new IOException("Connection Timout: Getting filename from slave");
        }
	}

	public void setSocketList(ArrayList<Socket> slaveSocketList) {
		this.slaveSocketList = slaveSocketList;
	}
}
