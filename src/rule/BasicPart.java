package rule;

import java.util.ArrayList;

import migration.MigratableProcess;

public abstract class BasicPart implements Runnable{

    protected int port;
    protected ArrayList<MigratableProcess> internalProcess;
}
