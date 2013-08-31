package migration;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;


public class TransactionalFileInputStream extends InputStream
    implements Serializable {

    private static final long serialVersionUID = 1L;

    public TransactionalFileInputStream(String inFile) {
        
    }

    @Override
    public int read() throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

}
