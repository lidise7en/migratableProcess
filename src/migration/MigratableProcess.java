package migration;
import java.io.Serializable;

public interface MigratableProcess extends Runnable, Serializable {

    //functions
    void suspend(); //will be called before object is serialized

    String toString(void);//get the string representation of process
}
