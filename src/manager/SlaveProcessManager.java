package manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.ArrayList;

import migration.MigratableProcess;
import rule.Slave;
import util.Constants;

public class SlaveProcessManager extends MasterProcessManager{

    //fields
    protected ArrayList<MigratableProcess> processList;
    protected ArrayList<Thread> threadList;

    public SlaveProcessManager() {}

    public SlaveProcessManager(int port, String hostIpAddr) {
        processList = new ArrayList<MigratableProcess>();
        threadList = new ArrayList<Thread>();
        host = new Slave(port, hostIpAddr, this.processList, this.threadList);

        new Thread(host).start();
        excuting();
    }

    public ArrayList<MigratableProcess> getProcessList() {
    	return processList;
    }
    public ArrayList<Thread> getThreadList() {
    	return threadList;
    }
    @Override
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

            if (cmdInput.equals("quit")) {
            	break;
            }

            if (cmdInput.equals("ps")) { 
                showProcesses();
            } else if (cmdInput != null && !cmdInput.equals("\n")) {
                MigratableProcess mProcess = launchProcess(cmdInput.split(" "));
                if (mProcess != null) {
                    synchronized(processList) {
                        processList.add(mProcess);
                    }
                    Thread thread = new Thread(mProcess);
                    thread.start();
                    synchronized(threadList) {
                    	threadList.add(thread);
                    }
                }
                
            }
        }
        disconnection();
    }

    /**
     * launch a process in ProcessManager
     * @param serializedObject arg[0]: name arg[1..n] args 
     */
    private MigratableProcess launchProcess(String[] cmdInput) {
        if (cmdInput.length == 0) {
            return null;
        }

        try {
            Class<?> obj = Class.forName(Constants.CLASS_PREFIX+cmdInput[0].toString());
            Constructor<?> objConstructor = obj.getConstructor(String[].class);

            String[] args = new String[cmdInput.length - 1];
            
            if (cmdInput.length > 1) {
                System.arraycopy(cmdInput, 1, args, 0, cmdInput.length - 1);
            }
            for(String e : args) {
            	System.out.println(e);
            }
            return (MigratableProcess) objConstructor.newInstance(new Object[] {args});
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException |
        		IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            System.out.println("Invalid command!");
        } 
        return null;
    }

    private void showProcesses() {

    		if (this.processList.size() == 0) {
    			System.out.println("No processes is running");
    		} else {
    			for (MigratableProcess mProcess : this.processList) {
    				System.out.println(mProcess.toString());
    			}
    		}
    	
    }

    @Override
    public void disconnection() {
    	Socket hostSocket = ((Slave)host).getSocket();
        try {
            PrintWriter serverOutput = new PrintWriter(hostSocket.getOutputStream(), true);
            serverOutput.println(Constants.CONN_QUIT);
            serverOutput.flush();
            System.out.println("Waiting for quit comfirm from Master.");
            while(true);
            
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
