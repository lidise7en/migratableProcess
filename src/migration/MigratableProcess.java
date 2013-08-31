package migration;
import java.io.Serializable;

interface MigratableProcess extends Runnable, Serializable {

    //functions
    void suspend(); //will be called before object is serialized
}
