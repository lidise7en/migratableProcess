package util;

public class Constants {
    /*
     * manager constants
     */
    public static final String ipAddrRE = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\."+
            "((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\."+
                    "((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
    public static final int MAX_PORT_NUM = 65535;

    /*
     * connection constants
     */
    public static final String CONN_REGISTER = "R";
    public static final String CONN_BUILD = "B";
    public static final String CONN_REQ = "REQ";
    public static final String CONN_LEAVE = "L";
    public static final String CONN_GET = "G";
    public static final String CONN_QUIT = "Q";
    
    public static final long CONN_WAIT_TIME = 2000;
    public static final int CONN_MAX_PROCESS = 5;
    public static final long CONN_POLL_INTERVAL = 5000;

    /*
     * IO constants 
     */
    public static final String FILE_FOLDER = System.getProperty("user.dir");
}
