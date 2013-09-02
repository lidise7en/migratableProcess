package manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import rule.BasicPart;
import rule.Master;

public class MasterProcessManager {

    public MasterProcessManager() {}

    public MasterProcessManager(int port) {
        BasicPart masterHost = new Master(port);
        new Thread(masterHost).start();
        excuting();
    }

    public void excuting() {
        String cmdInput = "";
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while (cmdInput.equals("quit")) {
            System.out.println("cmd% ");
            try {
                cmdInput = in.readLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
