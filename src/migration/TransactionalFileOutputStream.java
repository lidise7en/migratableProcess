package migration;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;


public class TransactionalFileOutputStream extends OutputStream
    implements Serializable {

    private static final long serialVersionUID = 1L;
 
    //fields
    private long offset; // file offset
    private String fileName;
    private RandomAccessFile file;

    /**
     * constructor with append flag
     * @param outFile
     * @param apflag, append write:true, else false
     */
    public TransactionalFileOutputStream(String outFile, boolean apflag) {
        fileName = outFile; 
        try {
            file = new RandomAccessFile(fileName, "w");
            offset = apflag ? file.length() : 0;
            file.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * transaction write: open, seek, perform, close
     */
    @Override
    public void write(int content) throws IOException {
        file = new RandomAccessFile(fileName, "w");
        if (file == null) {
            throw new IOException("This file has not been opened");
        }

        file.seek(offset++);
        file.write(content);
        file.close();
    }

}
