package manager;

import java.util.ArrayList;

import migration.MigratableProcess;
import rule.BasicPart;
import rule.Master;

public class MasterProcessManager extends SlaveProcessManager{

    public MasterProcessManager(int port) {
        processList = new ArrayList<MigratableProcess>();
        BasicPart realProcess = new Master(port);

        new Thread(realProcess).start();
        excuting();
    }

    @Override
    public void showProcesses(){
        //TODO: show processes List every slave
    }
}
