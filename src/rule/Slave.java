package rule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import util.Constants;

public class Slave extends BasicPart{

    protected String hostIpAddr;
    private Socket slaveSocket;

    public Slave(int port, String hostIpAddr) {
        this.port = port;
        this.hostIpAddr = hostIpAddr;
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

            String message = in.readLine();
            long timer = System.currentTimeMillis(); 
            //wait response for 5s
            while (message == null || 
                    (!message.equals(Constants.CONN_BUILD) && System.currentTimeMillis() - timer < Constants.CONN_WAIT_TIME) ) {}

            //connection build
            if (message.equals(Constants.CONN_BUILD)) {
                while(true){
                    //TODO: process message
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

}
