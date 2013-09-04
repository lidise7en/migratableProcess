package manager;

import util.Constants;


public class PMDispatchManager {
    private enum PART{
        MASTER, SLAVE;
    }

    //fields
    private String hostIpAddr;
    private int port;
    private PART rule;

    public PMDispatchManager(String[] args) {
        parse(args);
        System.out.println(this.port+" "+this.hostIpAddr+" "+this.rule);
        if (this.rule == PART.MASTER) {
            new MasterProcessManager(this.port); 
        } else {
            new SlaveProcessManager(this.port, this.hostIpAddr);
        }
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

                if (args[2].matches(Constants.ipAddrRE)) {
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
        if (portTmp < 0 || portTmp > Constants.MAX_PORT_NUM) {
            throw new Exception();
        }
        return portTmp;
    }

    /**
     * produce and initiate a processManager
     * @param args
     */
    public static void main(String[] args) {
       new PMDispatchManager(args);
       System.exit(0);
    }
}
