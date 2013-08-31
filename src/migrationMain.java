import migration.GrepProcess;

import com.google.gson.Gson;


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
}
