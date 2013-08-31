package migration;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;


public class TransactionalFileInputStream extends InputStream
    implements Serializable {

    private static final long serialVersionUID = 1L;

    //fields
    private long offset; // file offset
    private String fileName;
    private RandomAccessFile file;
    private int lastContent;

    public TransactionalFileInputStream(String inFile) {
        fileName = inFile;
        offset = 0;
        lastContent = 0;
    }

    /**
     * transaction read: open, seek, perform, close
     */
    @Override
    public int read() throws IOException {
        //not reach the end already
        if (lastContent != -1) {

            file = new RandomAccessFile(fileName, "r");
            if (file == null) {
                throw new IOException("This file has not been opened");
            }

            file.seek(offset++);
            lastContent = file.read();
            file.close();
        }
        return lastContent;
    }

}
