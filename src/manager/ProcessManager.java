package manager;

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

    public ProcessManager(String[] args) {
        parse(args);

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
     * produce and initiate a processManager
     * @param args
     */
    public static void main(String[] args) {
       new ProcessManager(args);
    }
}
