package rule;

import java.util.ArrayList;

import migration.MigratableProcess;

public class Master extends BasicPart{
    
    public Master(int port, ArrayList<MigratableProcess> internalProcess) {
        this.port = port;
        this.internalProcess = internalProcess;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

}
