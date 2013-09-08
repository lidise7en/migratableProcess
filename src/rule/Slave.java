package rule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import migration.MigratableProcess;
import util.Constants;

public class Slave extends BasicPart{

    protected String hostIpAddr;
    //private Socket slaveSocket;
    private Socket socketToServer;
    private ArrayList<MigratableProcess> processlist;
    public Slave(int port, String hostIpAddr, ArrayList<MigratableProcess> processlist) {
        this.port = port;
        this.hostIpAddr = hostIpAddr;
        this.processlist = processlist;
    }

    @Override
    public void run() {
        try {
        	socketToServer = new Socket(this.hostIpAddr, this.port);
            PrintWriter out = new PrintWriter(socketToServer.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socketToServer.getInputStream()));


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
                	//wait for process number request
                	BufferedReader numReq = new BufferedReader(new InputStreamReader(socketToServer.getInputStream()));
                	for(int i = 0;i < processlist.size();i ++) {
                		if(processlist.get(i).getAlive() == false) {
System.out.println("delete a process");
                			processlist.remove(i);
                			i --;
                		}
                	}
                    String numRequest = null;
                    while(numRequest == null) {
                    	numRequest = numReq.readLine();
                    }

                    if(numRequest.equals(Constants.CONN_REQ)) {
                    	//send process number
                    	//System.out.println("Slave receive the REQ message!");
                    	PrintWriter processNum = new PrintWriter(socketToServer.getOutputStream(), true);
                    	processNum.println(processlist.size());
                    	processNum.flush();
                    	//System.out.println("Slave sent the number of processes");
                    	//receive leave call or get call
                    	BufferedReader inOrder = new BufferedReader(new InputStreamReader(socketToServer.getInputStream()));
                    }
                    else if(numRequest != null && numRequest.equals(Constants.CONN_LEAVE)) {
                    		synchronized(this.processlist){
                    			//serialize object and send back filename
System.out.println("Slave receive the LEAVE msg.");
                    			MigratableProcess removedPro = this.processlist.remove(0);
                    			removedPro.suspend();
System.out.println("process is suspend");
                    			String filename = SerializeProcess(removedPro);
                    			PrintWriter leaveProcess = new PrintWriter(socketToServer.getOutputStream(), true);
                    			leaveProcess.println(filename);
                    			leaveProcess.flush();
System.out.println("filename is sent");
                    		}
                    }
                    else if(numRequest != null && numRequest.startsWith(Constants.CONN_GET)) {
                    		//receive filename and deserialize it
                    		String filename = numRequest.split(" ")[1];
System.out.println("Slave recevied filename: " + filename);
                    		MigratableProcess newProcess = DeserializeProcess(filename);
                    		if(newProcess == null)
System.out.println("The new process is null");
System.out.println("Slave before start.");
                    		new Thread(newProcess).start();
System.out.println("After start");
                    		synchronized(this.processlist){
System.out.println("Really add to the list");
System.out.println("before length" + processlist.size());
                    			this.processlist.add(newProcess);
System.out.println("after length" + processlist.size());
                    		}
System.out.println("add to pricesslist");
                    }
                    else if(numRequest.equals(Constants.CONN_QUIT)) {
                    	socketToServer.close();
                    	System.exit(0);
                    }else{
                    	System.out.println("LoadFinish msg from master.Ignore");
                    }
                }
            }
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("Connection build aborted...");
            System.exit(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("Connection build aborted...");
            System.exit(0);
        }
      
    }

    public static String SerializeProcess(MigratableProcess migratablePro) {
        String filename = null;

        try {
            Random rand = new Random();
            BigInteger bint = new BigInteger(32, rand);
            filename = Constants.FILE_FOLDER + File.separator + bint.toString() + ".dat";

            //write serialized object into file
            ObjectOutput s = new ObjectOutputStream(new FileOutputStream(filename));
            System.out.println("filename is" + filename);
            s.writeObject(migratablePro);
            s.flush();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filename;
    }

    @SuppressWarnings("resource")
    public static MigratableProcess DeserializeProcess(String filename) {
        ObjectInputStream in;
        MigratableProcess mProcess = null;
        try {
            in = new ObjectInputStream(new FileInputStream(filename));
            mProcess = (MigratableProcess)in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mProcess;
    }

    public Socket getSocket() {
    	return socketToServer;
    }
}
