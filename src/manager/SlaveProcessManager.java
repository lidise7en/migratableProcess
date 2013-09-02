package manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import migration.MigratableProcess;
import rule.BasicPart;
import rule.Slave;

public class SlaveProcessManager {

    //fields
    protected ArrayList<MigratableProcess> processList;

    public SlaveProcessManager() {}

    public SlaveProcessManager(int port, String hostIpAddr) {
        processList = new ArrayList<MigratableProcess>();
        BasicPart realProcess = new Slave(port, hostIpAddr);

        new Thread(realProcess).start();
        excuting();
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
            Class<?> obj = Class.forName(cmdInput[0]);
            Constructor<?> objConstructor = obj.getConstructor(String[].class);

            String[] args = null;
            if (cmdInput.length > 1) {
                System.arraycopy(cmdInput, 1, args, 0, cmdInput.length-1);
            }
            return (MigratableProcess) objConstructor.newInstance((Object[]) args);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException | SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
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

            if (cmdInput.equals("ps")) {
                showProcesses();
            } else if (cmdInput != null && !cmdInput.equals("\n")) {
                MigratableProcess mProcess = launchProcess(cmdInput.split(""));
                if (mProcess != null) {
                    synchronized(processList) {
                        processList.add(mProcess);
                    }
                }
                new Thread(mProcess).start();
            }
        }
    }

    public ArrayList<MigratableProcess> getProcessList() {
    	return processList;
    }

    public void showProcesses(){
        //TODO: show processes List
    }
}
