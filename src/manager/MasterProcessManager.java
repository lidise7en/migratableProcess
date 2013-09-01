package manager;

import java.util.ArrayList;

import migration.MigratableProcess;
import rule.BasicPart;
import rule.Master;

public class MasterProcessManager extends SlaveProcessManager{

    //fields
    private ArrayList<MigratableProcess> processList;

    public MasterProcessManager(int port) {
        processList = new ArrayList<MigratableProcess>();
        BasicPart realProcess = new Master(port);
    }
}
