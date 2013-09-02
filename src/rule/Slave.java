package rule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import util.Constants;
import migration.MigratableProcess;

public class Slave extends BasicPart{

    protected String hostIpAddr;
    private Socket slaveSocket;
    private ArrayList<MigratableProcess> processlist;
    public Slave(int port, String hostIpAddr, ArrayList<MigratableProcess> processlist) {
        this.port = port;
        this.hostIpAddr = hostIpAddr;
        this.processlist = processlist;
    }

    @SuppressWarnings("resource")
    @Override
    public void run() {
        try {
            Socket socketToServer = new Socket(this.hostIpAddr, this.port);
            PrintWriter out = new PrintWriter(socketToServer.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(slaveSocket.getInputStream()));

            //register
            out.println(Constants.CONN_REGISTER);
            out.flush();

            String message = null;
            long timer = System.currentTimeMillis(); 
            //wait response for 5s
            while (message == null || 
                    (!message.equals(Constants.CONN_BUILD) && System.currentTimeMillis() - timer < Constants.CONN_WAIT_TIME) ) {
            	message = in.readLine();
            }

            //connection build
            if (message.equals(Constants.CONN_BUILD)) {
            	System.out.println("Connection to master has built.");
                while(true) {
                    
                    PrintWriter processNum = new PrintWriter(socketToServer.getOutputStream(), true);
                    processNum.println(processlist.size());
                    processNum.flush();
                    BufferedReader inOrder = new BufferedReader(new InputStreamReader(slaveSocket.getInputStream()));
                    String migrateMsg = null;
                    long tim = System.currentTimeMillis();
                    while(migrateMsg == null || System.currentTimeMillis() - tim < Constants.CONN_WAIT_TIME) {
                    	migrateMsg = inOrder.readLine();
                    }
                    if(migrateMsg == Constants.CONN_LEAVE) {
                    	MigratableProcess removedPro = this.processlist.remove(0);
                    	removedPro.suspend();
                    	String serializedPro = SerializePro(removedPro);
                    	PrintWriter leaveProcess = new PrintWriter(socketToServer.getOutputStream(), true);
                    	leaveProcess.println(serializedPro);
                    	leaveProcess.flush();
                    }
                    else if(migrateMsg != null) {
                    	MigratableProcess newProcess = DeserializePro(migrateMsg);
                    	new Thread(newProcess).start();
                    	this.processlist.add(newProcess);
                    }
                }
            }
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
    }
    public String SerializePro(MigratableProcess migratablePro) {
    	//TODO serialize the process
    	return null;
    }
    public MigratableProcess DeserializePro(String serializedPro) {
    	//TODO deserialize the process
    	return null;
    }
}
