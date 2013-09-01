package manager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.Class<T>;
import java.util.ArrayList;

import migration.MigratableProcess;
import rule.BasicPart;
import rule.Master;
import rule.Slave;

public class ProcessManager {
    //constants and enums
    private static final String ipAddrRE = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\."+
    		"((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\."+
    				"((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
    private static final int MAX_PORT_NUM = 65535;
    private enum PART{
        MASTER, SLAVE;
    }

    //fields
    private String hostIpAddr;
    private int port;
    private PART rule;
    private ArrayList<MigratableProcess> processList;

    public ProcessManager(String[] args) {
        parse(args);
        processList = new ArrayList<MigratableProcess>();
        BasicPart realProcess = this.rule == PART.MASTER ? new Master(this.port, processList) : 
            new Slave(this.port, this.hostIpAddr, processList);

        //new Thread(launchProcess());
        //System.out.println(this.port+" "+this.hostIpAddr+" "+this.rule);
    }

    /**
     * pase command line
     * @param args
     */
    private void parse(String[] args) {
        boolean robustFlag = true;

        try{
            if (args.length == 1 && args[0].equals("-h")) {
                System.out.println("cmdLine Helper: \n"+
                        "For slave usage: [port -c hostIpAddr]\n"+
                        "For master usage: [port]");
                System.exit(0);
            } else if (args.length == 3) {
                rule = PART.SLAVE;
                port = parsePort(args[0]);

                if (!args[1].equals("-c")) {
                    throw new Exception();
                }

                if (args[2].matches(ipAddrRE)) {
                    hostIpAddr = args[2];
                } else {
                   throw new Exception();
                }

            } else if (args.length == 1) {
                rule = PART.MASTER;
                port = parsePort(args[0]);
            } else {
                robustFlag = false;
            }

        } catch (Exception e) {
            robustFlag = false;
        }

        if (!robustFlag) {
            System.out.println("Error Arguments, plase use -h to show cmd usage");
            System.exit(0);
        }

    }

    //small helper function to parse port number
    private int parsePort(String portString) throws Exception {
        int portTmp = Integer.parseInt(portString);
        if (portTmp < 0 || portTmp > MAX_PORT_NUM) {
            throw new Exception();
        }
        return portTmp;
    }

    /**
     * launch a process in ProcessManager
     * @param serializedObject arg[0]: name arg[1..n] args 
     */
    private MigratableProcess launchProcess(String[] serializedObject) {
        if (serializedObject.length == 0) {
            return null;
        }

        try {
            Class<?> obj = Class.forName(serializedObject[0]);
            Constructor<?> objConstructor = obj.getConstructor(String[].class);

            String[] args = null;
            if (serializedObject.length > 1) {
                System.arraycopy(serializedObject, 1, args, 0, serializedObject.length-1);
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
    /**
     * Create a new process with Process Manager
     * @param Class<T> 
     */
    private void createNewProcess(Class newInstance) {
        
    }
    /**
     * produce and initiate a processManager
     * @param args
     */
    public static void main(String[] args) {
       new ProcessManager(args);
    }
}
