package migration;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;


public class TransactionalFileOutputStream extends OutputStream
    implements Serializable {

    private static final long serialVersionUID = 1L;

    public TransactionalFileOutputStream(String outFile, boolean mflag) {
        
    }

    @Override
    public void write(int arg0) throws IOException {
        // TODO Auto-generated method stub

    }

}
