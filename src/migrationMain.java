
import com.google.gson.Gson;

import example.GrepProcess;

/**
 * 
 * @author zhuolinl, dili1
 *
 */
public class migrationMain {
    public static void main(String[] args) {
        String[] gProcessArgs = {"a","b","c"};
        try {
            GrepProcess gProcess = new GrepProcess(gProcessArgs);
            Gson gson = new Gson();
            System.out.println(gson.toJson(gProcess));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //TODO: create a new process and ship it to another node.
}
