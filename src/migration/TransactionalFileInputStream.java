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
        	RandomAccessFile file;
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

    @Override
    public int read(byte[] b) throws IOException {
        //not reach the end already
        if (lastContent != -1) {
        	RandomAccessFile file;
            file = new RandomAccessFile(fileName, "r");
            if (file == null) {
                throw new IOException("This file has not been opened");
            }

            file.seek(offset);
            lastContent = file.read(b);
            file.close();
            offset += b.length;
        }
        return lastContent;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        //not reach the end already
        if (lastContent != -1) {
        	RandomAccessFile file;
            file = new RandomAccessFile(fileName, "r");
            if (file == null) {
                throw new IOException("This file has not been opened");
            }

            file.seek(offset);
            lastContent = file.read(b, off, len);
            file.close();
            offset += len;
        }
        return lastContent;
    }

    public String getFileName() {
        return fileName;
    }
}
