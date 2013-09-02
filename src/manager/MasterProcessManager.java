package manager;

import rule.BasicPart;
import rule.Master;

public class MasterProcessManager extends SlaveProcessManager{

    public MasterProcessManager(int port) {
        BasicPart masterHost = new Master(port);
        new Thread(masterHost).start();
        excuting();
    }

    @Override
    public void showProcesses(){
        //TODO: show processes List every slave
    }
}
