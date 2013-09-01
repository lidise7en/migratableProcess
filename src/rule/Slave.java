package rule;

import java.util.ArrayList;

import migration.MigratableProcess;

public class Slave extends BasicPart{

    protected String hostIpAddr;

    public Slave(int port, String hostIpAddr, ArrayList<MigratableProcess> internalProcess) {
        this.port = port;
        this.internalProcess = internalProcess;
        this.hostIpAddr = hostIpAddr;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

}
