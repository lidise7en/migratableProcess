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
                    //send process number
                    PrintWriter processNum = new PrintWriter(socketToServer.getOutputStream(), true);
                    processNum.println(processlist.size());
                    processNum.flush();

                    //receive leave call or get call
                    BufferedReader inOrder = new BufferedReader(new InputStreamReader(slaveSocket.getInputStream()));
                    String migrateMsg = null;
                    long tim = System.currentTimeMillis();
                    while(migrateMsg == null && System.currentTimeMillis() - tim < Constants.CONN_WAIT_TIME) {
                    	migrateMsg = inOrder.readLine();
                    }

                    if(migrateMsg != null && migrateMsg.equals(Constants.CONN_LEAVE)) {
                        synchronized(this.processlist){
                            //serialize object and send back filename
                            MigratableProcess removedPro = this.processlist.remove(0);
                            removedPro.suspend();
                            String filename = SerializeProcess(removedPro);
                            PrintWriter leaveProcess = new PrintWriter(socketToServer.getOutputStream(), true);
                            leaveProcess.println(filename);
                            leaveProcess.flush();
                        }
                    }
                    else if(migrateMsg != null && migrateMsg.startsWith(Constants.CONN_GET)) {
                        //receive filename and deserialize it
                        String filename = migrateMsg.split(" ")[1];
                    	MigratableProcess newProcess = DeserializeProcess(filename);
                    	new Thread(newProcess).start();
                    	synchronized(this.processlist){
                    	    this.processlist.add(newProcess);
                    	}
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
}
