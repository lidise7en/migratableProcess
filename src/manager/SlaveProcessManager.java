package manager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.Class;
import java.util.ArrayList;

import migration.MigratableProcess;
import rule.BasicPart;
import rule.Slave;

public class SlaveProcessManager {

    //fields
    private ArrayList<MigratableProcess> processList;

    public SlaveProcessManager() {}

    public SlaveProcessManager(int port, String hostIpAddr) {
        processList = new ArrayList<MigratableProcess>();
        BasicPart realProcess = new Slave(port, hostIpAddr);
        //new Thread(launchProcess());
        //System.out.println(this.port+" "+this.hostIpAddr+" "+this.rule);
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
    public ArrayList<MigratableProcess> getProcessList() {
    	return processList;
    }
}
