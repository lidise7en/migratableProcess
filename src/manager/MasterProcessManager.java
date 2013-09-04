package manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

import rule.BasicPart;
import rule.Master;

public class MasterProcessManager {

	//fields
	protected BasicPart host;
	
    public MasterProcessManager() {}

    public MasterProcessManager(int port) {
        host = new Master(port);
        new Thread(host).start();
        excuting();
    }

    public void excuting() {
        String cmdInput = "";
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (!cmdInput.equals("quit")) {
            System.out.print("cmd% ");
            try {
                cmdInput = in.readLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        disconnection();
    }

    public void disconnection() {
    	List<Socket> socketList = ((Master)host).getSocketList();
    	for (Socket slavesocket : socketList) {
    		try {
				slavesocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
}
