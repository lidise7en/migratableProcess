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

public class Master extends BasicPart{

    //fields
    public ArrayList<Socket> slaveSocketList;

    public Master(int port) {
        this.port = port;
        this.slaveSocketList = new ArrayList<Socket>();
    }

    @SuppressWarnings("resource")
    @Override
    public void run() {
       try{
           ServerSocket server = new ServerSocket(this.port);
           new Thread(new MasterResponse(this.slaveSocketList)).start();
           while (true) {
               Socket slaveSocket = server.accept();
               BufferedReader serverInput = new BufferedReader(new InputStreamReader(slaveSocket.getInputStream()));
               PrintWriter serverOutput = new PrintWriter(slaveSocket.getOutputStream(), true);

               //slave register
               String message = serverInput.readLine();
               if (message != null && message.equals(Constants.CONN_REGISTER)) {
                   synchronized(slaveSocketList) {
                       slaveSocketList.add(slaveSocket);
                       
                   }
                   //connection build, response
                   serverOutput.println(Constants.CONN_BUILD);
                   serverOutput.flush();
               }
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
        
    }

}
